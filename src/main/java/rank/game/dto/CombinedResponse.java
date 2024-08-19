package rank.game.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class CombinedResponse {

    private Object statsResponse;
    private List<JsonNode> playerMatchData;

    public CombinedResponse(Object statsResponse, List<JsonNode> playerMatchData) {
        this.statsResponse = statsResponse;
        this.playerMatchData = playerMatchData;
    }

    public Object getStatsResponse() {
        return statsResponse;
    }

    public void setStatsResponse(Object statsResponse) {
        this.statsResponse = statsResponse;
    }

    public List<JsonNode> getPlayerMatchData() {
        return playerMatchData;
    }

    public void setPlayerMatchData(List<JsonNode> playerMatchData) {
        this.playerMatchData = playerMatchData;
    }
}
