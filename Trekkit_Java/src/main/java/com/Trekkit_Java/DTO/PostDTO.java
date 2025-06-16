package com.Trekkit_Java.DTO;

import java.sql.Timestamp;
import java.util.List;

/**
 * 게시글 데이터 전송 객체 (Data Transfer Object)
 * 
 * 역할:
 * - 게시글 관련 데이터를 계층 간에 전송
 * - 데이터베이스 테이블과 Java 객체 간의 매핑
 * - JSON 직렬화/역직렬화 지원
 * - 이미지 정보 포함한 완전한 게시글 데이터 표현
 * 
 * @author Trekkit Team
 * @version 2.0 (이미지 정보 추가)
 */
public class PostDTO {
    
    // ==================== 기본 게시글 정보 ====================
    
    /**
     * 게시글 고유 식별자 (Primary Key)
     * 데이터베이스에서 자동 생성 (AUTO_INCREMENT)
     */
    private Integer id;
    
    /**
     * 작성자 닉네임
     * 게시글을 작성한 사용자의 표시명
     */
    private String nickname;
    
    /**
     * 게시글 제목
     * 게시글의 주제를 나타내는 제목
     */
    private String title;
    
    /**
     * 등산한 산 이름
     * 게시글과 관련된 산 이름 (선택사항, null 가능)
     */
    private String mountain;
    
    /**
     * 게시글 본문 내용
     * 등산 경험, 팁, 후기 등의 상세 내용
     */
    private String content;
    
    /**
     * 게시글에 첨부된 이미지 경로 목록
     * 웹에서 접근 가능한 이미지 URL들의 리스트
     * 예: ["/uploads/image1.jpg", "/uploads/image2.png"]
     */
    private List<String> imagePaths;
    
    // ==================== 통계 정보 ====================
    
    /**
     * 조회수
     * 게시글이 조회된 총 횟수
     */
    private int viewCount;
    
    /**
     * 좋아요 수
     * 게시글에 좋아요를 누른 사용자 수
     */
    private int likeCount;
    
    /**
     * 댓글 수
     * 게시글에 달린 댓글의 총 개수
     */
    private int commentCount;
    
    // ==================== 이미지 관련 추가 정보 ====================
    
    /**
     * 게시글에 첨부된 이미지 개수
     * imagePaths 리스트의 크기와 동일
     * 프론트엔드에서 이미지 아이콘 표시 여부 결정에 사용
     */
    private int imageCount;
    
    /**
     * 게시글에 이미지가 있는지 여부를 나타내는 플래그
     * true: 이미지 있음 (imageCount > 0)
     * false: 이미지 없음 (imageCount = 0)
     * 프론트엔드에서 UI 렌더링 최적화에 사용
     */
    private boolean hasImages;
    
    // ==================== 시간 정보 ====================
    
    /**
     * 게시글 생성 시간
     * 게시글이 처음 작성된 시간 (자동 설정)
     */
    private Timestamp createdAt;
    
    /**
     * 게시글 수정 시간
     * 게시글이 마지막으로 수정된 시간 (수정 시 자동 업데이트)
     */
    private Timestamp updatedAt;
    
    // ==================== 생성자 ====================
    
    /**
     * 기본 생성자
     * MyBatis, Jackson 등에서 객체 생성 시 사용
     */
    public PostDTO() {}
    
    /**
     * 전체 필드를 포함한 생성자
     * 테스트 코드나 특정 상황에서 객체 생성 시 사용
     * 
     * @param id 게시글 ID
     * @param nickname 작성자 닉네임
     * @param title 게시글 제목
     * @param mountain 산 이름
     * @param content 게시글 내용
     * @param imagePaths 이미지 경로 목록
     * @param viewCount 조회수
     * @param likeCount 좋아요 수
     * @param commentCount 댓글 수
     * @param createdAt 생성 시간
     * @param updatedAt 수정 시간
     */
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
        
