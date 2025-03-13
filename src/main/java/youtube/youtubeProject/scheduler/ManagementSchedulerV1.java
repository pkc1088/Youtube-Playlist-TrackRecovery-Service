package youtube.youtubeProject.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import youtube.youtubeProject.service.user.UserService;
import youtube.youtubeProject.service.youtube.YoutubeService;

@Component
@RequiredArgsConstructor
public class ManagementSchedulerV1 {
    private final YoutubeService youtubeService;
    private final UserService userService;

    @Scheduled(fixedRate = 5000, initialDelayString = "5000")
    public void autoSchedule() {
        userService.autoAdd();
    }

}
