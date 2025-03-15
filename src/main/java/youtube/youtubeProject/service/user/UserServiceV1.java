package youtube.youtubeProject.service.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import youtube.youtubeProject.domain.Users;
import youtube.youtubeProject.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceV1 implements UserService {

    private final UserRepository userRepository;

    @Override
    public Users getUserByEmail(String email) {
        Users user = userRepository.findByUserEmail(email);
        if (user != null) {
            return user;
        }
        throw new RuntimeException("User not found - getUserByEmail");
    }

    @Override
    public Users getUserByUserId(String userId) {
        Users user = userRepository.findByUserId(userId);
        if (user != null) {
            return user;
        }
        throw new RuntimeException("User not found - getUserByUserId");
    }

    @Override
    public void saveUser(Users user) {
        userRepository.saveUser(user);
    }

    @Override
    public void updateRefreshTokenByLogin(String email, String refreshToken) {
        userRepository.updateRefreshTokenByLogin(email, refreshToken);
    }



//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String clientId;
//    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
//    private String clientSecret;
//    public void TestAddVideoToPlaylist(String userEmail, String playlistId, String videoId) {
//
//        System.err.println("trying to add video via token");
//        try {
//            /* accessToken <- refreshToken update logic*/
//            System.err.println("once a day");
//            Users user = userRepository.findByUserEmail(userEmail);
//            String refreshToken = user.getRefreshToken();
//            String accessToken = refreshAccessToken(refreshToken); // refresh로 업뎃한 access를 다시 디비에 업데이트할 필요없다
//            System.err.println("AccessToken updated");
//
//
//            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
//            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
//                    .setApplicationName("youtube-add-sample")
//                    .build();
//
//            ResourceId resourceId = new ResourceId();
//            resourceId.setKind("youtube#video");
//            resourceId.setVideoId(videoId);
//            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
//            playlistItemSnippet.setPlaylistId(playlistId);
//            playlistItemSnippet.setResourceId(resourceId);
//            //playlistItemSnippet.setPosition(videoPosition); // added
//            PlaylistItem playlistItem = new PlaylistItem();
//            playlistItem.setSnippet(playlistItemSnippet);
//
//            YouTube.PlaylistItems.Insert request = youtube.playlistItems().insert(Collections.singletonList("snippet"), playlistItem);
//            PlaylistItem response = request.execute();
//            System.err.println("completely added video(" + videoId + ") to " + playlistId);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public String refreshAccessToken(String refreshToken) { // 사실 이게 핵심인듯?
//        try {
//            GoogleRefreshTokenRequest refreshTokenRequest = new GoogleRefreshTokenRequest(
//                    new NetHttpTransport(),
//                    new GsonFactory(),
//                    refreshToken,
//                    clientId,
//                    clientSecret
//            );
//            TokenResponse tokenResponse = refreshTokenRequest.execute();
//            return tokenResponse.getAccessToken();
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to refresh access token");
//        }
//    }
//    public UserServiceV1(UserRepository userRepository) {
//        this.userRepository = userRepository; // @RequiredArgsConstructor 로 바꿔보기
//    }
//      access_token이 만료되었는지 확인 <- 만료기한은 1시간인데 나는 하루에 한번만 체크하니 무조건 만료일것임
//    private boolean isAccessTokenExpired(String accessToken) {
//        return true;
//    }
//    public void TestAddVideoToPlaylist(String accessToken, String playlistId, String videoId) {
//        System.err.println("trying to add video via token");
//        try {
//            if (isAccessTokenExpired(accessToken)) { // 항상 true 임
//                System.err.println("AccessToken expired");
//                Users user = userRepository.findByAccessToken(accessToken);
//                String refreshToken = user.getRefreshToken();
//                accessToken = refreshAccessToken(refreshToken);
//                // 새로운 access_token을 데이터베이스에 저장
//                // 근데 이건 불필요 할 수도 있다.
//                // 나는 하루에 한번 체크하는데 어차피 유효기간 1시간이면 매일 토큰 얻어야함
//                userRepository.updateAccessTokenByRefreshToken(refreshToken, accessToken);
//                System.err.println("AccessToken updated");
//            }
//
//            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
//            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
//                    .setApplicationName("youtube-add-sample")
//                    .build();
//
//            ResourceId resourceId = new ResourceId();
//            resourceId.setKind("youtube#video");
//            resourceId.setVideoId(videoId);
//            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
//            playlistItemSnippet.setPlaylistId(playlistId);
//            playlistItemSnippet.setResourceId(resourceId);
//            //playlistItemSnippet.setPosition(videoPosition); // added
//            PlaylistItem playlistItem = new PlaylistItem();
//            playlistItem.setSnippet(playlistItemSnippet);
//
//            YouTube.PlaylistItems.Insert request = youtube.playlistItems().insert(Collections.singletonList("snippet"), playlistItem);
//            PlaylistItem response = request.execute();
//            System.err.println("completely added video(" + videoId + ") to " + playlistId);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    public String getAccessTokenByEmail(String email) { // throw 추가했음
//        Users user = userRepository.findByUserEmail(email);
//        if (user != null) {
//            return user.getAccessToken();
//        }
//        throw new RuntimeException("User not found by Email");
//    }
//    public Users getUserByAccessToken(String accessToken) {
//        Users user = userRepository.findByAccessToken(accessToken);
//        if (user != null) {
//            return user;
//        }
//        throw new RuntimeException("User not found - getUserByAccessToken");
//    }
//    public void saveTokens(OAuth2AuthorizedClient authorizedClient, String email) { // temp test
//        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
//        String refreshToken = authorizedClient.getRefreshToken() != null ? authorizedClient.getRefreshToken().getTokenValue() : null;
//
//        User user = new User();
//        user.setUserEmail(email);
//        user.setAccessToken(accessToken.getTokenValue());
//        user.setRefreshToken(refreshToken);
//        //user.setExpiryDate(accessToken.getExpiresAt());
//
//        userRepository.saveTokens(user);
//    }
//
//    public String refreshAccessToken(String refreshToken) { // temp test
//        RestTemplate restTemplate = new RestTemplate();
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("client_id", "YOUR_CLIENT_ID");
//        params.add("client_secret", "YOUR_CLIENT_SECRET");
//        params.add("refresh_token", refreshToken);
//        params.add("grant_type", "refresh_token");
//
//        ResponseEntity<Map> response = restTemplate.postForEntity(
//                "https://oauth2.googleapis.com/token",
//                params,
//                Map.class
//        );
//
//        return (String) response.getBody().get("access_token");
//    }
}