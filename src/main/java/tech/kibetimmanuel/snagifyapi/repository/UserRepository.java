package tech.kibetimmanuel.snagifyapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.kibetimmanuel.snagifyapi.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
}
