package youtube.youtubeProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import youtube.youtubeProject.config.SecurityConfig;
import youtube.youtubeProject.config.SpringDataJpaConfig;

//@Import({SpringDataJpaConfig.class}) //, SecurityConfig.class

/*
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {UserServiceV1.class} // UserServiceV1 클래스를 제외
    )
)
테스트 해보기
 */
@EnableScheduling
@SpringBootApplication(scanBasePackages = "youtube.youtubeProject") //.controller
public class YoutubeProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoutubeProjectApplication.class, args);
	}

}
