package com.Trekkit_Java.DTO;

import java.time.LocalDateTime; // Java 8의 날짜/시간 API인 LocalDateTime 사용

public class QnaQuestionDTO {
    private int id; // 질문 ID (PRIMARY KEY, 자동 증가)
    private int userId; // 작성자 ID (user 테이블 참조)
    private String nickname; // 작성자 닉네임
    private String title; // 질문 제목
    private String content; // 질문 내용
    private String mountain; // 관련 산 이름 (선택 사항)
    // private String imagePaths; // 이미지 기능 제거로 인해 주석 처리됨
    private int viewCount; // 조회수 (기본값 0)
    private int answerCount; // 답변 수 (기본값 0)
    private int likeCount; // 좋아요 수 (기본값 0)
    private boolean isSolved; // 해결 여부 (true/false, 기본값 false)
    private Integer acceptedAnswerId; // 채택된 답변의 ID (NULL 가능하므로 Integer 타입 사용)
    private LocalDateTime createdAt; // 생성일시
    private LocalDateTime updatedAt; // 수정일시

    // Getter와 Setter 메서드: 각 필드의 값을 읽고 설정하는 역할을 합니다.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMountain() {
        return mountain;
    }

    public void setMountain(String mountain) {
        this.mountain = mountain;
    }

    // imagePaths 관련 Getter/Setter는 이미지 기능 제거로 인해 주석 처리됨
    // public String getImagePaths() {
    //     return imagePaths;
    // }
    // public void setImagePaths(String imagePaths) {
    //     this.imagePaths = imagePaths;
    // }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean getIsSolved() {
        return isSolved;
    }

    public void setIsSolved(boolean solved) {
        isSolved = solved;
    }

    public Integer getAcceptedAnswerId() {
        return acceptedAnswerId;
    }

    public void setAcceptedAnswerId(Integer acceptedAnswerId) {
        this.acceptedAnswerId = acceptedAnswerId;
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
