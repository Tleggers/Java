package com.Trekkit_Java.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.Post;
import repository.PostBookmarkMapper;
import repository.PostLikeMapper;
import repository.PostMapper;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private PostLikeMapper postLikeMapper;
    
    @Autowired
    private PostBookmarkMapper postBookmarkMapper;

    /**
     * 게시글 목록 조회 (필터링 및 페이징)
     */
    public List<Post> getPosts(String sort, String mountain, int page, int size) {
        int offset = page * size;
        
        if ("인기순".equals(sort)) {
            return postMapper.findAllByOrderByViewCountDesc(mountain, offset, size);
        }
        return postMapper.findAllByOrderByCreatedAtDesc(mountain, offset, size);
    }

    /**
     * 전체 게시글 수 조회
     */
    public int getTotalCount(String mountain) {
        return postMapper.getTotalCount(mountain);
    }

    /**
     * 게시글 상세 조회
     */
    public Post getPostById(Long id) {
        return postMapper.findById(id);
    }

    /**
     * 새 게시글 작성
     */
    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);

        postMapper.insertPost(post);
        return post;
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount(Long id) {
        postMapper.updateViewCount(id);
    }

    /**
     * 좋아요 토글
     */
    public boolean toggleLike(Long postId, String userId) {
        boolean isLiked = postLikeMapper.isLiked(postId, userId);
        
        if (isLiked) {
            postLikeMapper.deleteLike(postId, userId);
            postMapper.decrementLikeCount(postId);
            return false;
        } else {
            postLikeMapper.insertLike(postId, userId);
            postMapper.incrementLikeCount(postId);
            return true;
        }
    }

    /**
     * 좋아요 수 조회
     */
    public int getLikeCount(Long postId) {
        return postMapper.getLikeCount(postId);
    }

    /**
     * 북마크 토글
     */
    public boolean toggleBookmark(Long postId, String userId) {
        boolean isBookmarked = postBookmarkMapper.isBookmarked(postId, userId);
        
        if (isBookmarked) {
            postBookmarkMapper.deleteBookmark(postId, userId);
            return false;
        } else {
            postBookmarkMapper.insertBookmark(postId, userId);
            return true;
        }
    }

    /**
     * 모든 산 목록 조회
     */
    public List<String> getAllMountains() {
        return postMapper.findAllMountains();
    }

    /**
     * 게시글 삭제
     */
    public boolean deletePost(Long id) {
        int deleted = postMapper.deletePost(id);
        return deleted > 0;
    }

    /**
     * 사용자별 좋아요 상태 조회
     */
    public boolean isLikedByUser(Long postId, String userId) {
        return postLikeMapper.isLiked(postId, userId);
    }

    /**
     * 사용자별 북마크 상태 조회
     */
    public boolean isBookmarkedByUser(Long postId, String userId) {
        return postBookmarkMapper.isBookmarked(postId, userId);
    }
}