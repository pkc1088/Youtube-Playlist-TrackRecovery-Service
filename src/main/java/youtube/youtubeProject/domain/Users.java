package youtube.youtubeProject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String userPassword;
    private String userName;
    private String userHandler;

    private String userEmail;
    private String accessToken;
    private String refreshToken;

    public Users() {
    }

    public Users(String userId, String userPassword, String userName, String userHandler,
                 String userEmail, String accessToken, String refreshToken) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userHandler = userHandler;
        this.userEmail = userEmail;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    // 생성자, Getter, Setter
}