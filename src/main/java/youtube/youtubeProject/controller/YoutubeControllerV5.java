package youtube.youtubeProject.controller;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import youtube.youtubeProject.domain.Users;
import youtube.youtubeProject.service.user.UserService;
import youtube.youtubeProject.service.youtube.YoutubeService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class YoutubeControllerV5 {

    private final YoutubeService youtubeService;
    private final UserService userService;


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String initRegister() {
        return "/welcome";
    }

    @GetMapping("/welcome")
    public String welcomePage() {
        return "welcome";
    }

    /*
    @GetMapping("/mySignup")
    public String signup() {
        return "redirect:/oauth2/authorization/google?prompt=consent&access_type=offline";
    }
    // 로그인 시 (prompt=none)
    @GetMapping("/myLogin")
    public String login() {
        return "redirect:/oauth2/authorization/google?prompt=none";
    }

    @GetMapping("/memberRegister")
    public String memberRegister() {
        return "memberRegister";
    }

    @PostMapping("/memberRegister3")
    public String memberRegister3(@RequestParam String userId, @RequestParam String userPwd,
                                 @RequestParam String userName, @RequestParam String userHandler) {
        //String result =  youtubeService.memberRegister(userId, userPwd, userName);
        //User registerUser = user.save(new User(userId, userPwd, userName)); 이것도 YoutubeSerivce 맹키로 다 만들기
        //post 받은거 처리하는건 나중에 하자
        return "redirect:/welcome"; // 이건 나중에 initallyAddVideo(GetMapping) 페이지로 이동시켜서 플리 등록하게 해야함
    }

    @PostMapping("/memberRegister")
    public String memberRegister(@ModelAttribute Users user) {
        //String result =  youtubeService.memberRegister(userId, userPwd, userName);
        //User registerUser = user.save(new User(userId, userPwd, userName)); 이것도 YoutubeSerivce 맹키로 다 만들기
        //post 받은거 처리하는건 나중에 하자
        //log.info("member registered");
        return "redirect:/welcome"; // 이건 나중에 initallyAddVideo(GetMapping) 페이지로 이동시켜서 플리 등록하게 해야함
    }
    */

    @GetMapping("{channelId}/playlists") // 단순 보여주는 용도 (api 호출해서 보여주는게 아니라, 내 db에 있는 정보를 보여줘야함)
    public String getPlaylists(@PathVariable String channelId, Model model) throws IOException {
        List<Playlist> playlists = youtubeService.getPlaylistsByChannelId(channelId);
        model.addAttribute("playlists", playlists);
        return "playlists";
    }

    @GetMapping("{playlistId}/getVideos") // 단순 보여주는 용도 (api 호출해서 보여주는게 아니라, 내 db에 있는 정보를 보여줘야함)
    public String getVideos(@PathVariable String playlistId, Model model) {
        try {
            List<String> videos = youtubeService.getVideosFromPlaylist(playlistId);
            model.addAttribute("getVideos", videos); // 비디오 목록만 모델에 담는거임
        } catch (IOException e) {
            model.addAttribute("error", "Failed to fetch videos from playlist - getvideos() method.");
            e.printStackTrace();
        }
        return "getVideos";
    }

    @GetMapping("{playlistId}/initiallyAddVideoDetails") // not done
    public String InitiallyAddVideoDetails(@PathVariable String playlistId, Model model) throws IOException {
        try {
            List<Video> videos = youtubeService.initiallyAddVideoDetails(playlistId);
            model.addAttribute("addVideos", videos); // 비디오 목록만 모델에 담는거임
        } catch (IOException e) {
            model.addAttribute("error", "Failed to fetch videos from playlist - Addvideos() method.");
            e.printStackTrace();
        }
        return "addVideos";
    }

    @PostMapping("/fileTrackAndRecover")
    public String fileTrackAndRecover(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                                      @RequestParam String playlistIdForRecover, Model model) throws IOException {
        System.err.println("\n==================== Music Track And Recover System Start ====================");
        youtubeService.fileTrackAndRecover(authorizedClient, playlistIdForRecover); // 결과 알려주는 동작 추가해도 좋음
        System.err.println("==================== Music Track And Recover System Done ====================\n");
        return "redirect:/welcome";
    }

//    @GetMapping("search") // 필요없음 이건 내부적으로 수행해야함
//    public String searchVideo(@RequestParam String keyword, Model model) throws IOException {
//        String result = youtubeService.searchVideo(keyword);
//        model.addAttribute("result", result);
//        return "search";
//    }
//
//    @PostMapping("/addVideoToPlaylist") // 필요없음 이건 내부적으로 수행해야함
//    public String addVideoToPlaylist(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
//                                     @RequestParam String playlistId, @RequestParam String videoId) {
//        String result =  youtubeService.addVideoToPlaylist(authorizedClient, playlistId, videoId);
//        return "redirect:/welcome";
//    }
//
//    @PostMapping("/deleteFromPlaylist") // 필요없음 이건 내부적으로 수행해야함
//    public String deleteFromPlaylist(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
//                                     @RequestParam String playlistId, @RequestParam String videoId) {
//        String result = youtubeService.deleteFromPlaylist(authorizedClient, playlistId, videoId);
//        return "redirect:/welcome";
//    }

    @GetMapping("/tokenTest")
    public String tokenTest(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, Model model) {
        youtubeService.tokenTest(authorizedClient);
        return "redirect:/welcome";
    }

    @PostMapping("/TestAddVideoToPlaylist")
    public String TestAddVideoToPlaylist(@RequestParam String customerEmail, @RequestParam String playlistId, @RequestParam String videoId) {

        Users users = userService.getUserByEmail(customerEmail);
        String accessToken = users.getAccessToken();
        //String accessToken = userService.getAccessTokenByEmail(customerEmail);

        System.out.println("TestAddVideoToPlaylist - accessToken : " + accessToken);
        if (accessToken == null) {
            return "redirect:/error?message=Customer token not found";
        }

        userService.TestAddVideoToPlaylist(accessToken, playlistId, videoId);

        return "redirect:/welcome";
    }

    @GetMapping("/ScheduledTest")
    public String scheduledTest(Model model) {
        youtubeService.scheduledTest();
        return "redirect:/welcome";
    }
}