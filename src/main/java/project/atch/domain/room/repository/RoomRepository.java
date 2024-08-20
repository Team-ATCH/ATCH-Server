package project.atch.domain.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.atch.domain.room.entity.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByFromIdAndToId(Long fromId, Long toId);
    List<Room> findByFromIdOrToId(Long fromId, Long toId);

    @Query("SELECT r FROM Room r WHERE (r.fromId = :userId OR r.toId = :userId) " +
            "AND r.fromId NOT IN :blockedIds AND r.toId NOT IN :blockedIds")
    List<Room> findFilteredRooms(@Param("userId") Long userId, @Param("blockedIds") List<Long> blockedIds);

    @Query("SELECT DISTINCT r.id FROM Room r WHERE r.fromId IN :ids OR r.toId IN :ids")
    List<Long> findRoomIdsByUserIds(@Param("ids") List<Long> ids);

}
