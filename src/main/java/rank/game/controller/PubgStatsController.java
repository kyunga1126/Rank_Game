package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rank.game.dto.CombinedResponse;
import rank.game.service.PubgApiService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/search")
public class PubgStatsController {

    @Autowired
    private PubgApiService pubgApiService;

    @GetMapping("/pubg")
    public String searchPubg(HttpSession session, Model model) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);
        return "html/searchPubg";
    }

    @PostMapping("/pubg/user")
    public String searchPlayerStats(@RequestParam("platform") String platform,
                                    @RequestParam("playerName") String playerName,
                                    Model model, HttpSession session) {

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        try {
            CombinedResponse combinedResponse = pubgApiService.getCombinedResponse(platform, playerName);

            System.out.println("사용자 전체 통계 기록 " + combinedResponse.getStatsResponse());
            System.out.println("사용자 최근 20게임 기록 " + combinedResponse.getPlayerMatchData());
            model.addAttribute("playerName", playerName);
            model.addAttribute("statsResponse", combinedResponse.getStatsResponse()); // statsResponse 데이터를 전달
            model.addAttribute("playerMatchData", combinedResponse.getPlayerMatchData()); // playerMatchData 데이터를 전달
            model.addAttribute("platform", platform);
            return "html/pubg";
        } catch (Exception ex) {
            model.addAttribute("message", "플레이어 정보를 찾을 수 없습니다. 입력을 다시 확인해주세요.");
            model.addAttribute("searchUrl", "/search/pubg");
            return "html/message";
        }
    }


















//    @PostMapping("/pubg/user")
//    public String searchPlayerStats(@RequestParam("platform") String platform,
//                                    @RequestParam("playerName") String playerName,
//                                    Model model, HttpSession session) {
//
//        // 로그인 세션 추가
//        boolean isLogin = session.getAttribute("loginEmail") != null;
//        model.addAttribute("isLogin", isLogin);
//
//        try {
//            String statsJson = pubgApiService.getPlayerStatsByPlatform(platform, playerName);
//            model.addAttribute("playerName", playerName);
//            model.addAttribute("statsJson", statsJson);
//            model.addAttribute("platform", platform);
//            return "html/pubg";
//        } catch (Exception ex) {
//            model.addAttribute("message", "플레이어 정보를 찾을 수 없습니다. 입력을 다시 확인해주세요.");
//            model.addAttribute("searchUrl", "/search/pubg");
//            return "html/message";
//        }
//    }
}
