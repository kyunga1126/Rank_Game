package rank.game.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rank.game.dto.BoardDTO;
import rank.game.entity.BoardEntity;
import rank.game.repository.BoardRepository;
import rank.game.repository.CommentRepository;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    // 게시글 저장
    public void boardWrite(BoardEntity board, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            String[] fileDetails = saveFile(file);
            board.setFilename(fileDetails[0]);
            board.setFilepath(fileDetails[1]);
        }

        boardRepository.save(board); // 새 게시글 저장
    }

    // 파일 저장 로직을 별도의 메서드로 분리
    private String[] saveFile(MultipartFile file) throws Exception {
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\uploads";
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(projectPath, fileName);

        // 디렉터리가 존재하지 않으면 생성
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }

        file.transferTo(saveFile);

        return new String[]{fileName, "/uploads/" + fileName};
    }

    // 게시글 리스트 처리
    public Page<BoardEntity> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    // 게시글 검색 처리
    public Page<BoardEntity> boardSearchList(String searchKeyword, Pageable pageable) {
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    // 특정 게시글 불러오기
    public BoardDTO boardView(Long id) {
        // 게시글 조회
        BoardEntity board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        // Entity를 DTO로 변환하여 반환
        return BoardDTO.fromEntity(board);
    }

    // 특정 게시글 삭제
    @Transactional
    public void boardDelete(Long id) {
        // 먼저 댓글 삭제
        commentRepository.deleteByBoardId(id);
        // 그 다음 보드 삭제
        boardRepository.deleteById(id);
    }

    // 게시글 업데이트
    public void updateBoard(BoardEntity board, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            String[] fileDetails = saveFile(file);
            board.setFilename(fileDetails[0]);
            board.setFilepath(fileDetails[1]);
        }
        boardRepository.save(board); // 기존 게시글 업데이트
    }

    // 게시물 찾기
    public BoardEntity getBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    // 이전글 찾기
    public BoardEntity getPreviousPost(Long id) {
        List<BoardEntity> posts = boardRepository.findPreviousPosts(id, PageRequest.of(0, 1));
        return posts.isEmpty() ? null : posts.get(0);
    }

    // 다음글 찾기
    public BoardEntity getNextPost(Long id) {
        List<BoardEntity> posts = boardRepository.findNextPosts(id, PageRequest.of(0, 1));
        return posts.isEmpty() ? null : posts.get(0);
    }

    // 조회수 증가 서비스 메서드
    public void incrementViewCount(Long id) {
        BoardEntity board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));
        board.setViewCount(board.getViewCount() + 1);
        boardRepository.save(board);
    }

    public List<BoardDTO> getPopularPosts() {
        // 인기글을 선정하는 로직
        List<BoardEntity> popularPosts = boardRepository.findPopularPosts();

        return popularPosts.stream()
                .map(BoardDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
