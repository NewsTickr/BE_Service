package com.newstickr.newstickr.user.controller;

import com.newstickr.newstickr.user.dto.profile.UserProfileRequest;
import com.newstickr.newstickr.user.dto.profile.UserProfileResponse;
import com.newstickr.newstickr.user.entity.User;
import com.newstickr.newstickr.user.service.FileService;
import com.newstickr.newstickr.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin
@Slf4j
@Tag(name = "User API", description = "사용자 정보 조회/수정, 프로필 업로드/조회/다운로드 관련 API")
public class UserController {
    private final UserService userService;
    private final FileService fileService;

    @GetMapping("/")
    @Operation(summary = "현재 사용자의 정보 조회(테스트용)", description = "jwt를 사용하여 정보 조회(swagger에서 확인용)")
    public  ResponseEntity<User> getUserProfile(){
        Long userId = userService.getUserIdByAuthentication();
        User result = userService.getUserInfoForTest(userId);
        if(result != null){
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/info/{userId}")
    @Operation(summary = "유저에 대한 프로필 정보를 조회(타인도 조회가능)", description = "유저 식별자(id)로 조회, 닉네임, 이메일, 프로필 이미지 경로를 반환.")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        UserProfileResponse response = userService.getUserProfile(userId);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/info")
    @Operation(summary = "유저의 회원 정보를 수정", description = "수정가능한 회원정보는 닉네임과 이메일만, 회원 식별자는 jwt에 저장된 것을 사용함.")
    public ResponseEntity<Object> updateUserProfile(@RequestBody UserProfileRequest request){
        Long userId = userService.getUserIdByAuthentication();
        boolean result = userService.updateUserProfile(userId,request);
        if(result) {
            return ResponseEntity.ok("회원정보 수정 성공");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원정보 찾을 수 없음");
    }

    @GetMapping("/profile/{filePath}")
    @Operation(summary = "유저 프로필 이미지를 조회", description = "img 태그의 src에 {ip}:{port}/user/profile/{filePath}를 넣는 형태로 사용")
    public ResponseEntity<Resource> getUserProfile(@PathVariable String filePath) {
        try {
            String decodedFilePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
            Resource result = fileService.getImg(decodedFilePath);
            if(result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/download/{filePath}")
    @Operation(summary = "회원 정보 조회를 사용하여얻은 filePaht로 이미지를 다운로드")
    public ResponseEntity<Resource> downloadUserProfile(@PathVariable String filePath) {
        try{
            String decodedFilePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
            // 디코딩된 경로를 이용해 파일을 읽음
            return fileService.downloadImg(decodedFilePath);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }


    }

    @PostMapping(name = "/profile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저의 프로필 이미지를 업로드", description = "Multipart 형태의 이미지를 업로드")
    public ResponseEntity<Object> updateProfile(@RequestParam("file")MultipartFile file){
        Long userId = userService.getUserIdByAuthentication();

        String filePath = fileService.uploadImg(file);
        if (filePath == null) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 로직 오류");
        }
        boolean result = userService.updateUserProfileImg(userId,filePath);
        if(result) {
            return ResponseEntity.ok("프로필 업로드 성공");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원정보 찾을 수 없음");
    }

}
