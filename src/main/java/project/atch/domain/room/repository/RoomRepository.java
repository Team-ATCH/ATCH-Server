package project.atch.domain.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.atch.domain.room.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
