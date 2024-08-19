package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rank.game.dto.CommentDTO;
import rank.game.service.CommentService;

import javax.xml.stream.events.Comment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 댓글 추가 기능
    @PostMapping
    public ResponseEntity<Map<String, String>> addComment(@RequestBody CommentDTO commentDTO, HttpSession session) {
        Map<String, String> response = new HashMap<>();

        // 로그인 세션 확인
        if (session.getAttribute("loginEmail") == null) {
            response.put("message", "로그인이 필요합니다.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 댓글 내용 검증
        if (commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            response.put("message", "댓글을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        // 댓글 추가
        commentService.addComment(commentDTO);
        response.put("message", "");
        return ResponseEntity.ok(response);
    }

    // 댓글 가져오기 기능
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByBoardId(@PathVariable Long boardId) {
        List<CommentDTO> comments = commentService.getCommentsByBoardId(boardId);
        System.out.println(comments);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정 기능
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateComment(@PathVariable Long id, @RequestBody Map<String, String> request, HttpSession session) {
        Map<String, String> response = new HashMap<>();

        // 로그인 세션 확인
        if (session.getAttribute("loginEmail") == null) {
            response.put("message", "");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 댓글 내용 검증
        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            response.put("message", "");
            return ResponseEntity.badRequest().body(response);
        }

        // 댓글 수정
        commentService.updateComment(id, content);
        response.put("message", "");
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long id, HttpSession session) {
        Map<String, String> response = new HashMap<>();

        if (session.getAttribute("loginEmail") == null) {
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            commentService.deleteComment(id);
            response.put("message", "댓글이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "댓글 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
