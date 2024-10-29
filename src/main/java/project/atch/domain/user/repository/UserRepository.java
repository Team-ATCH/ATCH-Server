package project.atch.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.atch.domain.user.dto.UserDetailDto;
import project.atch.domain.user.entity.OAuthProvider;
import project.atch.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.oAuthProvider = :oAuthProvider")
    Optional<User> findByEmailAndOAuthProvider(@Param("email") String email, @Param("oAuthProvider") OAuthProvider oAuthProvider);


    @Query("SELECT new project.atch.domain.user.dto.UserDetailDto(u, u.character, i1, i2, i3) " +
            "FROM User u " +
            "LEFT JOIN Item i1 ON u.itemId1 = i1.id " +
            "LEFT JOIN Item i2 ON u.itemId2 = i2.id " +
            "LEFT JOIN Item i3 ON u.itemId3 = i3.id " +
            "WHERE u.locationPermission = true " +
            "AND u.latitude BETWEEN :latMin AND :latMax " +
            "AND u.longitude BETWEEN :lonMin AND :lonMax")
    List<UserDetailDto> findUsersWithItems(@Param("latMin") double latMin,
                                           @Param("latMax") double latMax,
                                           @Param("lonMin") double lonMin,
                                           @Param("lonMax") double lonMax);
}
