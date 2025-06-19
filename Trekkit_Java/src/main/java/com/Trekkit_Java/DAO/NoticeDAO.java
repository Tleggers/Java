package com.Trekkit_Java.DAO;

import com.Trekkit_Java.DTO.NoticeDTO; // 공지사항 데이터 전송 객체 (DTO) 임포트
import org.apache.ibatis.annotations.Mapper; // MyBatis 매퍼 인터페이스임을 나타내는 어노테이션
import org.apache.ibatis.annotations.Param; // MyBatis 쿼리에 여러 파라미터를 전달할 때 사용되는 어노테이션

import java.util.List; // 리스트(컬렉션) 사용을 위해 임포트

@Mapper // 이 인터페이스가 MyBatis의 매퍼임을 선언합니다. Spring이 이 인터페이스를 스캔하여 구현체를 생성합니다.
public interface NoticeDAO {

    /**
     * 모든 공지사항 목록을 페이지네이션하여 조회합니다.
     * 데이터 전송: offset(시작 위치)과 size(개수) 파라미터를 MyBatis Mapper XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 공지사항 목록(List<NoticeDTO>)을 반환합니다.
     * @param offset 조회 시작 위치
     * @param size 한 페이지당 공지사항 개수
     * @return 공지사항 목록
     */
    List<NoticeDTO> selectAllNotices(@Param("offset") int offset, @Param("size") int size);

    /**
     * 특정 카테고리에 해당하는 공지사항 목록을 페이지네이션하여 조회합니다.
     * (현재 프론트엔드에서 카테고리 필터링 기능이 제거되었지만, DAO 메서드는 유지됩니다.)
     * 데이터 전송: category, offset, size 파라미터를 MyBatis Mapper XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 공지사항 목록(List<NoticeDTO>)을 반환합니다.
     * @param category 조회할 카테고리
     * @param offset 조회 시작 위치
     * @param size 한 페이지당 공지사항 개수
     * @return 카테고리별 공지사항 목록
     */
    List<NoticeDTO> selectNoticesByCategory(@Param("category") String category, @Param("offset") int offset, @Param("size") int size);

    /**
     * 전체 공지사항의 총 개수를 조회합니다. (페이징 처리를 위해 사용됩니다.)
     * 데이터 전송: 없음
     * 데이터 수신: DB에서 조회된 공지사항의 총 개수(int)를 반환합니다.
     * @return 전체 공지사항의 총 개수
     */
    int getTotalNoticeCount();
    
    /**
     * 특정 카테고리에 해당하는 공지사항의 개수를 조회합니다.
     * (현재 프론트엔드에서 카테고리 필터링 기능이 제거되었지만, DAO 메서드는 유지됩니다.)
     * 데이터 전송: category 파라미터를 MyBatis Mapper XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 해당 카테고리의 공지사항 개수(int)를 반환합니다.
     * @param category 조회할 카테고리
     * @return 카테고리별 공지사항 개수
     */
    int selectCountByCategory(@Param("category") String category);

    /**
     * 공지사항 ID를 사용하여 특정 공지사항의 상세 정보를 조회합니다.
     * 데이터 전송: noticeId 파라미터를 MyBatis Mapper XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 단일 공지사항(NoticeDTO)을 반환합니다.
     * @param noticeId 조회할 공지사항의 ID
     * @return 조회된 공지사항 DTO
     */
    NoticeDTO selectNoticeById(@Param("noticeId") int noticeId);

    /**
     * 특정 공지사항의 조회수를 1 증가시킵니다.
     * 데이터 전송: noticeId 파라미터를 MyBatis Mapper XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param noticeId 조회수를 증가시킬 공지사항의 ID
     */
    void increaseViewCount(@Param("noticeId") int noticeId);
    
    /**
     * 새로운 공지사항을 데이터베이스에 삽입합니다.
     * 데이터 전송: 공지사항 정보가 담긴 NoticeDTO 객체를 MyBatis Mapper XML로 전송합니다.
     * 데이터 수신: 없음 (DB 삽입). `useGeneratedKeys`와 `keyProperty` 설정을 통해 삽입 후 자동 생성된 noticeId가 NoticeDTO 객체에 다시 채워집니다.
     * @param notice 삽입할 공지사항 DTO
     */
    void insertNotice(NoticeDTO notice);
    
    /**
     * 기존 공지사항의 내용을 수정합니다.
     * 데이터 전송: 수정할 공지사항 정보가 담긴 NoticeDTO 객체를 MyBatis Mapper XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param notice 수정할 공지사항 DTO
     */
    void updateNotice(NoticeDTO notice);
    
    /**
     * 특정 공지사항을 데이터베이스에서 삭제합니다.
     * 데이터 전송: noticeId 파라미터를 MyBatis Mapper XML로 전송합니다.
     * 데이터 수신: 없음 (DB 삭제)
     * @param noticeId 삭제할 공지사항의 ID
     */
    void deleteNotice(@Param("noticeId") int noticeId);
}
