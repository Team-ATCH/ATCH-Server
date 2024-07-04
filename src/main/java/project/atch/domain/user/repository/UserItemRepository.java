package project.atch.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import project.atch.domain.user.entity.Item;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.entity.UserItem;
import project.atch.domain.user.entity.UserItemId;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface UserItemRepository extends JpaRepository<UserItem, UserItemId> {
    Optional<UserItem> findByUserAndItem(User user, Item item);

    Optional<UserItem> findByUserAndUsed(User user, boolean used);
    @Query("SELECT ui FROM UserItem ui WHERE ui.user.id = :userId AND ui.item.id = :itemId")
    Optional<UserItem> findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Query("SELECT i.id, i.image FROM UserItem ui JOIN ui.item i WHERE ui.user.id = :userId")
    List<Object[]> findItemIdsAndImagesByUserId(@Param("userId") Long userId);
}
