package project.atch.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.atch.domain.user.entity.Item;
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
