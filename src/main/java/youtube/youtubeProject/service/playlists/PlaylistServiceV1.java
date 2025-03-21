package youtube.youtubeProject.service.playlists;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import youtube.youtubeProject.domain.Playlists;
import youtube.youtubeProject.domain.Users;
import youtube.youtubeProject.repository.playlists.PlaylistRepository;
import youtube.youtubeProject.repository.users.UserRepository;
import youtube.youtubeProject.service.musics.MusicService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class PlaylistServiceV1 implements PlaylistService {

    @Value("${youtube.api.key}")
    private String apiKey;
    private static YouTube youtube;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final MusicService musicService;

    public PlaylistServiceV1(UserRepository userRepository, PlaylistRepository playlistRepository, MusicService musicService) {
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.musicService = musicService;
        youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {}).setApplicationName("youtube").build();
    }

    @Override
    public List<Playlists> getPlaylistsByUserId(String userId){ // DB에 저장된 플레이리스트들을 반환하면 됨
        // DB 에서 userId로 조회 후 리턴
//        Users user = userRepository.findByUserId(userId);
        return playlistRepository.findAllPlaylistsByUserId(userId);
    }

    public void registerPlaylists(String userId) throws IOException {
        // 1. userId 로 oauth2 인증 거쳐 DB 에 저장됐을 channelId 얻기
        Users user = userRepository.findByUserId(userId);
        String channelId = user.getUserChannelId();
        // 2. channelId로 api 호출 통해 playlist 다 받아오기
        YouTube.Playlists.List request = youtube.playlists().list(Collections.singletonList("snippet, id, contentDetails"));
        request.setKey(apiKey);
        request.setChannelId(channelId);
        request.setMaxResults(50L);
        PlaylistListResponse response = request.execute();

        // 3. playlist 도메인에 저장하기
        for (Playlist getPlaylist : response.getItems()) {
            // 3.1 필요한 데이터만 추출해서 내 Playlist 엔티티에 매핑
            Playlists playlist = new Playlists();
            playlist.setPlaylistId(getPlaylist.getId());
            playlist.setPlaylistTitle(getPlaylist.getSnippet().getTitle());
            playlist.setServiceType("track&recover");
            playlist.setUser(user);
            // 3.2 Playlist 객체를 DB에 저장
            playlistRepository.save(playlist);

            // 4. 해당 플레이리스트에 딸린 모든 음악을 Music 도메인에 저장
            musicService.initiallyAddVideoDetails(getPlaylist.getId());
        }

    }
}
