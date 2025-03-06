/*
package youtube.youtubeProject.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 쿼리 파라미터 추가
        String playlistId = "YOUR_DEFAULT_PLAYLIST_ID"; // 기본값 설정 또는 동적으로 받아오기
        String videoId = "YOUR_DEFAULT_VIDEO_ID"; // 기본값 설정 또는 동적으로 받아오기
        String targetUrl = "/addVideoToPlaylist?playlistId=" + playlistId + "&videoId=" + videoId;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}*/
