package rank.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CyphersRecordDTO {
    private String matchId;
    private boolean win;
    private String characterName;
    private String playTime;

    // 기본 생성자
    public CyphersRecordDTO() {
    }

    // 필요한 생성자
    public CyphersRecordDTO(String matchId, boolean win, String characterName, String playTime) {
        this.matchId = matchId;
        this.win = win;
        this.characterName = characterName;
        this.playTime = playTime;
    }
}
