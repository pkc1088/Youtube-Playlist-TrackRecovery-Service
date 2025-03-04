package youtube.youtubeProject.service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class YoutubeService {
    //@Value 어노테이션을 사용하여 application.yml에서 정의한 YouTube API 키를 주입 받음
    @Value("${youtube.api.key}")
    private static String apiKey;

    private static YouTube youtube;

    public String searchChannelIdThruHandler() throws IOException {
        // API 클라이언트 설정
        String resultId = "";
        JsonFactory jsonFactory = new GsonFactory();
        youtube = new YouTube.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                jsonFactory, request -> {}).build();

        // 유저 이름으로 채널    정보를 조회
        YouTube.Channels.List request = youtube.channels().list(Collections.singletonList("snippet"));
        request.setForUsername("pkc1088");  // 여기서 핸들러 ID를 사용합니다.
        request.setKey(apiKey);  // API Key를 설정합니다.

        ChannelListResponse response = request.execute();

        // 채널 정보 출력
        List<Channel> channels = response.getItems();
        if (channels != null && !channels.isEmpty()) {
            Channel channel = channels.get(0);
            String channelId = channel.getId();  // 채널 ID 추출
            resultId = channelId;
            System.out.println("Channel ID: " + channelId);
        } else {
            System.out.println("Channel not found.");
        }
        return resultId;
    }



    public String searchPlaylist() throws IOException {
        //fields=items(id,snippet(title,position))
        JsonFactory jsonFactory = new GsonFactory();
        youtube = new YouTube.Builder(new com.google.api.client.http.javanet.NetHttpTransport()
                                                        , jsonFactory, request -> {}).build();
        String channelId = "UC6SN0-0k6z1fj5LmhYHd5UA";
        return getPlaylistDetails(channelId);
    }
    public static String getPlaylistDetails(String channelId) throws IOException {
        // 1. 사용자의 플레이리스트 목록 가져오기
        YouTube.Playlists.List playlistRequest = youtube.playlists()
                                .list(Collections.singletonList("snippet, contentDetails"));
        playlistRequest.setKey(apiKey);
        playlistRequest.setChannelId(channelId);
        playlistRequest.setMaxResults(30L);  // 가져올 최대 개수 (조정 가능)
        PlaylistListResponse playlistResponse = playlistRequest.execute();

        List<Playlist> playlists = playlistResponse.getItems();
        StringBuilder result = new StringBuilder();
        if (playlists != null && !playlists.isEmpty()) {
            for (Playlist playlist : playlists) {
                String playlistId = playlist.getId();
                String playlistTitle = playlist.getSnippet().getTitle();
                result.append(playlistId).append("\n");
                result.append(playlistTitle).append("\n");
                System.out.println("Playlist: " + playlistTitle);
                // 2. 플레이리스트 내 영상 제목 가져오기
                result.append(getPlaylistItems(playlistId)).append("\n");
            }
        } else {
            System.out.println("플레이리스트를 찾을 수 없습니다.");
        }
        return result.toString();
    }
    public static String getPlaylistItems(String playlistId) throws IOException {
        // 2. 플레이리스트 내 영상 목록 가져오기
        StringBuilder contentsList = new StringBuilder();
        YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems().list(Collections.singletonList("snippet"));
        playlistItemsRequest.setPlaylistId(playlistId);
        playlistItemsRequest.setKey(apiKey);
        playlistItemsRequest.setMaxResults(10L);  // 가져올 최대 개수 (조정 가능)

        PlaylistItemListResponse playlistItemsResponse = playlistItemsRequest.execute();
        List<PlaylistItem> playlistItems = playlistItemsResponse.getItems();

        if (playlistItems != null && !playlistItems.isEmpty()) {
            for (PlaylistItem item : playlistItems) {
                String videoId = item.getSnippet().getResourceId().getVideoId();
                String videoTitle = item.getSnippet().getTitle();
                System.out.println("  - Video: " + videoTitle + " (ID: " + videoId + ")");
                contentsList.append(videoId).append(" ").append(videoTitle).append("\n");
            }
        } else {
            System.out.println("해당 플레이리스트에 영상이 없습니다.");
        }
        return contentsList.toString();
    }



    public String searchVideo(String query) throws IOException {
        // JSON 데이터를 처리하기 위한 JsonFactory 객체 생성
        // JsonFactory jsonFactory = new JacksonFactory();
        JsonFactory jsonFactory = new GsonFactory();
        // YouTube 객체를 빌드하여 API에 접근할 수 있는 YouTube 클라이언트 생성
        youtube = new YouTube.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                jsonFactory, request -> {}).build();
        // YouTube Search API를 사용하여 동영상 검색을 위한 요청 객체 생성
        YouTube.Search.List search = youtube.search().list(Collections.singletonList("id, snippet"));

        // API 키 설정
        search.setKey(apiKey);
        // 검색어 설정
        search.setQ(query);
        // 검색 요청 실행 및 응답 받아오기
        SearchListResponse searchResponse = search.execute();
        // 검색 결과에서 동영상 목록 가져오기
        List<SearchResult> searchResultList = searchResponse.getItems();
        if (searchResultList != null && searchResultList.size() > 0) {
            //검색 결과 중 첫 번째 동영상 정보 가져오기
            SearchResult searchResult = searchResultList.get(0);
            // 동영상의 ID와 제목 가져오기
            String videoId = searchResult.getId().getVideoId();
            String videoTitle = searchResult.getSnippet().getTitle();
            return "Title: " + videoTitle + "\nURL: https://www.youtube.com/watch?v=" + videoId;
        }
        return "검색 결과가 없습니다";
    }
}