package youtube.youtubeProject.service.youtube;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import youtube.youtubeProject.domain.Music;
import youtube.youtubeProject.domain.Users;
import youtube.youtubeProject.repository.user.UserRepository;
import youtube.youtubeProject.repository.youtube.YoutubeRepository;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

//@RequiredArgsConstructor
@Service
public class YoutubeServiceV5 implements YoutubeService {

    @Value("${youtube.api.key}")
    private String apiKey;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private static YouTube youtube;
    private final YoutubeRepository youtubeRepository;
    private final UserRepository userRepository;

    public YoutubeServiceV5(YoutubeRepository youtubeRepository, UserRepository userRepository) {
        this.youtubeRepository = youtubeRepository;// YouTube 객체를 빌드하여 API에 접근할 수 있는 YouTube 클라이언트 생성
        this.userRepository = userRepository;
        youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {}).setApplicationName("youtube").build();
    }

    public List<String> getVideosFromPlaylist(String playlistId) throws IOException { // 단순 조회 - 테스트 용
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id, status"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(50L);
        PlaylistItemListResponse response = request.execute();

        List<String> videos = new ArrayList<>();

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId();
            String videoTitle = item.getSnippet().getTitle();
            String videoUploader = item.getSnippet().getVideoOwnerChannelTitle();
            String videoPrivacyStatus = item.getStatus().getPrivacyStatus(); // PlaylistItem 에는 getUploadStatus 메서드 없음

            if(videoPrivacyStatus.equals("public")) { // 그래서 그냥 public 으로 구분하면 됨
                System.out.println("searched : " + videoTitle + ", " + videoUploader + ", " + videoId + ", " + videoPrivacyStatus + ")");
            } else {
                System.err.println("searched : (" + videoTitle + ", " + videoUploader + ", " + videoId  + ", " + videoPrivacyStatus + ")");
            }
            videos.add(videoTitle + ", " + videoUploader + ", " + videoId + ", " + videoPrivacyStatus); // 보여주는건 그냥 다 보여줘도 됨, 그리고 어차피 이건 안 쓸 기능임
        }
        return videos;
    }

    public List<Video> initiallyAddVideoDetails(String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id, status"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(50L);
        PlaylistItemListResponse response = request.execute();
        List<Video> videos = new ArrayList<>();

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId();
            String videoName = item.getSnippet().getTitle(); // 비공개는 'Private Video' 라고만 받을 수 있음
            String videoPrivacyStatus = item.getStatus().getPrivacyStatus();

            try {
                Video video = getVideoDetails(videoId); // 이런식으로 비디오 상세 정보 뽑아서 디비에 저장가능
                String videoUploader = video.getSnippet().getChannelTitle();
                String description = video.getSnippet().getDescription();
                List<String> tags = video.getSnippet().getTags();

                videos.add(video);
                youtubeRepository.initiallyAddVideoDetails(playlistId, videoName, videoId, videoUploader); // 성공
                System.out.println("successfully added : " + videoName + "(" + videoId + ") by " + videoUploader);

            } catch(RuntimeException e) {
                if(videoPrivacyStatus.equals("unlisted")) {
                    System.err.println("Inaccessible Video Add : " + videoName + " (" + videoId + ", " + "Unavailable)");
                } else {
                    System.err.println("Inaccessible Video Add : " + videoName + " (" + videoId + ", " + "Deleted/Private)");
                }
            }
        }
        return videos;
    }

    public Video getVideoDetails(String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(Collections.singletonList("snippet, id, status")); // id 추가
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
        /*
        Video video;
        if (response.getItems().isEmpty()) {
            throw new RuntimeException("Deleted/Private Video When Initially Adding - REX : " + videoId);
        } else {
            video = response.getItems().get(0);
        }
        if(video.getStatus().getPrivacyStatus().equals("unlisted")) {
            throw new RuntimeException("Privacy Status Unlisted Video When Initially Adding - REX : "
                    + video.getSnippet().getTitle() + ", " +videoId);
        }
        */
    }


    public void fileTrackAndRecover(String userEmail, String playlistId) throws IOException {

        Map<String, Long> videos = getIllegalVideosFromPlaylist(playlistId); // videoId, Position 뽑기
        if(videos.isEmpty()) {
            System.err.println("There's no music to recover");
            return;
        }

        /* accessToken <- refreshToken update logic */
        System.err.println("once a day");
        Users user = userRepository.findByUserEmail(userEmail);
        String refreshToken = user.getRefreshToken();
        String accessToken = refreshAccessToken(refreshToken);
        // refresh 로 업뎃한 accessToken 을 다시 디비에 업데이트할 필요는 없다
        System.err.println("AccessToken updated : " + accessToken);


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
            TestAddVideoToPlaylist(accessToken, playlistId, videoForRecovery.getVideoId(), videoPosition);
            TestDeleteFromPlaylist(accessToken, playlistId, videoIdToDelete);
            //addVideoToPlaylist(authorizedClient, playlistId, videoForRecovery.getVideoId(), videoPosition);
            //deleteFromPlaylist(authorizedClient, playlistId, videoIdToDelete);
        }
    }

    public Map<String, Long> getIllegalVideosFromPlaylist(String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id, status"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(50L);
        PlaylistItemListResponse response = request.execute();

        Map<String, Long> videos = new HashMap<>();

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId();
            String videoTitle = item.getSnippet().getTitle();                   // 비공개는 'Private video' 라고만 받을 수 있음
            String videoPrivacyStatus = item.getStatus().getPrivacyStatus();
            long pos = item.getSnippet().getPosition();

//            if(videoTitle.equals("Private video")) {
//                System.err.println("Private video(" + videoId + ") is detected at position " + pos);
//                // 비정상적인 제목으로 의심되면 getVideoDetails() 호출해서 검증해도 됨
//                videos.put(videoId, pos);
//            }
            if(!videoPrivacyStatus.equals("public")) {
                System.err.println("Unavailable video(" + videoId + ") is detected at position " + pos);
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
        // String videoDescription = searchResult.getSnippet().getDescription();
        System.err.println("Found a music to replace : " + videoTitle + ", " + videoUploader);

        return new Music(videoId, videoTitle, videoUploader, "someDescription",
                    "someTags", playlistId, 5, "someone's Id");
    }

    public void TestAddVideoToPlaylist(String accessToken, String playlistId, String videoId, long videoPosition) {
        try {
//            /* accessToken <- refreshToken update logic 근데 이걸 add에 두지말고 더 상위에 두자*/
//            System.err.println("once a day");
//            Users user = userRepository.findByUserEmail(userEmail);
//            String refreshToken = user.getRefreshToken();
//            String accessToken = refreshAccessToken(refreshToken); // refresh로 업뎃한 access를 다시 디비에 업데이트할 필요없다
//            System.err.println("AccessToken updated");
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("youtube-add-sample")
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
            System.err.println("completely added video(" + videoId + ") to " + playlistId);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public void TestDeleteFromPlaylist(String accessToken, String playlistId, String videoId) {
        try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
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
                    return;
                }
            }

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public String refreshAccessToken(String refreshToken) { // 사실 이게 핵심인듯?
        try {
            GoogleRefreshTokenRequest refreshTokenRequest = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    refreshToken,
                    clientId,
                    clientSecret
            );
            TokenResponse tokenResponse = refreshTokenRequest.execute();
            return tokenResponse.getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to refresh access token");
        }
    }

//    public String searchVideo(String query) throws IOException {
//        YouTube.Search.List search = youtube.search().list(Collections.singletonList("id, snippet"));
//        search.setKey(apiKey);
//        search.setQ(query);
//        SearchListResponse searchResponse = search.execute();                   // 검색 요청 실행 및 응답 받아오기
//        List<SearchResult> searchResultList = searchResponse.getItems();        // 검색 결과에서 동영상 목록 가져오기
//        if (searchResultList != null && searchResultList.size() > 0) {
//            SearchResult searchResult = searchResultList.get(0);                //검색 결과 중 첫 번째 동영상 정보 가져오기
//            String videoId = searchResult.getId().getVideoId();                 // 동영상의 ID와 제목 가져오기
//            String videoTitle = searchResult.getSnippet().getTitle();
//            return "Title: " + videoTitle + "\nURL: https://www.youtube.com/watch?v=" + videoId;
//        }
//        return "검색 결과가 없습니다";
//    }

    public List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException {
        YouTube.Playlists.List request = youtube.playlists().list(Collections.singletonList("snippet, id, contentDetails"));
        request.setKey(apiKey);
        request.setChannelId(channelId);
        request.setMaxResults(50L);
        PlaylistListResponse response = request.execute();
        return response.getItems();
    }


//original fileTrackAndRecover
// soon delete
//    public void fileTrackAndRecover(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, String playlistId) throws IOException {
//
//        Map<String, Long> videos = getIllegalVideosFromPlaylist(playlistId); // videoId, Position 뽑기
//        if(videos.isEmpty()) {
//            System.err.println("There's no music to recover");
//            return;
//        }
//        try {
//            /* accessToken <- refreshToken update logic 근데 이걸 add에 두지말고 더 상위에 두자*/
//            System.err.println("once a day");
//            Users user = userRepository.findByUserEmail(userEmail);
//            String refreshToken = user.getRefreshToken();
//            String accessToken = refreshAccessToken(refreshToken); // refresh로 업뎃한 access를 다시 디비에 업데이트할 필요없다
//            System.err.println("AccessToken updated");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }
//
//
//        for (String videoIdToDelete : videos.keySet()) { // illegal video가 여러개일 수 있으니
//            long videoPosition = videos.get(videoIdToDelete);
//            System.err.println("Tracked Illegal Music (" + videoIdToDelete + ") at index " + videoPosition);
//
//            // 1. DB에서 videoId로 검색해서 백업된 videoTitle을 가져옴
//            String titleToSearch = youtubeRepository.getMusicTitleFromDBThruMusicId(videoIdToDelete);
//            // 2. 그 videoTitle로 유튜브에 검색을 함 search 해서 return Video로 받음
//            Music videoForRecovery = searchVideoToReplace(titleToSearch, playlistId);
//            // 3. DB를 업데이트한다 CRUD 동작은 service가 아니라 repository가 맡아서 한다.
//            youtubeRepository.dBTrackAndRecover(videoIdToDelete, videoForRecovery);
//            // 4. 실제 유튜브 플레이리스트에도 add와 delete를 해준다
//            addVideoToPlaylist(authorizedClient, playlistId, videoForRecovery.getVideoId(), videoPosition);
//            // TestAddVideoToPlaylist(String userEmail, String playlistId, String videoId);
//            deleteFromPlaylist(authorizedClient, playlistId, videoIdToDelete);
//        }
//    }

    // soon delete
//    public String deleteFromPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId) {
//        try {
//            GoogleCredential credential = new GoogleCredential()
//                    .setAccessToken(authorizedClient.getAccessToken().getTokenValue());
//            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
//                    .setApplicationName("youtube-cmdline-deletefrom-playlist-sample")
//                    .build();
//
//            // 재생목록에서 영상을 찾기 위해 playlistItems.list 호출
//            YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems().list(Collections.singletonList("id,snippet"));
//            playlistItemsRequest.setPlaylistId(playlistId);
//            playlistItemsRequest.setMaxResults(50L);
//
//            PlaylistItemListResponse playlistItemsResponse = playlistItemsRequest.execute();
//            List<PlaylistItem> playlistItems = playlistItemsResponse.getItems();
//
//            // 영상 ID와 일치하는 재생목록 항목을 찾음
//            for (PlaylistItem playlistItem : playlistItems) {
//                if (playlistItem.getSnippet().getResourceId().getVideoId().equals(videoId)) {
//                    YouTube.PlaylistItems.Delete deleteRequest = youtube.playlistItems().delete(playlistItem.getId());
//                    deleteRequest.execute();
//                    return "Video deleted from playlist: " + videoId;
//                }
//            }
//
//            return "Video not found in playlist: " + videoId;
//        } catch (IOException | GeneralSecurityException e) {
//            e.printStackTrace();
//            return "Failed to delete video from playlist";
//        }
//    }

    // soon delete
//    public String addVideoToPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId, long videoPosition) {
//        try {
//            GoogleCredential credential = new GoogleCredential()
//                    .setAccessToken(authorizedClient.getAccessToken().getTokenValue());
//            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
//                    .setApplicationName("youtube-cmdline-addto-playlist-sample")
//                    .build();
//
//            ResourceId resourceId = new ResourceId();
//            resourceId.setKind("youtube#video");
//            resourceId.setVideoId(videoId);
//
//            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
//            playlistItemSnippet.setPlaylistId(playlistId);
//            playlistItemSnippet.setResourceId(resourceId);
//            playlistItemSnippet.setPosition(videoPosition); // added
//
//            PlaylistItem playlistItem = new PlaylistItem();
//            playlistItem.setSnippet(playlistItemSnippet);
//
//            YouTube.PlaylistItems.Insert request = youtube.playlistItems().insert(Collections.singletonList("snippet"), playlistItem);
//            PlaylistItem response = request.execute();
//
//            return "Video added to playlist: " + response.getId();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Failed to add video to playlist";
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }
//    }





//    public void tokenTest(OAuth2AuthorizedClient authorizedClient) {
//        System.out.println(authorizedClient.getAccessToken() + "\n");
//        System.out.println("value " + authorizedClient.getAccessToken().getTokenValue());
//        System.out.println("Issued : " + authorizedClient.getAccessToken().getIssuedAt());
//        System.out.println("expire : " + authorizedClient.getAccessToken().getExpiresAt());
//        System.out.println("type : " + authorizedClient.getAccessToken().getTokenType());
//
//        System.out.println("\n" + authorizedClient.getRefreshToken() + "\n");
//
//        StringTokenizer st = new StringTokenizer(authorizedClient.getClientRegistration().toString(), ",");
//        while(st.hasMoreTokens()) {
//            System.out.println(st.nextToken());
//        }
//        System.out.println("\n" + authorizedClient.getPrincipalName());
//    }
//
//    public String memberRegister(String userId, String userPwd, String userName) {
//        return null;
//    }
}
