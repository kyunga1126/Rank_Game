package rank.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rank.game.dto.CyphersMatchDTO;
import rank.game.dto.CyphersPlayerDTO;
import rank.game.dto.CyphersRecordDTO;
import rank.game.service.CyphersApiService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/search/cyphers")
public class CyphersController {

    private final CyphersApiService cyphersApiService;

    @Autowired
    public CyphersController(CyphersApiService cyphersApiService) {
        this.cyphersApiService = cyphersApiService;
    }

    @GetMapping
    public String cyphers() {
        // 기본 페이지를 반환
        return "html/cyphers";
    }

    @GetMapping("/player")
    public String getPlayerInfo(@RequestParam("nickname") String nickname,
                                @RequestParam("gameType") String gameType,
                                Model model) {
        try {
            // 플레이어 정보와 전적 가져오기
            CyphersPlayerDTO playerDTO = cyphersApiService.getPlayerInfo(nickname, "match");
            List<CyphersMatchDTO> matchDetails = cyphersApiService.getMatchDetails(playerDTO.getPlayerId(), gameType);

            log.info("Match Details: {}", matchDetails);  // 디버깅용 로그 추가

            // 전적 정보 가공
            List<CyphersRecordDTO> gameRecords = matchDetails.stream()
                    .map(record -> new CyphersRecordDTO(
                            record.getMatchId(),
                            "WIN".equalsIgnoreCase(record.getResult()),
                            record.getCharacterName(),
                            record.getPlayTime()
                            // 필요한 경우 추가적인 필드를 여기에 포함
                    ))
                    .collect(Collectors.toList());

            playerDTO.setGameRecords(gameRecords);

            // 모델에 데이터 추가
            model.addAttribute("playerInfo", playerDTO);
            model.addAttribute("matchDetails", matchDetails);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching data: " + e.getMessage());
        }
        // 데이터와 함께 같은 페이지를 반환
        return "html/cyphers";
    }
}