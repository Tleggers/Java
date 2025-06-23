package com.Trekkit_Java.DTO;

import lombok.Data; // Lombok의 @Data 어노테이션 임포트
import java.time.LocalDateTime; // Java 8의 날짜/시간 API인 LocalDateTime 사용

/**
 * Q&A 답변의 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 데이터베이스와 애플리케이션 계층 간에 답변 데이터를 전송하는 데 사용됩니다.
 * Lombok의 @Data 어노테이션을 사용하여 Getter, Setter, toString, equals, hashCode 메서드가 자동으로 생성됩니다.
 */
@Data // Lombok: Getter, Setter, toString, equals, hashCode를 자동으로 생성합니다.
public class QnaAnswerDTO {
    private int id; // 답변 ID (PRIMARY KEY, 자동 증가)
    private int questionId; // 답변이 속한 질문의 ID (QnaQuestion 테이블 참조)
    private int userId; // 답변 작성자의 사용자 ID (User 테이블 참조)
    private String nickname; // 답변 작성자 닉네임 (조회용)
    private String userLoginId; // 답변 작성자의 로그인 ID (사용자 ID 문자열, 조회용)
    private String content; // 답변 내용
    private int likeCount; // 좋아요 수 (기본값 0)
    private boolean isAccepted; // 채택 여부 (true/false)
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime updatedAt; // 수정일시
}
