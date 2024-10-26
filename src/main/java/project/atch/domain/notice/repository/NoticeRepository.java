package project.atch.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.atch.domain.notice.entity.Notice;
import project.atch.domain.user.entity.User;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByUserOrderByCreatedAtDesc(User user);
}
