package rank.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rank.game.entity.Game;
import rank.game.entity.GameWinRecord;

import java.util.Optional;

public interface GameWinRecordRepository extends JpaRepository<GameWinRecord, Long> {
    Optional<GameWinRecord> findByGame(Game game);
}
