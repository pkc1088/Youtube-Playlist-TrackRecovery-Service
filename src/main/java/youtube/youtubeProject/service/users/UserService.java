package youtube.youtubeProject.service.users;

import youtube.youtubeProject.domain.Users;

public interface UserService {

    Users getUserByUserId(String userId);
    void saveUser(Users user);
    String getNewAccessTokenByUserId(String userId);
}
