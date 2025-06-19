package com.Trekkit_Java.DAO;

import com.Trekkit_Java.DTO.QnaQuestionDTO; // Q&A 질문 DTO 임포트
import com.Trekkit_Java.DTO.QnaAnswerDTO; // Q&A 답변 DTO 임포트
// import com.Trekkit_Java.DTO.QnaImageDTO; // 이미지 DTO는 기능 제거로 인해 임포트 제거
import com.Trekkit_Java.DTO.QnaLikeDTO; // Q&A 좋아요 DTO 임포트
import org.apache.ibatis.annotations.Mapper; // MyBatis 매퍼 인터페이스임을 나타내는 어노테이션
import org.apache.ibatis.annotations.Param; // MyBatis 쿼리에 여러 파라미터를 전달할 때 사용되는 어노테이션

import java.util.List; // 리스트(컬렉션) 사용을 위해 임포트

@Mapper // 이 인터페이스가 MyBatis의 매퍼임을 선언합니다. Spring이 이 인터페이스를 스캔하여 구현체를 생성합니다.
public interface QnaDAO {

    // --- Q&A 질문 관련 메서드 ---

    /**
     * 모든 Q&A 질문 목록을 조회합니다.
     * 데이터 수신: DB에서 조회된 질문 목록(List<QnaQuestionDTO>)을 반환합니다.
     * @return 질문 목록
     */
    List<QnaQuestionDTO> getAllQuestions();
    
    /**
     * 질문 ID를 사용하여 특정 Q&A 질문의 상세 정보를 조회합니다.
     * 데이터 전송: id 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 단일 질문(QnaQuestionDTO)을 반환합니다.
     * @param id 조회할 질문의 ID
     * @return 조회된 질문 DTO
     */
    QnaQuestionDTO getQuestionById(int id);
    
    /**
     * 새로운 Q&A 질문을 데이터베이스에 삽입합니다.
     * 데이터 전송: 질문 정보가 담긴 QnaQuestionDTO 객체를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 삽입). `useGeneratedKeys`와 `keyProperty` 설정을 통해 삽입 후 자동 생성된 id가 QnaQuestionDTO 객체에 다시 채워집니다.
     * @param question 삽입할 질문 DTO
     */
    void insertQuestion(QnaQuestionDTO question);
    
    /**
     * 기존 Q&A 질문의 내용을 수정합니다.
     * 데이터 전송: 수정할 질문 정보가 담긴 QnaQuestionDTO 객체를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param question 수정할 질문 DTO
     */
    void updateQuestion(QnaQuestionDTO question);
    
    /**
     * 특정 Q&A 질문을 데이터베이스에서 삭제합니다.
     * 데이터 전송: id 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 삭제)
     * @param id 삭제할 질문의 ID
     */
    void deleteQuestion(int id);
    
    /**
     * 특정 질문의 조회수를 1 증가시킵니다.
     * 데이터 전송: id 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param id 조회수를 증가시킬 질문의 ID
     */
    void incrementQuestionViewCount(int id);
    
    /**
     * 특정 질문의 답변 수를 1 증가시킵니다.
     * 데이터 전송: id 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param id 답변 수를 증가시킬 질문의 ID
     */
    void incrementQuestionAnswerCount(int id);
    
    /**
     * 특정 질문의 답변 수를 1 감소시킵니다.
     * 데이터 전송: id 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param id 답변 수를 감소시킬 질문의 ID
     */
    void decrementQuestionAnswerCount(int id);
    
    /**
     * 특정 질문의 해결 상태(is_solved)와 채택된 답변 ID(accepted_answer_id)를 업데이트합니다.
     * 데이터 전송: questionId, isSolved, acceptedAnswerId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param questionId 상태를 업데이트할 질문의 ID
     * @param isSolved 질문의 해결 여부 (true/false)
     * @param acceptedAnswerId 채택된 답변의 ID (없으면 null)
     */
    void updateQuestionSolvedStatus(@Param("questionId") int questionId, @Param("isSolved") boolean isSolved, @Param("acceptedAnswerId") Integer acceptedAnswerId);


