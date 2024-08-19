package rank.game.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "game_history")
public class GameHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "game_name", nullable = false)
    private String gameName;

    @Column(name = "game_vote", nullable = false)
    private Integer gameVote;

    @Column(name = "game_rank", nullable = false)
    private Integer gameRank;

    @Column(name = "vote_time", nullable = false)
    private LocalDateTime voteTime;
}
