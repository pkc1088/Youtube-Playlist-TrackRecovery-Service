package youtube.youtubeProject.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoId;
    private String videoTitle;
    private String videoUploader;
    private String videoDescription;
    private String videoTags; //    @ElementCollection private List<String> videoTags;

    private String videoPlaylistId; // FK 선언하기
    private int videoPlaylistPosition;

    private String userId; // 얘는 없어도 될 듯? 어차피 음악(여러명이 공유함)은 결국 FK인 플레이리스트에 의존적임

    public Music() {
    }

    public Music(String videoId, String videoTitle, String videoPlaylistId) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoPlaylistId = videoPlaylistId;
    }

    public Music(String videoId, String videoTitle, String videoUploader, String videoDescription, String videoTags,
                 String videoPlaylistId, int videoPlaylistPosition,
                 String userId) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.videoUploader = videoUploader;
        this.videoDescription = videoDescription;
        this.videoTags = videoTags;

        this.videoPlaylistId = videoPlaylistId;
        this.videoPlaylistPosition = videoPlaylistPosition;

        this.userId = userId;
    }
}
