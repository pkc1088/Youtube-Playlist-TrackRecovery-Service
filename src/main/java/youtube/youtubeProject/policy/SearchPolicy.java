package youtube.youtubeProject.policy;
import youtube.youtubeProject.domain.Music;

public interface SearchPolicy {
    String search(Music musicToSearch);
}
