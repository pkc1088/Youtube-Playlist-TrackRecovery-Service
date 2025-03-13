package youtube.youtubeProject.repository.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import youtube.youtubeProject.domain.Music;
import youtube.youtubeProject.domain.Users;
import youtube.youtubeProject.repository.user.SdjUserRepository;
import youtube.youtubeProject.repository.user.UserRepository;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class UserRepositoryV1 implements UserRepository {

    private final SdjUserRepository repository;

    public Users findByUserEmail(String email) {
        return repository.findByUserEmail(email);
    }

    public Users findByAccessToken(String email) {
        return repository.findByAccessToken(email);
    }

    @Override
    public void updateAccessTokenByRefreshToken(String refreshToken, String accessToken) {
        Optional<Users> optionalUsers = Optional.ofNullable(repository.findByAccessToken(accessToken));
        if(optionalUsers.isPresent()) {
            Users user = optionalUsers.get();
            user.setAccessToken(accessToken);
            //user.setRefreshToken(refreshToken);
        }
    }

    public void saveUser(Users user) {
        repository.save(user);
    }

    @Override
    public void updateRefreshTokenByLogin(String email, String refreshToken) {
        Optional<Users> optionalUsers = Optional.ofNullable(repository.findByUserEmail(email));
        if(optionalUsers.isPresent()) {
            Users user = optionalUsers.get();
            user.setRefreshToken(refreshToken);
        }
    }


//    @Override
//    public void saveTokens(User user) {
//        // 여기서 DB에 저장?
//        System.out.println("------------- user info -------------");
//        System.out.println(user.getUserId());
//        System.out.println(user.getAccessToken());
//        System.out.println(user.getUserEmail());
//        System.out.println(user.getUserName());
//    }

}
