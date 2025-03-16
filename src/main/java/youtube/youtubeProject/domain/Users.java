package youtube.youtubeProject.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Entity
public class Users {

    @Id
    private String userId;
    private String userName;
    private String userChannelId;
    private String userEmail;
    private String refreshToken;
    // private boolean serviceAvailable;

    // added
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Playlist> playlists; // 한 사용자는 여러 개의 플레이리스트를 가질 수 있음


    public Users() {}

    public Users(String userId, String userName, String userChannelId, String userEmail, String refreshToken) {
        this.userId = userId;
        this.userName = userName;
        this.userChannelId = userChannelId;
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
    }
}
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;