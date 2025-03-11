package youtube.youtubeProject.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import youtube.youtubeProject.domain.User;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class UserRepositoryV1 implements UserRepository{

    private final SdjUserRepository repository;

    public User findByUserEmail(String email) {
        return repository.findByUserEmail(email);
    }

    public void saveUser(User user) {
        repository.save(user);
    }
}
