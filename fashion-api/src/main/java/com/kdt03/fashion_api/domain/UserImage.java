package com.kdt03.fashion_api.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadDate;

    @Column(columnDefinition = "vector")
    private float[] embedding;
}
