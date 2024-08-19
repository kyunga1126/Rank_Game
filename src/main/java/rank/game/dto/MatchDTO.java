package rank.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchDTO {
    private boolean win;
    private String matchId;
    private String gameType;
    private Long gameCreation;
    private Long timePlayed;
    private String championName;
    private Integer champLevel;
    private int kills;
    private int deaths;
    private int assists;
    private double kda;
    private int visionWardsBought;
    private int totalMinionsKilled;
    private double csPerMinute;
    private long gameStartTimestamp;
    private String dateHeader;

}