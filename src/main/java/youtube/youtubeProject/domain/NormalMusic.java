package youtube.youtubeProject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class NormalMusic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoId;
    private String videoTitle;
    private String videoStatus;

    private String musicTitle;
    private String musicSinger;

    private String videoPlaylistId;
    private int videoPlaylistPosition;

    private String UserId;

    public NormalMusic() {
    }

    public NormalMusic(String videoId, String videoTitle, String videoStatus,
                       String musicTitle, String musicSinger,
                       String videoPlaylistId, int videoPlaylistPosition,
                       String UserId) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoStatus = videoStatus;
        this.musicTitle = musicTitle;
        this.musicSinger = musicSinger;
        this.videoPlaylistId = videoPlaylistId;
        this.videoPlaylistPosition = videoPlaylistPosition;
        this.UserId = UserId;
    }
}
