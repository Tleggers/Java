package com.Trekkit_Java.DTO;

import java.sql.Timestamp;

public class CommentDTO {
    private Integer id;
    private Integer postId;
    private String userId;
    private String nickname;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // 기본 생성자
    public CommentDTO() {}
    
    // 전체 생성자
    public CommentDTO(Integer id, Integer postId, String userId, String nickname, 
                     String content, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter and Setter methods
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "CommentDTO{" +
                "id=" + id +
                ", postId=" + postId +
                ", userId='" + userId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}