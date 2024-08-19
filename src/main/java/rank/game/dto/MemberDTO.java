package rank.game.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import rank.game.entity.MemberEntity;

@Getter
@Setter
@ToString
public class MemberDTO {
    private Long num;
    private String memberEmail;
    private String memberPassword;
    private String memberName;
    private String nickname;
    private String role;

    public static MemberDTO fromEntity(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setNum(memberEntity.getNum());
        memberDTO.setMemberEmail(memberEntity.getMemberEmail());
        memberDTO.setMemberPassword(memberEntity.getMemberPassword());
        memberDTO.setMemberName(memberEntity.getMemberName());
        memberDTO.setNickname(memberEntity.getNickname());
        memberDTO.setRole(memberEntity.getRole()); // Role 설정 추가
        return memberDTO;
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(this.role);
    }

    public boolean isManager() {
        return "ROLE_MANAGER".equals(this.role);
    }
}
