package com.newstickr.newstickr.user.service;

import com.newstickr.newstickr.user.dto.CustomOAuth2User;
import com.newstickr.newstickr.user.dto.profile.UserProfileRequest;
import com.newstickr.newstickr.user.dto.profile.UserProfileResponse;
import com.newstickr.newstickr.user.entity.User;
import com.newstickr.newstickr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserProfileResponse getUserProfile(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserProfileResponse userProfileResponse = new UserProfileResponse();
            userProfileResponse.setProfileImg(userOptional.get().getProfileImg());
            userProfileResponse.setName(userOptional.get().getName());
            userProfileResponse.setEmail(userOptional.get().getEmail());
            return userProfileResponse;
        }
        // [TODO] 사용자가 없을 경우 어떻게 처리할지 고민
        return null;
    }

    public boolean updateUserProfile(Long userId, UserProfileRequest userProfileRequest) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName(userProfileRequest.getName());
            user.setEmail(userProfileRequest.getEmail());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean updateUserProfileImg(Long userId, String profilePath) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            // [TODO] 사용자가 없을 경우 어떻게 처리할지 고민.
            return false;
        }
        try{
            // [TODO]이전 이미지 삭제 기능 필요한지는 잘 모르겠음.
            // 유저 객체의 프로필이미지 경로 수정
            User user = userOptional.get();
            user.setProfileImg(profilePath);
            userRepository.save(user);
            return true;
        }catch (Exception e){
            log.error(e.getMessage())       ;
            return false;
        }
    }
    public Long getUserIdByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null){
            return null;
        }
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        log.info(user.getId());
        String userId = user.getId();
        return Long.parseLong(userId);
    }

    public User getUserInfoForTest(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
    }
}
