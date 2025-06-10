package com.Trekkit_Java.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.Trekkit_Java.DTO.PostDTO;

@Mapper
public interface PostDAO {
    
    /**
     * 게시글 목록 조회 (정렬, 필터링, 페이징)
     * @param sort 정렬 방식 (latest: 최신순, popular: 인기순)
     * @param mountain 산 이름 (null이면 전체)
     * @param offset 시작 위치
     * @param size 가져올 개수
     * @return 게시글 목록
     */
    List<PostDTO> selectPosts(@Param("sort") String sort, 
                              @Param("mountain") String mountain,
                              @Param("offset") int offset, 
                              @Param("size") int size);
    
    /**
     * 게시글 총 개수 조회
     * @param mountain 산 이름 (null이면 전체)
     * @return 총 게시글 수
     */
    int selectPostCount(@Param("mountain") String mountain);
    
    /**
     * 게시글 상세 조회
     * @param id 게시글 ID
     * @return 게시글 정보
     */
    PostDTO selectPostById(@Param("id") int id);
    
    /**
     * 새 게시글 삽입
     * @param post 게시글 정보
     * @return 삽입된 행 수
     */
    int insertPost(PostDTO post);
    
    /**
     * 게시글 수정
     * @param post 수정할 게시글 정보
     * @return 수정된 행 수
     */
    int updatePost(PostDTO post);
    
    /**
     * 게시글 삭제
     * @param id 게시글 ID
     * @return 삭제된 행 수
     */
    int deletePost(@Param("id") int id);
    
    /**
     * 조회수 증가
     * @param id 게시글 ID
     * @return 수정된 행 수
     */
    int increaseViewCount(@Param("id") int id);
    
    /**
     * 좋아요 여부 확인
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요 개수 (0: 좋아요 안함, 1: 좋아요 함)
     */
    int selectLikeExists(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 좋아요 추가
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 삽입된 행 수
     */
    int insertLike(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 좋아요 삭제
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 삭제된 행 수
     */
    int deleteLike(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 게시글의 총 좋아요 수 조회
     * @param postId 게시글 ID
     * @return 총 좋아요 수
     */
    int selectLikeCount(@Param("postId") int postId);
    
    /**
     * 북마크 여부 확인
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 북마크 개수 (0: 북마크 안함, 1: 북마크 함)
     */
    int selectBookmarkExists(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 북마크 추가
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 삽입된 행 수
     */
    int insertBookmark(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 북마크 삭제
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 삭제된 행 수
     */
    int deleteBookmark(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 산 목록 조회 (중복 제거)
     * @return 산 이름 목록
     */
    List<String> selectMountains();
    
    /**
     * 게시글의 이미지 경로들 조회
     * @param postId 게시글 ID
     * @return 이미지 경로 목록
     */
    List<String> selectImagePaths(@Param("postId") int postId);
    
    /**
     * 게시글 이미지 경로 삽입
     * @param postId 게시글 ID
     * @param imagePath 이미지 경로
     * @return 삽입된 행 수
     */
    int insertImagePath(@Param("postId") int postId, @Param("imagePath") String imagePath);
    
    /**
     * 게시글의 모든 이미지 경로 삭제
     * @param postId 게시글 ID
     * @return 삭제된 행 수
     */
    int deleteImagePaths(@Param("postId") int postId);
    /**
     * posts 테이블의 like_count 컬럼 업데이트
     * @param postId 게시글 ID
     * @return 수정된 행 수
     */
    int updateLikeCount(@Param("postId") int postId);
    
    /**
     * posts 테이블의 comment_count 컬럼 업데이트
     * @param postId 게시글 ID
     * @return 수정된 행 수
     */
    int updateCommentCount(@Param("postId") int postId);
    
    /**
     * posts 테이블의 image_paths 컬럼 업데이트 (대안 방법)
     * @param postId 게시글 ID
     * @param imagePaths 이미지 경로들 (JSON 또는 쉼표 구분)
     * @return 수정된 행 수
     */
    int updateImagePaths(@Param("postId") int postId, @Param("imagePaths") String imagePaths);
    
    /**
     * 사용자별 좋아요한 게시글 확인
     * @param userId 사용자 ID
     * @param postIds 게시글 ID 목록
     * @return 좋아요한 게시글 ID 목록
     */
    List<Integer> selectUserLikedPosts(@Param("userId") String userId, @Param("postIds") List<Integer> postIds);
    
    /**
     * 사용자별 북마크한 게시글 확인
     * @param userId 사용자 ID
     * @param postIds 게시글 ID 목록
     * @return 북마크한 게시글 ID 목록
     */
    List<Integer> selectUserBookmarkedPosts(@Param("userId") String userId, @Param("postIds") List<Integer> postIds);
}