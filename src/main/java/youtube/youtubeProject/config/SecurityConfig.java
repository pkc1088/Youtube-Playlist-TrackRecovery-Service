package youtube.youtubeProject.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends SimpleUrlAuthenticationSuccessHandler { // ADDED : extends Simple~

    private final AuthenticationSuccessHandler oauth2LoginSuccessHandler; // ADDED

    public SecurityConfig(AuthenticationSuccessHandler oauth2LoginSuccessHandler) {  // ADDED
        this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll()       // 로그인되지 않은 사용자라면 /login으로 리다이렉트
                        .anyRequest().authenticated()                        // login 외 모든 요청은 인증 필요
                )
                .formLogin(form -> form
                        .loginPage("/login")                                 // 커스텀 로그인 페이지 설정
                        .permitAll()                                         // 로그인 페이지는 누구나 접근 가능
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 루트 페이지로 리다이렉트
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")                                 // OAuth2 로그인도 커스텀 로그인 페이지 사용
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 루트 페이지로 리다이렉트
                        .successHandler(oauth2LoginSuccessHandler) // Added : 커스텀 핸들러 등록
                );

        return http.build();
    }


//    @Bean
//    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(
//            ClientRegistrationRepository clientRegistrationRepository) {
//        return new DefaultOAuth2AuthorizationRequestResolver(
//                clientRegistrationRepository,
//                "/oauth2/authorization"
//        ) {
//            @Override
//            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
//                OAuth2AuthorizationRequest authorizationRequest = super.resolve(request);
//                if (authorizationRequest != null) {
//                    // 추가 파라미터 설정
//                    return OAuth2AuthorizationRequest.from(authorizationRequest)
//                            .additionalParameters(params -> {
//                                params.put("access_type", "offline");
//                                params.put("prompt", "consent");
//                            })
//                            .build();
//                }
//                return authorizationRequest;
//            }
//        };
//    }



}
/*
    .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/", "/login", "/welcome").permitAll() // 루트, 로그인 페이지, welcome 페이지는 인증 없이 접근 허용
            .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
    )
    .oauth2Login(oauth2 -> oauth2
            .defaultSuccessUrl("/", true) // 로그인 성공 시 루트 페이지로 리다이렉트
    )
    .formLogin(form -> form.disable() // 폼 로그인 비활성화
    );
*/
/*
    .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
    .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/addVideoToPlaylist", true));
*/
/*
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/", "/css/**", "/images/**",
                        "/js/**", "/h2-console/**").permitAll()
                .antMatchers("/api/v1/**").hasRole(Role.
                        USER.name())
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        return http.build();
    }


}*/