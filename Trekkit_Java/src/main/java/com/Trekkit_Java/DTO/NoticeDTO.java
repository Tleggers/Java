package com.Trekkit_Java.DTO;

import java.time.LocalDateTime; // SQL Timestamp 대신 Java 8의 날짜/시간 API인 LocalDateTime 사용

/**
 * 공지사항 데이터 전송 객체 (DTO)
 * 이 클래스는 데이터베이스의 'notices' 테이블과 연동되어 데이터를 주고받는 역할을 합니다.
 */
public class NoticeDTO {

    private int noticeId;       // 공지사항 ID (PRIMARY KEY, 자동 증가)
    private Integer userId;     // 작성자 ID (관리자) - 'user' 테이블의 'id'를 참조합니다. NULL이 가능하므로 Integer 타입 사용.
    private String title;       // 공지사항 제목
    private String content;     // 공지사항 내용
    private String category;    // 공지사항 카테고리 (예: "공통", "점검")
    private int viewCount;      // 공지사항 조회수 (기본값 0)
    private LocalDateTime createdAt; // 생성일시 (레코드 생성 시 자동 설정)
    private LocalDateTime updatedAt; // 수정일시 (레코드 업데이트 시 자동 설정)

    // user_id가 NULL인 경우를 대비하여 DTO에 닉네임 필드를 추가했습니다.
    // 이 필드는 데이터베이스 조인(JOIN)을 통해 채워지며, 서비스/컨트롤러 로직에서는 사용되지 않고 응답 데이터에 포함됩니다.
    private String nickname; // 작성자 닉네임

    // 기본 생성자: Spring이 객체를 생성할 때 사용합니다.
    public NoticeDTO() {}

    // Getter와 Setter 메서드: 각 필드의 값을 읽고 설정하는 역할을 합니다.
    // 이 메서드들은 MyBatis가 데이터베이스 컬럼과 DTO 필드를 매핑할 때,
    // 그리고 Spring이 HTTP 요청 본문(JSON)을 객체로 변환하거나 객체를 JSON으로 변환할 때 사용됩니다.

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
