package com.Trekkit_Java.DAO;

import com.Trekkit_Java.DTO.QnaQuestionDTO; // Q&A 질문 DTO 임포트
import com.Trekkit_Java.DTO.QnaAnswerDTO; // Q&A 답변 DTO 임포트
import com.Trekkit_Java.DTO.QnaLikeDTO; // Q&A 좋아요 DTO 임포트
import org.apache.ibatis.annotations.Mapper; // MyBatis 매퍼 인터페이스임을 나타내는 어노테이션
import org.apache.ibatis.annotations.Param; // MyBatis 쿼리에 여러 파라미터를 전달할 때 사용되는 어노테이션

import java.util.List; // List(컬렉션) 사용을 위해 임포트

/**
 * Q&A(질문 및 답변) 관련 데이터베이스 작업을 위한 MyBatis Mapper 인터페이스입니다.
 * 이 인터페이스의 메서드들은 매퍼 XML 파일의 SQL 쿼리와 매핑됩니다.
 */
@Mapper // 이 인터페이스가 MyBatis의 매퍼임을 선언합니다. Spring이 이 인터페이스를 스캔하여 구현체를 생성합니다.
public interface QnaDAO {

    // --- Q&A 질문 관련 메서드 ---

    /**
     * 모든 Q&A 질문 목록을 조회합니다.
     * @return 질문 목록 (List<QnaQuestionDTO>)
     */
    List<QnaQuestionDTO> getAllQuestions();
    
    /**
     * 질문 ID를 사용하여 특정 Q&A 질문의 상세 정보를 조회합니다.
     * @param id 조회할 질문의 ID
     * @return 조회된 질문 DTO (QnaQuestionDTO)
     */
    QnaQuestionDTO getQuestionById(int id);
    
    /**
     * 새로운 Q&A 질문을 데이터베이스에 삽입합니다.
     * 삽입 후 자동 생성된 ID가 QnaQuestionDTO 객체에 다시 채워질 수 있도록 매퍼 설정이 필요합니다.
     * @param question 삽입할 질문 DTO
     */
    void insertQuestion(QnaQuestionDTO question);
    
    /**
     * 기존 Q&A 질문의 내용을 수정합니다.
     * @param question 수정할 질문 DTO (ID를 포함해야 함)
     */
    void updateQuestion(QnaQuestionDTO question);
    
    /**
     * 특정 Q&A 질문을 데이터베이스에서 삭제합니다.
     * @param id 삭제할 질문의 ID
     */
    void deleteQuestion(int id);
    
    /**
     * 특정 질문의 조회수를 1 증가시킵니다.
     * @param id 조회수를 증가시킬 질문의 ID
     */
    void incrementQuestionViewCount(int id);
    
    /**
     * 특정 질문의 답변 수를 1 증가시킵니다.
     * @param id 답변 수를 증가시킬 질문의 ID
     */
    void incrementQuestionAnswerCount(int id);
    
    /**
     * 특정 질문의 답변 수를 1 감소시킵니다.
     * @param id 답변 수를 감소시킬 질문의 ID
     */
    void decrementQuestionAnswerCount(int id);
    
    /**
     * 특정 질문의 해결 상태(is_solved)와 채택된 답변 ID(accepted_answer_id)를 업데이트합니다.
     * @param questionId 상태를 업데이트할 질문의 ID
     * @param isSolved 질문의 해결 여부 (true/false)
     * @param acceptedAnswerId 채택된 답변의 ID (없으면 null)
     */
    void updateQuestionSolvedStatus(@Param("questionId") int questionId, @Param("isSolved") boolean isSolved, @Param("acceptedAnswerId") Integer acceptedAnswerId);


    // --- Q&A 답변 관련 메서드 ---

    /**
     * 특정 질문에 대한 모든 답변 목록을 조회합니다.
     * @param questionId 답변을 조회할 질문의 ID
     * @return 답변 목록 (List<QnaAnswerDTO>)
     */
    List<QnaAnswerDTO> getAnswersByQuestionId(int questionId);
    
    /**
     * 답변 ID를 사용하여 특정 Q&A 답변의 상세 정보를 조회합니다.
     * @param id 조회할 답변의 ID
     * @return 조회된 답변 DTO (QnaAnswerDTO)
     */
    QnaAnswerDTO getAnswerById(int id);
    
    /**
     * 새로운 Q&A 답변을 데이터베이스에 삽입합니다.
     * 삽입 후 자동 생성된 ID가 QnaAnswerDTO 객체에 다시 채워질 수 있도록 매퍼 설정이 필요합니다.
     * @param answer 삽입할 답변 DTO
     */
    void insertAnswer(QnaAnswerDTO answer);
    
