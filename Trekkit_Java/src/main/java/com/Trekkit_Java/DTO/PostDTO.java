package com.Trekkit_Java.DTO;

import java.sql.Timestamp; // SQL Timestamp 타입 사용
import java.util.List; // List 인터페이스 사용
import lombok.Data; // Lombok의 @Data 어노테이션 임포트

/**
 * 게시글의 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 데이터베이스와 애플리케이션 계층 간에 게시글 데이터를 전송하는 데 사용됩니다.
 * Lombok의 @Data 어노테이션을 사용하여 Getter, Setter, toString, equals, hashCode 메서드가 자동으로 생성됩니다.
 */
@Data // Lombok: Getter, Setter, toString, equals, hashCode를 자동으로 생성합니다.
public class PostDTO {
    private Long id; // 게시글 ID (PRIMARY KEY)
    private Long userId; // 게시글 작성자의 사용자 ID (User 테이블 참조)
    private String nickname; // 게시글 작성자의 닉네임
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String mountain; // 관련 산 이름
    private int viewCount; // 조회수 (기본값 0)
    private int likeCount; // 좋아요 수 (기본값 0)
    private int commentCount; // 댓글 수 (기본값 0)
    private Timestamp createdAt; // 게시글 생성 일시
    private Timestamp updatedAt; // 게시글 수정 일시
    private List<String> imagePaths; // 게시글에 첨부된 이미지 경로 목록
    private String userLoginId; // 게시글 작성자의 로그인 ID (사용자 ID 문자열, 조회용)
}
