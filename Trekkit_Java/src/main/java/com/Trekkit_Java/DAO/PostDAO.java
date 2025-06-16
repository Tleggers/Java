package com.Trekkit_Java.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.Trekkit_Java.DTO.PostDTO;

/**
 * 게시글 관련 데이터베이스 접근 객체 (Data Access Object)
 * 
 * 역할:
 * - MyBatis 매퍼 인터페이스
 * - 게시글 관련 SQL 쿼리 메서드 정의
 * - 데이터베이스와 Java 객체 간의 매핑
 * - 이미지 관리 기능 포함
 * 
 * @author Trekkit Team
 * @version 2.0 (이미지 관리 기능 추가)
 */
@Mapper // MyBatis 매퍼 인터페이스임을 나타내는 어노테이션
public interface PostDAO {
    
    // ==================== 게시글 기본 CRUD 메서드 ====================
    
    /**
     * 게시글 목록 조회 (정렬, 필터링, 페이징)
     * 
     * 기능:
     * - 정렬 방식에 따른 게시글 목록 조회
     * - 산 이름으로 필터링
     * - LIMIT/OFFSET을 이용한 페이징
     * 
     * @param sort 정렬 방식 ("최신순": 작성일 내림차순, "인기순": 좋아요수 내림차순)
     * @param mountain 산 이름 필터 (null이면 전체 조회)
     * @param offset 시작 위치 (페이징용, 0부터 시작)
     * @param size 가져올 게시글 개수
     * @return 조건에 맞는 게시글 목록
     */
    List<PostDTO> selectPosts(@Param("sort") String sort, 
                              @Param("mountain") String mountain,
                              @Param("offset") int offset, 
                              @Param("size") int size);
    
    /**
     * 게시글 총 개수 조회
     * 
     * 기능:
     * - 필터 조건에 맞는 총 게시글 수 조회
     * - 페이징 계산에 사용
     * 
     * @param mountain 산 이름 필터 (null이면 전체 조회)
     * @return 조건에 맞는 총 게시글 수
     */
    int selectPostCount(@Param("mountain") String mountain);
    
    /**
     * 게시글 상세 조회
     * 
     * 기능:
     * - 특정 게시글의 모든 정보 조회
     * - 이미지 경로는 별도 메서드로 조회
     * 
     * @param id 조회할 게시글 ID
     * @return 게시글 상세 정보 (이미지 경로 제외)
     */
    PostDTO selectPostById(@Param("id") int id);
    
    /**
     * 새 게시글 삽입
     * 
     * 기능:
     * - 새로운 게시글을 posts 테이블에 저장
     * - AUTO_INCREMENT로 ID 자동 생성
     * - 생성된 ID는 PostDTO 객체에 자동 설정됨
     * 
     * @param post 저장할 게시글 정보
     * @return 삽입된 행 수 (성공 시 1)
     */
    int insertPost(PostDTO post);
    
    /**
     * 게시글 수정
     * 
     * 기능:
     * - 기존 게시글의 제목, 내용, 산 이름 수정
     * - updated_at 컬럼 자동 업데이트
     * 
     * @param post 수정할 게시글 정보 (ID 필수)
     * @return 수정된 행 수 (성공 시 1)
     */
    int updatePost(PostDTO post);
    
    /**
     * 게시글 삭제
     * 
     * 기능:
     * - 특정 게시글을 posts 테이블에서 삭제
     * - CASCADE 설정으로 관련 데이터도 함께 삭제됨
     * 
     * @param id 삭제할 게시글 ID
     * @return 삭제된 행 수 (성공 시 1)
     */
    int deletePost(@Param("id") int id);
    
    /**
     * 조회수 증가
     * 
     * 기능:
     * - 게시글 조회 시 view_count 컬럼을 1 증가
     * - 동시성 문제 방지를 위해 원자적 연산 사용
     * 
     * @param id 조회수를 증가시킬 게시글 ID
     * @return 수정된 행 수 (성공 시 1)
     */
    int increaseViewCount(@Param("id") int id);
    
