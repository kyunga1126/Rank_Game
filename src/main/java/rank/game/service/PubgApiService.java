package rank.game.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rank.game.dto.CombinedResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PubgApiService {

    // application에 저장된 API 가져오기
    @Value("${pubg.api.key}")
    private String apiKey;

    // API 검색 주소
    private final String BASE_URL = "https://api.pubg.com/shards/";

    public CombinedResponse getCombinedResponse(String platform, String playerName) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/vnd.api+json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String platformUrl = BASE_URL + platform + "/players?filter[playerNames]=" + playerName;
        ResponseEntity<String> playerResponse = restTemplate.exchange(platformUrl, HttpMethod.GET, entity, String.class);

        if (playerResponse.getStatusCodeValue() == 200) {
            String playerId = extractPlayerId(playerResponse.getBody());
            String statsUrl = BASE_URL + platform + "/players/" + playerId + "/seasons/lifetime";
            ResponseEntity<String> statsResponse = restTemplate.exchange(statsUrl, HttpMethod.GET, entity, String.class);

            List<String> matchIds = extractRecentMatchIds(playerResponse.getBody());
            List<JsonNode> playerMatchData = getPlayerMatchesData(platform, playerId, matchIds, entity);


            if (statsResponse.getStatusCodeValue() == 200) {
                // statsResponse와 playerMatchData를 CombinedResponse 객체로 결합하여 반환
                return new CombinedResponse(statsResponse.getBody(), playerMatchData);
            }
        }
//            if (statsResponse.getStatusCodeValue() == 200) {
//                // 필요에 따라 statsResponse와 playerMatchData를 결합하여 반환할 수 있습니다.
//                return statsResponse.getBody(); // 예를 들어, 이 부분을 수정할 수 있습니다.
//            }
//        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "플레이어 정보를 찾을 수 없습니다.");
    }

    private String extractPlayerId(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data").get(0);
            return dataNode.path("id").asText();
        } catch (Exception e) {
            throw new RuntimeException("플레이어 정보를 처리하는 중 오류가 발생했습니다.", e);
        }
    }

    private List<String> extractRecentMatchIds(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode matchesNode = rootNode.path("data").get(0).path("relationships").path("matches").path("data");

            List<String> matchIds = new ArrayList<>();
            for (int i = 0; i < matchesNode.size() && i < 20; i++) {
                matchIds.add(matchesNode.get(i).path("id").asText());
            }

            return matchIds;
        } catch (Exception e) {
            throw new RuntimeException("매치 정보를 처리하는 중 오류가 발생했습니다.", e);
        }
    }

    private List<JsonNode> getPlayerMatchesData(String platform, String playerId, List<String> matchIds, HttpEntity<String> entity) {
        List<JsonNode> playerMatchesData = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        for (String matchId : matchIds) {
            String matchUrl = BASE_URL + platform + "/matches/" + matchId;
            ResponseEntity<String> matchResponse = restTemplate.exchange(matchUrl, HttpMethod.GET, entity, String.class);
            JsonNode playerData = extractPlayerMatchData(matchResponse.getBody(), playerId);
            if (playerData != null) {
                playerMatchesData.add(playerData);
            }
        }

        System.out.println("playerMatchesData" + playerMatchesData);
        return playerMatchesData;
    }

    private JsonNode extractPlayerMatchData(String matchResponse, String playerId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(matchResponse);
            JsonNode includedNode = rootNode.path("included");

            for (JsonNode node : includedNode) {
                if (node.path("type").asText().equals("participant") && node.path("attributes").path("stats").path("playerId").asText().equals(playerId)) {
                    return node.path("attributes").path("stats");
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("매치 정보를 처리하는 중 오류가 발생했습니다.", e);
        }
    }



















//    // 사용자가 입력한 플랫폼 및 플레이어이름으로 Url주소를 만든 후 정보를 가져오는 메서드
//    public String getPlayerStatsByPlatform(String platform, String playerName) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + apiKey);
//        headers.set("Accept", "application/vnd.api+json");
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        String platformUrl = BASE_URL + platform + "/players?filter[playerNames]=" + playerName;
//        ResponseEntity<String> playerResponse = restTemplate.exchange(platformUrl, HttpMethod.GET, entity, String.class);
//
//        if (playerResponse.getStatusCodeValue() == 200) {
//
//            // 플레이어ID를 통하여 유저의 seasonsLifetime을 가져오는 부분
//            String playerId = extractPlayerId(playerResponse.getBody());
//            String statsUrl = BASE_URL + platform + "/players/" + playerId + "/seasons/lifetime";
//            ResponseEntity<String> statsResponse = restTemplate.exchange(statsUrl, HttpMethod.GET, entity, String.class);
//
//            // 매치ID를 통하여 유저의 매치정보를 가져와서 플레이어ID에 해당하는 정보를 가져오는 부분
//            List<String> matchIds = extractRecentMatchIds(playerResponse.getBody());
//
//
//            if (statsResponse.getStatusCodeValue() == 200) {
//                return statsResponse.getBody();
//            }
//        }
//        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "플레이어 정보를 찾을 수 없습니다.");
//    }
//
//    // 플레이어의 ID를 추출해주는 메서드
//    private String extractPlayerId(String responseBody) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(responseBody);
//            JsonNode dataNode = rootNode.path("data").get(0);
//            return dataNode.path("id").asText();
//        } catch (Exception e) {
//            throw new RuntimeException("플레이어 정보를 처리하는 중 오류가 발생했습니다.");
//        }
//    }
//
//    // 플레이어의 ID 정보에서 최근 20게임의 매치ID추출하여 반환하는 메서드
//    private List<String> extractRecentMatchIds(String responseBody) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(responseBody);
//            JsonNode matchesNode = rootNode.path("data").get(0).path("relationships").path("matches").path("data");
//
//            List<String> matchIds = new ArrayList<>();
//            for (int i = 0; i < matchesNode.size() && i < 20; i++) {
//                matchIds.add(matchesNode.get(i).path("id").asText());
//            }
//
//            return matchIds;
//        } catch (Exception e) {
//            throw new RuntimeException("매치 정보를 처리하는 중 오류가 발생했습니다.", e);
//        }
//    }

}
