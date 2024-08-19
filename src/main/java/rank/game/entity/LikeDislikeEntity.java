package rank.game.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "like_dislike")
public class LikeDislikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Column(name = "is_like")
    private boolean isLike;

    public LikeDislikeEntity() {}

    public LikeDislikeEntity(BoardEntity board, MemberEntity member, boolean isLike) {
        this.board = board;
        this.member = member;
        this.isLike = isLike;
    }
}
