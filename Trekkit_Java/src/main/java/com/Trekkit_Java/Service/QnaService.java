package com.Trekkit_Java.Service;

import com.Trekkit_Java.DAO.QnaDAO; // QnaDAO 인터페이스 임포트 (데이터베이스 접근)
import com.Trekkit_Java.DTO.QnaQuestionDTO; // Q&A 질문 DTO 임포트
import com.Trekkit_Java.DTO.QnaAnswerDTO; // Q&A 답변 DTO 임포트
// import com.Trekkit_Java.DTO.QnaImageDTO; // 이미지 DTO는 기능 제거로 인해 임포트 제거
import com.Trekkit_Java.DTO.QnaLikeDTO; // Q&A 좋아요 DTO 임포트
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입을 위한 어노테이션
import org.springframework.stereotype.Service; // 이 클래스가 서비스 계층의 컴포넌트임을 나타내는 어노테이션
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리를 위한 어노테이션

import java.time.LocalDateTime; // Java 8의 날짜/시간 API (날짜/시간 자동 설정을 위함)
import java.util.List; // 리스트(컬렉션) 사용을 위해 임포트

@Service // 이 클래스가 서비스 컴포넌트임을 Spring에 알립니다.
@Transactional // 이 클래스의 모든 퍼블릭 메서드에 트랜잭션 기능을 적용합니다. (데이터 일관성 보장)
public class QnaService {

    @Autowired // QnaDAO의 인스턴스를 자동으로 주입받습니다.
    private QnaDAO qnaDAO;

    // --- Q&A 질문 관련 서비스 메서드 ---

    /**
     * 모든 Q&A 질문 목록을 조회합니다.
     * 데이터 흐름: Controller -> Service -> DAO -> DB
     * @return 질문 목록
     */
    public List<QnaQuestionDTO> getAllQuestions() {
        return qnaDAO.getAllQuestions(); // DAO를 통해 모든 질문을 조회하여 반환합니다.
    }

    /**
     * 특정 Q&A 질문의 상세 정보를 조회하고 조회수를 증가시킵니다.
     * 데이터 흐름: Controller -> Service -> DAO (조회수 증가) -> DAO (상세 조회) -> Service -> Controller
     * @param id 조회할 질문의 ID
     * @return 조회된 질문 DTO
     */
    public QnaQuestionDTO getQuestionById(int id) {
        qnaDAO.incrementQuestionViewCount(id); // DAO를 통해 해당 질문의 조회수를 1 증가시킵니다.
        return qnaDAO.getQuestionById(id); // DAO를 통해 해당 질문의 상세 정보를 조회하여 반환합니다.
    }

    /**
     * 새로운 Q&A 질문을 생성합니다.
     * 데이터 흐름: Controller -> Service -> DTO 시간 설정 -> DAO (삽입)
     * @param question 생성할 질문 DTO
     */
    @Transactional // 이 메서드 내의 DB 작업(삽입)이 하나의 트랜잭션으로 처리되도록 합니다.
    public void createQuestion(QnaQuestionDTO question) {
        question.setCreatedAt(LocalDateTime.now()); // 현재 시간으로 생성일시를 설정합니다.
        question.setUpdatedAt(LocalDateTime.now()); // 현재 시간으로 수정일시를 설정합니다.
        qnaDAO.insertQuestion(question); // DAO를 통해 질문을 DB에 삽입합니다.
        // 이미지 경로 처리 로직은 제거되었습니다.
    }

    /**
     * 기존 Q&A 질문의 내용을 수정합니다.
     * 데이터 흐름: Controller -> Service -> DTO 시간 설정 -> DAO (업데이트)
     * @param question 수정할 질문 DTO
     */
    @Transactional // 이 메서드 내의 DB 작업(업데이트)이 하나의 트랜잭션으로 처리되도록 합니다.
    public void updateQuestion(QnaQuestionDTO question) {
        question.setUpdatedAt(LocalDateTime.now()); // 현재 시간으로 수정일시를 설정합니다.
        qnaDAO.updateQuestion(question); // DAO를 통해 질문을 DB에서 업데이트합니다.
        // 이미지 업데이트 로직은 제거되었습니다.
    }

