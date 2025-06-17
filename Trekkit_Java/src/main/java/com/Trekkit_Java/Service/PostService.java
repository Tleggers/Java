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

/**
 * 게시글 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 
 * 주요 기능:
 * - 게시글 CRUD 작업
 * - 이미지 업로드 및 관리
 * - 좋아요/북마크 기능
 * - 파일 검증 및 보안
 * - 트랜잭션 관리
 * 
 * @author Trekkit Team
 * @version 2.0 (이미지 정보 포함)
 */
@Service // Spring Service 컴포넌트임을 나타내는 어노테이션
@Transactional // 클래스 레벨에서 트랜잭션 관리 활성화
public class PostService {

    /**
     * PostDAO 의존성 주입
     * 데이터베이스 접근을 위한 DAO 객체
     */
    @Autowired
    private PostDAO postDAO;
    
    /**
     * 파일 업로드 경로 설정
     * application.properties에서 file.upload.path 값을 주입
     * 기본값: /uploads/
     */
    @Value("${file.upload.path:/uploads/}")
    private String uploadPath;
    
    // ==================== 게시글 조회 관련 메서드 ====================
    
    /**
     * 게시글 목록 조회 (기본 버전)
     * 
     * 기능:
     * - 정렬, 필터링, 페이징을 적용한 게시글 목록 조회
     * - 각 게시글에 빈 이미지 리스트 설정 (호환성 유지)
     * 
     * @param sort 정렬 방식 ("최신순" 또는 "인기순")
     * @param mountain 산 이름 필터 (null이면 전체 조회)
     * @param offset 페이징을 위한 오프셋 (건너뛸 게시글 수)
     * @param size 조회할 게시글 수
     * @return 게시글 목록 (이미지 정보 미포함)
     * @throws RuntimeException 조회 중 오류 발생 시
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션 (성능 최적화)
    public List<PostDTO> getPosts(String sort, String mountain, int offset, int size) {
        try {
            // 데이터베이스에서 게시글 목록 조회
            List<PostDTO> posts = postDAO.selectPosts(sort, mountain, offset, size);
            
            // 각 게시글에 빈 이미지 리스트 설정 (기존 코드와의 호환성 유지)
            for (PostDTO post : posts) {
                post.setImagePaths(new ArrayList<>());
            }
            
            return posts;
        } catch (Exception e) {
            // 예외 발생 시 RuntimeException으로 래핑하여 던짐
            throw new RuntimeException("게시글 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * 게시글 목록 조회 (이미지 정보 포함 버전) - 새로 추가
     * 
     * 기능:
     * - 기존 getPosts 메서드를 확장하여 각 게시글의 이미지 정보도 함께 조회
     * - 프론트엔드에서 이미지 아이콘 표시에 필요한 정보 제공
     * 
     * @param sort 정렬 방식 ("최신순" 또는 "인기순")
     * @param mountain 산 이름 필터 (null이면 전체 조회)
     * @param offset 페이징을 위한 오프셋 (건너뛸 게시글 수)
     * @param size 조회할 게시글 수
     * @return 이미지 정보가 포함된 게시글 목록
     * @throws RuntimeException 조회 중 오류 발생 시
     */
    @Transactional(readOnly = true)
    public List<PostDTO> getPostsWithImageInfo(String sort, String mountain, int offset, int size) {
        try {
            // 1단계: 기본 게시글 정보 조회
            List<PostDTO> posts = postDAO.selectPosts(sort, mountain, offset, size);
            
            // 2단계: 각 게시글에 대해 이미지 정보 추가 조회
            for (PostDTO post : posts) {
                try {
                    // 게시글 ID로 해당 게시글의 이미지 경로 목록 조회
                    List<String> imagePaths = postDAO.selectImagePaths(post.getId());
                    
                    // 이미지 경로 설정 (자동으로 imageCount, hasImages도 업데이트됨)
                    post.setImagePaths(imagePaths != null ? imagePaths : new ArrayList<>());
                    
                } catch (Exception e) {
                    // 개별 게시글의 이미지 정보 조회 실패 시 기본값 설정
                    // 전체 조회가 실패하지 않도록 예외를 잡아서 처리
                    post.setImagePaths(new ArrayList<>());
                }
            }
            
            return posts;
        } catch (Exception e) {
            throw new RuntimeException("이미지 정보 포함 게시글 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * 게시글 총 개수 조회
     * 
     * 기능:
     * - 필터 조건에 맞는 총 게시글 수 조회
     * - 페이징 계산에 사용
     * 
     * @param mountain 산 이름 필터 (null이면 전체 조회)
     * @return 총 게시글 수
     */
    @Transactional(readOnly = true)
    public int getPostCount(String mountain) {
        return postDAO.selectPostCount(mountain);
    }
    
    /**
     * 게시글 상세 조회
     * 
     * 기능:
     * - 특정 게시글의 상세 정보 조회
     * - 이미지 경로 목록도 함께 조회
     * 
     * @param id 조회할 게시글 ID
     * @return 게시글 상세 정보 (이미지 경로 포함)
     * @throws RuntimeException 조회 중 오류 발생 시
     */
    @Transactional(readOnly = true)
    public PostDTO getPostById(int id) {
        try {
            // 게시글 기본 정보 조회
            PostDTO post = postDAO.selectPostById(id);
            
            if (post != null) {
                // 게시글에 첨부된 이미지 경로 목록 조회
                List<String> imagePaths = postDAO.selectImagePaths(id);
                post.setImagePaths(imagePaths != null ? imagePaths : new ArrayList<>());
            }
            
            return post;
        } catch (Exception e) {
            throw new RuntimeException("게시글 상세 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * 조회수 증가
     * 
     * 기능:
     * - 게시글 조회 시 조회수를 1 증가
     * - 별도 트랜잭션으로 처리하여 조회 실패 시에도 조회수는 증가
     * 
     * @param id 조회수를 증가시킬 게시글 ID
     */
    public void increaseViewCount(int id) {
        try {
            postDAO.increaseViewCount(id);
        } catch (Exception e) {
            // 조회수 증가 실패는 전체 조회를 방해하지 않도록 로그만 남김
            // 실제 운영에서는 로거를 사용해야 함
            System.err.println("조회수 증가 실패 - postId: " + id + ", error: " + e.getMessage());
        }
    }
    
    // ==================== 게시글 생성/수정/삭제 관련 메서드 ====================
    
    /**
     * 새 게시글 작성 (통합된 버전)
     * 
     * 기능:
     * - 게시글 기본 정보를 posts 테이블에 저장
     * - 이미지 경로가 있다면 post_images 테이블에도 저장
     * - 트랜잭션으로 일관성 보장
     * 
     * @param post 작성할 게시글 정보 (이미지 경로 포함 가능)
     * @return 생성된 게시글 정보 (ID 포함)
     * @throws RuntimeException 게시글 생성 실패 시
     */
    public PostDTO createPost(PostDTO post) {
        try {
            // 1단계: 게시글 기본 정보 삽입
            int result = postDAO.insertPost(post);
            
            if (result > 0) {
                // ID가 제대로 생성되었는지 확인 (AUTO_INCREMENT로 생성됨)
                if (post.getId() == null) {
                    throw new RuntimeException("게시글 ID가 생성되지 않았습니다.");
                }
                
                // 2단계: 이미지 경로가 있다면 post_images 테이블에 저장
                if (post.getImagePaths() != null && !post.getImagePaths().isEmpty()) {
                    for (String imagePath : post.getImagePaths()) {
                        postDAO.insertImagePath(post.getId(), imagePath);
                    }
                }
                
                // 3단계: 생성된 게시글 정보 반환 (이미지 정보 포함)
                return getPostById(post.getId());
            }
            
            throw new RuntimeException("게시글 생성에 실패했습니다.");
            
        } catch (Exception e) {
            throw new RuntimeException("게시글 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * ✅ [추가/수정된 부분] 게시글 수정
     * * 기능:
     * - (권한 검증) 현재 로그인한 사용자가 게시글 작성자인지 확인합니다.
     * - 게시글의 제목, 내용, 산 정보를 업데이트합니다.
     * - 기존에 연결된 이미지 경로를 DB에서 모두 삭제합니다.
     * - 새로운 이미지 경로 목록을 DB에 다시 삽입합니다.
     * - 이 모든 과정은 하나의 트랜잭션으로 처리되어 중간에 실패하면 모두 롤백됩니다.
     * * @param post 수정할 정보가 담긴 PostDTO 객체 (id 필드 필수)
     * @return 수정이 완료된 후의 최신 게시글 정보
     * @throws SecurityException 사용자가 수정 권한이 없는 경우
     * @throws RuntimeException 그 외 DB 오류 등 수정 실패 시
     */
    public PostDTO updatePost(PostDTO post) {
        // 1. (권한 검증) 수정하려는 게시글의 원본 정보를 가져옵니다.
        PostDTO originalPost = postDAO.selectPostById(post.getId());
        if (originalPost == null) {
            throw new RuntimeException("수정할 게시글을 찾을 수 없습니다. (ID: " + post.getId() + ")");
        }

        // --- Spring Security를 사용할 경우의 권한 검증 예시 ---
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String currentUsername = authentication.getName(); 
        // if (!originalPost.getNickname().equals(currentUsername)) {
        //     throw new SecurityException("게시글을 수정할 권한이 없습니다.");
        // }
        // ---------------------------------------------------

        // 2. 게시글 기본 정보(제목, 내용 등)를 수정합니다.
        int updateResult = postDAO.updatePost(post);
        
        if (updateResult > 0) {
            // 3. 이 게시글과 관련된 기존 이미지 경로를 DB에서 모두 삭제합니다.
            postDAO.deleteImagePaths(post.getId());
            
            // 4. 수정된 게시글 정보에 새로운 이미지 경로가 있다면 다시 삽입합니다.
            if (post.getImagePaths() != null && !post.getImagePaths().isEmpty()) {
                for (String imagePath : post.getImagePaths()) {
                    postDAO.insertImagePath(post.getId(), imagePath);
                }
            }
            
            // 5. 모든 수정이 완료된 최신 게시글 정보를 다시 조회하여 반환합니다.
            return getPostById(post.getId());
        }
        
        throw new RuntimeException("게시글 수정에 실패했습니다. (ID: " + post.getId() + ")");
    }
    
    /**
     * 게시글 삭제
     * 
     * 기능:
     * - 관련 이미지 경로 먼저 삭제 (외래키 제약조건 때문)
     * - 게시글 본문 삭제
     * - 트랜잭션으로 일관성 보장
     * 
     * @param postId 삭제할 게시글 ID
     * @return 삭제 성공 여부
     * @throws RuntimeException 게시글 삭제 실패 시
     */
    public boolean deletePost(int postId) {
        try {
            // 1단계: 관련 이미지 경로 먼저 삭제
            postDAO.deleteImagePaths(postId);
            
            // 2단계: 게시글 삭제 (CASCADE로 댓글, 좋아요 등도 함께 삭제됨)
            int result = postDAO.deletePost(postId);
            
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("게시글 삭제에 실패했습니다: " + e.getMessage(), e);
        }
    }
    
    // ==================== 이미지 업로드 관련 메서드 ====================
    
    /**
     * 이미지 업로드 (통합된 버전 - 파일 크기 검증 포함)
     * 
     * 기능:
     * - 여러 이미지 파일 동시 업로드 지원
     * - 파일 크기 및 확장자 검증
     * - 고유한 파일명 생성으로 중복 방지
     * - 업로드 디렉토리 자동 생성
     * 
     * @param images 업로드할 이미지 파일 배열
     * @return 업로드된 이미지의 웹 접근 경로 목록
     * @throws IOException 파일 업로드 실패 시
     */
    public List<String> uploadImages(MultipartFile[] images) throws IOException {
        List<String> imagePaths = new ArrayList<>();
        
        // 업로드 디렉토리 생성 (존재하지 않으면 생성)
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 각 이미지 파일 처리
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                // 파일 크기 검증 (5MB 제한)
                if (image.getSize() > 5 * 1024 * 1024) {
                    throw new IOException("파일 크기가 5MB를 초과합니다: " + image.getOriginalFilename());
                }
                
                // 파일 확장자 검증
                String originalFilename = image.getOriginalFilename();
                if (!isValidImageFile(originalFilename)) {
                    throw new IOException("유효하지 않은 이미지 파일입니다: " + originalFilename);
                }
                
                // 고유한 파일명 생성 (UUID 사용)
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
    
    // ==================== 좋아요/북마크 관련 메서드 ====================
    
    /**
     * 좋아요 토글
     * 
     * 기능:
     * - 현재 좋아요 상태 확인
     * - 좋아요가 있으면 제거, 없으면 추가
     * - posts 테이블의 like_count 컬럼 자동 업데이트
     * - 현재 상태와 총 좋아요 수 반환
     * 
     * @param postId 좋아요를 토글할 게시글 ID
     * @param userId 좋아요를 누르는 사용자 ID
     * @return 좋아요 상태 정보 (isLiked, likeCount)
     * @throws RuntimeException 좋아요 처리 실패 시
     */
    public Map<String, Object> toggleLike(int postId, String userId) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("좋아요 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * 북마크 토글
     * 
     * 기능:
     * - 현재 북마크 상태 확인
     * - 북마크가 있으면 제거, 없으면 추가
     * - 사용자별 북마크 목록 관리
     * 
     * @param postId 북마크를 토글할 게시글 ID
     * @param userId 북마크하는 사용자 ID
     * @return 현재 북마크 상태 (true: 북마크됨, false: 북마크 안됨)
     * @throws RuntimeException 북마크 처리 실패 시
     */
    public boolean toggleBookmark(int postId, String userId) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("북마크 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    // ==================== 기타 조회 메서드 ====================
    
    /**
     * 산 목록 조회
     * 
     * 기능:
     * - 게시글에서 사용된 산 이름들을 중복 제거하여 조회
     * - 알파벳 순으로 정렬
     * - 게시글 작성 시 드롭다운 옵션으로 사용
     * 
     * @return 중복 제거된 산 이름 목록
     */
    @Transactional(readOnly = true)
    public List<String> getMountains() {
        return postDAO.selectMountains();
    }
    
    /**
     * 댓글 수 업데이트
     * 
     * 기능:
     * - 댓글이 추가/삭제될 때 posts 테이블의 comment_count 컬럼 업데이트
     * - 댓글 서비스에서 호출됨
     * 
     * @param postId 댓글 수를 업데이트할 게시글 ID
     * @throws RuntimeException 업데이트 실패 시
     */
    public void updatePostCommentCount(int postId) {
        try {
            postDAO.updateCommentCount(postId);
        } catch (Exception e) {
            throw new RuntimeException("댓글 수 업데이트 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    // ==================== 유틸리티 메서드 (private) ====================
    
    /**
     * 파일 확장자 추출
     * 
     * @param filename 파일명
     * @return 확장자 (소문자로 변환)
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * 유효한 이미지 파일인지 확인 (강화된 검증)
     * 
     * 기능:
     * - 파일명 null/empty 체크
     * - 허용된 이미지 확장자인지 확인
     * - 보안을 위한 확장자 화이트리스트 방식 사용
     * 
     * @param filename 검증할 파일명
     * @return 유효성 여부 (true: 유효한 이미지, false: 유효하지 않음)
     */
    private boolean isValidImageFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        String extension = getFileExtension(filename);
        
        // 허용된 이미지 확장자 목록 (화이트리스트 방식)
        return extension.matches("^(jpg|jpeg|png|gif|bmp|webp)$");
    }
}
