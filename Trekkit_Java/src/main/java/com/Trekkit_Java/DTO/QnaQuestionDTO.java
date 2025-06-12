package com.Trekkit_Java.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class QnaQuestionDTO {
    private int id;
    private String userId;
    private String nickname;
    private String title;
    private String content;
    private String mountain;
    private List<String> imagePaths;
    private int viewCount;
    private int answerCount;
    private int likeCount;
    private boolean isSolved;
    private Integer acceptedAnswerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성자
    public QnaQuestionDTO() {}

    public QnaQuestionDTO(int id, String userId, String nickname, String title, String content, 
                         String mountain, int viewCount, int answerCount, int likeCount, 
                         boolean isSolved, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.mountain = mountain;
        this.viewCount = viewCount;
        this.answerCount = answerCount;
        this.likeCount = likeCount;
        this.isSolved = isSolved;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMountain() { return mountain; }
    public void setMountain(String mountain) { this.mountain = mountain; }

    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public int getAnswerCount() { return answerCount; }
    public void setAnswerCount(int answerCount) { this.answerCount = answerCount; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isSolved() { return isSolved; }
    public void setSolved(boolean solved) { isSolved = solved; }

    public Integer getAcceptedAnswerId() { return acceptedAnswerId; }
    public void setAcceptedAnswerId(Integer acceptedAnswerId) { this.acceptedAnswerId = acceptedAnswerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
