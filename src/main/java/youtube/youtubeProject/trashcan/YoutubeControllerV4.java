package youtube.youtubeProject.trashcan;


import com.google.api.services.youtube.model.Playlist;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import youtube.youtubeProject.service.YoutubeServiceV4;

import java.io.IOException;
import java.util.List;

//@Controller
public class YoutubeControllerV4 {

    //@Autowired
    private YoutubeServiceV4 youtubeService;


    //@GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html을 반환
    }

    //@GetMapping("/")
    public String welcomePage() {
        return "welcome";
    }

//    @GetMapping("/addVideoToPlaylist")
//    public String addVideoToPlaylist(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, @RequestParam String playlistId, @RequestParam String videoId, Model model) {
//        String result = youtubeService.addVideoToPlaylist(authorizedClient, playlistId, videoId);
//        model.addAttribute("message", result);
//        return "result"; // 결과를 보여줄 뷰 페이지
//    }

    //@PostMapping("/addVideoToPlaylist")
    public String addVideoToPlaylist(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, @RequestParam String playlistId, @RequestParam String videoId) {
        String result =  youtubeService.addVideoToPlaylist(authorizedClient, playlistId, videoId);
        return "redirect:/"; // 작업 완료 후 루트 페이지로 리다이렉트
    }

    //@PostMapping("/deleteFromPlaylist")
    public String deleteFromPlaylist(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, @RequestParam String playlistId, @RequestParam String videoId) {
        String result = youtubeService.deleteFromPlaylist(authorizedClient, playlistId, videoId);
        return "redirect:/"; // 작업 완료 후 루트 페이지로 리다이렉트
    }

    //@GetMapping("{channelId}/playlists")
    public String getPlaylists(@PathVariable String channelId, Model model) throws IOException {
        List<Playlist> playlists = youtubeService.getPlaylistsByChannelId(channelId);
        model.addAttribute("playlists", playlists);
        return "playlists"; // playlists.html로 이동
    }
/*
    //@GetMapping("{playlistId}/videos")
    public String getVideos(@PathVariable String playlistId, Model model) throws IOException {
//        List<Video> videos = youtubeService.getVideosFromPlaylist(playlistId);
//        model.addAttribute("videos", videos);
        try {
            List<Video> videos = youtubeService.getVideosFromPlaylist(playlistId);
            model.addAttribute("videos", videos); // 접근 가능한 영상 목록을 모델에 추가
        } catch (IOException e) {
            model.addAttribute("error", "Failed to fetch videos from playlist - getvideos() method.");
            e.printStackTrace();
        }
        return "videos"; // videos.html로 이동
    }
*/
    //@GetMapping("search")
    public String searchVideo(@RequestParam String keyword, Model model) throws IOException {
        String result = youtubeService.searchVideo(keyword);
        model.addAttribute("result", result);
        return "search"; // search.html로 이동
    }

//    @ResponseBody
//    @GetMapping("/addVideoToPlaylist")
//    public String addVideoToPlaylist(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient, @RequestParam String playlistId, @RequestParam String videoId) {
//        return youtubeService.addVideoToPlaylist(authorizedClient, playlistId, videoId);
//    }
//    @ResponseBody
//    @GetMapping("/youtube/{channelId}/playlists")
//    public List<Playlist> getPlaylists(@PathVariable String channelId) throws IOException {
//        return youtubeService.getPlaylistsByChannelId(channelId);
//    }
//
//    @ResponseBody
//    @GetMapping("/youtube/playlist/{playlistId}/videos")
//    public List<Video> getVideos(@PathVariable String playlistId) throws IOException {
//        return youtubeService.getVideosFromPlaylist(playlistId);
//    }
//
//    @ResponseBody
//    @GetMapping("/youtube/search")
//    public ResponseEntity<String> searchVideo(@RequestParam String keyword) throws IOException {
//        String result = youtubeService.searchVideo(keyword);
//        return ResponseEntity.ok(result);
//    }
}