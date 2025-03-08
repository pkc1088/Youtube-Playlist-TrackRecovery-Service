package youtube.youtubeProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import youtube.youtubeProject.domain.NormalMusic;
import youtube.youtubeProject.repository.MusicRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private final MusicRepository musicRepository;

    @Override
    public NormalMusic save(NormalMusic music) {
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
    public List<NormalMusic> findItems(String videoId) {
        return null;
    }

}