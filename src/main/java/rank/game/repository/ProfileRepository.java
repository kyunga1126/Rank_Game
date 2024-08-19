package rank.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rank.game.entity.ProfileEntity;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    ProfileEntity findByMemberEmail(String email);
}
