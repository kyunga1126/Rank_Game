package rank.game.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rank.game.dto.NoticeDTO;
import rank.game.entity.NoticeEntity;
import rank.game.repository.NoticeRepository;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    // 게시글 저장
    public void noticeWrite(NoticeEntity notice, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            String[] fileDetails = saveFile(file);
            notice.setFilename(fileDetails[0]);
            notice.setFilepath(fileDetails[1]);
        }

       noticeRepository.save(notice); // 새 게시글 저장
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
    public Page<NoticeEntity> noticeList(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    public List<NoticeEntity> notice() {
        return noticeRepository.findAll();
    }

    // 게시글 검색 처리
    public Page<NoticeEntity> noticeSearchList(String searchKeyword, Pageable pageable) {
        return noticeRepository.findByTitleContaining(searchKeyword, pageable);
    }

    // 특정 게시글 불러오기
    public NoticeDTO noticeView(Long id) {
        // 게시글 조회
        NoticeEntity notice = noticeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        // Entity를 DTO로 변환하여 반환
        return NoticeDTO.fromEntity(notice);
    }

    // 이전글 찾기
    public NoticeEntity getPreviousPost(Long id) {
        List<NoticeEntity> posts = noticeRepository.findPreviousPosts(id, PageRequest.of(0, 1));
        return posts.isEmpty() ? null : posts.get(0);
    }

    // 다음글 찾기
    public NoticeEntity getNextPost(Long id) {
        List<NoticeEntity> posts = noticeRepository.findNextPosts(id, PageRequest.of(0, 1));
        return posts.isEmpty() ? null : posts.get(0);
    }

    // 조회수 증가 서비스 메서드
    public void incrementViewCount(Long id) {
        NoticeEntity notice = noticeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid notice ID"));
        notice.setViewCount(notice.getViewCount() + 1);
        noticeRepository.save(notice);
    }

    // 특정 게시글 삭제
    @Transactional
    public void noticeDelete(Long id) {
        // 먼저 댓글 삭제
//        commentRepository.deleteByBoardId(id);
        // 그 다음 보드 삭제
        noticeRepository.deleteById(id);
    }

    // 게시글 업데이트
    public void updateNotice(NoticeEntity notice, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            String[] fileDetails = saveFile(file);
            notice.setFilename(fileDetails[0]);
            notice.setFilepath(fileDetails[1]);
        }
        noticeRepository.save(notice); // 기존 게시글 업데이트
    }

    // 게시물 찾기
    public NoticeEntity getNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    public List<NoticeDTO> getLatestNotices(int count) {
        return noticeRepository.findTop4ByOrderByCreatedAtDesc()
                .stream()
                .map(NoticeDTO::fromEntity)
                .collect(Collectors.toList());
    }

}