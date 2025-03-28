package youtube.youtubeProject.scheduler;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import youtube.youtubeProject.domain.Music;
import youtube.youtubeProject.domain.Playlists;
import youtube.youtubeProject.policy.SearchPolicy;
import youtube.youtubeProject.service.playlists.PlaylistService;
import youtube.youtubeProject.service.users.UserService;
import youtube.youtubeProject.service.youtube.YoutubeService;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ManagementScheduler {

    private final PlaylistService playlistService;
    private final YoutubeService youtubeService;
    private final UserService userService;
    private final SearchPolicy searchPolicy;

    @Autowired
    public ManagementScheduler( // @Qualifier("simpleSearchQuery")
            PlaylistService playlistService, YoutubeService youtubeService, UserService userService,
            @Qualifier("geminiSearchQuery") SearchPolicy searchPolicy) {
        this.playlistService = playlistService;
        this.youtubeService = youtubeService;
        this.userService = userService;
        this.searchPolicy = searchPolicy;
    }

//    @Scheduled(fixedRate = 30000, initialDelayString = "2000")
    public void geminiTest() {
        Music musicToSearch = new Music();
        musicToSearch.setVideoTitle("kiss and say goodbye");
        musicToSearch.setVideoUploader("the manhattans");
        String text = searchPolicy.search(musicToSearch);
        System.out.println(text);
    }


//    @Scheduled(fixedRate = 30000, initialDelayString = "1000")
    public void allAddScenarioToOnePerson() throws IOException {
        log.info("auto scheduler activated");
        // 1. 회원 등록은 이미 했음 (회원가입 시)
        String userId  = "112735690496635663877";
        // 2. 플레이리스트 & 음악 모두 등록
        playlistService.registerPlaylists(userId);
        log.info("auto scheduler done");
    }

//    @Scheduled(fixedRate = 50000, initialDelayString = "1000")  //    @Transactional
    public void allPlaylistsRecoveryOfOneParticularUserTest() throws IOException {
        log.info("auto scheduler activated");
        // 0. 전체 유저 목록에서 순차적으로 유저를 뽑아 오는 시나리오 있다 치고
        String userId  = "112735690496635663877";
        // 1. 유저 아이디로 accessToken 발급
        String accessToken = userService.getNewAccessTokenByUserId(userId);
        // 2. 유저 아이디로 조회한 모든 플레이리스트 & 음악을 디비에서 뽑아서 복구 시스템 가동
        List<Playlists> playListsSet = playlistService.getPlaylistsByUserId(userId);
        for (Playlists playlist : playListsSet) {
            log.info("{} start", playlist.getPlaylistTitle());
            // playlist 자체가 제거된 경우 예외처리 필요
            youtubeService.fileTrackAndRecover(userId, playlist.getPlaylistId(), accessToken);
        }

        log.info("auto scheduler done");
    }


//    @Scheduled(fixedRate = 30000, initialDelayString = "3000")
//    public void updateTest() throws IOException {
//        System.err.println("auto scheduler activated");
//        String playlistId = "PLNj4bt23Rjfsm0Km4iNM6RSBwXXOEym74";
//        musicService.updatePlaylist(playlistId);
//        System.err.println("auto scheduler done");
//    }
//

}
