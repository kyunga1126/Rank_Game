package rank.game.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rank.game.entity.MemberEntity;
import rank.game.entity.ProfileEntity;
import rank.game.entity.VoteEntity;
import rank.game.repository.MemberRepository;
import rank.game.repository.ProfileRepository;
import rank.game.repository.VoteRepository;
import rank.game.service.MemberService;
import rank.game.service.ProfileService;
import rank.game.service.VoteService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final VoteRepository voteRepository;
    private final VoteService voteService;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> handleFileUpload(@RequestParam("file") MultipartFile file, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String memberEmail = (String) session.getAttribute("loginEmail");
            if (memberEmail == null) {
                response.put("success", false);
                response.put("message", "세션에 이메일이 없습니다.");
                return response;
            }

            String fileUrl = profileService.saveProfile(memberEmail, file);
            System.out.println("File URL: " + fileUrl); // 로그 출력

            response.put("success", true);
            response.put("imageUrl", fileUrl);
        } catch (Exception e) {
            log.error("File upload error", e);
            response.put("success", false);
            response.put("message", "파일 업로드 중 오류 발생");
        }
        return response;
    }

    @GetMapping("/mainMyPage")
    public String mainMyPage(HttpSession session, Model model) {
        boolean isLogin = session.getAttribute("loginEmail") != null;
        model.addAttribute("isLogin", isLogin);

        String memberEmail = (String) session.getAttribute("loginEmail");
        if (memberEmail != null) {
            Optional<MemberEntity> memberInfo = memberRepository.findByMemberEmail(memberEmail);

            if (memberInfo.isPresent()) {
                MemberEntity member = memberInfo.get();
                List<VoteEntity> votes = voteRepository.findByNickname(member.getNickname());
                votes.sort((v1, v2) -> v2.getVoteTime().compareTo(v1.getVoteTime()));

                Map<LocalDate, List<String>> groupedVotes = votes.stream()
                        .collect(Collectors.groupingBy(
                                VoteEntity::getVoteTime,
                                Collectors.mapping(VoteEntity::getGameName, Collectors.toList())
                        ));

                Optional<ProfileEntity> profileOpt = profileService.getProfileByEmail(memberEmail);
                String profileImageUrl = profileOpt.map(ProfileEntity::getFilePath)
                        .orElse("https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdAqmkb%2FbtsHBpCK2sR%2F5f2REVwRSQkiJ3qWkAcZkK%2Fimg.png");

                model.addAttribute("memberEmail", memberEmail);
                model.addAttribute("memberName", member.getMemberName());
                model.addAttribute("memberNickname", member.getNickname());
                model.addAttribute("groupedVotes", groupedVotes);
                model.addAttribute("profileImageUrl", profileImageUrl);
            } else {
                model.addAttribute("error", "회원 정보를 찾을 수 없습니다.");
            }
        } else {
            model.addAttribute("error", "로그인 상태를 확인할 수 없습니다.");
        }

        return "html/myPage";
    }


    @PostMapping("/change")
    public String updateMember(HttpSession session,
                               @RequestParam(required = false) String memberNickname,
                               @RequestParam(required = false) String memberPassword,
                               Model model) {
        String memberEmail = (String) session.getAttribute("loginEmail");

        // 로그 출력
        log.info("memberEmail: {}", memberEmail);
        log.info("memberNickname: {}", memberNickname);
        log.info("memberPassword: {}", memberPassword);

        if (memberEmail != null) {
            try {
                // 회원 정보 업데이트
                memberService.updateMemberInfo(memberEmail, memberNickname, memberPassword);
                model.addAttribute("message", "회원 정보가 성공적으로 업데이트되었습니다.");
                model.addAttribute("searchUrl", "/");

                // 업데이트된 회원 정보를 다시 조회합니다.
                Optional<MemberEntity> updatedMemberInfo = memberRepository.findByMemberEmail(memberEmail);

                if (updatedMemberInfo.isPresent()) {
                    // 업데이트된 정보를 모델에 추가합니다.
                    model.addAttribute("memberEmail", memberEmail);
                    model.addAttribute("memberName", updatedMemberInfo.get().getMemberName());
                    model.addAttribute("memberNickname", updatedMemberInfo.get().getNickname());
                } else {
                    model.addAttribute("error", "업데이트된 회원 정보를 찾을 수 없습니다.");
                }

            } catch (IllegalArgumentException e) {
                if ("중복된 닉네임입니다.".equals(e.getMessage())) {
                    model.addAttribute("message", "중복된 닉네임입니다.");
                    model.addAttribute("searchUrl", "/profile/mainMyPage");
                } else {
                    model.addAttribute("error", e.getMessage());
                }
                log.error("Error updating member info: {}", e.getMessage());
            } catch (Exception e) {
                model.addAttribute("error", "회원 정보를 업데이트하는 중 오류가 발생했습니다.");
                log.error("Unexpected error: ", e);
            }
        } else {
            model.addAttribute("error", "로그인 상태를 확인할 수 없습니다.");
        }

        return "html/message"; // 업데이트된 정보를 포함하여 뷰로 이동
    }

    @PostMapping("/delete")
    public String memberDelete(HttpSession session, Model model) {
        String memberEmail = (String) session.getAttribute("loginEmail");

        if (memberEmail != null) {
            try {
                // 회원 탈퇴 처리
                memberService.deleteMember(memberEmail);
                session.invalidate(); // 세션 무효화

                model.addAttribute("message", "회원 탈퇴가 성공적으로 완료되었습니다.");
                model.addAttribute("searchUrl", "/");

            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                log.error("Error deleting member: {}", e.getMessage());
            } catch (Exception e) {
                model.addAttribute("error", "회원 탈퇴 중 오류가 발생했습니다.");
                log.error("Unexpected error: ", e);
            }
        } else {
            model.addAttribute("error", "로그인 상태를 확인할 수 없습니다.");
        }

        return "html/message";
    }
}
