package youtube.youtubeProject.service;

import youtube.youtubeProject.domain.User;

public interface UserService {
    public String getAccessTokenByEmail(String email);

    public void saveUser(User user);
}
