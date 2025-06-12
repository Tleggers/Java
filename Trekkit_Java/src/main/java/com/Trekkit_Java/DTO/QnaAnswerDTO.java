package com.Trekkit_Java.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class QnaAnswerDTO {
    private int id;
    private int questionId;
    private String userId;
    private String nickname;
    private String content;
    private List<String> imagePaths;
    private int likeCount;
    private boolean isAccepted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성자
    public QnaAnswerDTO() {}

    public QnaAnswerDTO(int id, int questionId, String userId, String nickname, 
                       String content, int likeCount, boolean isAccepted, LocalDateTime createdAt) {
        this.id = id;
        this.questionId = questionId;
        this.userId = userId;
        this.nickname = nickname;
        this.content = content;
        this.likeCount = likeCount;
        this.isAccepted = isAccepted;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isAccepted() { return isAccepted; }
    public void setAccepted(boolean accepted) { isAccepted = accepted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
