package project.atch.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import project.atch.domain.user.entity.Item;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.entity.UserItem;
import project.atch.domain.user.entity.UserItemId;


import java.util.Optional;

@Repository
public interface UserItemRepository extends JpaRepository<UserItem, UserItemId> {
    Optional<UserItem> findByUserAndItem(User user, Item item);

    Optional<UserItem> findByUserAndUsed(User user, boolean used);
    @Query("SELECT ui FROM UserItem ui WHERE ui.user.id = :userId AND ui.item.id = :itemId")
    Optional<UserItem> findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);


}