    // --- Q&A 답변 관련 메서드 ---

    /**
     * 특정 질문에 대한 모든 답변 목록을 조회합니다.
     * 데이터 전송: questionId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 답변 목록(List<QnaAnswerDTO>)을 반환합니다.
     * @param questionId 답변을 조회할 질문의 ID
     * @return 답변 목록
     */
    List<QnaAnswerDTO> getAnswersByQuestionId(int questionId);
    
    /**
     * 답변 ID를 사용하여 특정 Q&A 답변의 상세 정보를 조회합니다.
     * 데이터 전송: id 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 단일 답변(QnaAnswerDTO)을 반환합니다.
     * @param id 조회할 답변의 ID
     * @return 조회된 답변 DTO
     */
    QnaAnswerDTO getAnswerById(int id);
    
    /**
     * 새로운 Q&A 답변을 데이터베이스에 삽입합니다.
     * 데이터 전송: 답변 정보가 담긴 QnaAnswerDTO 객체를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 삽입). `useGeneratedKeys`와 `keyProperty` 설정을 통해 삽입 후 자동 생성된 id가 QnaAnswerDTO 객체에 다시 채워집니다.
     * @param answer 삽입할 답변 DTO
     */
    void insertAnswer(QnaAnswerDTO answer);
    
    /**
     * 기존 Q&A 답변의 내용을 수정합니다.
     * 데이터 전송: 수정할 답변 정보가 담긴 QnaAnswerDTO 객체를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param answer 수정할 답변 DTO
     */
    void updateAnswer(QnaAnswerDTO answer);
    
    /**
     * 특정 Q&A 답변을 데이터베이스에서 삭제합니다.
     * 데이터 전송: id 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 삭제)
     * @param id 삭제할 답변의 ID
     */
    void deleteAnswer(int id);
    
    /**
     * 특정 답변의 채택 상태(is_accepted)를 업데이트합니다.
     * 데이터 전송: answerId, isAccepted 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param answerId 상태를 업데이트할 답변의 ID
     * @param isAccepted 답변의 채택 여부 (true/false)
     */
    void updateAnswerAcceptedStatus(@Param("answerId") int answerId, @Param("isAccepted") boolean isAccepted);

    // --- Q&A 이미지 관련 메서드 (기능 제거로 인해 주석 처리됨) ---
    // void insertQnaImage(QnaImageDTO image);
    // List<QnaImageDTO> getImagesByQuestionId(int questionId);
    // List<QnaImageDTO> getImagesByAnswerId(int answerId);
    // void deleteImageById(int id);
    // void deleteImagesByQuestionId(int questionId);
    // void deleteImagesByAnswerId(int answerId);


    // --- Q&A 질문 좋아요 관련 메서드 ---

    /**
     * Q&A 질문에 대한 좋아요 정보를 삽입합니다.
     * 데이터 전송: 좋아요 정보가 담긴 QnaLikeDTO 객체를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 삽입)
     * @param like 삽입할 좋아요 DTO
     */
    void insertQuestionLike(QnaLikeDTO like);
    
    /**
     * 특정 Q&A 질문에 대한 특정 사용자의 좋아요 정보를 삭제합니다. (좋아요 취소)
     * 데이터 전송: questionId, userId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 삭제)
     * @param questionId 좋아요를 삭제할 질문의 ID
     * @param userId 좋아요를 삭제할 사용자의 ID
     */
    void deleteQuestionLike(@Param("questionId") int questionId, @Param("userId") int userId);
    
