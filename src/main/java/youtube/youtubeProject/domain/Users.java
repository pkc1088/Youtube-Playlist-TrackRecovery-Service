package youtube.youtubeProject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Users {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    @Id
    private String userId;
    private String userName;
    private String userChannelId;
    private String userEmail;
    private String refreshToken;

    public Users() {
    }

    public Users(String userId, String userName, String userChannelId, String userEmail, String refreshToken) {
        this.userId = userId;
        this.userName = userName;
        this.userChannelId = userChannelId;
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
    }
}