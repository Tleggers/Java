package com.Trekkit_Java.DAO;

import com.Trekkit_Java.DTO.NoticeDTO; // NoticeDTO 데이터 전송 객체 임포트
import org.apache.ibatis.annotations.Mapper; // MyBatis의 Mapper 어노테이션 임포트
import org.apache.ibatis.annotations.Param; // MyBatis의 @Param 어노테이션 임포트 (SQL 쿼리 파라미터 매핑)

import java.util.List; // List 인터페이스 사용을 위한 임포트

/**
 * 공지사항 관련 데이터베이스 작업을 위한 MyBatis Mapper 인터페이스입니다.
 * 이 인터페이스의 메서드들은 `NoticeMapper.xml` 파일의 SQL 쿼리와 매핑됩니다.
 */
@Mapper // 이 인터페이스가 MyBatis의 매퍼임을 선언합니다.
public interface NoticeDAO {

    /**
     * 모든 공지사항을 페이징 처리하여 조회합니다.
     *
     * @param offset 조회 시작 위치 (페이지네이션).
     * @param size 한 페이지당 공지사항 개수.
     * @return 페이징 처리된 공지사항 목록 (NoticeDTO 리스트).
     */
    List<NoticeDTO> selectAllNotices(@Param("offset") int offset, @Param("size") int size);

    /**
     * 특정 카테고리에 해당하는 공지사항을 페이징 처리하여 조회합니다.
     *
     * @param category 조회할 공지사항의 카테고리.
     * @param offset 조회 시작 위치 (페이지네이션).
     * @param size 한 페이지당 공지사항 개수.
     * @return 특정 카테고리에 해당하는 공지사항 목록 (NoticeDTO 리스트).
     */
    List<NoticeDTO> selectNoticesByCategory(@Param("category") String category, @Param("offset") int offset, @Param("size") int size);

    /**
     * 모든 공지사항의 총 개수를 조회합니다.
     *
     * @return 전체 공지사항의 총 개수.
     */
    int getTotalNoticeCount();
    
    /**
     * 특정 카테고리에 해당하는 공지사항의 총 개수를 조회합니다.
     *
     * @param category 개수를 조회할 공지사항의 카테고리.
     * @return 특정 카테고리에 해당하는 공지사항의 총 개수.
     */
    int selectCountByCategory(@Param("category") String category);

    /**
     * 특정 ID를 가진 공지사항을 조회합니다.
     * 조회 시 해당 공지사항의 조회수를 증가시킵니다.
     *
     * @param id 조회할 공지사항의 고유 ID.
     * @return 조회된 공지사항 (NoticeDTO 객체) 또는 null.
     */
    NoticeDTO selectNoticeById(@Param("id") int id);

    /**
     * 특정 ID를 가진 공지사항의 조회수를 1 증가시킵니다.
     *
     * @param id 조회수를 증가시킬 공지사항의 고유 ID.
     */
    void increaseViewCount(@Param("id") int id);
    
    /**
     * 새로운 공지사항을 데이터베이스에 삽입합니다.
     * Mybatis XML 매퍼에서 DTO의 'id' 필드를 통해 생성된 ID를 NoticeDTO 객체에 다시 주입할 수 있습니다.
     *
     * @param notice 삽입할 공지사항 정보 (NoticeDTO 객체).
     */
    void insertNotice(NoticeDTO notice);
    
    /**
     * 기존 공지사항을 수정합니다.
     * Mybatis XML 매퍼에서 DTO의 'id' 필드를 WHERE 조건으로 사용하여 해당 공지사항을 식별합니다.
     *
     * @param notice 수정할 공지사항 정보 (NoticeDTO 객체, 'id' 필드를 포함해야 함).
     */
    void updateNotice(NoticeDTO notice);
    
    /**
     * 특정 ID를 가진 공지사항을 데이터베이스에서 삭제합니다.
     *
     * @param id 삭제할 공지사항의 고유 ID.
     */
    void deleteNotice(@Param("id") int id);
}