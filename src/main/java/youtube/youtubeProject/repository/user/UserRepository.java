package youtube.youtubeProject.repository.user;

import youtube.youtubeProject.domain.Users;

public interface UserRepository {
    Users findByUserEmail(String email);
    void saveUser(Users user);
    void updateRefreshTokenByLogin(String email, String refreshToken);

//    Users findByAccessToken(String accessToken);
//    void updateAccessTokenByRefreshToken(String refreshToken, String accessToken);
//    void saveTokens(User user);
}
