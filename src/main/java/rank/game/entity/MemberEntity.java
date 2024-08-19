package rank.game.entity;
// 테이블 역할

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import rank.game.dto.MemberDTO;

@Entity
@Setter
@Getter
@ToString
@Table(name = "membership")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num", nullable = false)
    private Long num;

    @Column(name = "email", length = 50, unique = true)
    private String memberEmail;

    @Column(name = "pwd", length = 255, nullable = false)
    private String memberPassword;

    @Column(name = "name", length = 10, nullable = false)
    private String memberName;

    @Column(name = "nickname", length = 8)
    private String nickname;

    @Column(name = "role", length = 20)
    private String role;

    public static MemberEntity toMemberEntity(MemberDTO memberDTO) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNum(memberDTO.getNum());
        memberEntity.setMemberEmail(memberDTO.getMemberEmail());
        memberEntity.setMemberPassword(memberDTO.getMemberPassword());
        memberEntity.setMemberName(memberDTO.getMemberName());
        memberEntity.setNickname(memberDTO.getNickname());
        memberEntity.setRole(memberDTO.getRole()); // Role 설정 추가
        return memberEntity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MemberEntity that = (MemberEntity) obj;
        return num != null && num.equals(that.num);
    }

    @Override
    public int hashCode() {
        return 31 * (num != null ? num.hashCode() : 0);
    }
}