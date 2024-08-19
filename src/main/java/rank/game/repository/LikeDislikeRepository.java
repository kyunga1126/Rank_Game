package rank.game.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rank.game.entity.BoardEntity;
import rank.game.entity.LikeDislikeEntity;
import rank.game.entity.MemberEntity;

import java.util.Optional;

@Repository
public interface LikeDislikeRepository extends JpaRepository<LikeDislikeEntity, Long> {
//    Optional<LikeDislikeEntity> findByBoardAndMemberAndIsLike(BoardEntity board, MemberEntity member, boolean isLike);

    @Query("SELECT ld FROM LikeDislikeEntity ld WHERE ld.board = :board AND ld.member = :member AND ld.isLike = :isLike")
    Optional<LikeDislikeEntity> findByBoardAndMemberAndIsLike(@Param("board") BoardEntity board, @Param("member") MemberEntity member, @Param("isLike") boolean isLike);
}