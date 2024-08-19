package rank.game.service;

import jakarta.persistence.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rank.game.dto.VoteDTO;
import rank.game.entity.Game;
import rank.game.entity.GameHistory;
import rank.game.entity.VoteEntity;
import rank.game.repository.GameHistoryRepository;
import rank.game.repository.GameRepository;
import rank.game.repository.VoteRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoteService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private GameHistoryRepository gameHistoryRepository;

    public List<VoteDTO> findAllVoteDTOs() {
        List<Game> games = gameRepository.findAll();
        return games.stream()
                .map(game -> new VoteDTO(game.getGameName(), game.getImageUrl()))
                .collect(Collectors.toList());
    }

    public void incrementVote(String gameName) {
        List<Game> games = gameRepository.findByGameName(gameName);
        if (games.size() == 1) {
            Game game = games.get(0);
            game.setGameVote(game.getGameVote() + 1);
            gameRepository.save(game);

            // 게임 히스토리 업데이트 또는 추가
            updateGameHistory(game);

        } else if (games.isEmpty()) {
            throw new RuntimeException("Game not found");
        } else {
            throw new NonUniqueResultException("Multiple games found with name: " + gameName);
        }
    }

    public void saveVotes(String nickname, List<String> gameNames) {
        LocalDate today = LocalDate.now();
        if (hasVotedToday(nickname)) {
            throw new RuntimeException("User has already voted today");
        }

        for (String gameName : gameNames) {
            VoteEntity vote = new VoteEntity();
            vote.setNickname(nickname);
            vote.setGameName(gameName);
            vote.setVoteTime(today);
            voteRepository.save(vote);

            incrementVote(gameName);
        }
    }

    public boolean hasVotedToday(String nickname) {
        LocalDate today = LocalDate.now();
        return voteRepository.existsByNicknameAndVoteTime(nickname, today);
    }

    public List<VoteEntity> getVotes() {
        return voteRepository.findAll();
    }

    private void updateGameHistory(Game game) {
        LocalDateTime now = LocalDateTime.now();

        // 동일한 날짜 및 게임에 대한 기록이 있는지 확인
        List<GameHistory> existingHistories = gameHistoryRepository.findByVoteTimeBetweenAndGameId(
                now.toLocalDate().atStartOfDay(), now, game.getId());

        if (!existingHistories.isEmpty()) {
            // 기존 기록이 있으면 업데이트
            GameHistory existingHistory = existingHistories.get(0);
            existingHistory.setGameVote(game.getGameVote());
            existingHistory.setGameRank(game.getGameRank()); // 만약 gameRank가 결정되는 로직이 있다면 추가적으로 업데이트
            gameHistoryRepository.save(existingHistory);
        } else {
            // 기록이 없으면 새로 추가
            GameHistory newHistory = new GameHistory();
            newHistory.setGame(game);
            newHistory.setGameName(game.getGameName());
            newHistory.setGameVote(game.getGameVote());
            newHistory.setGameRank(game.getGameRank()); // gameRank가 있는 경우에만 설정
            newHistory.setVoteTime(now);
            gameHistoryRepository.save(newHistory);
        }
    }
}
