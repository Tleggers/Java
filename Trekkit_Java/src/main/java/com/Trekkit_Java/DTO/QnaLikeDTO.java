package com.Trekkit_Java.DTO;

import java.time.LocalDateTime; // Java 8의 날짜/시간 API인 LocalDateTime 사용
import lombok.Data; // Lombok의 @Data 어노테이션 임포트

/**
 * Q&A 좋아요(Like)의 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 데이터베이스와 애플리케이션 계층 간에 좋아요 데이터를 전송하는 데 사용됩니다.
 * 질문 또는 답변에 대한 좋아요 정보를 담을 수 있습니다.
 * Lombok의 @Data 어노테이션을 사용하여 Getter, Setter, toString, equals, hashCode 메서드가 자동으로 생성됩니다.
 */
@Data // Lombok: Getter, Setter, toString, equals, hashCode를 자동으로 생성합니다.
public class QnaLikeDTO {
    private int id; // 좋아요 ID (PRIMARY KEY, 자동 증가)
    private Integer questionId; // 좋아요가 질문에 대한 것일 경우 질문 ID (NULL 허용)
    private Integer answerId;   // 좋아요가 답변에 대한 것일 경우 답변 ID (NULL 허용)
    private int userId; // 좋아요를 누른 사용자의 ID
    private LocalDateTime createdAt; // 좋아요를 누른 시각
}
