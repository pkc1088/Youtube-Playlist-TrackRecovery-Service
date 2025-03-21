package youtube.youtubeProject.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import youtube.youtubeProject.domain.Playlists;
import youtube.youtubeProject.domain.Users;
import youtube.youtubeProject.gemini.GeminiService;
import youtube.youtubeProject.service.musics.MusicService;
import youtube.youtubeProject.service.playlists.PlaylistService;
import youtube.youtubeProject.service.users.UserService;
import youtube.youtubeProject.service.youtube.YoutubeService;
import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManagementScheduler {

    private final PlaylistService playlistService;
    private final YoutubeService youtubeService;

//    private final GeminiService service;

//    @Scheduled(fixedRate = 30000, initialDelayString = "3000")
//    public void geminiTest() {
//        // Music music 댓글, description 뽑아 내서 쿼리에 담으면 됨
//        String report =
//                "삼성전자는 약 489㎡ (약 150평) 규모의 전시장에서 가정용부터 상업용까지 다양한 냉난방공조 솔루션을 전시한다.\n" +
//                "삼성전자 DA사업부 최항석 상무는 “삼성전자는 독보적인 무풍 기술과 고효율 에너지 기술 그리고 편리한 연결성을 기반으로 글로벌 공조 시장에서 영향력을 확대하고 있다”며 “앞으로도 지역별 소비자 니즈와 환경에 최적화된 제품을 다양하게 선보일 것”이라고 말했다.";
//        String ending = "이 뉴스 기사를 한 줄로 짧게 감정 분석해";
//        String text = service.getCompletion(report + ending);
//        System.out.println(text);
//    }


    // 이거 먼저 테스트하면 됨. (단순히 유저 한 명으로 User/PlayLists/Music 테이블 다 들어오는지 확인
//    @Scheduled(fixedRate = 30000, initialDelayString = "2000")
    public void allAddScenarioToOnePerson() throws IOException {
        log.info("auto scheduler activated");
        // 1. 회원 등록은 이미 했음 (회원가입 시)
        String userId  = "112735690496635663877";
        // 2. 플레이리스트 & 음악 모두 등록
        playlistService.registerPlaylists(userId);
        log.info("auto scheduler done");
    }

    @Transactional
    @Scheduled(fixedRate = 50000, initialDelayString = "2000")
    public void allPlaylistsRecoveryOfOneParticularUserTest() throws IOException {
        log.info("auto scheduler activated");
        // 0. 전체 유저 목록에서 순차적으로 유저를 뽑아 오는 시나리오 있다 치고
        String userId  = "112735690496635663877";

        // 1. 유저 아이디로 조회한 모든 플레이리스트를 디비에서 뽑아서 복구 시스템 가동
        Set<Playlists> playListsSet = playlistService.getPlaylistsByUserId(userId);
        for (Playlists playlist : playListsSet) {
            log.info("{} start", playlist.getPlaylistTitle());
            youtubeService.fileTrackAndRecover(userId, playlist.getPlaylistId());
        }

        log.info("auto scheduler done");
    }




////    @Scheduled(fixedRate = 30000, initialDelayString = "3000")
//    public void updateTest() throws IOException {
//        System.err.println("auto scheduler activated");
//        String playlistId = "PLNj4bt23Rjfsm0Km4iNM6RSBwXXOEym74";
//        musicService.updatePlaylist(playlistId);
//        System.err.println("auto scheduler done");
//    }
//
////    @Scheduled(fixedRate = 60000, initialDelayString = "3000")
//    public void autoScheduleGeneralCustomer() throws IOException {
//        System.err.println("auto scheduler activated");
//        Users user = userService.getUserByUserId("112735690496635663877");
//        String playlistId = "PLNj4bt23Rjfsm0Km4iNM6RSBwXXOEym74";
//        youtubeService.fileTrackAndRecover(user.getUserId(), playlistId);
//        System.err.println("auto scheduler done");
//    }
}
