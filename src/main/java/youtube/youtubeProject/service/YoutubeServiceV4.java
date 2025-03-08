package youtube.youtubeProject.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class YoutubeServiceV4 {

    private static YouTube youtube;

    @Value("${youtube.api.key}")
    private String apiKey;

    public YoutubeServiceV4() {
        // YouTube 객체를 빌드하여 API에 접근할 수 있는 YouTube 클라이언트 생성
        youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {}).setApplicationName("youtube").build();
    }

    public String addVideoToPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId) {
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
            // GoogleCredential을 사용하여 YouTube 객체 생성
            GoogleCredential credential = new GoogleCredential()
                    .setAccessToken(authorizedClient.getAccessToken().getTokenValue());
            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("youtube-cmdline-deletefrom-playlist-sample")
                    .build();

            // 재생목록에서 영상을 찾기 위해 playlistItems.list 호출
            YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems().list(Collections.singletonList("id,snippet"));
            playlistItemsRequest.setPlaylistId(playlistId);
            playlistItemsRequest.setMaxResults(50L); // 최대 50개의 항목을 가져옴

            PlaylistItemListResponse playlistItemsResponse = playlistItemsRequest.execute();
            List<PlaylistItem> playlistItems = playlistItemsResponse.getItems();

            // 영상 ID와 일치하는 재생목록 항목을 찾음
            for (PlaylistItem playlistItem : playlistItems) {
                if (playlistItem.getSnippet().getResourceId().getVideoId().equals(videoId)) {
                    // 재생목록 항목 ID를 사용하여 삭제 요청
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

    public List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException {
        YouTube.Playlists.List request = youtube.playlists().list(Collections.singletonList("snippet, id, contentDetails"));
        request.setKey(apiKey);
        request.setChannelId(channelId);
        request.setMaxResults(50L);
        PlaylistListResponse response = request.execute();
        return response.getItems();
    }

    public List<Video> getVideosFromPlaylist(String playlistId) throws IOException {
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
                Video video = getVideoDetails(videoId);
//                if (isVideoAccessible(video)) {
//                    videos.add(video);
//                } else {
//                    System.err.println("Skipping inaccessible video : " + videoId);
//                }
                videos.add(video);
            } catch (IOException e) {
                System.err.println("Failed to fetch details for video : " + videoId); e.printStackTrace();
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
                System.err.println("Inaccessible Video : " + videoId + ", Name : " + videoName);
                // videoId를 알았으니 이제 DB에서 해당 id로 조회해서 영상 title 등을 가져와야함
            }
        }
        return videos;
    }

    private Video getVideoDetails(String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(Collections.singletonList("snippet, id, status")); // id 추가
        request.setKey(apiKey);
        request.setId(Collections.singletonList(videoId));
        VideoListResponse response = request.execute();

        if (response.getItems().isEmpty()) {
            throw new RuntimeException("Throw Video Not Found REX : " + videoId);
        }

        return response.getItems().get(0);
    }

    private boolean isVideoAccessible(Video video) {
        VideoStatus status = video.getStatus();
        if (status == null) {
            return false; // 상태 정보가 없으면 접근 불가로 처리
        }
        String privacyStatus = status.getPrivacyStatus();// 비공개 또는 삭제된 영상인지 확인
        String uploadStatus = status.getUploadStatus();
        // 공개 영상이고, 삭제되지 않은 경우만 접근 가능
        return "public".equals(privacyStatus) && !"deleted".equals(uploadStatus);
    }

    public String searchVideo(String query) throws IOException {
//        youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {}).setApplicationName("youtube").build();
//        youtube = new YouTube.Builder(new com.google.api.client.http.javanet.NetHttpTransport(), new GsonFactory(), request -> {}).build();
        // YouTube Search API를 사용하여 동영상 검색을 위한 요청 객체 생성
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

}

/* 원본 코드
 public List<Video> getVideosFromPlaylist(String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(50L);
        PlaylistItemListResponse response = request.execute();
        List<Video> videos = new ArrayList<>();

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId();
            // try/catch 필요할듯
            videos.add(getVideoDetails(videoId));
            //videos.add(videoId);
        }
        return videos;
    }

    private Video getVideoDetails(String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(Collections.singletonList("snippet, status"));
        request.setKey(apiKey);
        request.setId(Collections.singletonList(videoId));
        VideoListResponse response = request.execute();
        return response.getItems().get(0);
    }
 */

/*
    public String my_deleteFromPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId) throws IOException, GeneralSecurityException {
        try {
            GoogleCredential credential = new GoogleCredential()
                    .setAccessToken(authorizedClient.getAccessToken().getTokenValue());

            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("youtube-cmdline-delete-from-playlist-sample")
                    .build();

            ResourceId resourceId = new ResourceId();
            resourceId.setKind("youtube#video");
            resourceId.setVideoId(videoId);

            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
            playlistItemSnippet.setPlaylistId(playlistId);
            playlistItemSnippet.setResourceId(resourceId);

            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setSnippet(playlistItemSnippet);

            YouTube.PlaylistItems.Delete request = youtube.playlistItems().delete(videoId);
            request.execute();

            return "Video deleted from playlist : " + videoId;
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to delete video to playlist";
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
*/