package youtube.youtubeProject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import youtube.youtubeProject.repository.playLists.PlayListsRepository;
import youtube.youtubeProject.repository.playLists.PlayListsRepositoryV1;
import youtube.youtubeProject.repository.playLists.SdjPlayListsRepository;
import youtube.youtubeProject.repository.user.SdjUserRepository;
import youtube.youtubeProject.repository.user.UserRepository;
import youtube.youtubeProject.repository.user.UserRepositoryV1;
import youtube.youtubeProject.repository.youtube.SdjYoutubeRepository;
import youtube.youtubeProject.repository.youtube.YoutubeRepository;
import youtube.youtubeProject.repository.youtube.YoutubeRepositoryV5;
import youtube.youtubeProject.service.playListsService.PlayListsService;
import youtube.youtubeProject.service.playListsService.PlayListsServiceV1;
import youtube.youtubeProject.service.user.UserService;
import youtube.youtubeProject.service.user.UserServiceV1;
import youtube.youtubeProject.service.youtube.YoutubeService;
import youtube.youtubeProject.service.youtube.YoutubeServiceV5;

@Configuration
@RequiredArgsConstructor
public class SpringDataJpaConfig {

    private final SdjYoutubeRepository springDataJpaYoutubeRepository;
    private final SdjUserRepository sdjUserRepository;
    private final SdjPlayListsRepository sdjPlaylistRepository;

    @Bean
    public YoutubeService youtubeService() {
        return new YoutubeServiceV5(youtubeRepository(), userRepository(), playlistRepository());
    }

    @Bean
    public UserService userService() {
        return new UserServiceV1(userRepository());
    }

    @Bean
    public PlayListsService playListsService() {
        return new PlayListsServiceV1(userRepository(), playlistRepository(), youtubeService());
    }


    @Bean
    public YoutubeRepository youtubeRepository() {
        return new YoutubeRepositoryV5(springDataJpaYoutubeRepository);
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryV1(sdjUserRepository);
    }

    @Bean
    public PlayListsRepository playlistRepository() {
        return new PlayListsRepositoryV1(sdjPlaylistRepository);
    }

}