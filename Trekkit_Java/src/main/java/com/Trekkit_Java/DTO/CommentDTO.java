package com.Trekkit_Java.DTO;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private String nickname;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    // --- 추가: 댓글 작성자의 로그인 ID (userid)를 담을 필드 ---
    private String userLoginId; // user 테이블의 'userid' 필드를 매핑할 것입니다.
}