package youtube.youtubeProject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import youtube.youtubeProject.repository.musics.MusicRepository;
import youtube.youtubeProject.repository.musics.MusicRepositoryV1;
import youtube.youtubeProject.repository.musics.SdjMusicRepository;
import youtube.youtubeProject.repository.playlists.PlaylistRepository;
import youtube.youtubeProject.repository.playlists.PlaylistRepositoryV1;
import youtube.youtubeProject.repository.playlists.SdjPlaylistRepository;
import youtube.youtubeProject.repository.users.SdjUserRepository;
import youtube.youtubeProject.repository.users.UserRepository;
import youtube.youtubeProject.repository.users.UserRepositoryV1;
import youtube.youtubeProject.service.musics.MusicService;
import youtube.youtubeProject.service.musics.MusicServiceV1;
import youtube.youtubeProject.service.playlists.PlaylistService;
import youtube.youtubeProject.service.playlists.PlaylistServiceV1;
import youtube.youtubeProject.service.users.UserService;
import youtube.youtubeProject.service.users.UserServiceV1;
import youtube.youtubeProject.service.youtube.YoutubeService;
import youtube.youtubeProject.service.youtube.YoutubeServiceV5;

@Configuration
@RequiredArgsConstructor
public class SpringDataJpaConfig {

    private final SdjUserRepository sdjUserRepository;
    private final SdjPlaylistRepository sdjPlaylistRepository;
    private final SdjMusicRepository sdjMusicRepository;

    @Bean
    public YoutubeService youtubeService() {
        return new YoutubeServiceV5(userRepository(), playlistRepository(), musicRepository(), musicService());
    }

    @Bean
    public UserService userService() {
        return new UserServiceV1(userRepository());
    }

    @Bean
    public PlaylistService playlistService() {
        return new PlaylistServiceV1(userRepository(), playlistRepository(), musicService());
    }

    @Bean
    public MusicService musicService() {
        return new MusicServiceV1(playlistRepository(), musicRepository());
    }


    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryV1(sdjUserRepository);
    }

    @Bean
    public PlaylistRepository playlistRepository() {
        return new PlaylistRepositoryV1(sdjPlaylistRepository);
    }

    @Bean
    public MusicRepository musicRepository() {
        return new MusicRepositoryV1(sdjMusicRepository);
    }
}