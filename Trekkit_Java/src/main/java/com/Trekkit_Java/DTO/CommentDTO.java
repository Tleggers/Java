package com.Trekkit_Java.DTO;

import java.sql.Timestamp; // SQL Timestamp 타입 사용
import lombok.Data; // Lombok의 @Data 어노테이션 임포트

/**
 * 댓글의 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 데이터베이스와 애플리케이션 계층 간에 댓글 데이터를 전송하는 데 사용됩니다.
 * Lombok의 @Data 어노테이션을 사용하여 Getter, Setter, toString, equals, hashCode 메서드가 자동으로 생성됩니다.
 */
@Data // Lombok: Getter, Setter, toString, equals, hashCode를 자동으로 생성합니다.
public class CommentDTO {
    private Long id; // 댓글 ID (PRIMARY KEY)
    private Long postId; // 댓글이 속한 게시글의 ID (Post 테이블 참조)
    private Long userId; // 댓글 작성자의 사용자 ID (User 테이블 참조)
    private String nickname; // 댓글 작성자의 닉네임 (조회용)
    private String content; // 댓글 내용
    private Timestamp createdAt; // 댓글 생성 일시
    private Timestamp updatedAt; // 댓글 수정 일시
    private String userLoginId; // 댓글 작성자의 로그인 ID (사용자 ID 문자열, 조회용)
}
