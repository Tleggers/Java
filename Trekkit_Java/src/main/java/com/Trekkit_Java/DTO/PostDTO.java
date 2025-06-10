package com.Trekkit_Java.DTO;

import java.sql.Timestamp;
import java.util.List;

public class PostDTO {
    private Integer id;
    private String nickname;
    private String title;
    private String mountain;
    private String content;
    private List<String> imagePaths;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // 기본 생성자
    public PostDTO() {}
    
    // 전체 생성자
    public PostDTO(Integer id, String nickname, String title, String mountain, 
                   String content, List<String> imagePaths, int viewCount, 
                   int likeCount, int commentCount, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.nickname = nickname;
        this.title = title;
        this.mountain = mountain;
        this.content = content;
        this.imagePaths = imagePaths;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter and Setter methods
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
    
    public String getMountain() {
        return mountain;
    }
    
    public void setMountain(String mountain) {
        this.mountain = mountain;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public List<String> getImagePaths() {
        return imagePaths;
    }
    
    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
    
    public int getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
    
    public int getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    
    public int getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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
    
    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", title='" + title + '\'' +
                ", mountain='" + mountain + '\'' +
                ", content='" + content + '\'' +
                ", imagePaths=" + imagePaths +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

// 좋아요 관련 DTO
class LikeDTO {
    private int postId;
    private String userId;
    private Timestamp createdAt;
    
    public LikeDTO() {}
    
    public LikeDTO(int postId, String userId, Timestamp createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
    
    public int getPostId() {
        return postId;
    }
    
    public void setPostId(int postId) {
        this.postId = postId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

// 북마크 관련 DTO
class BookmarkDTO {
    private int postId;
    private String userId;
    private Timestamp createdAt;
    
    public BookmarkDTO() {}
    
    public BookmarkDTO(int postId, String userId, Timestamp createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
    
    public int getPostId() {
        return postId;
    }
    
    public void setPostId(int postId) {
        this.postId = postId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}