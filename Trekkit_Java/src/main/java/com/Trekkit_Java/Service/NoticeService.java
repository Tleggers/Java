package com.Trekkit_Java.Service;

import com.Trekkit_Java.DAO.NoticeDAO; // NoticeDAO 인터페이스 임포트 (데이터베이스 접근)
import com.Trekkit_Java.DTO.NoticeDTO; // NoticeDTO 데이터 전송 객체 임포트
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입을 위한 어노테이션
import org.springframework.stereotype.Service; // 이 클래스가 서비스 계층의 컴포넌트임을 나타내는 어노테이션
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리를 위한 어노테이션

import java.time.LocalDateTime; // Java 8의 날짜/시간 API (날짜/시간 자동 설정을 위함)
import java.util.List; // List(컬렉션) 사용을 위해 임포트
import java.util.Map; // Map 사용을 위해 임포트
import java.util.HashMap; // HashMap 구현체 사용

/**
 * 공지사항 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * DAO(Data Access Object)를 통해 데이터베이스와 상호작용하며, 트랜잭션 관리를 포함합니다.
 */
@Service // 이 클래스가 Spring 서비스 계층의 컴포넌트임을 나타냅니다.
@Transactional // 이 클래스의 모든 퍼블릭 메서드에 트랜잭션 기능을 적용합니다. (데이터 일관성 보장)
public class NoticeService {

    @Autowired // NoticeDAO의 인스턴스를 자동으로 주입받습니다.
    private NoticeDAO noticeDAO;

    /**
     * 페이징 처리된 공지사항 목록과 전체 개수를 조회합니다.
     * @param offset 조회 시작 위치 (페이지네이션).
     * @param size 한 페이지당 공지사항 개수.
     * @return 공지사항 목록 (List<NoticeDTO>)과 전체 개수(totalCount)를 포함하는 Map.
     */
    public Map<String, Object> getNotices(int offset, int size) {
        List<NoticeDTO> notices = noticeDAO.selectAllNotices(offset, size); // 페이징 처리된 공지사항 목록 조회
        int totalCount = noticeDAO.getTotalNoticeCount(); // 전체 공지사항 개수 조회

        Map<String, Object> response = new HashMap<>(); // 응답 데이터를 담을 Map 생성
        response.put("notices", notices); // 공지사항 목록 추가
        response.put("totalCount", totalCount); // 전체 개수 추가
        return response; // 응답 Map 반환
    }

    /**
     * 전체 공지사항의 개수를 조회합니다.
     * @return 전체 공지사항의 개수.
     */
    public int getNoticeCount() {
        return noticeDAO.getTotalNoticeCount(); // DAO를 통해 전체 공지사항 개수를 조회하여 반환합니다.
    }

    /**
     * 특정 공지사항의 상세 정보를 조회하고, 조회수를 1 증가시킵니다.
     * @param id 조회할 공지사항의 ID.
     * @return 조회된 공지사항 DTO (NoticeDTO).
     */
    @Transactional // 이 메서드 내의 DB 작업(조회수 증가 및 상세 조회)이 하나의 트랜잭션으로 처리되도록 합니다.
    public NoticeDTO getNoticeDetail(int id) {
        noticeDAO.increaseViewCount(id); // 해당 공지사항의 조회수를 1 증가시킵니다.
        NoticeDTO notice = noticeDAO.selectNoticeById(id); // 해당 공지사항의 상세 정보를 조회합니다.
        return notice; // 조회된 공지사항 DTO를 반환합니다.
    }

    /**
     * 새로운 공지사항을 생성합니다.
     * 생성일시와 수정일시를 현재 시간으로 설정하고 데이터베이스에 삽입합니다.
     * @param notice 생성할 공지사항 DTO.
     * @return 생성된 공지사항 DTO (DB에서 생성된 ID 포함).
     */
    @Transactional // 이 메서드 내의 DB 작업(삽입)이 하나의 트랜잭션으로 처리되도록 합니다.
    public NoticeDTO createNotice(NoticeDTO notice) {
        notice.setCreatedAt(LocalDateTime.now()); // 현재 시간으로 생성일시를 설정합니다.
        notice.setUpdatedAt(LocalDateTime.now()); // 현재 시간으로 수정일시를 설정합니다.
        noticeDAO.insertNotice(notice); // 공지사항을 DB에 삽입합니다.
        return noticeDAO.selectNoticeById(notice.getId()); // 삽입된 공지사항의 상세 정보를 조회하여 반환합니다.
    }

    /**
     * 기존 공지사항의 내용을 수정합니다.
     * 수정일시를 현재 시간으로 설정하고 데이터베이스에 업데이트합니다.
     * @param notice 수정할 공지사항 DTO.
     * @return 수정된 공지사항 DTO.
     */
    @Transactional // 이 메서드 내의 DB 작업(업데이트)이 하나의 트랜잭션으로 처리되도록 합니다.
    public NoticeDTO updateNotice(NoticeDTO notice) {
        notice.setUpdatedAt(LocalDateTime.now()); // 현재 시간으로 수정일시를 설정합니다.
        noticeDAO.updateNotice(notice); // 공지사항을 DB에서 업데이트합니다.
        return noticeDAO.selectNoticeById(notice.getId()); // 업데이트된 공지사항의 상세 정보를 조회하여 반환합니다.
    }
    
    /**
     * 특정 공지사항을 삭제합니다.
     * @param id 삭제할 공지사항의 ID.
     */
    @Transactional // 이 메서드 내의 DB 작업(삭제)이 하나의 트랜잭션으로 처리되도록 합니다.
    public void deleteNotice(int id) {
        noticeDAO.deleteNotice(id); // 공지사항을 DB에서 삭제합니다.
    }
}
