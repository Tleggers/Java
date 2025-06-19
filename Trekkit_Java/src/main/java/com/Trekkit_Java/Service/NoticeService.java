package com.Trekkit_Java.Service;

import com.Trekkit_Java.DAO.NoticeDAO; // NoticeDAO 인터페이스 임포트 (데이터베이스 접근)
import com.Trekkit_Java.DTO.NoticeDTO; // 공지사항 데이터 전송 객체 (DTO) 임포트
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입을 위한 어노테이션
import org.springframework.stereotype.Service; // 이 클래스가 서비스 계층의 컴포넌트임을 나타내는 어노테이션
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리를 위한 어노테이션

import java.time.LocalDateTime; // Java 8의 날짜/시간 API (Timestamp 대신 사용)
import java.util.List; // 리스트(컬렉션) 사용을 위해 임포트
import java.util.Map; // 맵(키-값 쌍) 사용을 위해 임포트
import java.util.HashMap; // Map 구현체 중 하나인 HashMap 임포트 (응답 데이터를 Map으로 구성할 때 사용)

@Service // 이 클래스가 서비스 컴포넌트임을 Spring에 알립니다.
@Transactional // 이 클래스의 모든 퍼블릭 메서드에 트랜잭션 기능을 적용합니다. (데이터 일관성 보장)
public class NoticeService {

    @Autowired // NoticeDAO의 인스턴스를 자동으로 주입받습니다.
    private NoticeDAO noticeDAO;

