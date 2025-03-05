package youtube.youtubeProject.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
public class YoutubeServiceV3 {
    private final HttpTransport httpTransport = new NetHttpTransport();
    private final JsonFactory jsonFactory = new GsonFactory();
    private final OAuth2AuthorizedClientService authorizedClientService;

    public YoutubeServiceV3(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public void addVideoToPlaylist(OAuth2AuthenticationToken authentication, String playlistId, String videoId) throws IOException {
        String userName = authentication.getName();
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", userName);
        String accessToken = client.getAccessToken().getTokenValue(); // OAuth2 토큰 가져오기

        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        // YouTube API 클라이언트 생성
        YouTube youtube = new YouTube.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("YouTube Data API Example")
                .build();

        // 비디오를 재생목록에 추가하는 요청 생성
        YouTube.PlaylistItems.Insert request = youtube.playlistItems()
                .insert(Collections.singletonList("snippet"), new PlaylistItem()
                        .setSnippet(new PlaylistItemSnippet()
                                .setPlaylistId(playlistId)  // 추가할 재생목록 ID
                                .setResourceId(new ResourceId()
                                        .setKind("youtube#video")
                                        .setVideoId(videoId))));  // 추가할 영상 ID
        try {
            // 요청 실행
            PlaylistItem response = request.execute();
            System.out.println("Video added to playlist: " + response.getSnippet().getTitle());
        } catch (GoogleJsonResponseException e) {
            System.err.println("Error during API call: " + e.getDetails());
        }
    }
//    private final HttpTransport httpTransport = new NetHttpTransport();
//    private final JsonFactory jsonFactory = new GsonFactory();
//
//    public void addVideoToPlaylist(OAuth2AuthenticationToken authentication, String playlistId, String videoId) throws IOException {
//        // Google OAuth2 인증을 통해 토큰 가져오기
//        OAuth2User oauth2User = authentication.getPrincipal();
//        String accessToken = oauth2User.getAttributes().get("access_token").toString();
//
//        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
//
//        // YouTube API 클라이언트 생성
//        YouTube youtube = new YouTube.Builder(httpTransport, jsonFactory, credential)
//                .setApplicationName("YouTube Data API Example")
//                .build();
//
//        // 비디오를 재생목록에 추가하는 요청 생성
//        YouTube.PlaylistItems.Insert request = youtube.playlistItems()
//                .insert(Collections.singletonList("snippet"), new PlaylistItem()
//                        .setSnippet(new PlaylistItemSnippet()
//                                .setPlaylistId(playlistId)  // 추가할 재생목록 ID
//                                .setResourceId(new ResourceId()
//                                        .setKind("youtube#video")
//                                        .setVideoId(videoId))));  // 추가할 영상 ID
//        try {
//            // 요청 실행
//            PlaylistItem response = request.execute();
//            System.out.println("Video added to playlist: " + response.getSnippet().getTitle());
//        } catch (GoogleJsonResponseException e) {
//            System.err.println("Error during API call: " + e.getDetails());
//        }
//    }
}

/*
@Service
public class YoutubeServiceV3 {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final OAuth2AuthorizedClientService authorizedClientService;

    public YoutubeServiceV3(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public String addVideoToPlaylist(String playlistId, String videoId, String userName) {
        try {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", userName);
            String accessToken = client.getAccessToken().getTokenValue();

            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
                request.getHeaders().setAuthorization("Bearer " + accessToken);
            }).setApplicationName("YouTube API Example").build();

            // PlaylistItemSnippet 생성
            PlaylistItemSnippet snippet = new PlaylistItemSnippet();
            snippet.setPlaylistId(playlistId);

            // ResourceId 생성 (영상 정보 추가)
            ResourceId resourceId = new ResourceId();
            resourceId.setKind("youtube#video");
            resourceId.setVideoId(videoId);
            snippet.setResourceId(resourceId);

            // PlaylistItem 객체 생성
            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setSnippet(snippet);

            // API 요청 수행
            YouTube.PlaylistItems.Insert request = youtube.playlistItems()
                    .insert(Collections.singletonList("snippet"), playlistItem);

            PlaylistItem response = request.execute();
            return "영상 추가 성공: " + response.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "영상 추가 실패: " + e.getMessage();
        }
    }
}
*/
