package rank.game.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rank.game.entity.MemberEntity;

import java.util.Optional;


@Repository
//Entity이름과 Entity PK컬럼의 타입
//DB를 넘길때 반드시 Entity객체로 넘겨야 한다.
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    boolean existsByMemberEmail(String memberEmail);
    boolean existsByNickname(String nickname);
    Optional<MemberEntity> findByMemberEmail(String memberEmail);

}
