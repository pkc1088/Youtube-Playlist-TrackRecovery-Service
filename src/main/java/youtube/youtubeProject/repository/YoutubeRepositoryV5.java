package youtube.youtubeProject.repository;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import youtube.youtubeProject.domain.Music;

import java.io.IOException;
import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class YoutubeRepositoryV5 implements YoutubeRepository {

    private final SpringDataJpaYoutubeRepository repository;

    @Override
    public String addVideoToPlaylist(String playlistId, String videoId, Music music) {
//        Music music1 = new Music();
//
//        repository.save(music);
        return null;
    }

    @Override
    public String deleteFromPlaylist(String playlistId, String videoId) {
        return null;
    }

    @Override
    public List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException {
        return null;
    }

    @Override
    public List<String> getVideosFromPlaylist(String playlistId) throws IOException {
        return null;
    }

    @Override
    public void initiallyAddVideoDetails(String playlistId, String videoTitle, String videoId, String videoUploader) throws IOException {
        repository.save(new Music(videoId, videoTitle, videoUploader, "someDescription",
                        "someTags", playlistId, 5, "someone's Id"));
//        Music findPlaylist =repository.findById(Long.valueOf(playlistId)).orElseThrow();

        return;
    }

    @Override
    public Video getVideoDetails(String videoId) throws IOException {
        return null;
    }

    @Override
    public String searchVideo(String query) throws IOException {
        return null;
    }
}
