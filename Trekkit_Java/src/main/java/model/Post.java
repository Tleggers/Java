package model;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Post {
    private Long id;
    private String nickname;
    private String title;
    private String mountain;           // Flutter와 매칭
    private String content;
    private List<String> imagePaths;   // Flutter와 매칭 (JSON 배열로 저장)
    private Integer viewCount;
    private Integer likeCount;         // 좋아요 수 추가
    private Integer commentCount;      // 댓글 수 추가
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // 기본 생성자
    public Post() {}

    // 전체 생성자
    public Post(String nickname, String title, String mountain, String content, 
                List<String> imagePaths, Integer viewCount, Integer likeCount, Integer commentCount) {
        this.nickname = nickname;
        this.title = title;
        this.mountain = mountain;
        this.content = content;
        this.imagePaths = imagePaths;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMountain() { return mountain; }
    public void setMountain(String mountain) { this.mountain = mountain; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}