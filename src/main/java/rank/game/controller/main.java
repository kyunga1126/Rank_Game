package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import rank.game.dto.BoardDTO;
import rank.game.dto.NoticeDTO;
import rank.game.entity.NoticeEntity;
import rank.game.service.BoardService;
import rank.game.service.NoticeService;

import java.util.List;


@Controller
@Slf4j
public class main {

    private final BoardService boardService;
    private final NoticeService noticeService;

    public main(BoardService boardService, NoticeService noticeService) {
        this.boardService = boardService;
        this.noticeService = noticeService;
    }

    @GetMapping("/")
    public String Main(HttpSession session, Model model) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        if (isLogin) {
            // 세션에서 관리자 여부와 매니저 여부를 가져옵니다.
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            Boolean isManager = (Boolean) session.getAttribute("isManager");

            // 기본값 설정
            if (isAdmin == null) isAdmin = false;
            if (isManager == null) isManager = false;

            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isManager", isManager);
        } else {
            // 로그인되지 않은 경우, 기본값을 설정
            model.addAttribute("isAdmin", false);
            model.addAttribute("isManager", false);
        }

        // 인기글 목록 가져오기
        List<BoardDTO> popularPosts = boardService.getPopularPosts();
        model.addAttribute("popularPosts", popularPosts);

        // 최신 공지사항 4개 가져오기
        List<NoticeDTO> latestNotices = noticeService.getLatestNotices(4);
        model.addAttribute("latestNotices", latestNotices);


        return "index";
    }
}
