package com.kdt03.fashion_api.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt03.fashion_api.domain.Member;
import com.kdt03.fashion_api.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final String BASE_DIR = "C:/uploads/";
    private final MemberRepository memberRepo;

    @Transactional
    public String uploadImage(MultipartFile file) throws IOException {
        return saveFile(file, "clothes/");
    }

    @Transactional
    public String uploadProfileImage(MultipartFile file, String id) throws IOException {
        String savedPath = saveFile(file, "profiles/");

        Member member = memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("없는 회원입니다."));
        
        member.setProfile("/uploads/" + savedPath);

        return savedPath;
    }

    private String saveFile(MultipartFile file, String subDir) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID() + "_" + originalFilename;

        File dest = new File(BASE_DIR + subDir + savedFilename);

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);

        return subDir + savedFilename;
    }

}