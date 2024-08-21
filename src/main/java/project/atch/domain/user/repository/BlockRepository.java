package project.atch.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.atch.domain.user.entity.Block;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    @Query("SELECT b.blockedId FROM Block b WHERE b.blockerId = :blockerId")
    List<Long> findBlockedIdsByBlockerId(@Param("blockerId") Long blockerId);
}
