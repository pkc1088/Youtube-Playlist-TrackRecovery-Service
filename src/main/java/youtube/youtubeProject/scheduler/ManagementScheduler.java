package youtube.youtubeProject.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import youtube.youtubeProject.domain.Users;
import youtube.youtubeProject.service.user.UserService;
import youtube.youtubeProject.service.youtube.YoutubeService;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ManagementScheduler {
    private final YoutubeService youtubeService;
    private final UserService userService;

//    @Scheduled(fixedRate = 30000, initialDelayString = "5000")
    public void updateTest() throws IOException {
        System.err.println("auto scheduler activated");

        String playlistId = "PLNj4bt23Rjfsm0Km4iNM6RSBwXXOEym74";
        youtubeService.updatePlaylist("pkc1088@gmail.com", playlistId);

        System.err.println("auto scheduler done");
    }


//    Verified!!!
    @Scheduled(fixedRate = 60000, initialDelayString = "5000")
    public void autoScheduleGeneralCustomer() throws IOException {
        System.err.println("auto scheduler activated");
        Users user = userService.getUserByEmail("pkc1088@gmail.com");
        String playlistId = "PLNj4bt23Rjfsm0Km4iNM6RSBwXXOEym74";
        youtubeService.fileTrackAndRecover(user.getUserEmail(), playlistId);
        System.err.println("auto scheduler done");
    }

    public void autoScheduleVIPCustomer() throws IOException {
        System.err.println("auto scheduler activated");
        Users user = userService.getUserByEmail("pkc1088@gmail.com");
        String playlistId = "PLNj4bt23Rjfsm0Km4iNM6RSBwXXOEym74";
        youtubeService.fileTrackAndRecover(user.getUserEmail(), playlistId);
        System.err.println("auto scheduler done");
    }
}
