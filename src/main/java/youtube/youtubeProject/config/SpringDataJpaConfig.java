package youtube.youtubeProject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import youtube.youtubeProject.repository.*;
import youtube.youtubeProject.service.UserService;
import youtube.youtubeProject.service.UserServiceV1;
import youtube.youtubeProject.service.YoutubeService;
import youtube.youtubeProject.service.YoutubeServiceV5;

@Configuration
@RequiredArgsConstructor
public class SpringDataJpaConfig {

    private final SdjYoutubeRepository springDataJpaYoutubeRepository;
    private final SdjUserRepository sdjUserRepository;

    @Bean
    public YoutubeService youtubeService() {
        return new YoutubeServiceV5(youtubeRepository());
    }

    @Bean
    public YoutubeRepository youtubeRepository() {
        return new YoutubeRepositoryV5(springDataJpaYoutubeRepository);
    }

    @Bean
    public UserService userService() {
        return new UserServiceV1(userRepository());
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryV1(sdjUserRepository);
    }
}