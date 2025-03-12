package youtube.youtubeProject.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Users;

public interface SdjUserRepository extends JpaRepository<Users, Long> {
//    Optional<User> findByUserId(String userId);
    Users findByUserEmail(String userEmail);

    Users findByAccessToken(String accessToken);
//    Optional<Music> findByVideoTitle(String videoTitle);

    // 여기에 기초적인 save 는 다 구현 되어 있음
}
