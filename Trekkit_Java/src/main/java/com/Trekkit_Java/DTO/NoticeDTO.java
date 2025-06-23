package com.Trekkit_Java.DTO;

import lombok.Data; // Lombok의 @Data 어노테이션 임포트
import java.time.LocalDateTime; // Java 8의 날짜/시간 API인 LocalDateTime 사용

/**
 * 공지사항의 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 데이터베이스와 애플리케이션 계층 간에 공지사항 데이터를 전송하는 데 사용됩니다.
 * Lombok의 @Data 어노테이션을 사용하여 Getter, Setter, toString, equals, hashCode 메서드가 자동으로 생성됩니다.
 */
@Data // Lombok: Getter, Setter, toString, equals, hashCode를 자동으로 생성합니다.
public class NoticeDTO {
    private int id; // 공지사항 ID (PRIMARY KEY, 자동 증가)
    private Integer userId; // 공지사항 작성자의 사용자 ID (User 테이블 참조, NULL 가능)
    private String title; // 공지사항 제목
    private String content; // 공지사항 내용
    private String category; // 공지사항 카테고리
    private int viewCount; // 조회수 (기본값 0)
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime updatedAt; // 수정일시
    private String nickname; // 작성자 닉네임 (조인(JOIN)을 통해 채워짐)
}
