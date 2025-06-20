package com.Trekkit_Java.DTO;

import java.sql.Timestamp;
import java.util.List;
import lombok.Data;

@Data
public class PostDTO {
    private Long id;
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
    // --- 추가: 게시글 작성자의 로그인 ID (userid)를 담을 필드 ---
    private String userLoginId; // user 테이블의 'userid' 필드를 매회핑할 것입니다.
}