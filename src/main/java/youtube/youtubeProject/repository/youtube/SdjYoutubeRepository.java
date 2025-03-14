package youtube.youtubeProject.repository.youtube;

import org.springframework.data.jpa.repository.JpaRepository;
import youtube.youtubeProject.domain.Music;

import java.util.List;
import java.util.Optional;

public interface SdjYoutubeRepository extends JpaRepository<Music, Long> {

    List<Music> findByVideoPlaylistIdLike(String playlistId);

    Optional<Music> findByVideoId(String videoId);

    void deleteByVideoId(String videoId);

    /*
    //    여기에 기초적인 save 는 다 구현 되어 있음
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);
    위아래 동일한 동작임
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);
     */
}
