package t1.springjwtt1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import t1.springjwtt1.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
