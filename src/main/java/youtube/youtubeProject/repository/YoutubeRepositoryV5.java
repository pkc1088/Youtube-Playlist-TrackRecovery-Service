package youtube.youtubeProject.repository;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import youtube.youtubeProject.domain.Music;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    public String getMusicTitleFromDBThruMusicId(String videoId) {
        Optional<Music> optionalMusic = repository.findByVideoId(videoId);
        String resultMusicTitle = "";
        if (optionalMusic.isPresent()) {
            Music musicFromDB = optionalMusic.get();
            resultMusicTitle = musicFromDB.getVideoTitle();
        }
        return resultMusicTitle;
    }

    public void fileTrackAndRecover(String videoIdToDelete, String videoTitleToDelete, Music videoToRecover) {
        //List<Music> findFirst = repository.findByVideoTitleLike(videoTitleToDelete);\
        //repository.deleteById(Long.valueOf(videoIdToDelete));
        Optional<Music> optionalMusic = repository.findByVideoId(videoIdToDelete);
        System.err.println("found " + optionalMusic + " from DB Thru SpringDataJPA");

        if (optionalMusic.isPresent()) {
            Music musicToUpdate = optionalMusic.get();
            musicToUpdate.setVideoId(videoToRecover.getVideoId());// videoToRecover의 정보로 엔티티 업데이트
            musicToUpdate.setVideoTitle(videoToRecover.getVideoTitle());
            musicToUpdate.setVideoUploader(videoToRecover.getVideoUploader());
            musicToUpdate.setVideoDescription(videoToRecover.getVideoDescription());
            musicToUpdate.setVideoTags(videoToRecover.getVideoTags());
            //musicToUpdate.setVideoPlaylistId(videoToRecover.getVideoPlaylistId()); 이것도 기존 플리에 저장할거니 그대로
            //musicToUpdate.setVideoPlaylistPosition(videoToRecover.getVideoPlaylistPosition()); 기존 자리에 둘거니 그대로
            //musicToUpdate.setUserId(videoToRecover.getUserId());

            // 안되면 delete 후 save 해도 됨 업데이트가 비디오 아이디가 바뀌는거라 잘 될 듯?
            System.err.println("musicToUpdate completed");

            // 업데이트된 엔티티 저장할 필요 없는걸로 아는데?
            //repository.save(musicToUpdate);
        } else {
            throw new RuntimeException("Video not found with ID: " + videoIdToDelete);
        }

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

    public String memberRegister(String userId, String userPwd, String userName) {

        return null;
    }
}
