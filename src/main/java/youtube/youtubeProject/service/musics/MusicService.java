package youtube.youtubeProject.service.musics;

import java.io.IOException;

public interface MusicService {

    public void initiallyAddVideoDetails(String playlistId) throws IOException;

    public void updatePlaylist(String playlistId) throws IOException;
}
