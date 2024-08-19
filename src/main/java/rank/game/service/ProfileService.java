package rank.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rank.game.entity.ProfileEntity;
import rank.game.repository.ProfileRepository;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public String saveProfile(String memberEmail, MultipartFile file) throws Exception {
        String profilePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\uploads";
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(profilePath, fileName);

        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }

        file.transferTo(saveFile);

        Optional<ProfileEntity> optionalProfile = Optional.ofNullable(profileRepository.findByMemberEmail(memberEmail));
        ProfileEntity profile;
        if (optionalProfile.isPresent()) {
            profile = optionalProfile.get();
        } else {
            profile = new ProfileEntity();
            profile.setMemberEmail(memberEmail);
        }

        profile.setFileName(fileName);
        profile.setFilePath("/uploads/" + fileName);
        System.out.println("프로필 저장 이름 " + profile.getFilePath());

        profileRepository.save(profile);

        return profile.getFilePath();
    }

    public Optional<ProfileEntity> getProfileByEmail(String memberEmail) {
        return Optional.ofNullable(profileRepository.findByMemberEmail(memberEmail));
    }
}