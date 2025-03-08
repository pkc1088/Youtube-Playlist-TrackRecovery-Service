package youtube.youtubeProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.NormalMusic;
import java.util.List;
import java.util.Optional;

public interface MusicRepository {// extends JpaRepository<NormalMusic, Long> {
    NormalMusic save(NormalMusic item);

    void update(String videoId);

    Optional<NormalMusic> findById(String videoId);

    List<NormalMusic> findAll(String videoId);
}