    // ==================== 좋아요 관련 메서드 ====================
    
    /**
     * 좋아요 여부 확인
     * 
     * 기능:
     * - 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
     * - post_likes 테이블 조회
     * 
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요 개수 (0: 좋아요 안함, 1: 좋아요 함)
     */
    int selectLikeExists(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 좋아요 추가
     * 
     * 기능:
     * - post_likes 테이블에 새로운 좋아요 레코드 추가
     * - 중복 좋아요 방지는 애플리케이션 레벨에서 처리
     * 
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 삽입된 행 수 (성공 시 1)
     */
    int insertLike(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 좋아요 삭제
     * 
     * 기능:
     * - post_likes 테이블에서 특정 좋아요 레코드 삭제
     * - 좋아요 취소 시 사용
     * 
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 삭제된 행 수 (성공 시 1)
     */
    int deleteLike(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 게시글의 총 좋아요 수 조회
     * 
     * 기능:
     * - 특정 게시글에 대한 전체 좋아요 수 계산
     * - post_likes 테이블에서 COUNT 연산
     * 
     * @param postId 게시글 ID
     * @return 총 좋아요 수
     */
    int selectLikeCount(@Param("postId") int postId);
    
    /**
     * posts 테이블의 like_count 컬럼 업데이트
     * 
     * 기능:
     * - post_likes 테이블의 실제 데이터를 기반으로 like_count 컬럼 업데이트
     * - 데이터 일관성 유지를 위해 사용
     * 
     * @param postId 게시글 ID
     * @return 수정된 행 수 (성공 시 1)
     */
    int updateLikeCount(@Param("postId") int postId);
    
    // ==================== 북마크 관련 메서드 ====================
    
    /**
     * 북마크 여부 확인
     * 
     * 기능:
     * - 특정 사용자가 특정 게시글을 북마크했는지 확인
     * - post_bookmarks 테이블 조회
     * 
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 북마크 개수 (0: 북마크 안함, 1: 북마크 함)
     */
    int selectBookmarkExists(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 북마크 추가
     * 
     * 기능:
     * - post_bookmarks 테이블에 새로운 북마크 레코드 추가
     * - 사용자별 북마크 목록 관리
     * 
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 삽입된 행 수 (성공 시 1)
     */
    int insertBookmark(@Param("postId") int postId, @Param("userId") String userId);
    
    /**
     * 북마크 삭제
     * 
     * 기능:
     * - post_bookmarks 테이블에서 특정 북마크 레코드 삭제
     * - 북마크 취소 시 사용
     * 
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 삭제된 행 수 (성공 시 1)
     */
    int deleteBookmark(@Param("postId") int postId, @Param("userId") String userId);
    
    // ==================== 산 목록 관련 메서드 ====================
    
    /**
     * 산 목록 조회 (중복 제거)
     * 
     * 기능:
     * - posts 테이블에서 사용된 산 이름들을 중복 제거하여 조회
     * - NULL이나 빈 문자열 제외
     * - 알파벳 순으로 정렬
     * 
     * @return 중복 제거된 산 이름 목록
     */
    List<String> selectMountains();
    
    // ==================== 이미지 관련 메서드 (새로 추가) ====================
    
    /**
     * 게시글의 이미지 경로들 조회 (순서대로)
     * 
     * 기능:
     * - post_images 테이블에서 특정 게시글의 모든 이미지 경로 조회
     * - image_order 컬럼 기준으로 정렬
     * - 게시글 상세 페이지에서 이미지 표시에 사용
     * 
     * @param postId 게시글 ID
     * @return 이미지 경로 목록 (업로드 순서대로 정렬)
     */
    List<String> selectImagePaths(@Param("postId") int postId);
    
    /**
     * 게시글 이미지 삽입 (상세 정보 포함) - 개선된 버전
     * 
     * 기능:
     * - post_images 테이블에 이미지 정보 저장
     * - 이미지 순서, 원본 파일명, 파일 크기 등 메타데이터 포함
     * - 향후 이미지 관리 기능 확장을 위한 구조
     * 
     * @param postId 게시글 ID
     * @param imagePath 저장된 이미지 경로
     * @param imageOrder 이미지 순서 (0부터 시작)
     * @param originalName 원본 파일명
     * @param fileSize 파일 크기 (바이트)
     * @return 삽입된 행 수 (성공 시 1)
     */
    int insertPostImage(@Param("postId") int postId,
                       @Param("imagePath") String imagePath,
                       @Param("imageOrder") int imageOrder,
                       @Param("originalName") String originalName,
                       @Param("fileSize") Long fileSize);
    
    /**
     * 게시글 이미지 경로 삽입 (기본 버전) - 기존 메서드 유지
     * 
     * 기능:
     * - post_images 테이블에 기본적인 이미지 경로만 저장
     * - 기존 코드와의 호환성 유지를 위해 제공
     * - 간단한 이미지 저장 시 사용
     * 
     * @param postId 게시글 ID
     * @param imagePath 저장된 이미지 경로
     * @return 삽입된 행 수 (성공 시 1)
     */
    int insertImagePath(@Param("postId") int postId, @Param("imagePath") String imagePath);
    
    /**
     * 게시글의 모든 이미지 삭제
     * 
     * 기능:
     * - post_images 테이블에서 특정 게시글의 모든 이미지 레코드 삭제
     * - 게시글 수정 시 기존 이미지 삭제에 사용
     * - 게시글 삭제 시에도 사용
     * 
     * @param postId 게시글 ID
     * @return 삭제된 행 수
     */
    int deleteImagePaths(@Param("postId") int postId);
    
    // ==================== 댓글 관련 메서드 ====================
    
    /**
     * posts 테이블의 comment_count 컬럼 업데이트
     * 
     * 기능:
     * - comments 테이블의 실제 데이터를 기반으로 comment_count 컬럼 업데이트
     * - 댓글 추가/삭제 시 호출됨
     * - 데이터 일관성 유지를 위해 사용
     * 
     * @param postId 게시글 ID
     * @return 수정된 행 수 (성공 시 1)
     */
    int updateCommentCount(@Param("postId") int postId);
    
    // ==================== 대안 방법 및 확장 메서드 ====================
    
    /**
     * posts 테이블의 image_paths 컬럼 업데이트 (대안 방법)
     * 
     * 기능:
     * - posts 테이블에 image_paths 컬럼이 있는 경우 사용
     * - JSON 형태 또는 쉼표로 구분된 문자열로 저장
     * - 정규화되지 않은 방식이지만 간단한 구현에 유용
     * 
     * @param postId 게시글 ID
     * @param imagePaths 이미지 경로들 (JSON 또는 쉼표 구분 문자열)
     * @return 수정된 행 수 (성공 시 1)
     */
    int updateImagePaths(@Param("postId") int postId, @Param("imagePaths") String imagePaths);
    
    /**
     * 사용자별 좋아요한 게시글 확인
     * 
     * 기능:
     * - 여러 게시글에 대해 특정 사용자의 좋아요 상태를 한 번에 확인
     * - 게시글 목록 페이지에서 좋아요 상태 표시에 사용
     * - 성능 최적화를 위한 배치 처리
     * 
     * @param userId 사용자 ID
     * @param postIds 확인할 게시글 ID 목록
     * @return 사용자가 좋아요한 게시글 ID 목록
     */
    List<Integer> selectUserLikedPosts(@Param("userId") String userId, @Param("postIds") List<Integer> postIds);
    
    /**
     * 사용자별 북마크한 게시글 확인
     * 
     * 기능:
     * - 여러 게시글에 대해 특정 사용자의 북마크 상태를 한 번에 확인
     * - 게시글 목록 페이지에서 북마크 상태 표시에 사용
     * - 성능 최적화를 위한 배치 처리
     * 
     * @param userId 사용자 ID
     * @param postIds 확인할 게시글 ID 목록
     * @return 사용자가 북마크한 게시글 ID 목록
     */
    List<Integer> selectUserBookmarkedPosts(@Param("userId") String userId, @Param("postIds") List<Integer> postIds);
}
