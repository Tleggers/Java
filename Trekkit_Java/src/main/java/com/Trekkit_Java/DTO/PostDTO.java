package com.Trekkit_Java.DTO;

import java.sql.Timestamp;
import java.util.List;
import lombok.Data;

@Data
public class PostDTO {
    private int id;
    private Long userId;
    private String nickname;
    private String title;
    private String content;
    private String mountain;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<String> imagePaths;
}