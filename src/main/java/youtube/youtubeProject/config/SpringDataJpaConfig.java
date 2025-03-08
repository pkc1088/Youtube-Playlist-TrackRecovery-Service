package youtube.youtubeProject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import youtube.youtubeProject.repository.MusicRepository;
import youtube.youtubeProject.repository.MusicRepositoryImpl;
import youtube.youtubeProject.repository.SpringDataJpaMusicRepository;
import youtube.youtubeProject.service.MusicService;
import youtube.youtubeProject.service.MusicServiceImpl;

@Configuration
@RequiredArgsConstructor
public class SpringDataJpaConfig {

    private final SpringDataJpaMusicRepository springDataJpaMusicRepository;

    @Bean
    public MusicService musicService() {
        return new MusicServiceImpl(musicRepository());
    }

    @Bean
    public MusicRepository musicRepository() {
        return new MusicRepositoryImpl(springDataJpaMusicRepository);
    }

}