package youtube.youtubeProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.User;

import java.util.Optional;

public interface SdjUserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByUserId(String userId);
    User findByUserEmail(String userEmail);
//    Optional<Music> findByVideoTitle(String videoTitle);

    // 여기에 기초적인 save 는 다 구현 되어 있음
}
