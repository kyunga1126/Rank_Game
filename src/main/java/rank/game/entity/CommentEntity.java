package rank.game.entity;

import jakarta.persistence.*;
import lombok.Data;
import rank.game.dto.CommentDTO;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comment")
public class CommentEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "board_id", nullable = false)
        private BoardEntity board;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", nullable = false)
        private MemberEntity member;

        @Column(name = "content", nullable = false)
        private String content;

        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @PrePersist
        protected void onCreate() {
                createdAt = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
                updatedAt = LocalDateTime.now();
        }

        public static CommentEntity toEntity(CommentDTO commentDTO, BoardEntity board, MemberEntity member) {
                CommentEntity commentEntity = new CommentEntity();
                commentEntity.setContent(commentDTO.getContent());
                commentEntity.setBoard(board);
                commentEntity.setMember(member);
                commentEntity.setCreatedAt(LocalDateTime.now());
                commentEntity.setUpdatedAt(LocalDateTime.now());
                return commentEntity;
        }

}
