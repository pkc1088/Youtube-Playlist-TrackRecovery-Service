package youtube.youtubeProject.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Users;

public interface SdjUserRepository extends JpaRepository<Users, String> {

    Users findByUserEmail(String userEmail);
    Users findByUserId(String userId);

//    Optional<User> findByUserId(String userId);
//?    Users findByAccessToken(String accessToken);
//    Optional<Music> findByVideoTitle(String videoTitle);

    // 여기에 기초적인 save 는 다 구현 되어 있음
}
