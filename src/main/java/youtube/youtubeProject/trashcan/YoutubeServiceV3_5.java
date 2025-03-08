package youtube.youtubeProject.trashcan;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//@Service
public class YoutubeServiceV3_5 {

    private static YouTube youtube;

//    @Value("${youtube.api.key}")
    private String apiKey;

    public YoutubeServiceV3_5() {
        youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {}).setApplicationName("youtube").build();
    }

    public String addVideoToPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId) {
        try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(authorizedClient.getAccessToken().getTokenValue());

            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("youtube-cmdline-addto-playlist-sample")
                    .build();

            ResourceId resourceId = new ResourceId();
            resourceId.setKind("youtube#video");
            resourceId.setVideoId(videoId);

            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
            playlistItemSnippet.setPlaylistId(playlistId);
            playlistItemSnippet.setResourceId(resourceId);

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

    public List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException {
        YouTube.Playlists.List request = youtube.playlists().list(Collections.singletonList("contentDetails"));
        request.setKey(apiKey);
        request.setChannelId(channelId);
        request.setMaxResults(50L);
        PlaylistListResponse response = request.execute();
        return response.getItems();
    }

    public List<Video> getVideosFromPlaylist(String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(50L);

        PlaylistItemListResponse response = request.execute();
        List<Video> videos = new ArrayList<>();

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId();
            videos.add(getVideoDetails(videoId));
        }
        return videos;
    }

    private Video getVideoDetails(String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(Collections.singletonList("snippet"));
        request.setKey(apiKey);
        request.setId(Collections.singletonList(videoId));
        VideoListResponse response = request.execute();
        return response.getItems().get(0);
    }

    public String searchVideo(String query) throws IOException {
        //JsonFactory jsonFactory = new GsonFactory();
        // YouTube 객체를 빌드하여 API에 접근할 수 있는 YouTube 클라이언트 생성
        youtube = new YouTube.Builder(new com.google.api.client.http.javanet.NetHttpTransport(), new GsonFactory(), request -> {}).build();
        //jsonfactory
        // YouTube Search API를 사용하여 동영상 검색을 위한 요청 객체 생성
        YouTube.Search.List search = youtube.search().list(Collections.singletonList("id, snippet"));
        search.setKey(apiKey);// API 키 설정
        search.setQ(query);  // 검색어 설정
        SearchListResponse searchResponse = search.execute();// 검색 요청 실행 및 응답 받아오기
        List<SearchResult> searchResultList = searchResponse.getItems();// 검색 결과에서 동영상 목록 가져오기
        if (searchResultList != null && searchResultList.size() > 0) {
            SearchResult searchResult = searchResultList.get(0);//검색 결과 중 첫 번째 동영상 정보 가져오기
            String videoId = searchResult.getId().getVideoId(); // 동영상의 ID와 제목 가져오기
            String videoTitle = searchResult.getSnippet().getTitle();
            return "Title: " + videoTitle + "\nURL: https://www.youtube.com/watch?v=" + videoId;
        }
        return "검색 결과가 없습니다";
    }

}