package com.Trekkit_Java.DAO;

import com.Trekkit_Java.DTO.NoticeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeDAO {

    // 모든 공지사항 목록 조회 (페이징)
    List<NoticeDTO> selectAllNotices(@Param("offset") int offset, @Param("size") int size);

    // 카테고리별 공지사항 목록 조회 (페이징)
    List<NoticeDTO> selectNoticesByCategory(@Param("category") String category, @Param("offset") int offset, @Param("size") int size);

    // 공지사항 총 개수 조회
    int selectTotalCount();
    
    // 카테고리별 공지사항 개수 조회
    int selectCountByCategory(@Param("category") String category);

    // 공지사항 상세 조회
    NoticeDTO selectNoticeById(@Param("noticeId") int noticeId);

    // 공지사항 조회수 증가
    void increaseViewCount(@Param("noticeId") int noticeId);
    
    // 새 공지사항 작성
    void insertNotice(NoticeDTO notice);
    
    // 공지사항 수정
    void updateNotice(NoticeDTO notice);
    
    // 공지사항 삭제
    void deleteNotice(@Param("noticeId") int noticeId);
}