    /**
     * 특정 Q&A 질문을 삭제합니다.
     * 관련 이미지 삭제 로직은 DB의 `ON DELETE CASCADE` 제약조건에 의존하거나, 필요에 따라 DAO 호출을 명시할 수 있습니다.
     * 데이터 흐름: Controller -> Service -> DAO (삭제)
     * @param id 삭제할 질문의 ID
     */
    @Transactional // 이 메서드 내의 DB 작업(삭제)이 하나의 트랜잭션으로 처리되도록 합니다.
    public void deleteQuestion(int id) {
        // 관련 이미지 삭제 로직 제거 (DB의 FOREIGN KEY ON DELETE CASCADE 설정에 의해 자동으로 처리될 수 있습니다.)
        // qnaDAO.deleteImagesByQuestionId(id); // 이미지 기능 제거로 인해 DAO 호출 제거
        qnaDAO.deleteQuestion(id); // DAO를 통해 질문을 DB에서 삭제합니다.
    }

    // --- Q&A 답변 관련 서비스 메서드 ---

    /**
     * 특정 질문에 대한 모든 답변 목록을 조회합니다.
     * 데이터 흐름: Controller -> Service -> DAO -> DB
     * @param questionId 답변을 조회할 질문의 ID
     * @return 답변 목록
     */
    public List<QnaAnswerDTO> getAnswersByQuestionId(int questionId) {
        return qnaDAO.getAnswersByQuestionId(questionId); // DAO를 통해 특정 질문의 모든 답변을 조회하여 반환합니다.
    }

    /**
     * 특정 Q&A 답변의 상세 정보를 조회합니다.
     * 데이터 흐름: Controller -> Service -> DAO -> DB
     * @param id 조회할 답변의 ID
     * @return 조회된 답변 DTO
     */
    public QnaAnswerDTO getAnswerById(int id) {
        return qnaDAO.getAnswerById(id); // DAO를 통해 특정 답변의 상세 정보를 조회하여 반환합니다.
    }

    /**
     * 새로운 Q&A 답변을 생성합니다. 답변 생성 시 해당 질문의 답변 수를 증가시킵니다.
     * 데이터 흐름: Controller -> Service -> DTO 시간 설정 -> DAO (삽입) -> DAO (질문 답변 수 증가)
     * @param answer 생성할 답변 DTO
     */
    @Transactional // 이 메서드 내의 DB 작업(삽입, 질문 답변 수 증가)이 하나의 트랜잭션으로 처리되도록 합니다.
    public void createAnswer(QnaAnswerDTO answer) {
        answer.setCreatedAt(LocalDateTime.now()); // 현재 시간으로 생성일시를 설정합니다.
        answer.setUpdatedAt(LocalDateTime.now()); // 현재 시간으로 수정일시를 설정합니다.
        qnaDAO.insertAnswer(answer); // DAO를 통해 답변을 DB에 삽입합니다.
        qnaDAO.incrementQuestionAnswerCount(answer.getQuestionId()); // 해당 질문의 답변 수를 1 증가시킵니다.
        // 이미지 경로 처리 로직은 제거되었습니다.
    }

    /**
     * 기존 Q&A 답변의 내용을 수정합니다.
     * 데이터 흐름: Controller -> Service -> DTO 시간 설정 -> DAO (업데이트)
     * @param answer 수정할 답변 DTO
     */
    @Transactional // 이 메서드 내의 DB 작업(업데이트)이 하나의 트랜잭션으로 처리되도록 합니다.
    public void updateAnswer(QnaAnswerDTO answer) {
        answer.setUpdatedAt(LocalDateTime.now()); // 현재 시간으로 수정일시를 설정합니다.
        qnaDAO.updateAnswer(answer); // DAO를 통해 답변을 DB에서 업데이트합니다.
        // 이미지 업데이트 로직은 제거되었습니다.
    }

    /**
     * 특정 Q&A 답변을 삭제합니다. 답변 삭제 시 해당 질문의 답변 수를 감소시킵니다.
     * 데이터 흐름: Controller -> Service -> DAO (삭제) -> DAO (질문 답변 수 감소)
     * @param id 삭제할 답변의 ID
     * @param questionId 답변이 속한 질문의 ID (질문 답변 수 감소를 위해 필요)
     */
    @Transactional // 이 메서드 내의 DB 작업(삭제, 질문 답변 수 감소)이 하나의 트랜잭션으로 처리되도록 합니다.
    public void deleteAnswer(int id, int questionId) {
        // 관련 이미지 삭제 로직 제거 (DB의 FOREIGN KEY ON DELETE CASCADE 설정에 의해 자동으로 처리될 수 있습니다.)
        // qnaDAO.deleteImagesByAnswerId(id); // 이미지 기능 제거로 인해 DAO 호출 제거
        qnaDAO.deleteAnswer(id); // DAO를 통해 답변을 DB에서 삭제합니다.
        qnaDAO.decrementQuestionAnswerCount(questionId); // 해당 질문의 답변 수를 1 감소시킵니다.
    }

