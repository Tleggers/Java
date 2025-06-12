package com.Trekkit_Java.Service;

import com.Trekkit_Java.DAO.QnaDAO;
import com.Trekkit_Java.DTO.QnaQuestionDTO;
import com.Trekkit_Java.DTO.QnaAnswerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QnaService {

    @Autowired
    private QnaDAO qnaDAO;

    // Q&A 질문 목록 조회
    public Map<String, Object> getQuestions(String sort, String mountain, int page, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("sort", sort);
        params.put("mountain", mountain);
        params.put("offset", page * size);
        params.put("size", size);

        List<QnaQuestionDTO> questions = qnaDAO.selectQuestions(params);
        int totalCount = qnaDAO.selectQuestionsCount(params);

        Map<String, Object> result = new HashMap<>();
        result.put("questions", questions);
        result.put("totalCount", totalCount);
        result.put("currentPage", page);
        result.put("totalPages", (int) Math.ceil((double) totalCount / size));

        return result;
    }

    // Q&A 질문 상세 조회
    @Transactional
    public QnaQuestionDTO getQuestionById(int id) {
        // 조회수 증가
        qnaDAO.updateQuestionViewCount(id);
        return qnaDAO.selectQuestionById(id);
    }

    // Q&A 질문 작성
    @Transactional
    public int createQuestion(QnaQuestionDTO questionDTO) {
        qnaDAO.insertQuestion(questionDTO);
        return questionDTO.getId();
    }

    // 특정 질문의 답변 목록 조회
    public List<QnaAnswerDTO> getAnswersByQuestionId(int questionId) {
        return qnaDAO.selectAnswersByQuestionId(questionId);
    }

    // 답변 작성
    @Transactional
    public int createAnswer(QnaAnswerDTO answerDTO) {
        // 답변 삽입
        qnaDAO.insertAnswer(answerDTO);
        
        // 질문의 답변 수 업데이트
        int answerCount = qnaDAO.selectAnswersByQuestionId(answerDTO.getQuestionId()).size();
        qnaDAO.updateQuestionAnswerCount(answerDTO.getQuestionId(), answerCount);
        
        return answerDTO.getId();
    }

    // 질문 좋아요 토글
    @Transactional
    public boolean toggleQuestionLike(int questionId, String userId) {
        int exists = qnaDAO.selectQuestionLikeExists(questionId, userId);
        
        if (exists > 0) {
            // 좋아요 취소
            qnaDAO.deleteQuestionLike(questionId, userId);
            qnaDAO.updateQuestionLikeCount(questionId, false);
            return false;
        } else {
            // 좋아요 추가
            qnaDAO.insertQuestionLike(questionId, userId);
            qnaDAO.updateQuestionLikeCount(questionId, true);
            return true;
        }
    }

    // 답변 좋아요 토글
    @Transactional
    public boolean toggleAnswerLike(int answerId, String userId) {
        int exists = qnaDAO.selectAnswerLikeExists(answerId, userId);
        
        if (exists > 0) {
            // 좋아요 취소
            qnaDAO.deleteAnswerLike(answerId, userId);
            qnaDAO.updateAnswerLikeCount(answerId, false);
            return false;
        } else {
            // 좋아요 추가
            qnaDAO.insertAnswerLike(answerId, userId);
            qnaDAO.updateAnswerLikeCount(answerId, true);
            return true;
        }
    }
}
