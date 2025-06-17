package com.Trekkit_Java.Service;

import com.Trekkit_Java.DAO.NoticeDAO;
import com.Trekkit_Java.DTO.NoticeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NoticeService {

    @Autowired
    private NoticeDAO noticeDAO;

    public List<NoticeDTO> getNotices(String category, int offset, int size) {
        if (category == null || category.isEmpty() || category.equals("전체")) {
            return noticeDAO.selectAllNotices(offset, size);
        } else {
            return noticeDAO.selectNoticesByCategory(category, offset, size);
        }
    }

    public int getNoticeCount(String category) {
        if (category == null || category.isEmpty() || category.equals("전체")) {
            return noticeDAO.selectTotalCount();
        } else {
            return noticeDAO.selectCountByCategory(category);
        }
    }

    public NoticeDTO getNoticeById(int noticeId) {
        noticeDAO.increaseViewCount(noticeId);
        return noticeDAO.selectNoticeById(noticeId);
    }
    
    /**
     * 새 공지사항을 생성하고, 생성된 전체 데이터를 반환합니다.
     * @param notice 생성할 공지사항 데이터
     * @return DB에 완전히 저장된 후의 공지사항 데이터 (생성된 ID, 타임스탬프 포함)
     */
    public NoticeDTO createNotice(NoticeDTO notice) {
        // 1. DB에 공지사항을 삽입합니다.
        // (noticemapper.xml의 useGeneratedKeys 설정 덕분에 notice 객체의 noticeId 필드에 자동 생성된 ID가 채워집니다.)
        noticeDAO.insertNotice(notice);
        
        // 2. 방금 생성된 ID를 사용하여 전체 공지사항 데이터를 다시 조회하여 반환합니다.
        // 이렇게 하면 DB에서 자동 생성된 created_at 같은 값까지 모두 포함된 완전한 객체를 얻을 수 있습니다.
        return noticeDAO.selectNoticeById(notice.getNoticeId());
    }

    /**
     * 공지사항을 수정한 뒤, 수정된 전체 데이터를 반환합니다.
     * @param notice 수정할 공지사항 데이터
     * @return DB에서 수정이 완료된 후의 최신 공지사항 데이터
     */
    public NoticeDTO updateNotice(NoticeDTO notice) {
        noticeDAO.updateNotice(notice);
        return noticeDAO.selectNoticeById(notice.getNoticeId());
    }
    
    public void deleteNotice(int noticeId) {
        noticeDAO.deleteNotice(noticeId);
    }
}