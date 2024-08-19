package rank.game.service;

import jakarta.persistence.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rank.game.dto.VoteDTO;
import rank.game.entity.Game;
import rank.game.entity.VoteEntity;
import rank.game.repository.GameRepository;
import rank.game.repository.VoteRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoteService {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private VoteRepository voteRepository;

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
}

