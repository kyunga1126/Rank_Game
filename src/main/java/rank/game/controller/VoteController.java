package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rank.game.dto.VoteDTO;
import rank.game.service.VoteService;

import java.util.List;

@Controller
public class VoteController {
    @Autowired
    private VoteService voteService;

    @GetMapping("/vote")
    public String getGames(HttpSession session, Model model) {
        List<VoteDTO> games = voteService.findAllVoteDTOs();
        model.addAttribute("games", games);
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        if (!isLogin) {
            model.addAttribute("message", "로그인 후 이용해주세요.");
            model.addAttribute("searchUrl", "/");
            return "html/message";
        }

        return "html/vote";
    }

    @PostMapping("/vote1")
    public String vote(@RequestParam List<String> selectedGames, HttpSession session, Model model) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        if (!isLogin) {
            model.addAttribute("message", "로그인 후 이용해주세요.");
            model.addAttribute("searchUrl", "/");
            return "html/message";
        }

        String nickname = (String) session.getAttribute("nickname");

        if (voteService.hasVotedToday(nickname)) {
            model.addAttribute("message", "하루에 한 번만 투표할 수 있습니다.");
            model.addAttribute("searchUrl", "/");
            return "html/message";
        }

        voteService.saveVotes(nickname, selectedGames);

        return "redirect:/review"; // 투표 완료 후 리뷰 페이지로 리디렉션
    }
}
