package youtube.youtubeProject.service.user;

import youtube.youtubeProject.domain.Users;

public interface UserService {
    public String getAccessTokenByEmail(String email);
    public Users getUserByAccessToken(String accessToken);
    Users getUserByEmail(String customerEmail);
    public void saveUser(Users user);
    void TestAddVideoToPlaylist(String accessToken, String playlistId, String videoId);
}
