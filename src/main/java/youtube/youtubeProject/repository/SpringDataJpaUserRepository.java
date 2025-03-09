package youtube.youtubeProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Music;
import youtube.youtubeProject.domain.User;

public interface SpringDataJpaUserRepository extends JpaRepository<User, Long> {

}
