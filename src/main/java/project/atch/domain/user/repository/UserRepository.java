package project.atch.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.atch.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAllByLocationPermissionTrue();
}
