package youtube.youtubeProject.domain;

import com.google.api.services.youtube.model.PlaylistItem;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

//@Data
//@AllArgsConstructor
@Entity
@Getter @Setter
@NoArgsConstructor
public class Playlist {

    @Id
    private String playlistId;
    private String playlistTitle;
    private String serviceType;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;


    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Music> playlistItems;


    public Playlist(String playlistId, String playlistTitle, String serviceType, Users user) {
        this.playlistId = playlistId;
        this.playlistTitle = playlistTitle;
        this.serviceType = serviceType;
        this.user = user;
    }
}