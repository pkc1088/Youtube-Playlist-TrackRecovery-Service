package youtube.youtubeProject.service.musics;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import youtube.youtubeProject.domain.Music;
import youtube.youtubeProject.repository.musics.MusicRepository;
import youtube.youtubeProject.repository.playlists.PlaylistRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MusicServiceV1 implements MusicService{

    @Value("${youtube.api.key}")
    private String apiKey;
    private static YouTube youtube;
    private final PlaylistRepository playlistRepository;
    private final MusicRepository musicRepository;

    public MusicServiceV1(PlaylistRepository playlistRepository, MusicRepository musicRepository) {
        this.playlistRepository = playlistRepository;
        this.musicRepository = musicRepository;
        youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {}).setApplicationName("youtube").build();
    }

//    public PlaylistItemListResponse getPlaylistItemListResponse(String playlistId, Long maxResults) throws IOException { // 내부에서 호출해야함
//        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id, status"));
//        request.setKey(apiKey);
//        request.setPlaylistId(playlistId);
//        request.setMaxResults(maxResults);
//        return request.execute();
//    }

    public List<PlaylistItem> getPlaylistItemListResponse(String playlistId, Long maxResults) throws IOException { // 내부에서 호출해야함
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id, status"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(maxResults);
        // page
        List<PlaylistItem> allPlaylists = new ArrayList<>();
        String nextPageToken = null;
        do {
            request.setPageToken(nextPageToken); // 다음 페이지 토큰 설정
            PlaylistItemListResponse response = request.execute();
            allPlaylists.addAll(response.getItems());
            nextPageToken = response.getNextPageToken();
        } while (nextPageToken != null); // 더 이상 페이지가 없을 때까지 반복

        return allPlaylists;
    }

    public Video getVideoDetailResponseWithFilter(String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(Collections.singletonList("snippet, id, status"));
        request.setKey(apiKey);
        request.setId(Collections.singletonList(videoId));
        VideoListResponse response = request.execute();

        Video video = response.getItems().get(0);
        if(video.getStatus().getPrivacyStatus().equals("public") && video.getStatus().getUploadStatus().equals("processed")) {
            return video;
        } else if (video.isEmpty()) {
            throw new IndexOutOfBoundsException("Deleted/Private Video");
        } else {
            throw new RuntimeException("Not A Public Video");
        }
    }

    // page 로 읽어들여야함
    @Override
    public void initiallyAddVideoDetails(String playlistId) throws IOException {
//        PlaylistItemListResponse response = getPlaylistItemListResponse(playlistId, 50L);
        List<PlaylistItem> response = getPlaylistItemListResponse(playlistId, 50L);
        for (PlaylistItem item : response) { // .getItems() 제거했음
            String videoId = item.getSnippet().getResourceId().getVideoId();
            DBAddAction(videoId, playlistId);
        }
    }

    public void DBAddAction(String videoId, String playlistId) throws IOException {
        try {
            Video video = getVideoDetailResponseWithFilter(videoId);
            Music music = new Music();

            music.setVideoId(videoId);
            music.setVideoTitle(video.getSnippet().getTitle());
            music.setVideoUploader(video.getSnippet().getChannelTitle());
            music.setVideoDescription("someDescription"); // video.getSnippet().getDescription();
            music.setVideoTags("someTags"); // video.getSnippet().getTags();
//            music.setVideoPlaylistPosition(5);  // 굳이? 필요한지 판단
            music.setPlaylist(playlistRepository.findByPlaylistId(playlistId));
            musicRepository.addUpdatePlaylist(playlistId, music);

        } catch (RuntimeException e) {
            log.info("Inaccessible Video Adding -> Aborted From DBAddAction");
        }
    }

    // page 로 읽어들여야함
    @Override
    public void updatePlaylist(String playlistId) throws IOException {
        log.info("update playlist start ... {}", playlistId);

        // 1. 고객 플레이리스트 담긴 디비 불러오기
        List<Music> musicDBList = musicRepository.findAllMusicByPlaylistId(playlistId);
        Set<String> musicDBSet = musicDBList.stream().map(Music::getVideoId).collect(Collectors.toSet());

        // 2. API 검색으로 고객 플레이리스트 목록 불러오기
//        PlaylistItemListResponse response = getPlaylistItemListResponse(playlistId, 50L);
        List<PlaylistItem> response = getPlaylistItemListResponse(playlistId, 50L);

        List<String> apiMusicList = new ArrayList<>();
        for (PlaylistItem item : response) { // .getItems() 제거했음
            String videoId = item.getSnippet().getResourceId().getVideoId();
            apiMusicList.add(videoId);
        }
        Set<String> apiMusicSet = new HashSet<>(apiMusicList);

        // 3. 둘의 차이를 비교
        // 3-1. API 에는 있지만 DB 에는 없는 음악
        Set<String> addedMusicSet = new HashSet<>(apiMusicSet);
        addedMusicSet.removeAll(musicDBSet);
        // 3-2. DB 에는 있지만 API 에는 없는 음악
        Set<String> removedMusicSet = new HashSet<>(musicDBSet);
        removedMusicSet.removeAll(apiMusicSet);

        // 4. 고객이 추가 혹은 제거한 영상을 DB 에 반영
        if(!addedMusicSet.isEmpty())
            for(String videoId : addedMusicSet) {
                DBAddAction(videoId, playlistId);
            } else { log.info("nothing to add"); }

        if(!removedMusicSet.isEmpty())
            for(String videoId : removedMusicSet) {
                musicRepository.deleteUpdatePlaylist(playlistId, videoId);
            } else { log.info("nothing to remove"); }

        // 5. 고려 사항
        // 5-1. 플레이리스트 자체가 없을 수가 있다. (고객이 제거해서 -> 2번에서 catch 로 잡아야 할 듯)
        // 5-2. 플레이리스트 이름이 변경 됐을 수 있다.
        // 5-3. 고려하지 않아도 될 사항 : 플리 자체가 추가 됐다면 그건 고객이 다시 등록을 해야한다.
        log.info("update playlist done ... {}", playlistId);
    }
}
