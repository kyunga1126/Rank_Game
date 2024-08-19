package rank.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rank.game.entity.GameHistory;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    List<GameHistory> findByGameIdOrderByVoteTimeAsc(Long gameId);

    // 특정 기간 내 상위 10개의 게임을 투표 수에 따라 정렬해서 가져옴
    List<GameHistory> findTop10ByVoteTimeBetweenOrderByGameVoteDesc(LocalDateTime start, LocalDateTime end);

    // 주간/월간 데이터를 가져올 수 있도록 수정
    List<GameHistory> findByVoteTimeBetweenOrderByGameVoteDesc(LocalDateTime start, LocalDateTime end);

    List<GameHistory> findByVoteTimeBetween(LocalDateTime start, LocalDateTime end);
}