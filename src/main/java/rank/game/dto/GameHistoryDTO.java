package rank.game.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameHistoryDTO {
    private Long id;
    private Long gameId;
    private String gameName;
    private Integer gameVote;
    private Integer gameRank;
    private LocalDateTime voteTime;
    private Integer rankChange;  // 순위 변동 필드 변경

    public GameHistoryDTO() {}

    public GameHistoryDTO(Long id, Long gameId, String gameName, Integer gameVote, Integer gameRank, LocalDateTime voteTime, Integer rankChange) {
        this.id = id;
        this.gameId = gameId;
        this.gameName = gameName;
        this.gameVote = gameVote;
        this.gameRank = gameRank;
        this.voteTime = voteTime;
        this.rankChange = rankChange;
    }
}
