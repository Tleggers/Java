package com.Trekkit_Java.DTO;

import java.sql.Timestamp;

/**
 * 공지사항 데이터 전송 객체 (DTO)
 */
public class NoticeDTO {

    private int noticeId;
    private Integer userId; // 관리자 ID (null 가능)
    private String title;
    private String content;
    private String category;
    private int viewCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 기본 생성자
    public NoticeDTO() {}

    // Getter and Setter 메서드
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
