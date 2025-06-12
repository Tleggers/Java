package com.Trekkit_Java.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DTO.QnaAnswerDTO;
import com.Trekkit_Java.DTO.QnaQuestionDTO;
import com.Trekkit_Java.Service.QnaService;

@RestController
@RequestMapping("/api/qna")
public class QnaController {

    @Autowired
    private QnaService qnaService;

    // Q&A 질문 목록 조회
    @GetMapping("/questions")
    public Map<String, Object> getQuestions(
        @RequestParam(value = "sort", defaultValue = "latest") String sort,
        @RequestParam(value = "mountain", required = false) String mountain,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        
    	Map<String, Object> result = qnaService.getQuestions(sort, mountain, page, size);
        return result;
    }

    // Q&A 질문 상세 조회
    @GetMapping("/questions/{id}")
    public ResponseEntity<QnaQuestionDTO> getQuestion(@PathVariable(value = "id") int id) {
        QnaQuestionDTO question = qnaService.getQuestionById(id);
        if (question != null) {
            return ResponseEntity.ok(question);
        }
        return ResponseEntity.notFound().build();
    }

    // Q&A 질문 작성
    @PostMapping("/questions")
    public ResponseEntity<Map<String, Object>> createQuestion(@RequestBody QnaQuestionDTO questionDTO) {
        try {
            int questionId = qnaService.createQuestion(questionDTO);
            return ResponseEntity.ok(Map.of("success", true, "questionId", questionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 특정 질문의 답변 목록 조회
    @GetMapping("/questions/{questionId}/answers")
    public ResponseEntity<List<QnaAnswerDTO>> getAnswers(@PathVariable(value = "questionId") int questionId) {
        List<QnaAnswerDTO> answers = qnaService.getAnswersByQuestionId(questionId);
        return ResponseEntity.ok(answers);
    }
    
    // 답변 작성
    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<Map<String, Object>> createAnswer(
            @PathVariable(value = "questionId") int questionId, 
            @RequestBody QnaAnswerDTO answerDTO) {
        try {
            answerDTO.setQuestionId(questionId);
            int answerId = qnaService.createAnswer(answerDTO);
            return ResponseEntity.ok(Map.of("success", true, "answerId", answerId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 질문 좋아요 토글
    @PostMapping("/questions/{questionId}/like")
    public ResponseEntity<Map<String, Object>> toggleQuestionLike(
            @PathVariable(value = "questionId") int questionId,
            @RequestParam(value = "userId") String userId) {
        try {
            boolean isLiked = qnaService.toggleQuestionLike(questionId, userId);
            return ResponseEntity.ok(Map.of("success", true, "isLiked", isLiked));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 답변 좋아요 토글
    @PostMapping("/answers/{answerId}/like")
    public ResponseEntity<Map<String, Object>> toggleAnswerLike(
            @PathVariable(value = "answerId") int answerId,
            @RequestParam(value = "userId") String userId) {
        try {
            boolean isLiked = qnaService.toggleAnswerLike(answerId, userId);
            return ResponseEntity.ok(Map.of("success", true, "isLiked", isLiked));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
