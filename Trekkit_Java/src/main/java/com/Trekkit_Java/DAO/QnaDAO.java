package com.Trekkit_Java.DAO;

import com.Trekkit_Java.DTO.QnaQuestionDTO;
import com.Trekkit_Java.DTO.QnaAnswerDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface QnaDAO {
    
    // 질문 관련
    List<QnaQuestionDTO> selectQuestions(Map<String, Object> params);
    int selectQuestionsCount(Map<String, Object> params);
    QnaQuestionDTO selectQuestionById(int id);
    int insertQuestion(QnaQuestionDTO question);
    int updateQuestionViewCount(int id);
    int updateQuestionAnswerCount(@Param("questionId") int questionId, @Param("count") int count);
    
    // 답변 관련
    List<QnaAnswerDTO> selectAnswersByQuestionId(int questionId);
    int insertAnswer(QnaAnswerDTO answer);
    
    // 좋아요 관련
    int selectQuestionLikeExists(@Param("questionId") int questionId, @Param("userId") String userId);
    int insertQuestionLike(@Param("questionId") int questionId, @Param("userId") String userId);
    int deleteQuestionLike(@Param("questionId") int questionId, @Param("userId") String userId);
    int updateQuestionLikeCount(@Param("questionId") int questionId, @Param("increment") boolean increment);
    
    int selectAnswerLikeExists(@Param("answerId") int answerId, @Param("userId") String userId);
    int insertAnswerLike(@Param("answerId") int answerId, @Param("userId") String userId);
    int deleteAnswerLike(@Param("answerId") int answerId, @Param("userId") String userId);
    int updateAnswerLikeCount(@Param("answerId") int answerId, @Param("increment") boolean increment);
}
