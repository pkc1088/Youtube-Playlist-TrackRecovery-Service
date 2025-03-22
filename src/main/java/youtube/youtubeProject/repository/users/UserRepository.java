package youtube.youtubeProject.repository.users;

import youtube.youtubeProject.domain.Users;

public interface UserRepository {

    Users findByUserId(String userId);
    void saveUser(Users user);

}
//    void updateRefreshTokenByLogin(String email, String refreshToken);
//    Users findByUserEmail(String email);
//    Users findByAccessToken(String accessToken);
//    void updateAccessTokenByRefreshToken(String refreshToken, String accessToken);
//    void saveTokens(User user);