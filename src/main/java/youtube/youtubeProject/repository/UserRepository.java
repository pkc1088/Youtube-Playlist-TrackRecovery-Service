package youtube.youtubeProject.repository;

import youtube.youtubeProject.domain.User;

import java.util.Optional;

public interface UserRepository {
    User findByUserEmail(String email);
    void saveUser(User user);
}
