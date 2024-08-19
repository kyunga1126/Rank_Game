package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import rank.game.dto.MemberDTO;
import rank.game.dto.NoticeDTO;
import rank.game.entity.NoticeEntity;
import rank.game.service.MemberService;
import rank.game.service.NoticeService;

@Slf4j
@Controller
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // 게시판 리스트로 이동
    @GetMapping
    public String noticeList(Model model, HttpSession session,
                            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "searchKeyword", required = false, defaultValue = "") String searchKeyword) {

        // 페이지는 0부터 시작하므로 -1 해줍니다.
        Pageable currentPageable = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        // 세션에서 로그인된 유저의 MemberDTO 가져오기
        if (session.getAttribute("loginMember") != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
            model.addAttribute("isAdmin", loginMember.isAdmin());
            model.addAttribute("isManager", loginMember.isManager());
        } else {
            model.addAttribute("isAdmin", false);  // 기본 값 설정
            model.addAttribute("isManager", false);  // 기본 값 설정
        }

        // 로그인 세션 추가
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        
        // 페이징 처리
        Page<NoticeEntity> pageResult;
        if (searchKeyword.isEmpty()) {
            pageResult = noticeService.noticeList(currentPageable);
        } else {
            pageResult = noticeService.noticeSearchList(searchKeyword, currentPageable);
        }

        int nowPage = pageResult.getNumber() + 1;
        int startPage = Math.max(nowPage - 2, 1);
        int endPage = Math.min(nowPage + 3, pageResult.getTotalPages());

        model.addAttribute("list", pageResult);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("searchKeyword", searchKeyword);


        return "html/notice/noticeList";
    }

    // 공지사항 작성 페이지 이동
    @GetMapping("/write")
    public String createForm(Model model, HttpSession session) {
        // 세션에서 로그인된 유저의 MemberDTO 가져오기
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        boolean isLogin = session.getAttribute("loginEmail") != null;

        if (loginMember.isAdmin() || loginMember.isManager()) {
            model.addAttribute("isLogin", true);
            model.addAttribute("isLogin", isLogin);
            model.addAttribute("isAdmin", loginMember.isAdmin());
            model.addAttribute("isManager", loginMember.isManager());

            model.addAttribute("nickname", session.getAttribute("nickname"));
            model.addAttribute("loginEmail", session.getAttribute("loginEmail"));
            return "html/notice/noticeWrite";
        } else {
            model.addAttribute("isLogin", false);
            model.addAttribute("message", "공지사항 작성 권한이 없습니다.");
            model.addAttribute("searchUrl", "/notice");
            return "html/message";
        }
    }

    // 글작성 처리
    @PostMapping("/writepro")
    public String noticeWritePro(NoticeEntity notice, Model model, MultipartFile file, HttpSession session) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        String email = (String) session.getAttribute("loginEmail");
        String nickname = (String) session.getAttribute("nickname");
        notice.setMemberEmail(email);
        notice.setNickname(nickname);

        try {
            if (file != null && !file.isEmpty()) {
                System.out.println("Controller filename: " + file.getOriginalFilename());
            } else {
                System.out.println("No file uploaded");
            }

            noticeService.noticeWrite(notice, file);

            model.addAttribute("message", "글 작성이 완료되었습니다.");
        } catch (MaxUploadSizeExceededException e) {
            model.addAttribute("message", "파일 크기가 너무 큽니다. 최대 업로드 크기를 확인해주세요.");
        } catch (Exception e) {
            model.addAttribute("message", "글 작성 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        model.addAttribute("searchUrl", "/notice");

        return "html/message";
    }

    // 특정 게시글 불러오기
    @GetMapping("/view/{id}")
    public String noticeView(@PathVariable Long id, Model model,
                            @RequestParam(value = "searchKeyword", required = false, defaultValue = "") String searchKeyword,
                            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                            HttpSession session) {

        NoticeDTO notice = noticeService.noticeView(id);

        if (notice == null) {
            model.addAttribute("message", "해당 게시글을 찾을 수 없습니다.");
            return "redirect:/notice";
        }

        // 로그인 이메일을 세션에서 가져오기
        String loginEmail = (String) session.getAttribute("loginEmail");
        Long memberNum = (Long) session.getAttribute("memberNum");
        String nickname = (String) session.getAttribute("nickname");

        // 이전글과 다음글 정보 가져오기
        NoticeEntity previousPost = noticeService.getPreviousPost(id);
        NoticeEntity nextPost = noticeService.getNextPost(id);

        // 조회수 증가 로직
        String sessionKey = "viewed_notice_" + id;
        if (session.getAttribute(sessionKey) == null) {
            noticeService.incrementViewCount(id);
            session.setAttribute(sessionKey, true);
        }

        // 세션에서 로그인된 유저의 MemberDTO 가져오기
        if (session.getAttribute("loginMember") != null) {
            MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
            model.addAttribute("isAdmin", loginMember.isAdmin());
            model.addAttribute("isManager", loginMember.isManager());
        } else {
            model.addAttribute("isAdmin", false);  // 기본 값 설정
            model.addAttribute("isManager", false);  // 기본 값 설정
        }

        // 특정 게시글 가져오기
        model.addAttribute("notice", notice);
        model.addAttribute("createdAt", notice.getCreatedAt());
        model.addAttribute("nickname", nickname);
        model.addAttribute("memberNum", memberNum);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("page", page);

        // 이미지 파일 경로 추가
        model.addAttribute("imagePath", notice.getFilepath());

        // 이전글과 다음글 정보 추가
        model.addAttribute("previousPost", previousPost);
        model.addAttribute("nextPost", nextPost);

        // 로그인 상태면 true
        model.addAttribute("isLogin", loginEmail != null);

        return "html/notice/noticeView";
    }

    // 공지사항 삭제 기능
    @GetMapping("/delete/{id}")
    public String deleteNotice(@PathVariable Long id, Model model, HttpSession session) {
        // 로그인 정보 가져오기
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        // 삭제 권한 확인
        if (session.getAttribute("loginMember") != null) {
            noticeService.noticeDelete(id);
            model.addAttribute("message", "글 삭제가 완료되었습니다.");
            model.addAttribute("searchUrl", "/notice");
            return "html/message";
        } else {
            model.addAttribute("message", "게시글 삭제 권한이 없습니다.");
            model.addAttribute("searchUrl", "/notice");
            return "html/message";
        }
    }

        // 게시글 수정
        @GetMapping("/modify/{id}")
        public String noticeModify(@PathVariable Long id, Model model, HttpSession session) {
            // 로그인 정보 가져오기
            boolean isLogin = session.getAttribute("loginEmail") != null;
            model.addAttribute("isLogin", isLogin);

            // 게시글 조회
            NoticeEntity noticeTemp = noticeService.getNoticeById(id);

            // 수정 권한 확인
            if (session.getAttribute("loginMember") != null) {
                model.addAttribute("notice", noticeTemp);
                return "html/notice/noticeModify";
            } else {
                model.addAttribute("message", "게시글 수정 권한이 없습니다.");
                model.addAttribute("searchUrl", "/notice");
                return "html/message";
            }
        }

        // 게시글 수정 처리
        @PostMapping("/update/{id}")
        public String noticeUpdate(@PathVariable Long id, NoticeEntity notice, Model model, MultipartFile file, HttpSession session) throws Exception {
            // 로그인 정보 가져오기
            boolean isLogin = session.getAttribute("loginEmail") != null;
            model.addAttribute("isLogin", isLogin);

            // 게시글 조회
            NoticeEntity noticeTemp = noticeService.getNoticeById(id);

            // 수정 권한 확인
            if (session.getAttribute("loginMember") != null) {
                // 제목과 내용 수정
                noticeTemp.setTitle(notice.getTitle());
                noticeTemp.setContent(notice.getContent());

                // 파일 처리
                if (file != null && !file.isEmpty()) {
                    noticeService.updateNotice(noticeTemp, file);
                } else {
                    noticeService.updateNotice(noticeTemp, null);
                }

                model.addAttribute("message", "글 수정이 완료되었습니다.");
                model.addAttribute("searchUrl", "/notice");
            } else {
                model.addAttribute("message", "게시글 수정 권한이 없습니다.");
                model.addAttribute("searchUrl", "/notice");
            }

            return "html/message";

    }
}
