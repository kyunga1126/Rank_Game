package rank.game.dto;

import lombok.*;
import rank.game.entity.BoardEntity;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardDTO {

    private Long id;
    private String memberEmail;  // 사용자 ID, 엔티티와 달리 DTO에서는 String 타입으로 처리할 수 있습니다.
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String filename;
    private String filepath;
    private int viewCount;
    private boolean deleted = false;  // 논리 삭제를 위한 필드
    private int likeCount;
    private int dislikeCount;
    private int commentCount;

    public static BoardDTO fromEntity(BoardEntity boardEntity) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setMemberEmail(boardEntity.getMemberEmail());
        boardDTO.setNickname(boardEntity.getNickname());
        boardDTO.setTitle(boardEntity.getTitle());
        boardDTO.setContent(boardEntity.getContent());
        boardDTO.setFilename(boardEntity.getFilename());
        boardDTO.setFilepath(boardEntity.getFilepath());
        boardDTO.setViewCount(boardEntity.getViewCount());
        boardDTO.setCreatedAt(boardEntity.getCreatedAt());
        boardDTO.setUpdatedAt(boardEntity.getUpdatedAt());
        boardDTO.setDeleted(boardEntity.isDeleted());
        boardDTO.setLikeCount(boardEntity.getLikeCount());
        boardDTO.setDislikeCount(boardEntity.getDislikeCount());
        return boardDTO;
    }
}