    /**
     * 특정 Q&A 질문의 총 좋아요 개수를 조회합니다.
     * 데이터 전송: questionId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 좋아요 개수(int)를 반환합니다.
     * @param questionId 좋아요 개수를 조회할 질문의 ID
     * @return 해당 질문의 총 좋아요 개수
     */
    int getQuestionLikeCount(int questionId);
    
    /**
     * 특정 Q&A 질문에 대한 특정 사용자의 좋아요 상태를 확인합니다.
     * 데이터 전송: questionId, userId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 좋아요 정보(QnaLikeDTO)를 반환합니다. (좋아요가 있으면 객체, 없으면 null)
     * @param questionId 좋아요 상태를 확인할 질문의 ID
     * @param userId 좋아요 상태를 확인할 사용자의 ID
     * @return 좋아요 정보 DTO (존재하면 DTO 객체, 없으면 null)
     */
    QnaLikeDTO checkQuestionLikeStatus(@Param("questionId") int questionId, @Param("userId") int userId);
    
    /**
     * 특정 Q&A 질문의 좋아요 수를 1 증가시킵니다.
     * 데이터 전송: questionId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param questionId 좋아요 수를 증가시킬 질문의 ID
     */
    void incrementQuestionLikeCount(int questionId);
    
    /**
     * 특정 Q&A 질문의 좋아요 수를 1 감소시킵니다.
     * 데이터 전송: questionId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param questionId 좋아요 수를 감소시킬 질문의 ID
     */
    void decrementQuestionLikeCount(int questionId);

    // --- Q&A 답변 좋아요 관련 메서드 ---

    /**
     * Q&A 답변에 대한 좋아요 정보를 삽입합니다.
     * 데이터 전송: 좋아요 정보가 담긴 QnaLikeDTO 객체를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 삽입)
     * @param like 삽입할 좋아요 DTO
     */
    void insertAnswerLike(QnaLikeDTO like);
    
    /**
     * 특정 Q&A 답변에 대한 특정 사용자의 좋아요 정보를 삭제합니다. (좋아요 취소)
     * 데이터 전송: answerId, userId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 삭제)
     * @param answerId 좋아요를 삭제할 답변의 ID
     * @param userId 좋아요를 삭제할 사용자의 ID
     */
    void deleteAnswerLike(@Param("answerId") int answerId, @Param("userId") int userId);
    
    /**
     * 특정 Q&A 답변의 총 좋아요 개수를 조회합니다.
     * 데이터 전송: answerId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 좋아요 개수(int)를 반환합니다.
     * @param answerId 좋아요 개수를 조회할 답변의 ID
     * @return 해당 답변의 총 좋아요 개수
     */
    int getAnswerLikeCount(int answerId);
    
    /**
     * 특정 Q&A 답변에 대한 특정 사용자의 좋아요 상태를 확인합니다.
     * 데이터 전송: answerId, userId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: DB에서 조회된 좋아요 정보(QnaLikeDTO)를 반환합니다. (좋아요가 있으면 객체, 없으면 null)
     * @param answerId 좋아요 상태를 확인할 답변의 ID
     * @param userId 좋아요 상태를 확인할 사용자의 ID
     * @return 좋아요 정보 DTO (존재하면 DTO 객체, 없으면 null)
     */
    QnaLikeDTO checkAnswerLikeStatus(@Param("answerId") int answerId, @Param("userId") int userId);
    
    /**
     * 특정 Q&A 답변의 좋아요 수를 1 증가시킵니다.
     * 데이터 전송: answerId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param answerId 좋아요 수를 증가시킬 답변의 ID
     */
    void incrementAnswerLikeCount(int answerId);
    
    /**
     * 특정 Q&A 답변의 좋아요 수를 1 감소시킵니다.
     * 데이터 전송: answerId 파라미터를 매퍼 XML로 전송합니다.
     * 데이터 수신: 없음 (DB 업데이트)
     * @param answerId 좋아요 수를 감소시킬 답변의 ID
     */
    void decrementAnswerLikeCount(int answerId);
}
