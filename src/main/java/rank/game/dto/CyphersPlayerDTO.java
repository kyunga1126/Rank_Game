package rank.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CyphersPlayerDTO {
    private String playerId;
    private String nickname;
    private String tier;
    private List<CyphersRecordDTO> gameRecords;
}
