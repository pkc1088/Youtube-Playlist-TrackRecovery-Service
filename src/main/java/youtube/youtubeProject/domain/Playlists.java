package youtube.youtubeProject.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

//@Data
//@AllArgsConstructor
@Entity
@Getter @Setter
@NoArgsConstructor
public class Playlists {

    @Id
    private String playlistId;
    private String playlistTitle;
    private String serviceType;
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;


    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Music> playlistItems;


    public Playlists(String playlistId, String playlistTitle, String serviceType, Users user) {
        this.playlistId = playlistId;
        this.playlistTitle = playlistTitle;
        this.serviceType = serviceType;
        this.user = user;
    }
}