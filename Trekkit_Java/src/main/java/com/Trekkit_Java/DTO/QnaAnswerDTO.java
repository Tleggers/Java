package com.Trekkit_Java.DTO;

import java.time.LocalDateTime; // Java 8의 날짜/시간 API인 LocalDateTime 사용

public class QnaAnswerDTO {
    private int id; // 답변 ID (PRIMARY KEY, 자동 증가)
    private int questionId; // 이 답변이 속한 질문의 ID (qna_questions 테이블 참조)
    private int userId; // 답변 작성자 ID (user 테이블 참조)
    private String nickname; // 답변 작성자 닉네임
    private String content; // 답변 내용
    // private String imagePaths; // 이미지 기능 제거로 인해 주석 처리됨
    private int likeCount; // 좋아요 수 (기본값 0)
    private boolean isAccepted; // 답변 채택 여부 (true/false, 기본값 false)
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime updatedAt; // 수정일시

    // Getter와 Setter 메서드: 각 필드의 값을 읽고 설정하는 역할을 합니다.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // imagePaths 관련 Getter/Setter는 이미지 기능 제거로 인해 주석 처리됨
    // public String getImagePaths() {
    //     return imagePaths;
    // }
    // public void setImagePaths(String imagePaths) {
    //     this.imagePaths = imagePaths;
    // }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(boolean accepted) {
        isAccepted = accepted;
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
}
