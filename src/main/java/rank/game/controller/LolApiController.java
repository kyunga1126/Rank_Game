package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rank.game.dto.PlayerDTO;
import rank.game.service.RiotApiService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/search")
public class LolApiController {

    private final RiotApiService riotApiService;

    @Autowired
    public LolApiController(RiotApiService riotApiService) {
        this.riotApiService = riotApiService;
    }

    @GetMapping("/lol")
    public String lol(HttpSession session, Model model) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        // 세션에서 최근 검색어를 가져와 모델에 추가
        List<String> recentSearches = (List<String>) session.getAttribute("recentSearches");
        if (recentSearches == null) {
            recentSearches = new ArrayList<>();
        }
        model.addAttribute("recentSearches", recentSearches);
        model.addAttribute("isLogin", isLogin);
        return "html/leagueOfLegends";
    }

    @PostMapping("/sorted-players")
    public String getSortedPlayers(@RequestParam String searchLOL, HttpSession session, Model model) {

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        System.out.println("sas" + searchLOL)       ;
        // searchLOL 파라미터에서 gameName과 tagLine을 분리
        String[] parts = searchLOL.split("#");
        if (parts.length != 2) {
            model.addAttribute("message", "잘못된 형식입니다. 게임 이름과 태그를 'gameName#tagLine' 형식으로 입력해주세요.");
            model.addAttribute("searchUrl", "/search/lol");
            return "html/message";
        }
        String gameName = parts[0];
        String tagLine = parts[1];

        // SortedPlayers 호출
        PlayerDTO playerInfo = riotApiService.getSortedPlayers(gameName, tagLine);

        if (playerInfo != null) {
            // 세션에 playerInfo, gameName, tagLine, puuid, tier 저장
            session.setAttribute("playerInfo", playerInfo);
            session.setAttribute("gameName", gameName);
            session.setAttribute("tagLine", tagLine);
            session.setAttribute("puuid", playerInfo.getPuuid());
            session.setAttribute("tier", playerInfo.getTier());

            // 최근 검색어를 세션에 저장
            List<String> recentSearches = (List<String>) session.getAttribute("recentSearches");
            if (recentSearches == null) {
                recentSearches = new ArrayList<>();
            }
            String searchEntry = gameName + "#" + tagLine;
            recentSearches.remove(searchEntry);
            recentSearches.add(0, searchEntry);
            if (recentSearches.size() > 5) {
                recentSearches.remove(5);
            }
            session.setAttribute("recentSearches", recentSearches);

            // Model에 데이터 추가
            model.addAttribute("playerInfo", playerInfo);
            model.addAttribute("gameName", gameName);
            model.addAttribute("tagLine", tagLine);
            model.addAttribute("puuid", playerInfo.getPuuid());
            model.addAttribute("tier", playerInfo.getTier());

            // leagueOfLegends 뷰 반환
            return "html/searchLOL";
        } else {
            model.addAttribute("message", "플레이어 정보를 찾을 수 없습니다. 입력 다시 해주세요.");
            model.addAttribute("searchUrl", "/search/lol");
            return "html/message";
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model, HttpSession session) {

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        model.addAttribute("message", "잘못된 형식입니다. 게임 이름과 태그를 'gameName#tagLine' 형식으로 입력해주세요.");
        model.addAttribute("searchUrl", "/search/lol");
        return "html/message";
    }

    @PostMapping("/member")
    public String searchMember(HttpSession session, Model model){

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        // 세션에서 playerInfo, gameName, tagLine, puuid, tier 가져오기
        PlayerDTO playerInfo = (PlayerDTO) session.getAttribute("playerInfo");
        String gameName = (String) session.getAttribute("gameName");
        String tagLine = (String) session.getAttribute("tagLine");
        String puuid = (String) session.getAttribute("puuid");
        String tier = (String) session.getAttribute("tier");

        if (playerInfo != null) {
            model.addAttribute("playerInfo", playerInfo);
            model.addAttribute("gameName", gameName);
            model.addAttribute("tagLine", tagLine);
            model.addAttribute("puuid", puuid);
            model.addAttribute("tier", tier);
            return "html/searchLOL";
        } else {
            model.addAttribute("error", "No player information found in sessi   on.");
            return "error";
        }
    }
}
