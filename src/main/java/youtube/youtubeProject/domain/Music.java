package youtube.youtubeProject.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoId;
    private String videoTitle;
    private String videoUploader;
    private String videoDescription;
    private String videoTags; //    @ElementCollection private List<String> videoTags;
    private int videoPlaylistPosition;

    @ManyToOne
    @JoinColumn(name = "playlistId", nullable = false) // FK 선언하기
    private Playlist playlist;


    public Music(String videoId, String videoTitle, String playlist) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
//        this.playlist = playlist;
    }

    public Music(String videoId, String videoTitle, String videoUploader, String videoDescription, String videoTags,
                 int videoPlaylistPosition, Playlist playlist) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoUploader = videoUploader;
        this.videoDescription = videoDescription;
        this.videoTags = videoTags;
        this.videoPlaylistPosition = videoPlaylistPosition;
        this.playlist = playlist;
    }
}
