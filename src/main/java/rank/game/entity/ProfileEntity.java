package rank.game.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@Table(name = "profile")
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num", nullable = false)
    private Long num;

    @Column(name = "email", length = 50, unique = true)
    private String memberEmail;

    @Column(name = "fileName", length = 255, nullable = false)
    private String fileName;

    @Column(name = "filePath", length = 255, nullable = false)
    private String filePath;
}