    /**
     * 특정 Q&A 질문에 대해 답변을 채택합니다. 질문의 해결 상태를 '해결됨'으로, 채택된 답변 ID를 설정합니다.
     * 데이터 흐름: Controller -> Service -> DAO (질문 상태 업데이트) -> DAO (답변 상태 업데이트)
     * @param questionId 답변이 속한 질문의 ID
     * @param answerId 채택할 답변의 ID
     */
    @Transactional // 이 메서드 내의 DB 작업이 하나의 트랜잭션으로 처리되도록 합니다.
    public void setAcceptedAnswer(int questionId, int answerId) {
        // Q&A 질문의 해결 상태를 true로 설정하고, 채택된 답변 ID를 answerId로 설정합니다.
        qnaDAO.updateQuestionSolvedStatus(questionId, true, answerId);
        // 채택된 답변의 is_accepted 상태를 true로 설정합니다.
        qnaDAO.updateAnswerAcceptedStatus(answerId, true);
    }

    /**
     * 특정 Q&A 질문의 답변 채택을 해제합니다. 질문의 해결 상태를 '미해결'로, 채택된 답변 ID를 null로 설정합니다.
     * 데이터 흐름: Controller -> Service -> DAO (질문 상태 업데이트) -> DAO (답변 상태 업데이트)
     * @param questionId 채택 해제할 답변이 속한 질문의 ID
     * @param answerId 채택 해제할 답변의 ID
     */
    @Transactional // 이 메서드 내의 DB 작업이 하나의 트랜잭션으로 처리되도록 합니다.
    public void unsetAcceptedAnswer(int questionId, int answerId) {
        // Q&A 질문의 해결 상태를 false로 설정하고, 채택된 답변 ID를 null로 설정합니다.
        qnaDAO.updateQuestionSolvedStatus(questionId, false, null);
        // 채택 해제된 답변의 is_accepted 상태를 false로 설정합니다.
        qnaDAO.updateAnswerAcceptedStatus(answerId, false);
    }


    // --- Q&A 질문 좋아요 관련 서비스 메서드 ---

    /**
     * 특정 Q&A 질문에 대한 좋아요 상태를 토글(추가/취소)합니다.
     * 좋아요가 없으면 추가하고, 있으면 삭제합니다.
     * 데이터 흐름: Controller -> Service -> DAO (좋아요 상태 확인) -> DAO (좋아요 삽입/삭제) -> DAO (질문 좋아요 수 증감)
     * @param questionId 좋아요를 토글할 질문의 ID
     * @param userId 좋아요를 요청한 사용자의 ID
     * @return 좋아요가 추가되었으면 true, 취소되었으면 false
     */
    @Transactional // 이 메서드 내의 DB 작업이 하나의 트랜잭션으로 처리되도록 합니다.
    public boolean toggleQuestionLike(int questionId, int userId) {
        // 해당 질문에 대한 사용자의 기존 좋아요 상태를 확인합니다.
        QnaLikeDTO existingLike = qnaDAO.checkQuestionLikeStatus(questionId, userId);
        if (existingLike == null) {
            // 기존 좋아요가 없으면 새로 추가합니다.
            QnaLikeDTO newLike = new QnaLikeDTO();
            newLike.setQuestionId(questionId); // 좋아요 대상 질문 ID 설정
            newLike.setUserId(userId); // 좋아요를 누른 사용자 ID 설정
            newLike.setCreatedAt(LocalDateTime.now()); // 현재 시간 설정
            qnaDAO.insertQuestionLike(newLike); // DB에 좋아요 정보 삽입
            qnaDAO.incrementQuestionLikeCount(questionId); // 해당 질문의 좋아요 수를 1 증가시킵니다.
            return true; // 좋아요가 추가되었음을 반환
        } else {
            // 기존 좋아요가 있으면 삭제(취소)합니다.
            qnaDAO.deleteQuestionLike(questionId, userId); // DB에서 좋아요 정보 삭제
            qnaDAO.decrementQuestionLikeCount(questionId); // 해당 질문의 좋아요 수를 1 감소시킵니다.
            return false; // 좋아요가 취소되었음을 반환
        }
    }