    /**
     * 기존 Q&A 답변의 내용을 수정합니다.
     * @param answer 수정할 답변 DTO (ID를 포함해야 함)
     */
    void updateAnswer(QnaAnswerDTO answer);
    
    /**
     * 특정 Q&A 답변을 데이터베이스에서 삭제합니다.
     * @param id 삭제할 답변의 ID
     */
    void deleteAnswer(int id);
    
    /**
     * 특정 답변의 채택 상태(is_accepted)를 업데이트합니다.
     * @param answerId 상태를 업데이트할 답변의 ID
     * @param isAccepted 답변의 채택 여부 (true/false)
     */
    void updateAnswerAcceptedStatus(@Param("answerId") int answerId, @Param("isAccepted") boolean isAccepted);


    // --- Q&A 질문 좋아요 관련 메서드 ---

    /**
     * Q&A 질문에 대한 좋아요 정보를 삽입합니다.
     * @param like 삽입할 좋아요 DTO (QnaLikeDTO)
     */
    void insertQuestionLike(QnaLikeDTO like);
    
    /**
     * 특정 Q&A 질문에 대한 특정 사용자의 좋아요 정보를 삭제합니다. (좋아요 취소)
     * @param questionId 좋아요를 삭제할 질문의 ID
     * @param userId 좋아요를 삭제할 사용자의 ID
     */
    void deleteQuestionLike(@Param("questionId") int questionId, @Param("userId") int userId);
    
    /**
     * 특정 Q&A 질문의 총 좋아요 개수를 조회합니다.
     * @param questionId 좋아요 개수를 조회할 질문의 ID
     * @return 해당 질문의 총 좋아요 개수
     */
    int getQuestionLikeCount(int questionId);
    
    /**
     * 특정 Q&A 질문에 대한 특정 사용자의 좋아요 상태를 확인합니다.
     * @param questionId 좋아요 상태를 확인할 질문의 ID
     * @param userId 좋아요 상태를 확인할 사용자의 ID
     * @return 좋아요 정보 DTO (QnaLikeDTO, 존재하면 DTO 객체, 없으면 null)
     */
    QnaLikeDTO checkQuestionLikeStatus(@Param("questionId") int questionId, @Param("userId") int userId);
    
    /**
     * 특정 Q&A 질문의 좋아요 수를 1 증가시킵니다.
     * @param questionId 좋아요 수를 증가시킬 질문의 ID
     */
    void incrementQuestionLikeCount(int questionId);
    
    /**
     * 특정 Q&A 질문의 좋아요 수를 1 감소시킵니다.
     * @param questionId 좋아요 수를 감소시킬 질문의 ID
     */
    void decrementQuestionLikeCount(int questionId);

    // --- Q&A 답변 좋아요 관련 메서드 ---

    /**
     * Q&A 답변에 대한 좋아요 정보를 삽입합니다.
     * @param like 삽입할 좋아요 DTO (QnaLikeDTO)
     */
    void insertAnswerLike(QnaLikeDTO like);
    
    /**
     * 특정 Q&A 답변에 대한 특정 사용자의 좋아요 정보를 삭제합니다. (좋아요 취소)
     * @param answerId 좋아요를 삭제할 답변의 ID
     * @param userId 좋아요를 삭제할 사용자의 ID
     */
    void deleteAnswerLike(@Param("answerId") int answerId, @Param("userId") int userId);
    
    /**
     * 특정 Q&A 답변의 총 좋아요 개수를 조회합니다.
     * @param answerId 좋아요 개수를 조회할 답변의 ID
     * @return 해당 답변의 총 좋아요 개수
     */
    int getAnswerLikeCount(int answerId);
    
    /**
     * 특정 Q&A 답변에 대한 특정 사용자의 좋아요 상태를 확인합니다.
     * @param answerId 좋아요 상태를 확인할 답변의 ID
     * @param userId 좋아요 상태를 확인할 사용자의 ID
     * @return 좋아요 정보 DTO (QnaLikeDTO, 존재하면 DTO 객체, 없으면 null)
     */
    QnaLikeDTO checkAnswerLikeStatus(@Param("answerId") int answerId, @Param("userId") int userId);
    
    /**
     * 특정 Q&A 답변의 좋아요 수를 1 증가시킵니다.
     * @param answerId 좋아요 수를 증가시킬 답변의 ID
     */
    void incrementAnswerLikeCount(int answerId);
    
    /**
     * 특정 Q&A 답변의 좋아요 수를 1 감소시킵니다.
     * @param answerId 좋아요 수를 감소시킬 답변의 ID
     */
    void decrementAnswerLikeCount(int answerId);
}