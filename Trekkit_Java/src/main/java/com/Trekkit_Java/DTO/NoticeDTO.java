package com.Trekkit_Java.DTO;

import lombok.Data; // Lombok의 @Data 어노테이션 임포트
import java.time.LocalDateTime;

@Data // @Data 어노테이션을 사용하여 Getter, Setter, toString, equals, hashCode 자동 생성
public class NoticeDTO {
    private int id; // noticeId -> id로 변경 (DB 컬럼명과 일치)
    private Integer userId;
    private String title;
    private String content;
    private String category;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nickname; // 작성자 닉네임 (JOIN으로 채워짐)
}