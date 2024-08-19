package rank.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rank.game.dto.GameHistoryDTO;
import rank.game.entity.Game;
import rank.game.entity.GameHistory;
import rank.game.repository.GameHistoryRepository;
import rank.game.repository.GameRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GameRankService {

    @Autowired
    private GameHistoryRepository gameHistoryRepository;
    @Autowired
    private GameRepository gameRepository;

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public List<GameHistory> getVoteTrend(Long gameId) {
        return gameHistoryRepository.findByGameIdOrderByVoteTimeAsc(gameId);
    }

    public List<GameHistory> getTop10GamesByPeriod(LocalDateTime start, LocalDateTime end) {
        return gameHistoryRepository.findTop10ByVoteTimeBetweenOrderByGameVoteDesc(start, end);
    }

    public List<GameHistoryDTO> getDailyRankChanges() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(1);

        // 오늘과 어제의 데이터를 각각 가져옴
        List<GameHistory> todayGames = gameHistoryRepository.findByVoteTimeBetween(start, end);
        List<GameHistory> yesterdayGames = gameHistoryRepository.findByVoteTimeBetween(start.minusDays(1), start);

        // 어제의 게임 데이터를 게임 ID로 매핑
        Map<Long, GameHistory> yesterdayGameMap = yesterdayGames.stream()
                .collect(Collectors.toMap(gameHistory -> gameHistory.getGame().getId(), gameHistory -> gameHistory));

        List<GameHistoryDTO> gameHistoryDTOList = new ArrayList<>();

        for (GameHistory todayGame : todayGames) {
            Game game = todayGame.getGame();

            GameHistoryDTO dto = new GameHistoryDTO(
                    todayGame.getId(),
                    game.getId(),
                    game.getGameName(),
                    todayGame.getGameVote(),
                    todayGame.getGameRank(),
                    todayGame.getVoteTime(),
                    0
            );

            GameHistory yesterdayGame = yesterdayGameMap.get(game.getId());
            if (yesterdayGame != null) {
                int rankChange = yesterdayGame.getGameRank() - todayGame.getGameRank();
                dto.setRankChange(rankChange);
            } else {

                dto.setRankChange(todayGames.size());
            }

            gameHistoryDTOList.add(dto);
        }

        return gameHistoryDTOList.stream()
                .sorted((g1, g2) -> g1.getGameRank() - g2.getGameRank()) // 현재 순위 기준으로 정렬
                .limit(10)
                .collect(Collectors.toList());
    }
}