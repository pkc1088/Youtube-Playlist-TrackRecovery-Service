package youtube.youtubeProject.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import youtube.youtubeProject.domain.NormalMusic;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class MusicRepositoryImpl implements MusicRepository  {

    private final SpringDataJpaMusicRepository repository;

    @Override
    public NormalMusic save(NormalMusic item) {
        return null;
    }

    @Override
    public void update(String videoId) {

    }

    @Override
    public Optional<NormalMusic> findById(String videoId) {
        return Optional.empty();
    }

    @Override
    public List<NormalMusic> findAll(String videoId) {
        return null;
    }
}
