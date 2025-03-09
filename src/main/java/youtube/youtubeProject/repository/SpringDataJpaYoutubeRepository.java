package youtube.youtubeProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Music;

import java.util.List;

public interface SpringDataJpaYoutubeRepository extends JpaRepository<Music, Long> {
    List<Music> findByVideoTitleLike(String videoId);
    List<Music> findByVideoDescriptionLike(String videoId);
}
