package rank.game.dto;

import lombok.*;
import rank.game.entity.NoticeEntity;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class NoticeDTO {

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
//    private int likeCount;
//    private int dislikeCount;
    private int commentCount;
    private boolean isActive;

    public static NoticeDTO fromEntity(NoticeEntity noticeEntity) {
        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setId(noticeEntity.getId());
        noticeDTO.setMemberEmail(noticeEntity.getMemberEmail());
        noticeDTO.setNickname(noticeEntity.getNickname());
        noticeDTO.setTitle(noticeEntity.getTitle());
        noticeDTO.setContent(noticeEntity.getContent());
        noticeDTO.setFilename(noticeEntity.getFilename());
        noticeDTO.setFilepath(noticeEntity.getFilepath());
        noticeDTO.setViewCount(noticeEntity.getViewCount());
        noticeDTO.setCreatedAt(noticeEntity.getCreatedAt());
        noticeDTO.setUpdatedAt(noticeEntity.getUpdatedAt());
        noticeDTO.setDeleted(noticeEntity.isDeleted());
//        boardDTO.setLikeCount(boardEntity.getLikeCount());
//        boardDTO.setDislikeCount(boardEntity.getDislikeCount());
        return noticeDTO;
    }
}
