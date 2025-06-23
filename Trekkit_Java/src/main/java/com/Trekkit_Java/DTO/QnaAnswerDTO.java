package com.Trekkit_Java.DTO;

import lombok.Data; // Lombok @Data 임포트
import java.time.LocalDateTime;

@Data // Lombok의 @Data 어노테이션을 사용하여 Getter, Setter, toString, equals, hashCode 자동 생성
public class QnaAnswerDTO {
    private int id;
    private int questionId;
    private int userId;
    private String nickname; // 답변 작성자 닉네임 (조회용)
    private String userLoginId; // 답변 작성자의 로그인 ID (새로 추가)
    private String content;
    private int likeCount;
    private boolean isAccepted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}