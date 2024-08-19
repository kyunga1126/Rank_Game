package rank.game.entity;

import jakarta.persistence.*;
import lombok.Data;
import rank.game.dto.BoardDTO;
import rank.game.dto.NoticeDTO;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notice")
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String memberEmail;

    @Column(name = "nickname", length = 8)
    private String nickname;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "filename")
    private String filename;

    @Column(name = "filepath")
    private String filepath;

    @Column(name = "view_count")
    private int viewCount=0;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;  // 논리 삭제를 위한 필드

    @Column(nullable = false)
    private boolean isActive;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

//     BoardDTO 객체를 BoardEntity로 변환하는 메서드
    public static NoticeEntity toNoticeEntity(NoticeDTO noticeDTO) {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setMemberEmail(noticeDTO.getMemberEmail());
        noticeEntity.setNickname(noticeDTO.getNickname());
        noticeEntity.setTitle(noticeDTO.getTitle());
        noticeEntity.setContent(noticeDTO.getContent());
        noticeEntity.setCreatedAt(LocalDateTime.now());
        noticeEntity.setUpdatedAt(LocalDateTime.now());
        noticeEntity.setViewCount(noticeDTO.getViewCount());
        noticeEntity.setDeleted(noticeDTO.isDeleted());  // deleted 필드 추가+
//        boardEntity.setLikeCount(boardDTO.getLikeCount());
//        boardEntity.setDislikeCount(boardDTO.getDislikeCount());
        noticeEntity.setFilepath(noticeDTO.getFilepath());
        noticeEntity.setFilename(noticeDTO.getFilename());
        return noticeEntity;
    }
}
