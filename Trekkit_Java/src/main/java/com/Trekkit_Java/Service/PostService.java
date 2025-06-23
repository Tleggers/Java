package com.Trekkit_Java.Service;

import com.Trekkit_Java.DAO.PostDAO; // PostDAO 인터페이스 임포트 (데이터베이스 접근)
import com.Trekkit_Java.DTO.PostDTO; // PostDTO 데이터 전송 객체 임포트
import lombok.RequiredArgsConstructor; // Lombok의 @RequiredArgsConstructor (final 필드 생성자 자동 생성)
import org.springframework.stereotype.Service; // 이 클래스가 서비스 계층의 컴포넌트임을 나타내는 어노테이션
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리를 위한 어노테이션
import org.springframework.web.multipart.MultipartFile; // MultipartFile (파일 업로드) 추가

import java.util.ArrayList; // ArrayList 사용
import java.util.HashMap; // HashMap 사용
import java.util.List; // List 인터페이스 사용
import java.util.Map; // Map 인터페이스 사용
import java.util.Objects; // 객체 비교 유틸리티 사용 (Objects.equals)
import java.io.File; // 파일 시스템 접근 (File)
import java.nio.file.Files; // 파일 복사 (Files)
import java.nio.file.Path; // 파일 경로 (Path)
import java.nio.file.Paths; // 파일 경로 생성 (Paths)
import java.util.UUID; // 고유 ID 생성 (UUID)
import org.springframework.beans.factory.annotation.Value; // @Value 어노테이션 (속성 값 주입)

/**
 * 게시글(Post) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * DAO(Data Access Object)를 통해 데이터베이스와 상호작용하며, 트랜잭션 관리, 파일 업로드 등을 포함합니다.
 */
@Service // 이 클래스가 Spring 서비스 계층의 컴포넌트임을 나타냅니다.
@RequiredArgsConstructor // Lombok: final 필드(postDAO)를 주입하는 생성자를 자동으로 생성합니다.
public class PostService {

    private final PostDAO postDAO; // PostDAO를 주입받아 게시글 관련 DB 작업을 수행합니다.
    
    @Value("${file.upload.path}") // application.properties 또는 application.yml에서 'file.upload.path' 속성 값을 주입
    private String uploadPath; // 파일이 업로드될 경로

    /**
     * 새로운 게시글을 생성하고 데이터베이스에 삽입합니다.
     * 게시글에 이미지가 포함된 경우, 이미지 경로도 함께 저장합니다.
     * @param postDTO 생성할 게시글 정보.
     * @param userId 게시글 작성자의 ID.
     * @return 생성된 게시글 DTO (DB에서 생성된 ID 포함).
     */
    @Transactional // 이 메서드 내의 DB 작업(게시글 삽입, 이미지 경로 삽입)이 하나의 트랜잭션으로 처리되도록 합니다.
    public PostDTO createPost(PostDTO postDTO, Long userId) {
        postDTO.setUserId(userId); // 게시글 DTO에 작성자 ID를 설정합니다.
        postDAO.save(postDTO); // 게시글을 DB에 삽입합니다.
        
        // 이미지가 존재하면 각 이미지 경로를 DB에 저장합니다.
        if (postDTO.getImagePaths() != null && !postDTO.getImagePaths().isEmpty()) {
            for (String imagePath : postDTO.getImagePaths()) {
                postDAO.saveImage(postDTO.getId(), imagePath); // 게시글 ID와 이미지 경로 저장
            }
        }
        return postDTO; // 생성된 게시글 DTO를 반환합니다.
    }
    
