package youtube.youtubeProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import youtube.youtubeProject.config.SecurityConfig;
import youtube.youtubeProject.config.SpringDataJpaConfig;

//@Import({SpringDataJpaConfig.class}) //, SecurityConfig.class
@SpringBootApplication(scanBasePackages = "youtube.youtubeProject") //.controller
public class YoutubeProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoutubeProjectApplication.class, args);
	}

}
