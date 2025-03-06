package youtube.youtubeProject.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class YoutubeServiceV4 {

    public String addVideoToPlaylist(OAuth2AuthorizedClient authorizedClient, String playlistId, String videoId) {
        try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(authorizedClient.getAccessToken().getTokenValue());
            YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("youtube-cmdline-addto-playlist-sample")
                    .build();

            ResourceId resourceId = new ResourceId();
            resourceId.setKind("youtube#video");
            resourceId.setVideoId(videoId);

            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
            playlistItemSnippet.setPlaylistId(playlistId);
            playlistItemSnippet.setResourceId(resourceId);

            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setSnippet(playlistItemSnippet);

            YouTube.PlaylistItems.Insert request = youtube.playlistItems().insert(Collections.singletonList("snippet"), playlistItem);
            PlaylistItem response = request.execute();

            return "Video added to playlist: " + response.getId();
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to add video to playlist";
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}