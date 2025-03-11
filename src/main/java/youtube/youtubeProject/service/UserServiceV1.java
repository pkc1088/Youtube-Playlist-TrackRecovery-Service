package youtube.youtubeProject.service;

import org.springframework.stereotype.Service;
import youtube.youtubeProject.domain.User;
import youtube.youtubeProject.repository.UserRepository;
import youtube.youtubeProject.repository.UserRepositoryV1;

@Service
public class UserServiceV1 implements UserService {

    private final UserRepository userRepository;

    public UserServiceV1(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getAccessTokenByEmail(String email) {
        User user = userRepository.findByUserEmail(email);

        return user.getAccessToken();
    }

    @Override
    public void saveUser(User user) {
        //saveUser(user);
        userRepository.saveUser(user);
    }
}