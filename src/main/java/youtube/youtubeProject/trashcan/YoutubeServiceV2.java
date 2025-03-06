package youtube.youtubeProject.trashcan;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class YoutubeServiceV2 {

    private static YouTube youtube;

    @Value("${youtube.api.key}")
    private String apiKey; // API Key는 application.properties에 설정할 것입니다.

    public YoutubeServiceV2() {
        youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {}).setApplicationName("youtube").build();
    }

//    public String addVideoToPlaylist(String playlistId, String videoId) {
//        try {
//            // PlaylistItemSnippet 생성
//            PlaylistItemSnippet snippet = new PlaylistItemSnippet();
//            snippet.setPlaylistId(playlistId);
//            // ResourceId 생성 (영상 정보 추가)
//            ResourceId resourceId = new ResourceId();
//            resourceId.setKind("youtube#video"); // YouTube 영상 타입
//            resourceId.setVideoId(videoId); // 추가할 영상 ID
//            snippet.setResourceId(resourceId);
//            // PlaylistItem 객체 생성
//            PlaylistItem playlistItem = new PlaylistItem();
//            playlistItem.setSnippet(snippet);
//            // API 요청 수행
//            YouTube.PlaylistItems.Insert request = youtube.playlistItems()
//                    .insert(Collections.singletonList("snippet"), playlistItem);
//            PlaylistItem response = request.execute();
//            return "영상 추가 성공: " + response.getId();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "영상 추가 실패: " + e.getMessage();
//        }
//
//    }

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
        JsonFactory jsonFactory = new GsonFactory();
        // YouTube 객체를 빌드하여 API에 접근할 수 있는 YouTube 클라이언트 생성
        youtube = new YouTube.Builder(new com.google.api.client.http.javanet.NetHttpTransport(), jsonFactory, request -> {}).build();
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
