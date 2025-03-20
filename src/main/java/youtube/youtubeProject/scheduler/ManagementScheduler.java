package youtube.youtubeProject.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import youtube.youtubeProject.domain.Users;
import youtube.youtubeProject.gemini.GeminiService;
import youtube.youtubeProject.service.user.UserService;
import youtube.youtubeProject.service.youtube.YoutubeService;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ManagementScheduler {

    private final YoutubeService youtubeService;
    private final UserService userService;
    private final GeminiService service;

//    @Scheduled(fixedRate = 30000, initialDelayString = "3000")
    public void geminiTest() {
        // Music music 댓글, description 뽑아 내서 쿼리에 담으면 됨
        String report =
                "삼성전자는 약 489㎡ (약 150평) 규모의 전시장에서 가정용부터 상업용까지 다양한 냉난방공조 솔루션을 전시한다.\n" +
                "삼성전자 DA사업부 최항석 상무는 “삼성전자는 독보적인 무풍 기술과 고효율 에너지 기술 그리고 편리한 연결성을 기반으로 글로벌 공조 시장에서 영향력을 확대하고 있다”며 “앞으로도 지역별 소비자 니즈와 환경에 최적화된 제품을 다양하게 선보일 것”이라고 말했다.";
        String ending = "이 뉴스 기사를 한 줄로 짧게 감정 분석해";
        String text = service.getCompletion(report + ending);
        System.out.println(text);
    }

    /*
    로그인 귀찮으니 refresh Token 으로 픒레이리스트에 바로 집어넣게 Schedule 짜자
     */

//    @Scheduled(fixedRate = 30000, initialDelayString = "3000")
    public void updateTest() throws IOException {
        System.err.println("auto scheduler activated");
        String playlistId = "PLNj4bt23Rjfsm0Km4iNM6RSBwXXOEym74";
        youtubeService.updatePlaylist(playlistId);
        System.err.println("auto scheduler done");
    }

//    @Scheduled(fixedRate = 60000, initialDelayString = "3000")
    public void autoScheduleGeneralCustomer() throws IOException {
        System.err.println("auto scheduler activated");
        Users user = userService.getUserByUserId("112735690496635663877");
        String playlistId = "PLNj4bt23Rjfsm0Km4iNM6RSBwXXOEym74";
        youtubeService.fileTrackAndRecover(user.getUserId(), playlistId);
        System.err.println("auto scheduler done");
    }


//    @Scheduled(fixedRate = 30000, initialDelayString = "2000")
    public void channelTest() throws IOException {
        System.err.println("auto scheduler activated");
        String userId  = "112735690496635663877";

        System.err.println("auto scheduler done");
    }

}
