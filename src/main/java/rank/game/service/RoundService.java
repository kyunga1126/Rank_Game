package rank.game.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rank.game.entity.Game;
import rank.game.entity.GameWinRecord;
import rank.game.entity.Round;
import rank.game.repository.GameRepository;
import rank.game.repository.GameWinRecordRepository;
import rank.game.repository.RoundRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoundService {

    private static final Logger logger = LoggerFactory.getLogger(RoundService.class);

    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GameWinRecordRepository gameWinRecordRepository;

    public RoundService(GameRepository gameRepository, RoundRepository roundRepository, GameWinRecordRepository gameWinRecordRepository) {
        this.gameRepository = gameRepository;
        this.roundRepository = roundRepository;
        this.gameWinRecordRepository = gameWinRecordRepository;
    }

    @Transactional
    public List<Round> initializeRound(String roundName) {
        if ("결승".equals(roundName)) {
            return initializeFinalRound();
        } else {
            int gameCount = getGameCountForRound(roundName);
            return initializeRound(roundName, gameCount);
        }
    }

    @Transactional
    public List<Round> initializeRound(String roundName, List<Long> winnerIds) {
        logger.info("Initializing round: {}, with winners: {}", roundName, winnerIds);

        roundRepository.deleteByRoundName(roundName);

        List<Game> winners = gameRepository.findAllById(winnerIds);

        if (winners.size() < 2) {
            throw new IllegalArgumentException("Insufficient winners available for the next round.");
        }

        Collections.shuffle(winners);

        List<Round> rounds = new ArrayList<>();
        for (int i = 0; i < winners.size() / 2; i++) {
            Game game1 = winners.get(i * 2);
            Game game2 = winners.get(i * 2 + 1);

            logger.debug("Creating round: {}, Game1: {}, Game2: {}", roundName, game1.getGameName(), game2.getGameName());

            Round round = new Round(roundName, game1, game2);
            rounds.add(round);
        }

        roundRepository.saveAll(rounds);
        logger.info("Successfully saved {} rounds for {}", rounds.size(), roundName);

        return rounds;
    }

    private int getGameCountForRound(String roundName) {
        switch (roundName) {
            case "32강":
                return 16;
            case "16강":
                return 8;
            case "8강":
                return 4;
            case "4강":
                return 2;
            case "결승":
                return 1;
            default:
                throw new IllegalArgumentException("Invalid round name");
        }
    }

    @Transactional
    public List<Round> initializeRound(String roundName, int gameCount) {
        logger.info("Initializing round: {}, gameCount: {}", roundName, gameCount);

        roundRepository.deleteByRoundName(roundName);

        List<Game> games = gameRepository.findAll();

        if (games.size() < gameCount * 2) {
            throw new IllegalArgumentException("Insufficient games available.");
        }

        Collections.shuffle(games);

        List<Round> rounds = new ArrayList<>();
        for (int i = 0; i < gameCount; i++) {
            Game game1 = games.get(i * 2);
            Game game2 = games.get(i * 2 + 1);

            logger.debug("Creating round: {}, Game1: {}, Game2: {}", roundName, game1.getGameName(), game2.getGameName());

            Round round = new Round(roundName, game1, game2);
            rounds.add(round);
        }

        roundRepository.saveAll(rounds);
        logger.info("Successfully saved {} rounds for {}", rounds.size(), roundName);

        return rounds;
    }

    @Transactional
    public List<Round> initializeFinalRound() {
        logger.info("Initializing final round");

        roundRepository.deleteByRoundName("결승");

        List<Round> semiFinalRounds = roundRepository.findByRoundName("4강");
        if (semiFinalRounds.size() != 2) {
            throw new IllegalStateException("4강 라운드가 정확히 2개의 매치로 구성되어 있어야 합니다.");
        }

        Game finalist1 = semiFinalRounds.get(0).getWinner();
        Game finalist2 = semiFinalRounds.get(1).getWinner();

        if (finalist1 == null || finalist2 == null) {
            throw new IllegalStateException("4강 라운드의 승자가 결정되지 않았습니다.");
        }

        Round finalRound = new Round("결승", finalist1, finalist2);
        roundRepository.save(finalRound);

        logger.info("Successfully initialized final round");

        return Collections.singletonList(finalRound);
    }

    @Transactional
    public void voteForGame(Long roundId, Long gameId) {
        Round round = roundRepository.findById(roundId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid round ID"));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game ID"));

        if (round.getWinner() != null) {
            throw new IllegalStateException("Voting for this round has already been completed.");
        }

        round.setWinner(game);
        round.setWinCount(round.getWinCount() + 1);

        if ("결승".equals(round.getRoundName())) {
            GameWinRecord gameWinRecord = gameWinRecordRepository.findByGame(game)
                    .orElseGet(() -> new GameWinRecord(game));
            gameWinRecord.incrementWins();
            gameWinRecordRepository.save(gameWinRecord);
        }

        roundRepository.save(round);
        logger.info("Vote recorded for game: {} in round: {}", game.getGameName(), round.getRoundName());
    }

    @Transactional(readOnly = true)
    public List<Round> getRoundsByRoundName(String roundName) {
        List<Round> rounds = roundRepository.findByRoundName(roundName);
        logger.info("Retrieved {} rounds for roundName: {}", rounds.size(), roundName);
        return rounds;
    }

    @Transactional(readOnly = true)
    public List<Round> getRoundsWithResults() {
        List<Round> rounds = roundRepository.findAll();
        return rounds.stream()
                .filter(round -> round.getWinner() != null)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GameWinRate> getFinalWinRates() {
        List<Game> allGames = gameRepository.findAll();
        List<GameWinRate> winRates = allGames.stream()
                .map(game -> {
                    GameWinRecord record = gameWinRecordRepository.findByGame(game).orElse(null);
                    int winCount = (record != null) ? record.getWinCount() : 0;
                    double winRate = calculateFinalWinRate(winCount);
                    return new GameWinRate(game.getGameName(), game.getImageUrl(), winCount, winRate);
                })
                .sorted((g1, g2) -> Integer.compare(g2.getWinCount(), g1.getWinCount()))
                .collect(Collectors.toList());

        return winRates;
    }

    private double calculateFinalWinRate(int winCount) {
        int totalFinals = roundRepository.countByRoundName("결승");
        return (totalFinals > 0) ? (double) winCount / totalFinals * 100 : 0;
    }

    public static class GameWinRate {
        private final String gameName;
        private final String imageUrl;
        private final int winCount;
        private final double winRate;

        public GameWinRate(String gameName, String imageUrl, int winCount, double winRate) {
            this.gameName = gameName;
            this.imageUrl = imageUrl;
            this.winCount = winCount;
            this.winRate = winRate;
        }

        public String getGameName() {
            return gameName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public int getWinCount() {
            return winCount;
        }

        public double getWinRate() {
            return winRate;
        }
    }
}
