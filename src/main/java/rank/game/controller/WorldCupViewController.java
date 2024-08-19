package rank.game.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rank.game.entity.Round;
import rank.game.service.RoundService;

import java.util.List;

@Controller
@RequestMapping("/worldcup")
public class WorldCupViewController {

    private final RoundService roundService;

    public WorldCupViewController(RoundService roundService) {
        this.roundService = roundService;
    }

    @GetMapping
    public String showWorldCupPage(Model model, @RequestParam(defaultValue = "32") int roundSize) {
        model.addAttribute("roundSize", roundSize);
        return "html/worldcup";
    }

    @PostMapping("/initialize")
    public ResponseEntity<List<Round>> initializeRound(
            @RequestParam String roundName,
            @RequestParam(required = false) List<Long> winners) {
        try {
            List<Round> rounds;
            if (winners == null || winners.isEmpty()) {
                rounds = roundService.initializeRound(roundName);
            } else {
                rounds = roundService.initializeRound(roundName, winners);
            }
            if (rounds == null || rounds.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(rounds, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rounds/{roundName}")
    public ResponseEntity<List<Round>> getRoundsByRoundName(@PathVariable String roundName) {
        try {
            List<Round> rounds = roundService.getRoundsByRoundName(roundName);
            if (rounds.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(rounds, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/rounds/{roundId}/vote")
    public ResponseEntity<Void> voteForGame(@PathVariable Long roundId, @RequestParam Long winnerId) {
        try {
            roundService.voteForGame(roundId, winnerId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/results")
    public ResponseEntity<List<Round>> getResults() {
        try {
            List<Round> rounds = roundService.getRoundsWithResults();
            return new ResponseEntity<>(rounds, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/results/win-rates")
    public ResponseEntity<List<RoundService.GameWinRate>> getFinalWinRates() {
        try {
            List<RoundService.GameWinRate> winRates = roundService.getFinalWinRates();
            return new ResponseEntity<>(winRates, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
