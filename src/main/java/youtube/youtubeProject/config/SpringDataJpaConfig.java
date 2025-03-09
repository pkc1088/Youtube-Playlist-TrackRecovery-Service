package youtube.youtubeProject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import youtube.youtubeProject.repository.SpringDataJpaYoutubeRepository;
import youtube.youtubeProject.repository.YoutubeRepository;
import youtube.youtubeProject.repository.YoutubeRepositoryV5;
import youtube.youtubeProject.service.YoutubeService;
import youtube.youtubeProject.service.YoutubeServiceV5;

@Configuration
@RequiredArgsConstructor
public class SpringDataJpaConfig {

    private final SpringDataJpaYoutubeRepository springDataJpaYoutubeRepository;

    @Bean
    public YoutubeService youtubeService() {
        return new YoutubeServiceV5(youtubeRepository());
    }

    @Bean
    public YoutubeRepository youtubeRepository() {
        return new YoutubeRepositoryV5(springDataJpaYoutubeRepository);
    }

}