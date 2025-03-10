package youtube.youtubeProject.controller;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import youtube.youtubeProject.domain.User;
import youtube.youtubeProject.service.YoutubeService;
import youtube.youtubeProject.service.YoutubeServiceV4;
import youtube.youtubeProject.service.YoutubeServiceV5;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class YoutubeControllerV5 {

    //@Autowired
    private final YoutubeService youtubeService;
    //private final User user;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String initRegister() {
        return "memberRegister";//"welcome"; // welcome이 아니라 memberRegister 로 보내야함
    }

    @GetMapping("/welcome")
    public String welcomePage() {
        return "welcome";
    }

    @GetMapping("/memberRegister")
    public String memberRegister() {
        return "memberRegister";
    }

    /*@PostMapping("/memberRegister3")
    public String memberRegister3(@RequestParam String userId, @RequestParam String userPwd,
                                 @RequestParam String userName, @RequestParam String userHandler) {
        //String result =  youtubeService.memberRegister(userId, userPwd, userName);
        //User registerUser = user.save(new User(userId, userPwd, userName)); 이것도 YoutubeSerivce 맹키로 다 만들기
        //post 받은거 처리하는건 나중에 하자
        return "redirect:/welcome"; // 이건 나중에 initallyAddVideo(GetMapping) 페이지로 이동시켜서 플리 등록하게 해야함
    }*/
    @PostMapping("/memberRegister")
    public String memberRegister(@ModelAttribute User user) {
        //String result =  youtubeService.memberRegister(userId, userPwd, userName);
        //User registerUser = user.save(new User(userId, userPwd, userName)); 이것도 YoutubeSerivce 맹키로 다 만들기
        //post 받은거 처리하는건 나중에 하자
        //log.info("member registered");
        return "redirect:/welcome"; // 이건 나중에 initallyAddVideo(GetMapping) 페이지로 이동시켜서 플리 등록하게 해야함
    }

    @GetMapping("{channelId}/playlists") // 단순 보여주는 용도 (api 호출해서 보여주는게 아니라, 내 db에 있는 정보를 보여줘야함)
    // 즉 내부적으로 추적하는건 당연히 api써서 내가 해결해야하는거고, 사용자한테 그냥 등록된 리스트 보여주는건 내 db에 저장된걸 보여야함
    public String getPlaylists(@PathVariable String channelId, Model model) throws IOException {
        List<Playlist> playlists = youtubeService.getPlaylistsByChannelId(channelId);
        model.addAttribute("playlists", playlists);
        return "playlists";
    }

    @GetMapping("{playlistId}/getVideos") // 단순 보여주는 용도 (api 호출해서 보여주는게 아니라, 내 db에 있는 정보를 보여줘야함)
    public String getVideos(@PathVariable String playlistId, Model model) throws IOException {
        try {
            List<String> videos = youtubeService.getVideosFromPlaylist(playlistId);
            model.addAttribute("getVideos", videos); // 비디오 목록만 모델에 담는거임
        } catch (IOException e) {
            model.addAttribute("error", "Failed to fetch videos from playlist - getvideos() method.");
            e.printStackTrace();
        }
        return "getVideos";
    }
//    @GetMapping("{playlistId}/getVideos") // 단순 보여주는 용도 (api 호출해서 보여주는게 아니라, 내 db에 있는 정보를 보여줘야함)
//    public String getVideos(@PathVariable String playlistId, Model model) throws IOException {
//        try {
//            Map<String, String> videos = youtubeService.getVideosFromPlaylist(playlistId);
//            model.addAttribute("getVideos", videos); // 비디오 목록만 모델에 담는거임
//        } catch (IOException e) {
//            model.addAttribute("error", "Failed to fetch videos from playlist - getvideos() method.");
//            e.printStackTrace();
//        }
//        return "getVideos";
//    }

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
//        model.addAttribute("playlistIdForRecover", playlistId);
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

    /*
    @GetMapping("/add")
    public String addForm() {
        return "addForm";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemService.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/items/{itemId}";
    }
     */
}