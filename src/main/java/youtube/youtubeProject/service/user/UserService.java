package youtube.youtubeProject.service.user;

import youtube.youtubeProject.domain.Users;

public interface UserService {
    String getAccessTokenByEmail(String email);
    Users getUserByAccessToken(String accessToken);
    Users getUserByEmail(String customerEmail);
    void saveUser(Users user);

    void updateRefreshTokenByLogin(String email, String refreshToken); // login시 refreshToken 업데이트
    void TestAddVideoToPlaylist(String accessToken, String playlistId, String videoId);
}
