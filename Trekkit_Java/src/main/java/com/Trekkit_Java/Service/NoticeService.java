package com.Trekkit_Java.Service;

import com.Trekkit_Java.DAO.NoticeDAO;
import com.Trekkit_Java.DTO.NoticeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class NoticeService {

    @Autowired
    private NoticeDAO noticeDAO;

    public Map<String, Object> getNotices(int offset, int size) {
        List<NoticeDTO> notices = noticeDAO.selectAllNotices(offset, size);
        int totalCount = noticeDAO.getTotalNoticeCount();

        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("totalCount", totalCount);
        return response;
    }

    public int getNoticeCount() {
        return noticeDAO.getTotalNoticeCount();
    }

    @Transactional
    public NoticeDTO getNoticeDetail(int id) { // noticeId -> id로 변경
        noticeDAO.increaseViewCount(id); // noticeId -> id로 변경
        NoticeDTO notice = noticeDAO.selectNoticeById(id); // noticeId -> id로 변경
        return notice;
    }

    @Transactional
    public NoticeDTO createNotice(NoticeDTO notice) {
        notice.setCreatedAt(LocalDateTime.now());
        notice.setUpdatedAt(LocalDateTime.now());
        noticeDAO.insertNotice(notice);
        // DTO의 ID 필드가 noticeId에서 id로 변경되었으므로, getter도 getId()로 변경
        return noticeDAO.selectNoticeById(notice.getId()); // getNoticeId() -> getId()
    }

    @Transactional
    public NoticeDTO updateNotice(NoticeDTO notice) {
        notice.setUpdatedAt(LocalDateTime.now());
        noticeDAO.updateNotice(notice);
        // DTO의 ID 필드가 noticeId에서 id로 변경되었으므로, getter도 getId()로 변경
        return noticeDAO.selectNoticeById(notice.getId()); // getNoticeId() -> getId()
    }
    
    public void deleteNotice(int id) { // noticeId -> id로 변경
        noticeDAO.deleteNotice(id); // noticeId -> id로 변경
    }
}