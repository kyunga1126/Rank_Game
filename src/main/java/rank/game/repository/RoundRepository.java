package rank.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rank.game.entity.Game;
import rank.game.entity.Round;

import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Long> {

    List<Round> findByRoundName(String roundName);

    void deleteByRoundName(String roundName);

    int countByWinnerAndRoundName(Game winner, String roundName);

    int countByRoundName(String roundName);
}