    /**
     * 클라이언트로부터 받은 이미지 파일들을 서버의 지정된 경로에 업로드하고,
     * 저장된 파일의 URL 경로 목록을 반환합니다.
     * @param files 업로드할 MultipartFile 배열.
     * @return 업로드된 이미지 파일의 상대 경로 목록.
     * @throws Exception 파일 시스템 작업 중 오류 발생 시.
     */
    @Transactional // 파일 시스템 작업이지만, DB 트랜잭션과 연계될 수 있으므로 트랜잭션으로 관리 (주의 필요)
    public List<String> uploadPostImages(MultipartFile[] files) throws Exception {
        List<String> imagePaths = new ArrayList<>(); // 업로드된 이미지 경로를 저장할 리스트
        
        // 업로드 디렉토리가 없으면 생성합니다.
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // 디렉토리 생성
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename(); // 원본 파일명
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 파일 확장자 추출
                String uuid = UUID.randomUUID().toString(); // 고유한 파일명 생성을 위한 UUID
                String newFilename = uuid + fileExtension; // 새로운 파일명
                Path filePath = Paths.get(uploadPath, newFilename); // 저장될 파일의 전체 경로

                Files.copy(file.getInputStream(), filePath); // 파일을 지정된 경로에 복사
                // 저장된 파일의 URL 경로를 리스트에 추가 (예: /uploads/abc-def.jpg)
                imagePaths.add("/uploads/" + newFilename);
            }
        }
        return imagePaths; // 업로드된 이미지 경로 목록 반환
    }

    /**
     * 특정 게시글의 상세 정보를 조회하고, 조회수를 1 증가시킵니다.
     * 게시글에 연결된 이미지 경로도 함께 조회하여 DTO에 설정합니다.
     * @param postId 조회할 게시글의 ID.
     * @return 조회된 게시글 DTO (PostDTO).
     */
    @Transactional // 이 메서드 내의 DB 작업(조회수 증가, 게시글 조회, 이미지 조회)이 하나의 트랜잭션으로 처리되도록 합니다.
    public PostDTO getPostById(Long postId) {
        postDAO.increaseViewCount(postId); // 해당 게시글의 조회수를 1 증가시킵니다.
        PostDTO post = postDAO.findById(postId); // 게시글 상세 정보를 조회합니다.
        if (post != null) {
            List<String> images = postDAO.findImagesByPostId(postId); // 게시글에 연결된 이미지 경로들을 조회합니다.
            post.setImagePaths(images); // 조회된 이미지 경로들을 DTO에 설정합니다.
        }
        return post; // 조회된 게시글 DTO를 반환합니다.
    }

    /**
     * 모든 게시글을 페이징 처리하여 조회하고, 각 게시글에 연결된 이미지 경로를 설정합니다.
     * 읽기 전용 트랜잭션으로 설정하여 성능을 최적화합니다.
     * @param page 조회할 페이지 번호.
     * @param size 한 페이지당 게시글 개수.
     * @param sort 정렬 기준 (예: "최신순", "인기순").
     * @param mountain 특정 산으로 필터링할 경우의 산 이름.
     * @return 게시글 목록 (List<PostDTO>)과 전체 개수(totalCount)를 포함하는 Map.
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정
    public Map<String, Object> getAllPosts(int page, int size, String sort, String mountain) {
        Map<String, Object> params = new HashMap<>(); // 쿼리 파라미터를 담을 Map
        params.put("offset", page * size); // DB 쿼리에서 사용할 offset (건너뛸 레코드 수)
        params.put("limit", size); // DB 쿼리에서 사용할 limit (가져올 레코드 수)
        params.put("sort", sort); // 정렬 기준
        params.put("mountain", mountain); // 산 필터

        List<PostDTO> posts = postDAO.findAll(params); // 모든 게시글을 필터링 및 페이징 처리하여 조회
        int totalCount = postDAO.count(mountain); // 전체 게시글 개수 조회 (필터링된 산 기준)
        
        // 각 게시글에 연결된 이미지 경로를 조회하여 DTO에 설정합니다.
        for (PostDTO post : posts) {
            List<String> images = postDAO.findImagesByPostId(post.getId());
            post.setImagePaths(images);
        }

        Map<String, Object> response = new HashMap<>(); // 응답 데이터를 담을 Map
        response.put("posts", posts); // 게시글 목록 추가
        response.put("totalCount", totalCount); // 전체 개수 추가
        return response; // 응답 Map 반환
    }

    /**
     * 기존 게시글의 내용을 수정하고, 이미지 목록을 갱신합니다.
     * 게시글을 수정하려는 사용자가 해당 게시글의 작성자인지 확인하여 권한을 검사합니다.
     * @param postDTO 수정할 게시글 정보.
     * @param userId 게시글 수정을 요청한 사용자의 ID.
     * @return 수정된 게시글 DTO.
     * @throws IllegalArgumentException 게시글이 존재하지 않을 경우 발생.
     * @throws SecurityException 게시글을 수정할 권한이 없을 경우 발생.
     */
    @Transactional // 이 메서드 내의 DB 작업(게시글 업데이트, 이미지 삭제/삽입)이 하나의 트랜잭션으로 처리되도록 합니다.
    public PostDTO updatePost(PostDTO postDTO, Long userId) {
        PostDTO originalPost = postDAO.findById(postDTO.getId()); // 기존 게시글 정보를 조회합니다.
        if (originalPost == null) {
            // 수정할 게시글이 존재하지 않으면 예외를 던집니다.
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
        // 현재 사용자의 ID와 원본 게시글의 작성자 ID가 다르면 권한 없음 예외를 던집니다.
        if (!Objects.equals(originalPost.getUserId(), userId)) {
            throw new SecurityException("게시글을 수정할 권한이 없습니다.");
        }
        postDAO.update(postDTO); // 게시글을 DB에서 업데이트합니다.

        // 기존 이미지를 모두 삭제하고 새로운 이미지 경로들을 다시 저장합니다.
        postDAO.deleteImagesByPostId(postDTO.getId()); // 기존 이미지 경로 삭제
        if (postDTO.getImagePaths() != null && !postDTO.getImagePaths().isEmpty()) {
            for (String imagePath : postDTO.getImagePaths()) {
                postDAO.saveImage(postDTO.getId(), imagePath); // 새로운 이미지 경로 저장
            }
        }
        return postDAO.findById(postDTO.getId()); // 업데이트된 게시글의 상세 정보를 조회하여 반환합니다.
    }
    
    /**
     * 특정 게시글을 삭제합니다.
     * 게시글을 삭제하려는 사용자가 해당 게시글의 작성자인지 확인하여 권한을 검사합니다.
     * @param postId 삭제할 게시글의 ID.
     * @param userId 게시글 삭제를 요청한 사용자의 ID.
     * @throws IllegalArgumentException 게시글이 존재하지 않을 경우 발생.
     * @throws SecurityException 게시글을 삭제할 권한이 없을 경우 발생.
     */
    @Transactional // 이 메서드 내의 DB 작업(게시글 삭제)이 하나의 트랜잭션으로 처리되도록 합니다.
    public void deletePost(Long postId, Long userId) {
        PostDTO post = postDAO.findById(postId); // 게시글 정보를 조회합니다.
        if (post == null) {
            // 게시글이 존재하지 않으면 예외를 던집니다.
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
        // 현재 사용자의 ID와 게시글의 작성자 ID가 다르면 권한 없음 예외를 던집니다.
        if (!Objects.equals(post.getUserId(), userId)) {
            throw new SecurityException("게시글을 삭제할 권한이 없습니다.");
        }
        postDAO.delete(postId); // 게시글을 DB에서 삭제합니다.
    }
    
    /**
     * 특정 게시글에 대한 좋아요 상태를 토글(추가/취소)합니다.
     * 좋아요가 없으면 추가하고, 있으면 삭제합니다. 이후 게시글의 좋아요 수를 갱신합니다.
     * @param postId 좋아요를 토글할 게시글의 ID.
     * @param userId 좋아요를 요청한 사용자의 ID.
     * @return 좋아요가 추가되었으면 true, 취소되었으면 false.
     */
    @Transactional // 이 메서드 내의 DB 작업(좋아요 삽입/삭제, 좋아요 수 갱신)이 하나의 트랜잭션으로 처리되도록 합니다.
    public boolean toggleLike(Long postId, Long userId) {
        boolean isLiked;
        // 사용자가 해당 게시글에 이미 좋아요를 눌렀는지 확인합니다.
        if (postDAO.findLikeByPostIdAndUserId(postId, userId) > 0) {
            postDAO.deleteLike(postId, userId); // 이미 좋아요를 눌렀으면 삭제 (취소)
            isLiked = false;
        } else {
            postDAO.addLike(postId, userId); // 좋아요를 누르지 않았으면 추가
            isLiked = true;
        }
        postDAO.updateLikeCount(postId); // 게시글의 좋아요 수를 갱신합니다.
        return isLiked; // 좋아요 상태 반환
    }

    /**
     * 특정 게시글의 현재 좋아요 개수를 조회합니다.
     * @param postId 좋아요 개수를 조회할 게시글의 ID.
     * @return 해당 게시글의 총 좋아요 개수.
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정
    public int getLikeCount(Long postId) {
        return postDAO.getLikeCount(postId); // DAO를 통해 좋아요 개수를 조회하여 반환합니다.
    }

    /**
     * 특정 게시글에 대한 북마크 상태를 토글(추가/취소)합니다.
     * 북마크가 없으면 추가하고, 있으면 삭제합니다.
     * @param postId 북마크를 토글할 게시글의 ID.
     * @param userId 북마크를 요청한 사용자의 ID.
     * @return 북마크가 추가되었으면 true, 취소되었으면 false.
     */
    @Transactional // 이 메서드 내의 DB 작업(북마크 삽입/삭제)이 하나의 트랜잭션으로 처리되도록 합니다.
    public boolean toggleBookmark(Long postId, Long userId) {
        boolean isBookmarked;
        // 사용자가 해당 게시글을 이미 북마크했는지 확인합니다.
        if (postDAO.findBookmarkByPostIdAndUserId(postId, userId) > 0) {
            postDAO.deleteBookmark(postId, userId); // 이미 북마크했으면 삭제 (취소)
            isBookmarked = false;
        } else {
            postDAO.addBookmark(postId, userId); // 북마크하지 않았으면 추가
            isBookmarked = true;
        }
        return isBookmarked; // 북마크 상태 반환
    }

    /**
     * 데이터베이스에 저장된 모든 산 이름을 조회합니다.
     * @return 모든 산 이름의 리스트.
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정
    public List<String> getAllMountainNames() {
        return postDAO.findAllMountainNames(); // DAO를 통해 모든 산 이름을 조회하여 반환합니다.
    }
}
