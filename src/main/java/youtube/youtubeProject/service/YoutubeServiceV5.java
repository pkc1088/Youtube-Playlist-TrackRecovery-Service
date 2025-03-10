package youtube.youtubeProject.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import youtube.youtubeProject.domain.Music;
import youtube.youtubeProject.repository.YoutubeRepository;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

//@RequiredArgsConstructor
@Service
public class YoutubeServiceV5 implements YoutubeService{

    @Value("${youtube.api.key}")
    private String apiKey;
    private static YouTube youtube;
    private final YoutubeRepository youtubeRepository;

    public YoutubeServiceV5(YoutubeRepository youtubeRepository) {
        this.youtubeRepository = youtubeRepository;// YouTube 객체를 빌드하여 API에 접근할 수 있는 YouTube 클라이언트 생성
        youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {}).setApplicationName("youtube").build();
    }

    public String addVideoToPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId, long videoPosition) {
        try {
            GoogleCredential credential = new GoogleCredential()
                    .setAccessToken(authorizedClient.getAccessToken().getTokenValue());
            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("youtube-cmdline-addto-playlist-sample")
                    .build();

            ResourceId resourceId = new ResourceId();
            resourceId.setKind("youtube#video");
            resourceId.setVideoId(videoId);

            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
            playlistItemSnippet.setPlaylistId(playlistId);
            playlistItemSnippet.setResourceId(resourceId);
            playlistItemSnippet.setPosition(videoPosition); // added

            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setSnippet(playlistItemSnippet);

            YouTube.PlaylistItems.Insert request = youtube.playlistItems().insert(Collections.singletonList("snippet"), playlistItem);
            PlaylistItem response = request.execute();

            return "Video added to playlist: " + response.getId();
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to add video to playlist";
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteFromPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId) {
        try {
            GoogleCredential credential = new GoogleCredential()
                    .setAccessToken(authorizedClient.getAccessToken().getTokenValue());
            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("youtube-cmdline-deletefrom-playlist-sample")
                    .build();

            // 재생목록에서 영상을 찾기 위해 playlistItems.list 호출
            YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems().list(Collections.singletonList("id,snippet"));
            playlistItemsRequest.setPlaylistId(playlistId);
            playlistItemsRequest.setMaxResults(50L);

            PlaylistItemListResponse playlistItemsResponse = playlistItemsRequest.execute();
            List<PlaylistItem> playlistItems = playlistItemsResponse.getItems();

            // 영상 ID와 일치하는 재생목록 항목을 찾음
            for (PlaylistItem playlistItem : playlistItems) {
                if (playlistItem.getSnippet().getResourceId().getVideoId().equals(videoId)) {
                    YouTube.PlaylistItems.Delete deleteRequest = youtube.playlistItems().delete(playlistItem.getId());
                    deleteRequest.execute();
                    return "Video deleted from playlist: " + videoId;
                }
            }

            return "Video not found in playlist: " + videoId;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return "Failed to delete video from playlist";
        }
    }

    public List<Video> initiallyAddVideoDetails(String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(50L);
        PlaylistItemListResponse response = request.execute();
        List<Video> videos = new ArrayList<>();

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId();
            String videoName = item.getSnippet().getTitle(); // 비공개는 'Private Video' 라고만 받을 수 있음
            try {
                Video video = getVideoDetails(videoId); // 이런식으로 비디오 상세 정보 뽑아서 디비에 저장가능
                String description = video.getSnippet().getDescription();
                String videoUploader = video.getSnippet().getChannelTitle();
                List<String> tags = video.getSnippet().getTags();

                videos.add(video);
                System.out.println("successfully added : " + videoName + "(" + videoId + ") by " + videoUploader);

                youtubeRepository.initiallyAddVideoDetails(playlistId, videoName, videoId, videoUploader); // 성공

            } catch (IOException e) {
                System.err.println("Failed to fetch details for video : " + videoId); e.printStackTrace();
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                System.err.println("Inaccessible Video : " + videoName + "(" + videoId + ")");
                // 만약 최초 등록시 고객의 플리에 접근금지된 영상이 존재하면 알아내는 용도 (내가 대신 추가해 줄 수는 없다 당연히)
            }
        }
        return videos;
    }

    public Video getVideoDetails(String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(Collections.singletonList("snippet, id, status")); // id 추가
        request.setKey(apiKey);
        request.setId(Collections.singletonList(videoId));
        VideoListResponse response = request.execute();
        if (response.getItems().isEmpty()) {
            throw new RuntimeException("Video Not Found When Initially Adding - REX : " + videoId);
        }
        return response.getItems().get(0);
    }

    public void fileTrackAndRecover(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, String playlistId) throws IOException {

        Map<String, Long> videos = getIllegalVideosFromPlaylist(playlistId); // videoId, Position 뽑기
        if(videos.isEmpty()) {
            System.err.println("There's no music to recover");
            return;
        }
        for (String videoIdToDelete : videos.keySet()) { // illegal video가 여러개일 수 있으니
            long videoPosition = videos.get(videoIdToDelete);
            System.err.println("Tracked Illegal Music (" + videoIdToDelete + ") at index " + videoPosition);

            // 1. DB에서 videoId로 검색해서 백업된 videoTitle을 가져옴
            String titleToSearch = youtubeRepository.getMusicTitleFromDBThruMusicId(videoIdToDelete);
            // 2. 그 videoTitle로 유튜브에 검색을 함 search 해서 return Video로 받음
            Music videoForRecovery = searchVideoToReplace(titleToSearch, playlistId);
            // 3. DB를 업데이트한다 CRUD 동작은 service가 아니라 repository가 맡아서 한다.
            youtubeRepository.dBTrackAndRecover(videoIdToDelete, videoForRecovery);
            // 4. 실제 유튜브 플레이리스트에도 add와 delete를 해준다
            addVideoToPlaylist(authorizedClient, playlistId, videoForRecovery.getVideoId(), videoPosition);
            deleteFromPlaylist(authorizedClient, playlistId, videoIdToDelete);
        }
        return;
    }

    public Map<String, Long> getIllegalVideosFromPlaylist(String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(50L);
        PlaylistItemListResponse response = request.execute();

        Map<String, Long> videos = new HashMap<>();

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId();
            String videoTitle = item.getSnippet().getTitle();                   // 비공개는 'Private video' 라고만 받을 수 있음
            long pos = item.getSnippet().getPosition();

            if(videoTitle.equals("Private video")) {
                System.err.println("Private video(" + videoId + ") is detected at position " + pos);
                // 비정상적인 제목으로 의심되면 getVideoDetails() 호출해서 검증해도 됨
                videos.put(videoId, pos);
            }
        }
        return videos;
    }

    public Music searchVideoToReplace(String query, String playlistId) throws IOException {
        YouTube.Search.List search = youtube.search().list(Collections.singletonList("id, snippet"));
        search.setKey(apiKey);
        search.setQ(query);
        SearchListResponse searchResponse = search.execute();                   // 검색 요청 실행 및 응답 받아오기
        List<SearchResult> searchResultList = searchResponse.getItems();        // 검색 결과에서 동영상 목록 가져오기
        SearchResult searchResult = searchResultList.get(0);                    //검색 결과 중 첫 번째 동영상 정보 가져오기
        String videoId = searchResult.getId().getVideoId();                     // 동영상의 ID와 제목 가져오기
        String videoTitle = searchResult.getSnippet().getTitle();
        String videoUploader = searchResult.getSnippet().getChannelTitle();

        System.err.println("Found a music to replace : " + videoTitle + ", " + videoUploader);

        return new Music(videoId, videoTitle, videoUploader, "someDescription",
                    "someTags", playlistId, 5, "someone's Id");
    }

    public String searchVideo(String query) throws IOException {
        YouTube.Search.List search = youtube.search().list(Collections.singletonList("id, snippet"));
        search.setKey(apiKey);
        search.setQ(query);
        SearchListResponse searchResponse = search.execute();                   // 검색 요청 실행 및 응답 받아오기
        List<SearchResult> searchResultList = searchResponse.getItems();        // 검색 결과에서 동영상 목록 가져오기
        if (searchResultList != null && searchResultList.size() > 0) {
            SearchResult searchResult = searchResultList.get(0);                //검색 결과 중 첫 번째 동영상 정보 가져오기
            String videoId = searchResult.getId().getVideoId();                 // 동영상의 ID와 제목 가져오기
            String videoTitle = searchResult.getSnippet().getTitle();
            return "Title: " + videoTitle + "\nURL: https://www.youtube.com/watch?v=" + videoId;
        }
        return "검색 결과가 없습니다";
    }

    public List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException {
        YouTube.Playlists.List request = youtube.playlists().list(Collections.singletonList("snippet, id, contentDetails"));
        request.setKey(apiKey);
        request.setChannelId(channelId);
        request.setMaxResults(50L);
        PlaylistListResponse response = request.execute();
        return response.getItems();
    }

    public List<String> getVideosFromPlaylist(String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(50L);
        PlaylistItemListResponse response = request.execute();

        List<String> videos = new ArrayList<>(); /// String 타입 말고 다른 방법 Map이나 기타 등등 생각해보기

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId(); // 이제 Private video 라고 뜨는 얘 Id를 디비에서 조회
            String videoTitle = item.getSnippet().getTitle(); // 비공개는 'Private video' 라고만 받을 수 있음
            System.out.println("successfully searched : " + videoTitle + "(" + videoId + ")");
            if(videoTitle.equals("Private video")) {
                System.err.println("Private video(" + videoId + ") is detected!!");
                // 비정상적인 제목으로 의심되면 getVideoDetails() 호출해서 검증해도 됨
            }
            videos.add(videoTitle + ", " + videoId);
        }
        return videos;
    }

    public String memberRegister(String userId, String userPwd, String userName) {
        return null;
    }
}
