package youtube.youtubeProject.policy.simple;

import youtube.youtubeProject.domain.Music;
import youtube.youtubeProject.policy.SearchPolicy;

public class SimpleSearchPolicy implements SearchPolicy {

    public String search(Music musicToSearch) {
        return musicToSearch.getVideoTitle().concat("-").concat(musicToSearch.getVideoUploader());
    }
}
