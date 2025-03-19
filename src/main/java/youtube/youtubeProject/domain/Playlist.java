package youtube.youtubeProject.domain;

import com.google.api.services.youtube.model.PlaylistItem;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Entity
//@RequiredArgsConstructor ??
public class Playlist {

    @Id
    private String playlistId;
    private String playlistTitle;
    private String serviceType;


//    @ManyToOne
//    @JoinColumn
//    private Users user;
//
//    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Music> playlistItems;
    // 해당 플레이리스트의 음악 항목들
    // 방향성과 pk/fk는 별개다 gpt 참고

    public Playlist() {}

    public Playlist(String playlistId, String playlistTitle, String serviceType, Users user) {
        this.playlistId = playlistId;
        this.playlistTitle = playlistTitle;
        this.serviceType = serviceType;
//        this.user = user;
    }
}