package com.Trekkit_Java.DTO;

import java.time.LocalDateTime; // Java 8의 날짜/시간 API인 LocalDateTime 사용

public class QnaLikeDTO {
    private int id; // 좋아요 ID (PRIMARY KEY, 자동 증가)
    private Integer questionId; // 좋아요가 질문에 대한 것일 경우 질문 ID (NULL 가능)
    private Integer answerId;   // 좋아요가 답변에 대한 것일 경우 답변 ID (NULL 가능)
    private int userId; // 좋아요를 누른 사용자의 ID
    private LocalDateTime createdAt; // 좋아요를 누른 시각

    // Getter와 Setter 메서드: 각 필드의 값을 읽고 설정하는 역할을 합니다.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
