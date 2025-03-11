package youtube.youtubeProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Music;

import java.util.Optional;

public interface SdjYoutubeRepository extends JpaRepository<Music, Long> {
//    List<Music> findByVideoTitleLike(String videoId);
//    List<Music> findByVideoDescriptionLike(String videoId);
    Optional<Music> findByVideoId(String videoId);
//    Optional<Music> findByVideoTitle(String videoTitle);

    // 여기에 기초적인 save 는 다 구현 되어 있음
}
