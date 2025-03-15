package youtube.youtubeProject.repository.youtube;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import youtube.youtubeProject.domain.Music;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class YoutubeRepositoryV5 implements YoutubeRepository {

    private final SdjYoutubeRepository repository;

    @Override
    public List<Music> findAllMusicByPlaylistId(String playlistId) {
        return repository.findByVideoPlaylistIdLike(playlistId);
    }

    @Override
    public void addUpdatePlaylist(String playlistId, Music music) {
        repository.save(music);
    }

    @Override
    public void deleteUpdatePlaylist(String playlistId, String videoId) {
        repository.deleteByVideoId(videoId);
    }

    @Override
    public Optional<Music> getMusicFromDBThruMusicId(String videoId) {
        return repository.findByVideoId(videoId);
    }

    @Override
    public void dBTrackAndRecover(String videoIdToDelete, Music videoToRecover) {

        Optional<Music> optionalMusic = repository.findByVideoId(videoIdToDelete);
        System.err.println("Found Record : " + optionalMusic);
        if (optionalMusic.isPresent()) {
            Music musicToUpdate = optionalMusic.get();
            musicToUpdate.setVideoId(videoToRecover.getVideoId());// videoToRecover의 정보로 엔티티 업데이트
            musicToUpdate.setVideoTitle(videoToRecover.getVideoTitle());
            musicToUpdate.setVideoUploader(videoToRecover.getVideoUploader());
            musicToUpdate.setVideoDescription(videoToRecover.getVideoDescription());
            musicToUpdate.setVideoTags(videoToRecover.getVideoTags());
             //getVideoPlaylistPosition/Id/UserId() 기존 그대로 유지할거니 건들 필요 없음
            System.err.println("DB update completed");
        } else {
            throw new RuntimeException("DB update error: " + videoIdToDelete);
        }
    }

//    @Override
//    public String addVideoToPlaylist(String playlistId, String videoId, Music music) {
////        Music music1 = new Music();
////
////        repository.save(music);
//        return null;
//    }
//
//    @Override
//    public String deleteFromPlaylist(String playlistId, String videoId) {
//        return null;
//    }

//    @Override
//    public List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException {
//        return null;
//    }
//
//    @Override
//    public List<String> getVideosFromPlaylist(String playlistId) throws IOException {
//        return null;
//    }



//    @Override
//    public Video getVideoDetails(String videoId) throws IOException {
//        return null;
//    }

//    @Override
//    public String searchVideo(String query) throws IOException {
//        return null;
//    }

//    public String memberRegister(String userId, String userPwd, String userName) {
//
//        return null;
//    }
}
