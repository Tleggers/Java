package com.Trekkit_Java.DTO;

import java.time.LocalDateTime; // Java 8의 날짜/시간 API인 LocalDateTime 사용
import lombok.Data; // Lombok의 @Data 어노테이션 임포트

/**
 * Q&A 질문의 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 데이터베이스와 애플리케이션 계층 간에 질문 데이터를 전송하는 데 사용됩니다.
 * Lombok의 @Data 어노테이션을 사용하여 Getter, Setter, toString, equals, hashCode 메서드가 자동으로 생성됩니다.
 */
@Data // Lombok: Getter, Setter, toString, equals, hashCode를 자동으로 생성합니다.
public class QnaQuestionDTO {
    private int id; // 질문 ID (PRIMARY KEY, 자동 증가)
    private int userId; // 작성자 ID (User 테이블 참조)
    private String nickname; // 작성자 닉네임 (조회용으로 사용될 수 있음)
    private String title; // 질문 제목
    private String content; // 질문 내용
    private String mountain; // 관련 산 이름 (선택 사항)
    private int viewCount; // 조회수 (기본값 0)
    private int answerCount; // 답변 수 (기본값 0)
    private int likeCount; // 좋아요 수 (기본값 0)
    private boolean isSolved; // 해결 여부 (true/false, 기본값 false)
    private Integer acceptedAnswerId; // 채택된 답변의 ID (NULL 가능하므로 Integer 타입 사용)
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime updatedAt; // 수정일시
}
