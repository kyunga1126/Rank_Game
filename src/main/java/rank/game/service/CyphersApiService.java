package rank.game.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rank.game.dto.CyphersMatchDTO;
import rank.game.dto.CyphersPlayerDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CyphersApiService {

    private static final String API_KEY = "tEbiFWqHPbvcGrQ67QtGpnIBNwO3L7jC";
    private static final String BASE_URL = "https://api.neople.co.kr/cy";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = Logger.getLogger(CyphersApiService.class.getName());

    public CyphersPlayerDTO getPlayerInfo(String nickname, String wordType) {
        String url = BASE_URL + "/players?nickname=" + nickname + "&wordType=" + wordType + "&apikey=" + API_KEY;
        try {
            ResponseEntity<JsonNode> responseEntity = restTemplate.getForEntity(url, JsonNode.class);
            JsonNode response = responseEntity.getBody();
            logger.info("Player Info Response: " + response);

            CyphersPlayerDTO playerDTO = new CyphersPlayerDTO();
            if (response != null && response.has("rows") && response.get("rows").isArray() && response.get("rows").size() > 0) {
                JsonNode playerData = response.get("rows").get(0);

                playerDTO.setPlayerId(getJsonValue(playerData, "playerId"));
                playerDTO.setNickname(getJsonValue(playerData, "nickname"));

                // Additional data
                playerDTO = getPlayerDetails(playerDTO.getPlayerId(), playerDTO);
            }
            return playerDTO;
        } catch (HttpClientErrorException e) {
            logger.severe("Failed to fetch player info: " + e.getMessage());
            throw new RuntimeException("Failed to fetch player info: " + e.getMessage());
        }
    }

    private CyphersPlayerDTO getPlayerDetails(String playerId, CyphersPlayerDTO playerDTO) {
        String url = BASE_URL + "/players/" + playerId + "?apikey=" + API_KEY;
        try {
            ResponseEntity<JsonNode> responseEntity = restTemplate.getForEntity(url, JsonNode.class);
            JsonNode response = responseEntity.getBody();
            logger.info("Player Details Response: " + response);

            if (response != null) {
                String tier = getJsonValue(response, "tierName");
                if (tier.equals("null")) {
                    tier = "없음";
                }
                playerDTO.setTier(tier);
            }
            return playerDTO;
        } catch (HttpClientErrorException e) {
            logger.severe("Failed to fetch player details: " + e.getMessage());
            throw new RuntimeException("Failed to fetch player details: " + e.getMessage());
        }
    }

    public List<CyphersMatchDTO> getMatchDetails(String playerId, String gameType) {
        String url = BASE_URL + "/players/" + playerId + "/matches?gameTypeId=" + gameType + "&limit=10&apikey=" + API_KEY;
        try {
            ResponseEntity<JsonNode> responseEntity = restTemplate.getForEntity(url, JsonNode.class);
            JsonNode response = responseEntity.getBody();
            logger.info("Match Details Response: " + response);

            List<CyphersMatchDTO> matchDetails = new ArrayList<>();
            if (response != null && response.has("matches") && response.get("matches").has("rows") && response.get("matches").get("rows").isArray() && response.get("matches").get("rows").size() > 0) {
                for (JsonNode matchNode : response.get("matches").get("rows")) {
                    matchDetails.add(extractMatchDetail(matchNode));
                }
            } else {
                logger.warning("No matches found for playerId: " + playerId);
            }
            return matchDetails;
        } catch (HttpClientErrorException e) {
            logger.severe("Failed to fetch match details: " + e.getMessage());
            throw new RuntimeException("Failed to fetch match details: " + e.getMessage());
        }
    }

    private CyphersMatchDTO extractMatchDetail(JsonNode matchNode) {
        JsonNode playInfoNode = matchNode.get("playInfo");
        if (playInfoNode == null) {
            throw new RuntimeException("No playInfo found for match: " + getJsonValue(matchNode, "matchId"));
        }

        CyphersMatchDTO matchDetail = new CyphersMatchDTO();
        matchDetail.setMatchId(getJsonValue(matchNode, "matchId"));
        matchDetail.setResult(getJsonValue(playInfoNode, "result"));
        matchDetail.setCharacterName(getJsonValue(playInfoNode, "characterName"));
        matchDetail.setPlayTime(getJsonValue(playInfoNode, "playTime"));
        matchDetail.setCharacterId(getJsonValue(playInfoNode, "characterId"));
        matchDetail.setLevel(getJsonValue(playInfoNode, "level"));

        // KDA와 킬 정보 가져오기
        int kills = Integer.parseInt(getJsonValue(playInfoNode, "killCount"));
        int deaths = Integer.parseInt(getJsonValue(playInfoNode, "deathCount"));
        int assists = Integer.parseInt(getJsonValue(playInfoNode, "assistCount"));

        double kda = (deaths == 0) ? (kills + assists) : (kills + assists) / (double) deaths;
        matchDetail.setKda(String.format("%.2f", kda));
        matchDetail.setKills(String.valueOf(kills));
        matchDetail.setDeaths(String.valueOf(deaths));
        matchDetail.setAssists(String.valueOf(assists));

        return matchDetail;
    }

    private String getJsonValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null ? fieldNode.asText() : "N/A";
    }
}
