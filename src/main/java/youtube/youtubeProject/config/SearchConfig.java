package youtube.youtubeProject.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import youtube.youtubeProject.policy.SearchPolicy;
import youtube.youtubeProject.policy.gemini.GeminiSearchPolicy;
import youtube.youtubeProject.policy.simple.SimpleSearchPolicy;

@Configuration
public class SearchConfig {

    @Bean
    @Qualifier("simpleSearchQuery")
    public SearchPolicy simpleSearchQuery() {
        return new SimpleSearchPolicy();
    }

    @Bean
    @Qualifier("geminiSearchQuery")
    public SearchPolicy geminiSearchQuery(RestClient restClient) {
        return new GeminiSearchPolicy(restClient);
    }

    @Bean
    public RestClient geminiRestClient(@Value("${gemini.baseurl}") String baseUrl, @Value("${googleai.api.key}") String apiKey) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}