package rank.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerDTO {
    private String puuid;
    private String gameName;
    private String tagLine;
    private String tier;

}
