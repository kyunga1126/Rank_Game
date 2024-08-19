package rank.game.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rank.game.dto.MemberDTO;
import rank.game.entity.MemberEntity;
import rank.game.repository.MemberRepository;
import rank.game.repository.VoteRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Member;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final VoteRepository voteRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    private static String number;
    private final String senderEmail = "myoping99@gmail.com";


    // 회원가입
    public void save(MemberDTO memberDTO, String memberPassword) {
        // 모든 회원은 기본적으로 "USER" 권한을 가짐
        memberDTO.setRole("ROLE_USER");
        memberDTO.setMemberPassword(passwordEncoder.encode(memberPassword));

        // 특정 아이디와 비밀번호를 가진 회원에게 "ADMIN" 권한 부여
        if ("admin@admin.com".equals(memberDTO.getMemberEmail()) && "123456789".equals(memberPassword)) {
            memberDTO.setRole("ROLE_ADMIN");
        }

        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        memberRepository.save(memberEntity);
    }

    // 어드민 계정 생성
    public void createAdminAccount() {
        if (!memberRepository.existsByMemberEmail("admin@admin.com")) {
            MemberDTO adminDTO = new MemberDTO();
            adminDTO.setMemberEmail("admin@admin.com");
            adminDTO.setMemberPassword("123456789");
            adminDTO.setMemberName("Admin");
            adminDTO.setNickname("Admin");
            adminDTO.setRole("ROLE_ADMIN");
            save(adminDTO, "123456789");
            log.info("Admin account created: admin@admin.com");
        }
    }

    // 이메일 중복 확인
    public boolean emailExists(String memberEmail) {
        return memberRepository.existsByMemberEmail(memberEmail);
    }

    // 닉네임 중복 확인
    public boolean nicknameExists(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 로그인
    public MemberDTO login(MemberDTO memberDTO) {
        Optional<MemberEntity> byMemberEmail = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
        if (byMemberEmail.isPresent()) {
            MemberEntity memberEntity = byMemberEmail.get();
            String encodedPassword = memberEntity.getMemberPassword();
            if (passwordEncoder.matches(memberDTO.getMemberPassword(), encodedPassword)) {
                return MemberDTO.fromEntity(memberEntity);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Transactional
    public void updateMemberInfo(String memberEmail, String memberNickname, String memberPassword) {
        // 이메일을 기준으로 회원 정보 조회
        MemberEntity member = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("No member found with email: " + memberEmail));

        String oldNickname = member.getNickname();

        // 닉네임 업데이트
        if (memberNickname != null && !memberNickname.isEmpty() && !memberNickname.equals(oldNickname)) {
            // 닉네임 중복 확인
            if (memberRepository.existsByNickname(memberNickname)) {
                throw new IllegalArgumentException("중복된 닉네임입니다.");
            }
            member.setNickname(memberNickname);

            // 투표 엔티티의 닉네임 업데이트
            voteRepository.updateNickname(oldNickname, memberNickname);
        }

        // 비밀번호 업데이트
        if (memberPassword != null && !memberPassword.isEmpty()) {
            member.setMemberPassword(passwordEncoder.encode(memberPassword));
        }

        // 업데이트된 회원 정보 저장
        memberRepository.save(member);
    }

    //회원탈퇴
    public void deleteMember(String memberEmail) {
        Optional<MemberEntity> member = memberRepository.findByMemberEmail(memberEmail);

        if (member.isPresent()) {
            // Delete the member
            memberRepository.delete(member.get());
            log.info("Member with email {} has been deleted.", memberEmail);
        } else {
            // Throw an exception if the member is not found
            throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
        }
    }

    public Page<MemberDTO> findAllMembers(Pageable pageable) {
        Page<MemberEntity> memberEntities = memberRepository.findAll(pageable);
        return memberEntities.map(MemberDTO::fromEntity);
    }

    // 권한 변경
    public boolean changeMemberRole(Long memberId, String newRole) {
        Optional<MemberEntity> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
            MemberEntity member = memberOptional.get();
            member.setRole(newRole);
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    //메일 전송
    @Transactional
    public void updatePassword(String memberEmail, String temporaryPassword) {
        MemberEntity member = memberRepository.findByMemberEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("No member found with email: " + memberEmail));

        // 비밀번호를 암호화하여 임시 비밀번호로 설정
        member.setMemberPassword(passwordEncoder.encode(temporaryPassword));
        memberRepository.save(member); // 데이터베이스에 새로운 비밀번호 저장
    }

    public MimeMessage createMessage(String email, String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setFrom(senderEmail);
        messageHelper.setTo(email);
        messageHelper.setSubject(subject);
        messageHelper.setText(content, true);

        return mimeMessage;
    }

    public void sendPasswordResetEmail(String email, String temporaryPassword) throws MessagingException {
        String subject = "RankGame 임시 비밀번호 안내";
        String content = "고객님의 임시 비밀번호는 " + temporaryPassword + " 입니다. 로그인 후 비밀번호를 변경해 주세요.";
        MimeMessage mimeMessage = createMessage(email, subject, content);
        javaMailSender.send(mimeMessage);
    }

    public static String createNumber() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) { // 총 8자리 인증 번호 생성
            int idx = random.nextInt(3); // 0~2 사이의 값을 랜덤하게 받아와 idx에 집어넣습니다

            // 0,1,2 값을 switch case를 통해 꼬아버립니다.
            // 숫자와 ASCII 코드를 이용합니다.
            switch (idx) {
                case 0:
                    // 0일 때, a~z 까지 랜덤 생성 후 key에 추가
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1:
                    // 1일 때, A~Z 까지 랜덤 생성 후 key에 추가
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    // 2일 때, 0~9 까지 랜덤 생성 후 key에 추가
                    key.append(random.nextInt(10));
                    break;
            }
        }
        return key.toString();
    }

    public String sendMail(String email) throws MessagingException {
        String number = createNumber();
        log.info("Number : {}", number);

        String subject = "[RankGame] 이메일 인증 번호 발송";
        String content = "<html><body style='background-color: #000000 !important; margin: 0 auto; max-width: 600px; word-break: break-all; padding-top: 50px; color: #ffffff;'>";
        content += "<h1 style='padding-top: 50px; font-size: 30px;'>이메일 주소 인증</h1>";
        content += "<p style='padding-top: 20px; font-size: 18px; opacity: 0.6; line-height: 30px; font-weight: 400;'>안녕하세요? RankGame 관리자 입니다.<br />";
        content += "RankGame 서비스 사용을 위해 회원가입시 고객님께서 입력하신 이메일 주소의 인증이 필요합니다.<br />";
        content += "하단의 인증 번호로 이메일 인증을 완료하시면, 정상적으로 RankGame 서비스를 이용하실 수 있습니다.<br />";
        content += "항상 최선의 노력을 다하는 RankGame이 되겠습니다.<br />";
        content += "감사합니다.</p>";
        content += "<div class='code-box' style='margin-top: 50px; padding-top: 20px; color: #000000; padding-bottom: 20px; font-size: 25px; text-align: center; background-color: #f4f4f4; border-radius: 10px;'>" + number + "</div>";
        content += "</body></html>";

        MimeMessage mimeMessage = createMessage(email, subject, content);
        javaMailSender.send(mimeMessage);
        log.info("[Mail 전송 완료]");

        return number;
    }
}