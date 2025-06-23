package com.Trekkit_Java.DAO;

import com.Trekkit_Java.DTO.NoticeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeDAO {

    List<NoticeDTO> selectAllNotices(@Param("offset") int offset, @Param("size") int size);

    List<NoticeDTO> selectNoticesByCategory(@Param("category") String category, @Param("offset") int offset, @Param("size") int size);

    int getTotalNoticeCount();
    
    int selectCountByCategory(@Param("category") String category);

    // noticeId -> id로 변경
    NoticeDTO selectNoticeById(@Param("id") int id);

    // noticeId -> id로 변경
    void increaseViewCount(@Param("id") int id);
    
    // noticeId -> id로 변경 (DTO 필드명에 맞춰)
    void insertNotice(NoticeDTO notice); // NoticeDTO의 keyProperty는 "id"를 참조할 것임
    
    // noticeId -> id로 변경 (DTO 필드명에 맞춰)
    void updateNotice(NoticeDTO notice); // NoticeDTO의 WHERE 조건은 "id"를 참조할 것임
    
    // noticeId -> id로 변경
    void deleteNotice(@Param("id") int id);
}