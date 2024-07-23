package project.atch.domain.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.atch.domain.room.entity.Room;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByFromIdAndToId(Long fromId, Long toId);
}
