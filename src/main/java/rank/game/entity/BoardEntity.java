package rank.game.entity;

import jakarta.persistence.*;
import lombok.Data;
import rank.game.dto.BoardDTO;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "board")
public class BoardEntity {

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

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "dislike_count")
    private Integer dislikeCount = 0;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

//     BoardDTO 객체를 BoardEntity로 변환하는 메서드
    public static BoardEntity toBoardEntity(BoardDTO boardDTO) {
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setMemberEmail(boardDTO.getMemberEmail());
        boardEntity.setNickname(boardDTO.getNickname());
        boardEntity.setTitle(boardDTO.getTitle());
        boardEntity.setContent(boardDTO.getContent());
        boardEntity.setCreatedAt(LocalDateTime.now());
        boardEntity.setUpdatedAt(LocalDateTime.now());
        boardEntity.setViewCount(boardDTO.getViewCount());
        boardEntity.setDeleted(boardDTO.isDeleted());  // deleted 필드 추가+
        boardEntity.setLikeCount(boardDTO.getLikeCount());
        boardEntity.setDislikeCount(boardDTO.getDislikeCount());
        boardEntity.setFilepath(boardDTO.getFilepath());
        boardEntity.setFilename(boardDTO.getFilename());
        return boardEntity;
    }
}
