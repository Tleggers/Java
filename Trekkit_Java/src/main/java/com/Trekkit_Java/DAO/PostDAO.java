package com.Trekkit_Java.DAO;

import com.Trekkit_Java.DTO.PostDTO; // Post 데이터 전송 객체 (DTO) 임포트
import org.apache.ibatis.annotations.Mapper; // MyBatis 매퍼 인터페이스임을 나타내는 어노테이션
import org.apache.ibatis.annotations.Param; // MyBatis 쿼리에 여러 파라미터를 전달할 때 사용되는 어노테이션
import org.apache.ibatis.annotations.Select; // MyBatis의 @Select 어노테이션 (간단한 SQL 쿼리 인라인 정의)

import java.util.List; // List(컬렉션) 사용을 위해 임포트
import java.util.Map; // Map 사용을 위해 임포트

/**
 * 게시글(Post) 관련 데이터베이스 작업을 위한 MyBatis Mapper 인터페이스입니다.
 * 이 인터페이스의 메서드들은 매퍼 XML 파일의 SQL 쿼리 또는 인라인 쿼리(@Select)와 매핑됩니다.
 */
@Mapper // 이 인터페이스가 MyBatis의 매퍼임을 선언합니다.
public interface PostDAO {
    /**
     * 모든 게시글을 필터링 및 페이징 처리하여 조회합니다.
     * 검색 조건(정렬, 산, 페이지)은 Map으로 전달됩니다.
     * @param params 정렬 기준, 페이지 번호, 페이지 크기, 산 이름 등을 포함하는 Map.
     * @return 게시글 목록 (List<PostDTO>).
     */
    List<PostDTO> findAll(Map<String, Object> params);

    /**
     * 특정 산에 해당하는 게시글의 총 개수를 조회합니다.
     * @param mountain 개수를 조회할 산의 이름.
     * @return 해당 산의 게시글 총 개수.
     */
    int count(@Param("mountain") String mountain);

    /**
     * 게시글 ID를 사용하여 특정 게시글의 상세 정보를 조회합니다.
     * @param postId 조회할 게시글의 ID.
     * @return 조회된 게시글 (PostDTO 객체) 또는 null.
     */
    PostDTO findById(@Param("postId") Long postId);

    /**
     * 새로운 게시글을 데이터베이스에 삽입합니다.
     * 삽입 후 자동 생성된 ID가 PostDTO 객체에 다시 채워질 수 있도록 매퍼 설정이 필요합니다.
     * @param post 삽입할 게시글 DTO.
     * @return 삽입된 레코드의 수.
     */
    int save(PostDTO post);

    /**
     * 기존 게시글의 내용을 수정합니다.
     * @param post 수정할 게시글 DTO (ID를 포함해야 함).
     * @return 수정된 레코드의 수.
     */
    int update(PostDTO post);

    /**
     * 특정 게시글을 데이터베이스에서 삭제합니다.
     * @param postId 삭제할 게시글의 ID.
     * @return 삭제된 레코드의 수.
     */
    int delete(@Param("postId") Long postId);

    /**
     * 특정 게시글의 조회수를 1 증가시킵니다.
     * @param postId 조회수를 증가시킬 게시글의 ID.
     */
    void increaseViewCount(@Param("postId") Long postId);

    /**
     * 게시글에 첨부된 이미지 경로를 저장합니다.
     * @param postId 이미지가 속한 게시글의 ID.
     * @param imagePath 저장할 이미지의 파일 경로.
     */
    void saveImage(@Param("postId") Long postId, @Param("imagePath") String imagePath);

    /**
     * 특정 게시글에 첨부된 모든 이미지 경로를 조회합니다.
     * @param postId 이미지를 조회할 게시글의 ID.
     * @return 이미지 경로 목록 (List<String>).
     */
    List<String> findImagesByPostId(@Param("postId") Long postId);

    /**
     * 특정 게시글에 첨부된 모든 이미지를 삭제합니다.
     * 게시글 삭제 시 관련 이미지도 함께 삭제할 때 사용됩니다.
     * @param postId 이미지를 삭제할 게시글의 ID.
     */
    void deleteImagesByPostId(@Param("postId") Long postId);

    /**
     * 특정 게시글에 대한 좋아요 정보를 삽입합니다.
     * @param postId 좋아요를 누른 게시글의 ID.
     * @param userId 좋아요를 누른 사용자의 ID.
     */
    void addLike(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 특정 게시글에 대한 특정 사용자의 좋아요 정보를 삭제합니다. (좋아요 취소)
     * @param postId 좋아요를 취소할 게시글의 ID.
     * @param userId 좋아요를 취소할 사용자의 ID.
     */
    void deleteLike(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 특정 게시글에 대한 특정 사용자의 좋아요 존재 여부를 조회합니다.
     * @param postId 조회할 게시글의 ID.
     * @param userId 조회할 사용자의 ID.
     * @return 좋아요 정보의 ID (존재하면 ID 값, 없으면 0).
     */
    int findLikeByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 특정 게시글의 좋아요 수를 갱신합니다.
     * 이 메서드는 좋아요/취소 시 좋아요 테이블의 카운트를 기반으로 Post 테이블의 likeCount를 업데이트합니다.
     * @param postId 좋아요 수를 갱신할 게시글의 ID.
     */
    void updateLikeCount(@Param("postId") Long postId);

    /**
     * 특정 게시글의 댓글 수를 갱신합니다.
     * 이 메서드는 댓글 추가/삭제 시 댓글 테이블의 카운트를 기반으로 Post 테이블의 commentCount를 업데이트합니다.
     * @param postId 댓글 수를 갱신할 게시글의 ID.
     */
    void updateCommentCount(@Param("postId") Long postId);
    
    /**
     * 특정 게시글의 현재 좋아요 개수를 직접 조회합니다.
     * `updateLikeCount` 메서드와 함께 사용되어 UI에 실시간 좋아요 수를 반영할 수 있습니다.
     * @param postId 좋아요 개수를 조회할 게시글의 ID.
     * @return 해당 게시글의 총 좋아요 개수.
     */
    int getLikeCount(@Param("postId") Long postId);

    // [추가] 북마크 관련 DAO 메소드

    /**
     * 특정 게시글에 대한 북마크 정보를 삽입합니다.
     * @param postId 북마크할 게시글의 ID.
     * @param userId 북마크한 사용자의 ID.
     */
    void addBookmark(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 특정 게시글에 대한 특정 사용자의 북마크 정보를 삭제합니다. (북마크 취소)
     * @param postId 북마크를 취소할 게시글의 ID.
     * @param userId 북마크를 취소할 사용자의 ID.
     */
    void deleteBookmark(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 특정 게시글에 대한 특정 사용자의 북마크 존재 여부를 조회합니다.
     * @param postId 조회할 게시글의 ID.
     * @param userId 조회할 사용자의 ID.
     * @return 북마크 정보의 ID (존재하면 ID 값, 없으면 0).
     */
    int findBookmarkByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 데이터베이스에 저장된 모든 산 이름을 조회합니다.
     * 산 이름은 오름차순으로 정렬됩니다.
     * @return 모든 산 이름의 리스트.
     */
    @Select("SELECT name FROM mountain ORDER BY name ASC")
    List<String> findAllMountainNames();
}