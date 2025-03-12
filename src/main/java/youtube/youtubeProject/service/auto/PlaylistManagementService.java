package youtube.youtubeProject.service.auto;

import youtube.youtubeProject.service.user.UserService;
import youtube.youtubeProject.service.youtube.YoutubeService;

//@Service
public class PlaylistManagementService {

    private YoutubeService youtubeService;
    private UserService userService;

//    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void managePlaylists() {
//        List<Users> users = userService.getAllUsers();
//        for (Users user : users) {
//            try {
//                String accessToken = user.getAccessToken();
//                String playlistId = user.getPlaylistId(); // 고객별 플레이리스트 ID
//                String videoId = "추가할 영상 ID";
//                youtubeService.TestAddVideoToPlaylist(accessToken, playlistId, videoId);
//            } catch (Exception e) {
//                // 토큰이 만료된 경우 갱신 후 재시도
//                userService.refreshAndSaveToken(user.getEmail());
//                String newAccessToken = userService.getAccessTokenByEmail(user.getEmail());
//                youtubeService.TestAddVideoToPlaylist(newAccessToken, playlistId, videoId);
//            }
//        }
    }
}