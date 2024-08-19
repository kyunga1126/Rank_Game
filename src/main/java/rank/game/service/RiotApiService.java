package rank.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rank.game.dto.PlayerDTO;

import java.util.Map;

@Slf4j
@Service
public class RiotApiService {
    private final RestTemplate restTemplate;
    private final String apiKey = "RGAPI-d9fd943a-0d67-46f7-abb7-3907c182df58";
    private final String headerName = "X-Riot-Token";

    public RiotApiService() {
        this.restTemplate = new RestTemplate();
    }

    public String getPuuid(String gameName, String tagLine) {
        String url = String.format("https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%s/%s?api_key=%s",
                gameName, tagLine, apiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.set(headerName, apiKey);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return (String) response.getBody().get("puuid");
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("요청 한도를 초과했습니다. 잠시 대기 후 다시 시도합니다.");
            try {
                Thread.sleep(1000); // 대기 시간
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            return getPuuid(gameName, tagLine); // 재귀 호출
        } catch (HttpClientErrorException e) {
            log.error("HTTP 오류 발생: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("오류 발생: " + e.getMessage());
        }
        return null;
    }

    public String getSummonerIdByPuuid(String puuid) {
        String url = String.format("https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/%s?api_key=%s",
                puuid, apiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.set(headerName, apiKey);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return (String) response.getBody().get("id");
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("요청 한도를 초과했습니다. 잠시 대기 후 다시 시도합니다.");
            try {
                Thread.sleep(1000); // 대기 시간
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            return getSummonerIdByPuuid(puuid); // 재귀 호출
        } catch (HttpClientErrorException e) {
            log.error("HTTP 오류 발생: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("오류 발생: " + e.getMessage());
        }
        return null;
    }

    public PlayerDTO getSortedPlayers(String gameName, String tagLine) {
        String puuid = getPuuid(gameName, tagLine);
        if (puuid != null) {
            String summonerId = getSummonerIdByPuuid(puuid);
            String tier = getLEAGUEV4(summonerId);
            PlayerDTO playerInfo = new PlayerDTO(puuid, gameName, tagLine, tier);
            log.info("gameName: {}, tagLine: {}, puuid: {}, summonerId: {}, tier: {}", gameName, tagLine, puuid, summonerId, tier);
            try {
                Thread.sleep(1200); // 요청 간 대기 시간
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return playerInfo;
        }

        return null;
    }

    public String getLEAGUEV4(String summonerId) {
        String url = String.format("https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/%s?api_key=%s",
                summonerId, apiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.set(headerName, apiKey);

        try {
            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
            log.info("Response from LEAGUEV4 API: {}", (Object) response.getBody()); // 응답 데이터 로그 출력
            if (response.getBody() != null && response.getBody().length > 0) {
                for (Map<String, Object> entry : response.getBody()) {
                    if ("RANKED_SOLO_5x5".equals(entry.get("queueType"))) {
                        return (String) entry.get("tier");
                    }
                }
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("요청 한도를 초과했습니다. 잠시 대기 후 다시 시도합니다.");
            try {
                Thread.sleep(1000); // 대기 시간
                return getLEAGUEV4(summonerId); // 재시도
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        } catch (HttpClientErrorException e) {
            log.error("HTTP 오류 발생: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("오류 발생: " + e.getMessage());
        }
        return null;
    }
}
