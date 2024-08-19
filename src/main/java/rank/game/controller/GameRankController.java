package rank.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import rank.game.dto.GameHistoryDTO;
import rank.game.entity.Game;
import rank.game.entity.GameHistory;
import rank.game.service.GameRankService;

import java.time.LocalDateTime;
import java.util.List;
@Controller
public class GameRankController {

    @Autowired
    private GameRankService gameService;

    @GetMapping("/review")
    public String getReviewPage(Model model) {
        List<GameHistory> top10Games = gameService.getTop10GamesByPeriod(LocalDateTime.now().minusDays(7), LocalDateTime.now());
        List<Game> games = gameService.getAllGames();

        model.addAttribute("top10Games", top10Games);
        model.addAttribute("games", games);

        return "html/review";
    }

    @GetMapping("/game/{id}/vote-trend")
    @ResponseBody
    public List<GameHistory> getVoteTrend(@PathVariable Long id) {
        return gameService.getVoteTrend(id);
    }

    @GetMapping("/api/games/top10")
    @ResponseBody
    public ResponseEntity<List<GameHistory>> getTop10Games(@RequestParam(required = false) String period) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start;

        if ("daily".equalsIgnoreCase(period)) {
            start = end.minusDays(1);
        } else if ("weekly".equalsIgnoreCase(period)) {
            start = end.minusWeeks(1);
        } else if ("monthly".equalsIgnoreCase(period)) {
            start = end.minusMonths(1);
        } else {
            start = end.minusDays(7);  // 기본적으로 7일 동안의 데이터를 가져옵니다.
        }

        List<GameHistory> top10Games = gameService.getTop10GamesByPeriod(start, end);
        return ResponseEntity.ok(top10Games);
    }

    @GetMapping("/api/games/daily-rank-changes")
    @ResponseBody
    public ResponseEntity<List<GameHistoryDTO>> getDailyRankChanges() {
        List<GameHistoryDTO> dailyRankChanges = gameService.getDailyRankChanges();
        return ResponseEntity.ok(dailyRankChanges);
    }
}
