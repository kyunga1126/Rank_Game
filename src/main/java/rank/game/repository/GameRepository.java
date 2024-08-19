package rank.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rank.game.entity.Game;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByGameName(String gameName);
}
