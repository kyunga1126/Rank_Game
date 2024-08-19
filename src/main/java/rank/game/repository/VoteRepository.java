package rank.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rank.game.entity.VoteEntity;

import java.time.LocalDate;
import java.util.List;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
    List<VoteEntity> findByNickname(String nickname);

    boolean existsByNicknameAndVoteTime(String nickname, LocalDate voteTime);

    @Modifying
    @Query("UPDATE VoteEntity v SET v.nickname = :newNickname WHERE v.nickname = :oldNickname")
    void updateNickname(@Param("oldNickname") String oldNickname, @Param("newNickname") String newNickname);
}