    /**
     * 공지사항 목록을 페이지네이션하여 조회합니다. (프론트엔드에서 카테고리 필터링이 제거됨)
     *
     * 데이터 흐름:
     * 1. Controller -> Service.getNotices() 호출 (offset, size 전달)
     * 2. Service -> NoticeDAO.selectAllNotices() 호출하여 페이지네이션된 공지사항 목록 조회
     * 3. Service -> NoticeDAO.getTotalNoticeCount() 호출하여 전체 공지사항 개수 조회
     * 4. Service -> 조회된 목록과 개수를 Map<String, Object> 형태로 묶어 Controller로 반환
     *
     * @param offset 조회 시작 위치
     * @param size 한 페이지당 공지사항 개수
     * @return 공지사항 목록(List<NoticeDTO>)과 총 개수(int)를 포함하는 Map
     */
    public Map<String, Object> getNotices(int offset, int size) {
        List<NoticeDTO> notices = noticeDAO.selectAllNotices(offset, size); // DAO를 통해 DB에서 공지사항 목록을 조회합니다.
        int totalCount = noticeDAO.getTotalNoticeCount(); // DAO를 통해 전체 공지사항의 총 개수를 조회합니다.

        // 응답 데이터를 Map 형태로 구성하여 프론트엔드에 전달합니다.
        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices); // "notices" 키에 공지사항 목록을 넣습니다.
        response.put("totalCount", totalCount); // "totalCount" 키에 총 개수를 넣습니다.
        return response;
    }

    /**
     * 전체 공지사항의 총 개수를 조회합니다. (페이징 처리를 위해 사용)
     * 이 메서드는 현재 `getNotices` 내부에서 `getTotalNoticeCount()`를 호출하도록 통일되었습니다.
     *
     * 데이터 흐름:
     * 1. Controller -> Service.getNoticeCount() 호출
     * 2. Service -> NoticeDAO.getTotalNoticeCount() 호출하여 전체 공지사항 개수 조회
     * 3. Service -> 조회된 개수를 Controller로 반환
     *
     * @return 전체 공지사항의 총 개수
     */
    public int getNoticeCount() {
        return noticeDAO.getTotalNoticeCount(); // DAO를 통해 전체 공지사항 개수를 조회하여 반환합니다.
    }

    /**
     * 특정 공지사항의 상세 정보를 조회하고, 조회수를 증가시킵니다.
     *
     * 데이터 흐름:
     * 1. Controller -> Service.getNoticeDetail() 호출 (noticeId 전달)
     * 2. Service -> NoticeDAO.increaseViewCount() 호출하여 조회수 증가 (DB 업데이트)
     * 3. Service -> NoticeDAO.selectNoticeById() 호출하여 상세 공지사항 정보 조회
     * 4. Service -> 조회된 NoticeDTO 객체를 Controller로 반환
     *
     * @param noticeId 조회할 공지사항의 ID
     * @return 조회된 NoticeDTO 객체 (없으면 null)
     */
    @Transactional // 이 메서드 내의 DB 작업(조회수 증가, 상세 조회)이 하나의 트랜잭션으로 처리되도록 합니다.
    public NoticeDTO getNoticeDetail(int noticeId) {
        noticeDAO.increaseViewCount(noticeId); // DAO를 통해 해당 공지사항의 조회수를 1 증가시킵니다.
        NoticeDTO notice = noticeDAO.selectNoticeById(noticeId); // DAO를 통해 해당 ID의 공지사항 상세 정보를 조회합니다.
        // DTO의 닉네임 필드는 매퍼(Mapper XML)에서 JOIN을 통해 이미 채워져 있으므로, 서비스에서 별도로 처리할 필요가 없습니다.
        return notice; // 조회된 공지사항 객체를 반환합니다.
    }

    /**
     * 새로운 공지사항을 생성합니다.
     *
     * 데이터 흐름:
     * 1. Controller -> Service.createNotice() 호출 (NoticeDTO 전달)
     * 2. Service -> NoticeDTO에 생성/수정 시간 설정
     * 3. Service -> NoticeDAO.insertNotice() 호출하여 DB에 공지사항 삽입
     * 4. Service -> 삽입된 공지사항의 ID를 사용하여 다시 상세 조회 후 Controller로 반환 (자동 생성된 ID 및 시간 정보 포함)
     *
     * @param notice 생성할 공지사항 데이터
     * @return DB에 성공적으로 저장된 후의 완전한 NoticeDTO 객체
     */
    @Transactional // 이 메서드 내의 DB 작업(삽입)이 하나의 트랜잭션으로 처리되도록 합니다.
    public NoticeDTO createNotice(NoticeDTO notice) {
        notice.setCreatedAt(LocalDateTime.now()); // 현재 시간으로 생성일시를 설정합니다.
        notice.setUpdatedAt(LocalDateTime.now()); // 현재 시간으로 수정일시를 설정합니다.
        noticeDAO.insertNotice(notice); // DAO를 통해 공지사항을 DB에 삽입합니다. (이때 noticeId가 DTO에 채워집니다)
        // 삽입 후 자동 생성된 noticeId를 사용하여 DB에서 최신 정보를 다시 조회하여 반환합니다.
        // 이렇게 하면 DB에서 자동 설정된 created_at, updated_at 등의 값도 DTO에 반영됩니다.
        return noticeDAO.selectNoticeById(notice.getNoticeId());
    }

    /**
     * 기존 공지사항을 수정합니다.
     *
     * 데이터 흐름:
     * 1. Controller -> Service.updateNotice() 호출 (NoticeDTO 전달)
     * 2. Service -> NoticeDTO에 수정 시간 설정
     * 3. Service -> NoticeDAO.updateNotice() 호출하여 DB에 공지사항 업데이트
     * 4. Service -> 업데이트된 공지사항의 ID를 사용하여 다시 상세 조회 후 Controller로 반환
     *
     * @param notice 수정할 공지사항 데이터
     * @return DB에서 수정이 완료된 후의 최신 NoticeDTO 객체
     */
    @Transactional // 이 메서드 내의 DB 작업(업데이트)이 하나의 트랜잭션으로 처리되도록 합니다.
    public NoticeDTO updateNotice(NoticeDTO notice) {
        notice.setUpdatedAt(LocalDateTime.now()); // 현재 시간으로 수정일시를 설정합니다.
        noticeDAO.updateNotice(notice); // DAO를 통해 공지사항을 DB에서 업데이트합니다.
        // 업데이트 후 DB에서 최신 정보를 다시 조회하여 반환합니다.
        return noticeDAO.selectNoticeById(notice.getNoticeId());
    }
    
    /**
     * 특정 공지사항을 삭제합니다.
     *
     * 데이터 흐름:
     * 1. Controller -> Service.deleteNotice() 호출 (noticeId 전달)
     * 2. Service -> NoticeDAO.deleteNotice() 호출하여 DB에서 공지사항 삭제
     * 3. Service -> Controller로 반환
     *
     * @param noticeId 삭제할 공지사항의 ID
     */
    public void deleteNotice(int noticeId) {
        noticeDAO.deleteNotice(noticeId); // DAO를 통해 해당 공지사항을 DB에서 삭제합니다.
    }
}
