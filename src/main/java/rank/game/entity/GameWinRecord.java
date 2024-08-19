package rank.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "game_win_records")
public class GameWinRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "win_count", nullable = false)
    private int winCount; // 총 우승 횟수

    public GameWinRecord() {}

    public GameWinRecord(Game game) {
        this.game = game;
        this.winCount = 0; // 초기 우승 횟수는 0으로 설정
    }

    public void incrementWins() {
        this.winCount++;
    }
}
