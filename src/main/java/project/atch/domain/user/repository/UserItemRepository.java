package project.atch.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import project.atch.domain.user.dto.ItemDetail;
import project.atch.domain.user.entity.Item;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.entity.UserItem;
import project.atch.domain.user.entity.UserItemId;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface UserItemRepository extends JpaRepository<UserItem, UserItemId> {
    Optional<UserItem> findByUserAndItem(User user, Item item);

    boolean existsByUserAndItem(User user, Item item);

    void deleteByUserId(Long userId);

    @Query("SELECT ui FROM UserItem ui WHERE ui.user.id = :userId AND ui.item.id = :itemId")
    Optional<UserItem> findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Query("SELECT CASE WHEN COUNT(ui) > 0 THEN true ELSE false END FROM UserItem ui WHERE ui.user.id = :userId AND ui.item.id = :itemId")
    boolean existsByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);


    @Query("SELECT new project.atch.domain.user.dto.ItemDetail(i.id, i.image) " +
            "FROM UserItem ui JOIN ui.item i WHERE ui.user.id = :userId AND i.category = :category")
    List<ItemDetail> findItemIdsAndImagesByUserId(@Param("userId") Long userId, @Param("category")String category);
}
