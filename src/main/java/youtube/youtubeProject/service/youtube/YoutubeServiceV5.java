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
import java.util.stream.Collectors;

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

    // 수정 필요
    public List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException {
        YouTube.Playlists.List request = youtube.playlists().list(Collections.singletonList("snippet, id, contentDetails"));
        request.setKey(apiKey);
        request.setChannelId(channelId);
        request.setMaxResults(50L);
        PlaylistListResponse response = request.execute();
        return response.getItems();
    }

    public PlaylistItemListResponse getPlaylistItemListResponse(String playlistId, Long maxResults) throws IOException { // 내부에서 호출해야함
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id, status"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(maxResults);
        return request.execute();
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

    public void DBAddAction(String videoId, String playlistId) throws IOException {
        try {
            Video video = getVideoDetailResponseWithFilter(videoId);
            String videoTitle = video.getSnippet().getTitle();
            String videoUploader = video.getSnippet().getChannelTitle();
            //String description = video.getSnippet().getDescription();
            //List<String> tags = video.getSnippet().getTags();
            //String videoPrivacyStatus = item.getStatus().getPrivacyStatus();

            Music newMusic = new Music(videoId, videoTitle, videoUploader, "someDescription",
                    "someTags", playlistId, 5, "someone'sId");

            youtubeRepository.addUpdatePlaylist(playlistId, newMusic);

        } catch (RuntimeException e) {
            System.err.println("Inaccessible Video Adding -> Aborted From DBAddAction");
        }
    }

    public void initiallyAddVideoDetails(String playlistId) throws IOException {
        PlaylistItemListResponse response = getPlaylistItemListResponse(playlistId, 50L);

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId();
            DBAddAction(videoId, playlistId);
        }
    }


    @Override
    public void updatePlaylist(String userEmail, String playlistId) throws IOException { // 나중에 user 기반으로 디비 조회하도록 RDB 설계해야함
        System.err.println("update playlist ...");

        // 1. 고객 플레이리스트 담긴 디비 불러오기
        List<Music> musicDBList = youtubeRepository.findAllMusicByPlaylistId(playlistId);
        Set<String> musicDBSet = musicDBList.stream().map(Music::getVideoId).collect(Collectors.toSet());

        // 2. API 검색으로 고객 플레이리스트 목록 불러오기
        PlaylistItemListResponse response = getPlaylistItemListResponse(playlistId, 50L);
        List<String> apiMusicList = new ArrayList<>();
        for (PlaylistItem item : response.getItems()) {
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
            } else {System.out.println("nothing to add");}

        if(!removedMusicSet.isEmpty())
            for(String videoId : removedMusicSet) {
                youtubeRepository.deleteUpdatePlaylist(playlistId, videoId);
            } else {System.out.println("nothing to remove");}

        // 5. 고려 사항
        // 5-1. 플레이리스트 자체가 없을 수가 있다. (고객이 제거해서 -> 2번에서 catch 로 잡아야 할 듯)
        // 5-2. 플레이리스트 이름이 변경 됐을 수 있다.
        // 5-3. 고려하지 않아도 될 사항 : 플리 자체가 추가 됐다면 그건 고객이 다시 등록을 해야한다.
        System.err.println("update playlist done");
    }

    public void fileTrackAndRecover(String userEmail, String playlistId) throws IOException {

        // 1. 사용자의 업데이트된 목록 최신화 로직
        updatePlaylist(userEmail, playlistId);

        // 2. 비정상적인 파일 추적
        Map<String, Long> videos = getIllegalVideosFromPlaylist(playlistId); // videoId, Position 뽑기
        if(videos.isEmpty()) {
            System.err.println("There's no music to recover");
            return;
        }

        // 3. 토큰 획득 1일 1회 (accessToken <- refreshToken)
        System.err.println("once a day : accessToken <- refreshToken");
        Users user = userRepository.findByUserEmail(userEmail);
        String refreshToken = user.getRefreshToken();
        String accessToken = refreshAccessToken(refreshToken);

        // 4. 복구 시스템 가동
        for (String videoIdToDelete : videos.keySet()) {
            // illegal video 가 여러개일 수 있으니
            long videoPosition = videos.get(videoIdToDelete);
            System.err.println("Tracked Illegal Music (" + videoIdToDelete + ") at index " + videoPosition);

            // 4-1. DB 에서 videoId로 검색해서 백업된 Music 객체를 가져옴
            Optional<Music> optionalBackUpMusic = youtubeRepository.getMusicFromDBThruMusicId(videoIdToDelete);
            Music backupMusic = optionalBackUpMusic.orElse(null);

            // 4-2. 그 Music 으로 유튜브에 검색을 함 search 해서 return 받음
            if(backupMusic == null) {
                // 4-2-1. backupMusic 이 null 이면 백업 안된 영상. 즉 사용자가 최근에 추가했지만 빠르게 삭제된 영상
                TestDeleteFromPlaylist(accessToken, playlistId, videoIdToDelete);
                // updatePlaylist 행위때 그런 음악은 디비에 저장 안했으니 디비 수정할 이유는 없다
                continue;
            }
            // 4-2-2. backupMusic 이 null 이 아니면 백업된 영상임 (search 알고리즘 강화 필요)
            Music videoForRecovery = searchVideoToReplace(backupMusic, playlistId);

            // 4-3. DB를 업데이트한다 CRUD 동작은 service 가 아니라 repository 가 맡아서 한다.
            youtubeRepository.dBTrackAndRecover(videoIdToDelete, videoForRecovery);

            // 4-4. 실제 유튜브 플레이리스트에도 add 와 delete
            TestAddVideoToPlaylist(accessToken, playlistId, videoForRecovery.getVideoId(), videoPosition);
            TestDeleteFromPlaylist(accessToken, playlistId, videoIdToDelete);
        }
    }

    public Map<String, Long> getIllegalVideosFromPlaylist(String playlistId) throws IOException {

        PlaylistItemListResponse response = getPlaylistItemListResponse(playlistId, 50L);
        Map<String, Long> videos = new HashMap<>();

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId();
            String videoTitle = item.getSnippet().getTitle();
            String videoPrivacyStatus = item.getStatus().getPrivacyStatus();
            long pos = item.getSnippet().getPosition();

            if(!videoPrivacyStatus.equals("public")) {
                System.err.println("Unplayable video(" + videoTitle + " : " + videoId + ") is detected at position " + pos);
                videos.put(videoId, pos);
            }
        }
        return videos;
    }

    public Music searchVideoToReplace(Music musicToSearch, String playlistId) throws IOException {

        String query = musicToSearch.getVideoTitle().concat("-").concat(musicToSearch.getVideoUploader());
        System.err.println("searched with : " + query);
        // query 강화 필요
        // getUsefulInfoFromDescription();

        YouTube.Search.List search = youtube.search().list(Collections.singletonList("id, snippet"));
        search.setKey(apiKey);
        search.setQ(query);

        SearchListResponse searchResponse = search.execute();                   // 검색 요청 실행 및 응답 받아오기
        List<SearchResult> searchResultList = searchResponse.getItems();        // 검색 결과에서 동영상 목록 가져오기
        SearchResult searchResult = searchResultList.get(0);                    // 검색 결과 중 첫 번째 동영상 정보 가져오기

        String videoId = searchResult.getId().getVideoId();
        String videoTitle = searchResult.getSnippet().getTitle();
        String videoUploader = searchResult.getSnippet().getChannelTitle();
        System.err.println("Found a music to replace : " + videoTitle + ", " + videoUploader);
        // String videoDescription = searchResult.getSnippet().getDescription();
        // String videoTags = not available here

        return new Music(videoId, videoTitle, videoUploader, "someDescription",
                    "someTags", playlistId, 5, "someone's Id");
    }

    // insert 할 떄 요구되는 속성 다시 점검
    public void TestAddVideoToPlaylist(String accessToken, String playlistId, String videoId, long videoPosition) {
        try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("youtube-add-playlist-item")
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

    // 불필요한 동작 수정 필요
    public void TestDeleteFromPlaylist(String accessToken, String playlistId, String videoId) {
        try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("youtube-delete-playlist-item")
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
                    // 조건에 맞으면 여러개 한번에 삭제할 수 있음
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


    /*
    //                if(videoPrivacyStatus.equals("unlisted")) {
    //                    System.err.println("Inaccessible Video Add : " + videoTitle + " (" + videoId + ", " + "Unavailable)");
    //                } else {
    //                    System.err.println("Inaccessible Video Add : " + videoTitle + " (" + videoId + ", " + "Deleted/Private)");
    //                }
    //

        try {
    //                Video video = getVideoDetailResponseWithFilter(videoId);
    //                String videoTitle = video.getSnippet().getTitle();
    //                String videoUploader = video.getSnippet().getChannelTitle();
    //                //String description = video.getSnippet().getDescription();
    //
    //                Music newMusic = new Music(videoId, videoTitle, videoUploader, "someDescription",
    //                        "someTags", playlistId, 5, "someone'sId");
    //                youtubeRepository.addUpdatePlaylist(playlistId, newMusic);
    //
    //            } catch (RuntimeException e) {
    //                System.out.println("cant proceed addUpdate");
    //                // 해당 영상 (최근에 추가했으나 비공개된)을 실제 플레이리스트에서 삭제하는 로직
    //            }

     */


    // delete soon
//    public List<String> getVideosFromPlaylist(String playlistId) throws IOException { // 내부에서 호출해야함
//        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet, id, status"));
//        request.setKey(apiKey);
//        request.setPlaylistId(playlistId);
//        request.setMaxResults(50L);
//        PlaylistItemListResponse response = request.execute();
//
//        List<String> videos = new ArrayList<>();
//
//        for (PlaylistItem item : response.getItems()) {
//            String videoId = item.getSnippet().getResourceId().getVideoId();
//            String videoTitle = item.getSnippet().getTitle();
//            String videoUploader = item.getSnippet().getVideoOwnerChannelTitle();
//            String videoPrivacyStatus = item.getStatus().getPrivacyStatus(); // PlaylistItem 에는 getUploadStatus 메서드 없음
//
//            if(videoPrivacyStatus.equals("public")) { // 그래서 그냥 public 으로 구분하면 됨
//                System.out.println("searched : " + videoTitle + ", " + videoUploader + ", " + videoId + ", " + videoPrivacyStatus + ")");
//            } else {
//                System.err.println("searched : (" + videoTitle + ", " + videoUploader + ", " + videoId  + ", " + videoPrivacyStatus + ")");
//            }
//            videos.add(videoTitle + ", " + videoUploader + ", " + videoId + ", " + videoPrivacyStatus); // 보여주는건 그냥 다 보여줘도 됨, 그리고 어차피 이건 안 쓸 기능임
//        }
//        return videos;
//    }

/*
//            Music musicToSearch = null;
//            if (optionalMusicToSearch.isPresent()) musicToSearch = optionalMusicToSearch.get();

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
//
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
