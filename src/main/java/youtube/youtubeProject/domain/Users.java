package youtube.youtubeProject.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

//@AllArgsConstructor
@Entity
@Getter @Setter
@NoArgsConstructor
public class Users {

    @Id
    private String userId;
    private String userName;
    private String userChannelId;
    private String userEmail;
    private String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // 이 필드는 Playlists 의 Users user 가 주인임
    private Set<Playlists> playlists;
    // private boolean serviceAvailable;

    public Users(String userId, String userName, String userChannelId, String userEmail, String refreshToken) {
        this.userId = userId;
        this.userName = userName;
        this.userChannelId = userChannelId;
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
    }
}
//    public Users() {}
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;