package com.Trekkit_Java.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DTO.QnaAnswerDTO;
import com.Trekkit_Java.DTO.QnaQuestionDTO;
import com.Trekkit_Java.Service.QnaService;
import com.Trekkit_Java.Util.ExtractToken;
import com.Trekkit_Java.Util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/qna")
public class QnaController {

    @Autowired
    private QnaService qnaService;

    @Autowired
    private JwtUtil jwtUtil; 

    private int getUserIdFromRequest(HttpServletRequest request) {
        String token = ExtractToken.extractToken(request);
        if (token == null) {
            throw new IllegalArgumentException("JWT 토큰이 없습니다. 로그인 상태를 확인해주세요.");
        }
        return jwtUtil.extractUserId(token).intValue();
    }

    @PostMapping("/questions-list")
    public ResponseEntity<List<QnaQuestionDTO>> getAllQuestionsPost(@RequestBody(required = false) Map<String, Object> reqBody) {
        List<QnaQuestionDTO> questions = qnaService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }
    
    @PostMapping("/questions-detail/{id}")
    public ResponseEntity<QnaQuestionDTO> getQuestionByIdPost(@PathVariable("id") int id, @RequestBody(required = false) Map<String, Object> reqBody) {
        QnaQuestionDTO question = qnaService.getQuestionById(id);
        if (question != null) {
            return ResponseEntity.ok(question);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/questions")
    public ResponseEntity<String> createQuestion(@RequestBody QnaQuestionDTO questionDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            questionDTO.setUserId(userId);
            qnaService.createQuestion(questionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("질문이 성공적으로 생성되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문 생성 실패: " + e.getMessage());
        }
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<String> updateQuestion(@PathVariable("id") int id, @RequestBody QnaQuestionDTO questionDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(id);
            if (existingQuestion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다.");
            }
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("질문을 수정할 권한이 없습니다.");
            }

            questionDTO.setId(id);
            questionDTO.setUserId(userId);
            // 닉네임은 DB에서 조회 시 채워지므로, 수정 시 DTO에 굳이 다시 설정할 필요는 없습니다.
            // questionDTO.setNickname(existingQuestion.getNickname()); // 이 줄은 제거하거나 주석 처리합니다.

            qnaService.updateQuestion(questionDTO);
            return ResponseEntity.ok("질문이 성공적으로 업데이트되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문 업데이트 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(id);
            if (existingQuestion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다.");
            }
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("질문을 삭제할 권한이 없습니다.");
            }

            qnaService.deleteQuestion(id);
            return ResponseEntity.ok("질문이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문 삭제 실패: " + e.getMessage());
        }
    }

    @PutMapping("/questions/{questionId}/accept-answer/{answerId}")
    public ResponseEntity<String> setAcceptedAnswer(@PathVariable("questionId") int questionId, @PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(questionId);
            if (existingQuestion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다.");
            }
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변을 채택할 권한이 없습니다. 질문 작성자만 채택할 수 있습니다.");
            }
            QnaAnswerDTO answerToAccept = qnaService.getAnswerById(answerId);
            if (answerToAccept == null || answerToAccept.getQuestionId() != questionId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않거나 해당 질문에 속하지 않는 답변입니다.");
            }

            qnaService.setAcceptedAnswer(questionId, answerId);
            return ResponseEntity.ok("답변이 성공적으로 채택되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 채택 실패: " + e.getMessage());
        }
    }

    @PutMapping("/questions/{questionId}/unset-accepted-answer/{answerId}")
    public ResponseEntity<String> unsetAcceptedAnswer(@PathVariable("questionId") int questionId, @PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(questionId);
            if (existingQuestion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다.");
            }
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변 채택을 해제할 권한이 없습니다. 질문 작성자만 해제할 수 있습니다.");
            }
            QnaAnswerDTO answerToUnset = qnaService.getAnswerById(answerId);
            if (answerToUnset == null || answerToUnset.getQuestionId() != questionId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않거나 해당 질문에 속하지 않는 답변입니다.");
            }

            qnaService.unsetAcceptedAnswer(questionId, answerId);
            return ResponseEntity.ok("답변 채택이 성공적으로 해제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 채택 해제 실패: " + e.getMessage());
        }
    }
    
    @PostMapping("/questions-answers/{questionId}")
    public ResponseEntity<List<QnaAnswerDTO>> getAnswersByQuestionIdPost(@PathVariable("questionId") int questionId, @RequestBody(required = false) Map<String, Object> reqBody) {
        List<QnaAnswerDTO> answers = qnaService.getAnswersByQuestionId(questionId);
        return ResponseEntity.ok(answers);
    }
    
    @GetMapping("/answers/{id}")
    public ResponseEntity<QnaAnswerDTO> getAnswerById(@PathVariable("id") int id) {
        QnaAnswerDTO answer = qnaService.getAnswerById(id);
        if (answer != null) {
            return ResponseEntity.ok(answer);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/answers")
    public ResponseEntity<String> createAnswer(@RequestBody QnaAnswerDTO answerDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            answerDTO.setUserId(userId);
            // QnaAnswerDTO에 nickname 필드가 없으므로, 이 부분을 제거하거나 주석 처리해야 합니다.
            // 백엔드에서 닉네임을 DTO에 설정할 필요가 없습니다. (조회 시에만 가져올 것임)
            // answerDTO.setNickname(어딘가에서 가져온 닉네임); // 이 줄은 제거합니다.

            qnaService.createAnswer(answerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("답변이 성공적으로 생성되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 생성 실패: " + e.getMessage());
        }
    }

    @PutMapping("/answers/{id}")
    public ResponseEntity<String> updateAnswer(@PathVariable("id") int id, @RequestBody QnaAnswerDTO answerDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            QnaAnswerDTO existingAnswer = qnaService.getAnswerById(id);
            if (existingAnswer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("답변을 찾을 수 없습니다.");
            }
            if (existingAnswer.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변을 수정할 권한이 없습니다.");
            }

            answerDTO.setId(id);
            answerDTO.setUserId(userId);
            // QnaAnswerDTO에 nickname 필드가 없으므로, 이 줄을 제거하거나 주석 처리해야 합니다.
            // answerDTO.setNickname(existingAnswer.getNickname()); // <-- 이 줄을 제거합니다.

            qnaService.updateAnswer(answerDTO);
            return ResponseEntity.ok("답변이 성공적으로 업데이트되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 업데이트 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/answers/{id}/question/{questionId}")
    public ResponseEntity<String> deleteAnswer(@PathVariable("id") int id, @PathVariable("questionId") int questionId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            QnaAnswerDTO existingAnswer = qnaService.getAnswerById(id);
            if (existingAnswer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("답변을 찾을 수 없습니다.");
            }
            if (existingAnswer.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변을 삭제할 권한이 없습니다.");
            }

            qnaService.deleteAnswer(id, questionId);
            return ResponseEntity.ok("답변이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 삭제 실패: " + e.getMessage());
        }
    }

    // --- 좋아요 관련 API ---

    @PostMapping("/questions/{questionId}/like")
    public ResponseEntity<Boolean> toggleQuestionLike(@PathVariable("questionId") int questionId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            boolean liked = qnaService.toggleQuestionLike(questionId, userId);
            return ResponseEntity.ok(liked);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @GetMapping("/questions/{questionId}/like-count")
    public ResponseEntity<Integer> getQuestionLikeCount(@PathVariable("questionId") int questionId) {
        int likeCount = qnaService.getQuestionLikeCount(questionId);
        return ResponseEntity.ok(likeCount);
    }

    @GetMapping("/questions/{questionId}/like-status")
    public ResponseEntity<Boolean> isQuestionLikedByUser(@PathVariable("questionId") int questionId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            boolean isLiked = qnaService.isQuestionLikedByUser(questionId, userId);
            return ResponseEntity.ok(isLiked);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/answers/{answerId}/like")
    public ResponseEntity<Boolean> toggleAnswerLike(@PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            boolean liked = qnaService.toggleAnswerLike(answerId, userId);
            return ResponseEntity.ok(liked);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @GetMapping("/answers/{answerId}/like-count")
    public ResponseEntity<Integer> getAnswerLikeCount(@PathVariable("answerId") int answerId) {
        int likeCount = qnaService.getAnswerLikeCount(answerId);
        return ResponseEntity.ok(likeCount);
    }

    @GetMapping("/answers/{answerId}/like-status")
    public ResponseEntity<Boolean> isAnswerLikedByUser(@PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            boolean isLiked = qnaService.isAnswerLikedByUser(answerId, userId);
            return ResponseEntity.ok(isLiked);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}