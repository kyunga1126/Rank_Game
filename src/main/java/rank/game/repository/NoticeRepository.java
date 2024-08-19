package rank.game.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rank.game.entity.BoardEntity;
import rank.game.entity.NoticeEntity;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {

    Page<NoticeEntity> findByTitleContaining(String searchKeyword, Pageable pageable);

    // 이전글 찾기
    @Query("SELECT b FROM NoticeEntity b WHERE b.createdAt < (SELECT b2.createdAt FROM NoticeEntity b2 WHERE b2.id = :id) ORDER BY b.createdAt DESC")
    List<NoticeEntity> findPreviousPosts(@Param("id") Long id, Pageable pageable);

    // 다음글 찾기
    @Query("SELECT b FROM NoticeEntity b WHERE b.createdAt > (SELECT b2.createdAt FROM NoticeEntity b2 WHERE b2.id = :id) ORDER BY b.createdAt ASC")
    List<NoticeEntity> findNextPosts(@Param("id") Long id, Pageable pageable);

    // 최신 공지사항 4개를 가져오는 메서드
    List<NoticeEntity> findTop4ByOrderByCreatedAtDesc();

}
