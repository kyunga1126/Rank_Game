package rank.game.dto;

import lombok.Data;
import rank.game.entity.CommentEntity;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private Long boardId;
    private Long memberNum;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentDTO fromEntity(CommentEntity commentEntity) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(commentEntity.getId());
        commentDTO.setContent(commentEntity.getContent());
        commentDTO.setCreatedAt(commentEntity.getCreatedAt());
        commentDTO.setBoardId(commentEntity.getBoard().getId());
        commentDTO.setMemberNum(commentEntity.getMember().getNum());
        commentDTO.setNickname(commentEntity.getMember().getNickname());
        commentDTO.setUpdatedAt(commentEntity.getUpdatedAt());
        return commentDTO;
    }

}
