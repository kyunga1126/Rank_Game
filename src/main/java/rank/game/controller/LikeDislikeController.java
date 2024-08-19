package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import rank.game.entity.MemberEntity;
import rank.game.repository.MemberRepository;
import rank.game.service.LikeDislikeService;

@Controller
public class LikeDislikeController {

    @Autowired
    private LikeDislikeService likeDislikeService;

    @Autowired
    private MemberRepository memberRepository;

    @PostMapping("/board/like/{id}")
    public ResponseEntity<String> likeBoard(@PathVariable Long id, HttpSession session) {
        // 세션에서 memberNum을 Long으로 가져오기
        Long memberNum = (Long) session.getAttribute("memberNum");
        if (memberNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // memberNum을 사용하여 MemberEntity를 찾습니다.
        MemberEntity member = memberRepository.findById(memberNum).orElse(null);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 사용자입니다.");
        }

        try {
            boolean result = likeDislikeService.toggleLikeBoard(id, memberNum);
            if (result) {
                return ResponseEntity.ok("좋아요를 눌렀습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 좋아요 눌렀습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @PostMapping("/board/dislike/{id}")
    public ResponseEntity<String> dislikeBoard(@PathVariable Long id, HttpSession session) {
        // 세션에서 memberNum을 Long으로 가져오기
        Long memberNum = (Long) session.getAttribute("memberNum");
        if (memberNum == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // memberNum을 사용하여 MemberEntity를 찾습니다.
        MemberEntity member = memberRepository.findById(memberNum).orElse(null);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 사용자입니다.");
        }

        try {
            boolean result = likeDislikeService.toggleDislikeBoard(id, memberNum);
            if (result) {
                return ResponseEntity.ok("싫어요를 눌렀습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 싫어요를 눌렀습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
