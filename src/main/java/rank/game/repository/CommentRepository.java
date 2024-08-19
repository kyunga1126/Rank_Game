package rank.game.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import rank.game.entity.BoardEntity;
import rank.game.entity.CommentEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByBoard(BoardEntity board);  // 수정된 메서드 시그니처

    void deleteByBoardId(Long boardId);
}