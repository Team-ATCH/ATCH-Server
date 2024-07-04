package project.atch.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.atch.domain.user.entity.Character;


public interface CharacterRepository extends JpaRepository<Character, Long> {
}