    /**
     * 특정 Q&A 질문의 총 좋아요 개수를 조회합니다.
     * 데이터 흐름: Controller -> Service -> DAO -> DB
     * @param questionId 좋아요 개수를 조회할 질문의 ID
     * @return 해당 질문의 총 좋아요 개수
     */
    public int getQuestionLikeCount(int questionId) {
        return qnaDAO.getQuestionLikeCount(questionId); // DAO를 통해 좋아요 개수를 조회하여 반환합니다.
    }

    /**
     * 특정 Q&A 질문에 대해 특정 사용자가 좋아요를 눌렀는지 여부를 확인합니다.
     * 데이터 흐름: Controller -> Service -> DAO -> DB
     * @param questionId 좋아요 상태를 확인할 질문의 ID
     * @param userId 좋아요 상태를 확인할 사용자의 ID
     * @return 좋아요를 눌렀으면 true, 아니면 false
     */
    public boolean isQuestionLikedByUser(int questionId, int userId) {
        // DAO를 통해 좋아요 정보가 존재하는지 확인합니다. 존재하면 true, null이면 false를 반환합니다.
        return qnaDAO.checkQuestionLikeStatus(questionId, userId) != null;
    }


    // --- Q&A 답변 좋아요 관련 서비스 메서드 ---

    /**
     * 특정 Q&A 답변에 대한 좋아요 상태를 토글(추가/취소)합니다.
     * 좋아요가 없으면 추가하고, 있으면 삭제합니다.
     * 데이터 흐름: Controller -> Service -> DAO (좋아요 상태 확인) -> DAO (좋아요 삽입/삭제) -> DAO (답변 좋아요 수 증감)
     * @param answerId 좋아요를 토글할 답변의 ID
     * @param userId 좋아요를 요청한 사용자의 ID
     * @return 좋아요가 추가되었으면 true, 취소되었으면 false
     */
    @Transactional // 이 메서드 내의 DB 작업이 하나의 트랜잭션으로 처리되도록 합니다.
    public boolean toggleAnswerLike(int answerId, int userId) {
        // 해당 답변에 대한 사용자의 기존 좋아요 상태를 확인합니다.
        QnaLikeDTO existingLike = qnaDAO.checkAnswerLikeStatus(answerId, userId);
        if (existingLike == null) {
            // 기존 좋아요가 없으면 새로 추가합니다.
            QnaLikeDTO newLike = new QnaLikeDTO();
            newLike.setAnswerId(answerId); // 좋아요 대상 답변 ID 설정
            newLike.setUserId(userId); // 좋아요를 누른 사용자 ID 설정
            newLike.setCreatedAt(LocalDateTime.now()); // 현재 시간 설정
            qnaDAO.insertAnswerLike(newLike); // DB에 좋아요 정보 삽입
            qnaDAO.incrementAnswerLikeCount(answerId); // 해당 답변의 좋아요 수를 1 증가시킵니다.
            return true; // 좋아요가 추가되었음을 반환
        } else {
            // 기존 좋아요가 있으면 삭제(취소)합니다.
            qnaDAO.deleteAnswerLike(answerId, userId); // DB에서 좋아요 정보 삭제
            qnaDAO.decrementAnswerLikeCount(answerId); // 해당 답변의 좋아요 수를 1 감소시킵니다.
            return false; // 좋아요가 취소되었음을 반환
        }
    }

    /**
     * 특정 Q&A 답변의 총 좋아요 개수를 조회합니다.
     * 데이터 흐름: Controller -> Service -> DAO -> DB
     * @param answerId 좋아요 개수를 조회할 답변의 ID
     * @return 해당 답변의 총 좋아요 개수
     */
    public int getAnswerLikeCount(int answerId) {
        return qnaDAO.getAnswerLikeCount(answerId); // DAO를 통해 좋아요 개수를 조회하여 반환합니다.
    }

    /**
     * 특정 Q&A 답변에 대해 특정 사용자가 좋아요를 눌렀는지 여부를 확인합니다.
     * 데이터 흐름: Controller -> Service -> DAO -> DB
     * @param answerId 좋아요 상태를 확인할 답변의 ID
     * @param userId 좋아요 상태를 확인할 사용자의 ID
     * @return 좋아요를 눌렀으면 true, 아니면 false
     */
    public boolean isAnswerLikedByUser(int answerId, int userId) {
        // DAO를 통해 좋아요 정보가 존재하는지 확인합니다. 존재하면 true, null이면 false를 반환합니다.
        return qnaDAO.checkAnswerLikeStatus(answerId, userId) != null;
    }
}
