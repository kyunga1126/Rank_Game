package rank.game.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_name", nullable = false)
    private String gameName;

    @Column(name = "game_vote")
    private Integer gameVote;

    @Column(name = "game_rank")
    private Integer gameRank;

    @Column(name = "game_image", nullable = false)
    private String imageUrl;

    // Default constructor for JPA
    public Game() {}

    // Parameterized constructor for convenience
    public Game(String gameName, Integer gameVote, Integer gameRank, String imageUrl) {
        this.gameName = gameName;
        this.gameVote = gameVote;
        this.gameRank = gameRank;
        this.imageUrl = imageUrl;
    }
}
