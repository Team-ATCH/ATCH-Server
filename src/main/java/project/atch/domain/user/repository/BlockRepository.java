package project.atch.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.atch.domain.user.entity.Block;

public interface BlockRepository extends JpaRepository<Block, Long> {
}
