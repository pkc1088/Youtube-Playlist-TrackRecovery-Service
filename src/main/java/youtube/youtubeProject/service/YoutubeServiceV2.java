package youtube.youtubeProject.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
public class YoutubeServiceV2 {

    private static YouTube youtube;

    @Value("${youtube.api.key}")
    private String apiKey; // API Key는 application.properties에 설정할 것입니다.

    public YoutubeServiceV2() {
        youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {}).setApplicationName("youtube").build();
    }

    public List<Playlist> getPlaylistsByChannelId(String channelId) throws IOException {
        YouTube.Playlists.List request = youtube.playlists().list(Collections.singletonList("snippet,contentDetails"));
        request.setKey(apiKey);
        request.setChannelId(channelId);
        request.setMaxResults(50L); // 최대 50개의 플레이리스트 가져오기

        PlaylistListResponse response = request.execute();
        return response.getItems();
    }

    public List<Video> getVideosFromPlaylist(String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems().list(Collections.singletonList("snippet,contentDetails"));
        request.setKey(apiKey);
        request.setPlaylistId(playlistId);
        request.setMaxResults(50L); // 최대 50개의 비디오 가져오기

        PlaylistItemListResponse response = request.execute();
        List<Video> videos = new ArrayList<>();

        for (PlaylistItem item : response.getItems()) {
            String videoId = item.getSnippet().getResourceId().getVideoId();
            videos.add(getVideoDetails(videoId));
        }
        return videos;
    }

    private Video getVideoDetails(String videoId) throws IOException {
        YouTube.Videos.List request = youtube.videos().list(Collections.singletonList("snippet,contentDetails,statistics"));
        request.setKey(apiKey);
        request.setId(Collections.singletonList(videoId));

        VideoListResponse response = request.execute();
        return response.getItems().get(0);
    }
}
