package rank.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rank.game.entity.GameHistory;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    List<GameHistory> findByGameIdOrderByVoteTimeAsc(Long gameId);

    List<GameHistory> findTop10ByVoteTimeBetweenOrderByGameVoteDesc(LocalDateTime start, LocalDateTime end);

    List<GameHistory> findByVoteTimeBetweenOrderByGameVoteDesc(LocalDateTime start, LocalDateTime end);

    List<GameHistory> findByVoteTimeBetween(LocalDateTime start, LocalDateTime end);

    // 새롭게 추가된 메서드
    List<GameHistory> findByVoteTimeBetweenAndGameId(LocalDateTime start, LocalDateTime end, Long gameId);

}
