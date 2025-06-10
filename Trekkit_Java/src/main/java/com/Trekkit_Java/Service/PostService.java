package com.Trekkit_Java.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.Trekkit_Java.DAO.PostDAO;
import com.Trekkit_Java.DTO.PostDTO;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostDAO postDAO;
    
    // application.properties에서 파일 업로드 경로 설정
    @Value("${file.upload.path:/uploads/}")
    private String uploadPath;
    
    /**
     * 게시글 목록 조회
     * @param sort 정렬 방식
     * @param mountain 산 이름
     * @param offset 시작 위치
     * @param size 페이지 크기
     * @return 게시글 목록
     */
    @Transactional(readOnly = true)
    public List<PostDTO> getPosts(String sort, String mountain, int offset, int size) {
        try {
            List<PostDTO> posts = postDAO.selectPosts(sort, mountain, offset, size);
            
            // 각 게시글에 빈 이미지 리스트 설정
            for (PostDTO post : posts) {
                post.setImagePaths(new ArrayList<>());
            }
            
            return posts;
        } catch (Exception e) {
            throw new RuntimeException("게시글 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * 게시글 총 개수 조회
     * @param mountain 산 이름
     * @return 총 게시글 수
     */
    @Transactional(readOnly = true)
    public int getPostCount(String mountain) {
        return postDAO.selectPostCount(mountain);
    }
    
    /**
     * 게시글 상세 조회
     * @param id 게시글 ID
     * @return 게시글 정보
     */
    @Transactional(readOnly = true)
    public PostDTO getPostById(int id) {
        try {
            PostDTO post = postDAO.selectPostById(id);
            if (post != null) {
                // 빈 이미지 리스트 설정
                post.setImagePaths(new ArrayList<>());
            }
            return post;
        } catch (Exception e) {
            throw new RuntimeException("게시글 상세 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }
    /**
     * 조회수 증가
     * @param id 게시글 ID
     */
    public void increaseViewCount(int id) {
        postDAO.increaseViewCount(id);
    }
    
    /**
     * 새 게시글 작성 (통합된 버전)
     * @param post 게시글 정보
     * @return 생성된 게시글 정보
     */
    public PostDTO createPost(PostDTO post) {
        // 게시글 삽입
        int result = postDAO.insertPost(post);
        
        if (result > 0) {
            // ID가 제대로 설정되었는지 확인
            if (post.getId() == null) {
                throw new RuntimeException("게시글 ID가 생성되지 않았습니다.");
            }
            
            // 이미지 경로가 있다면 저장
            if (post.getImagePaths() != null && !post.getImagePaths().isEmpty()) {
                for (String imagePath : post.getImagePaths()) {
                    postDAO.insertImagePath(post.getId(), imagePath);
                }
            }
            
            // 생성된 게시글 정보 반환
            return postDAO.selectPostById(post.getId());
        }
        
        throw new RuntimeException("게시글 생성에 실패했습니다.");
    }
    
    /**
     * 게시글 수정
     * @param post 수정할 게시글 정보
     * @return 수정된 게시글 정보
     */
    public PostDTO updatePost(PostDTO post) {
        // 게시글 기본 정보 수정
        int result = postDAO.updatePost(post);
        
        if (result > 0) {
            // 기존 이미지 경로 삭제
            postDAO.deleteImagePaths(post.getId());
            
            // 새 이미지 경로 저장
            if (post.getImagePaths() != null && !post.getImagePaths().isEmpty()) {
                for (String imagePath : post.getImagePaths()) {
                    postDAO.insertImagePath(post.getId(), imagePath);
                }
            }
            
            // 수정된 게시글 정보 반환
            return postDAO.selectPostById(post.getId());
        }
        
        throw new RuntimeException("게시글 수정에 실패했습니다.");
    }
    
    /**
     * 게시글 삭제
     * @param postId 게시글 ID
     * @return 삭제 성공 여부
     */
    public boolean deletePost(int postId) {
        try {
            // 관련 이미지 경로 먼저 삭제
            postDAO.deleteImagePaths(postId);
            
            // 게시글 삭제
            int result = postDAO.deletePost(postId);
            
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("게시글 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 이미지 업로드 (통합된 버전 - 파일 크기 검증 포함)
     * @param images 업로드할 이미지 파일들
     * @return 업로드된 이미지 경로 목록
     * @throws IOException 파일 업로드 실패 시
     */
    public List<String> uploadImages(MultipartFile[] images) throws IOException {
        List<String> imagePaths = new ArrayList<>();
        
        // 업로드 디렉토리 생성
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                // 파일 크기 검증 (예: 5MB 제한)
                if (image.getSize() > 5 * 1024 * 1024) {
                    throw new IOException("파일 크기가 5MB를 초과합니다: " + image.getOriginalFilename());
                }
                
                // 파일 확장자 검증
                String originalFilename = image.getOriginalFilename();
                if (!isValidImageFile(originalFilename)) {
                    throw new IOException("유효하지 않은 이미지 파일입니다: " + originalFilename);
                }
                
                // 고유한 파일명 생성
                String fileExtension = getFileExtension(originalFilename);
                String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;
                
                // 파일 저장
                Path filePath = uploadDir.resolve(uniqueFilename);
                Files.copy(image.getInputStream(), filePath);
                
                // 웹에서 접근 가능한 경로로 변환
                String webPath = "/uploads/" + uniqueFilename;
                imagePaths.add(webPath);
            }
        }
        
        return imagePaths;
    }
    
    /**
     * 좋아요 토글
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요 상태 정보
     */
    public Map<String, Object> toggleLike(int postId, String userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 현재 좋아요 상태 확인
        boolean isLiked = postDAO.selectLikeExists(postId, userId) > 0;
        
        if (isLiked) {
            // 좋아요 취소
            postDAO.deleteLike(postId, userId);
            result.put("isLiked", false);
        } else {
            // 좋아요 추가
            postDAO.insertLike(postId, userId);
            result.put("isLiked", true);
        }
        
        // posts 테이블의 like_count 컬럼 업데이트
        postDAO.updateLikeCount(postId);
        
        // 업데이트된 좋아요 수 조회
        int totalLikes = postDAO.selectLikeCount(postId);
        result.put("likeCount", totalLikes);
        
        return result;
    }
    
    /**
     * 북마크 토글
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 북마크 상태
     */
    public boolean toggleBookmark(int postId, String userId) {
        // 현재 북마크 상태 확인
        boolean isBookmarked = postDAO.selectBookmarkExists(postId, userId) > 0;
        
        if (isBookmarked) {
            // 북마크 취소
            postDAO.deleteBookmark(postId, userId);
            return false;
        } else {
            // 북마크 추가
            postDAO.insertBookmark(postId, userId);
            return true;
        }
    }
    
    /**
     * 산 목록 조회
     * @return 산 이름 목록
     */
    @Transactional(readOnly = true)
    public List<String> getMountains() {
        return postDAO.selectMountains();
    }
    
    /**
     * 파일 확장자 추출
     * @param filename 파일명
     * @return 확장자
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * 유효한 이미지 파일인지 확인 (강화된 검증)
     * @param filename 파일명
     * @return 유효성 여부
     */
    private boolean isValidImageFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        String extension = getFileExtension(filename);
        return extension.matches("^(jpg|jpeg|png|gif|bmp|webp)$");
    }
 // PostService.java에 추가할 메소드

    /**
     * 댓글 수 업데이트 (PostDAO에서 호출)
     */
    public void updatePostCommentCount(int postId) {
        try {
            postDAO.updateCommentCount(postId);
        } catch (Exception e) {
            throw new RuntimeException("댓글 수 업데이트 중 오류 발생: " + e.getMessage(), e);
        }
    }
}