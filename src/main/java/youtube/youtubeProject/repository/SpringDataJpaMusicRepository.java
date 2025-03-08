package youtube.youtubeProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import youtube.youtubeProject.domain.NormalMusic;

import java.util.List;

public interface SpringDataJpaMusicRepository extends JpaRepository<NormalMusic, Long> {

    List<NormalMusic> findByVideoIdLike(String videoId);

    List<NormalMusic> findByVideoTitleLike(String videoTitle);

    List<NormalMusic> findByMusicTitleLike(String musicTitle);
    List<NormalMusic> findByMusicSingerLike(String musicSinger);

    List<NormalMusic> findByVideoPlaylistIdLike(String videoPlaylistId);
    //List<NormalMusic> findByVideoPlaylistPositionLike(int videoPlaylistPosition); 에러


    //쿼리 직접 실행
    //@Query("select i from NormalMusic i where i.videoId like :videoId")
    //List<NormalMusic> findNormalMusic(@Param("musicName") String musicName);
}
