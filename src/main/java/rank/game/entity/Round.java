package rank.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "rounds")
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "round_name", nullable = false)
    private String roundName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game1_id", nullable = false)
    private Game game1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game2_id", nullable = false)
    private Game game2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_id")
    private Game winner;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "win_count", nullable = false)
    private int winCount; // 우승 횟수를 저장하는 필드

    public Round() {}

    public Round(String roundName, Game game1, Game game2) {
        this.roundName = roundName;
        this.game1 = game1;
        this.game2 = game2;
        this.isActive = true;
        this.winCount = 0; // 초기화 시 우승 횟수 0으로 설정
    }
}
