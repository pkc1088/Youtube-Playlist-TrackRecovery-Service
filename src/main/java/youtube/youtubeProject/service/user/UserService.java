package youtube.youtubeProject.service.user;

import youtube.youtubeProject.domain.Users;

public interface UserService {

    Users getUserByEmail(String customerEmail);
    Users getUserByUserId(String userId);
    void saveUser(Users user);
    //void updateRefreshTokenByLogin(String email, String refreshToken); // login 시 refreshToken 업데이트



}