        // 이미지 관련 정보 자동 설정
        updateImageInfo();
    }
    
    // ==================== Getter/Setter 메서드 ====================
    
    /**
     * 게시글 ID 조회
     * @return 게시글 고유 식별자
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * 게시글 ID 설정
     * @param id 설정할 게시글 ID
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * 작성자 닉네임 조회
     * @return 작성자 닉네임
     */
    public String getNickname() {
        return nickname;
    }
    
    /**
     * 작성자 닉네임 설정
     * @param nickname 설정할 작성자 닉네임
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    /**
     * 게시글 제목 조회
     * @return 게시글 제목
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * 게시글 제목 설정
     * @param title 설정할 게시글 제목
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * 산 이름 조회
     * @return 등산한 산 이름 (null 가능)
     */
    public String getMountain() {
        return mountain;
    }
    
    /**
     * 산 이름 설정
     * @param mountain 설정할 산 이름
     */
    public void setMountain(String mountain) {
        this.mountain = mountain;
    }
    
    /**
     * 게시글 내용 조회
     * @return 게시글 본문 내용
     */
    public String getContent() {
        return content;
    }
    
    /**
     * 게시글 내용 설정
     * @param content 설정할 게시글 내용
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * 이미지 경로 목록 조회
     * @return 게시글에 첨부된 이미지 파일 경로 목록
     */
    public List<String> getImagePaths() {
        return imagePaths;
    }
    
    /**
     * 이미지 경로 목록 설정
     * 이미지 경로가 설정되면 자동으로 imageCount와 hasImages도 업데이트됨
     * 
     * @param imagePaths 설정할 이미지 경로 목록
     */
    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
        // 이미지 관련 정보 자동 업데이트
        updateImageInfo();
    }
    
    /**
     * 조회수 조회
     * @return 게시글 조회수
     */
    public int getViewCount() {
        return viewCount;
    }
    
    /**
     * 조회수 설정
     * @param viewCount 설정할 조회수
     */
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
    
    /**
     * 좋아요 수 조회
     * @return 게시글 좋아요 수
     */
    public int getLikeCount() {
        return likeCount;
    }
    
    /**
     * 좋아요 수 설정
     * @param likeCount 설정할 좋아요 수
     */
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    
    /**
     * 댓글 수 조회
     * @return 게시글 댓글 수
     */
    public int getCommentCount() {
        return commentCount;
    }
    
    /**
     * 댓글 수 설정
     * @param commentCount 설정할 댓글 수
     */
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    
    /**
     * 이미지 개수 조회
     * @return 게시글에 첨부된 이미지 개수
     */
    public int getImageCount() {
        return imageCount;
    }
    
    /**
     * 이미지 개수 설정
     * 이미지 개수가 설정되면 자동으로 hasImages 플래그도 업데이트됨
     * 
     * @param imageCount 설정할 이미지 개수
     */
    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
        // 이미지 개수가 0보다 크면 hasImages를 true로 설정
        this.hasImages = imageCount > 0;
    }
    
    /**
     * 이미지 존재 여부 조회
     * @return true: 이미지 있음, false: 이미지 없음
     */
    public boolean isHasImages() {
        return hasImages;
    }
    
    /**
     * 이미지 존재 여부 설정
     * @param hasImages 이미지 존재 여부
     */
    public void setHasImages(boolean hasImages) {
        this.hasImages = hasImages;
    }
    
    /**
     * 게시글 생성 시간 조회
     * @return 게시글 생성 시간
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /**
     * 게시글 생성 시간 설정
     * @param createdAt 설정할 생성 시간
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * 게시글 수정 시간 조회
     * @return 게시글 수정 시간
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * 게시글 수정 시간 설정
     * @param updatedAt 설정할 수정 시간
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // ==================== 유틸리티 메서드 ====================
    
    /**
     * 이미지 관련 정보 자동 업데이트
     * imagePaths 리스트를 기반으로 imageCount와 hasImages를 설정
     * 
     * 내부적으로 사용되는 private 메서드
     */
    private void updateImageInfo() {
        if (this.imagePaths != null) {
            this.imageCount = this.imagePaths.size();
            this.hasImages = this.imageCount > 0;
        } else {
            this.imageCount = 0;
            this.hasImages = false;
        }
    }
    
    /**
     * 객체의 문자열 표현 반환
     * 디버깅 및 로깅 목적으로 사용
     * 
     * @return 객체의 모든 필드 정보를 포함한 문자열
     */
    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", title='" + title + '\'' +
                ", mountain='" + mountain + '\'' +
                ", content='" + content + '\'' +
                ", imagePaths=" + imagePaths +
                ", imageCount=" + imageCount +
                ", hasImages=" + hasImages +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

/**
 * 좋아요 관련 데이터 전송 객체
 * 
 * 역할:
 * - 게시글 좋아요 정보 관리
 * - post_likes 테이블과 매핑
 * - 사용자별 좋아요 상태 추적
 */
class LikeDTO {
    
    /**
     * 좋아요가 눌린 게시글 ID
     */
    private int postId;
    
    /**
     * 좋아요를 누른 사용자 ID
     */
    private String userId;
    
    /**
     * 좋아요를 누른 시간
     */
    private Timestamp createdAt;
    
    /**
     * 기본 생성자
     */
    public LikeDTO() {}
    
    /**
     * 전체 필드 생성자
     * 
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @param createdAt 좋아요 생성 시간
     */
    public LikeDTO(int postId, String userId, Timestamp createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
    
    // Getter/Setter 메서드들
    
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

/**
 * 북마크 관련 데이터 전송 객체
 * 
 * 역할:
 * - 게시글 북마크 정보 관리
 * - post_bookmarks 테이블과 매핑
 * - 사용자별 북마크 목록 관리
 */
class BookmarkDTO {
    
    /**
     * 북마크된 게시글 ID
     */
    private int postId;
    
    /**
     * 북마크한 사용자 ID
     */
    private String userId;
    
    /**
     * 북마크한 시간
     */
    private Timestamp createdAt;
    
    /**
     * 기본 생성자
     */
    public BookmarkDTO() {}
    
    /**
     * 전체 필드 생성자
     * 
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @param createdAt 북마크 생성 시간
     */
    public BookmarkDTO(int postId, String userId, Timestamp createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
    
    // Getter/Setter 메서드들
    
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
