package com.Trekkit_Java.Controller;

import java.util.List; // List 인터페이스 사용을 위한 임포트
import java.util.Map; // Map 인터페이스 사용을 위한 임포트

import org.springframework.beans.factory.annotation.Autowired; // Spring의 의존성 자동 주입 어노테이션
import org.springframework.http.HttpStatus; // HTTP 상태 코드 사용을 위한 임포트
import org.springframework.http.ResponseEntity; // HTTP 응답을 위한 클래스 임포트
import org.springframework.web.bind.annotation.DeleteMapping; // DELETE HTTP 메서드 매핑 어노테이션
import org.springframework.web.bind.annotation.GetMapping; // GET HTTP 메서드 매핑 어노테이션
import org.springframework.web.bind.annotation.PathVariable; // URL 경로 변수 추출 어노테이션
import org.springframework.web.bind.annotation.PostMapping; // POST HTTP 메서드 매핑 어노테이션
import org.springframework.web.bind.annotation.PutMapping; // PUT HTTP 메서드 매핑 어노테이션
import org.springframework.web.bind.annotation.RequestBody; // 요청 본문 매핑 어노테이션
import org.springframework.web.bind.annotation.RequestMapping; // 요청 매핑 어노테이션
import org.springframework.web.bind.annotation.RestController; // REST 컨트롤러 어노테이션

import com.Trekkit_Java.DTO.QnaAnswerDTO; // Q&A 답변 데이터 전송 객체(DTO) 임포트
import com.Trekkit_Java.DTO.QnaQuestionDTO; // Q&A 질문 데이터 전송 객체(DTO) 임포트
import com.Trekkit_Java.Service.QnaService; // Q&A 관련 비즈니스 로직 서비스 임포트
import com.Trekkit_Java.Util.ExtractToken; // JWT 토큰 추출 유틸리티 임포트
import com.Trekkit_Java.Util.JwtUtil; // JWT 유틸리티 (토큰 파싱 등) 임포트

import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 정보 접근을 위한 임포트

/**
 * Q&A(질문과 답변) 관련 API 요청을 처리하는 Spring REST 컨트롤러입니다.
 * 모든 요청은 "/api/qna" 경로로 매핑됩니다.
 * JWT 토큰을 통해 사용자 인증 및 권한을 확인합니다.
 */
@RestController // 이 클래스가 RESTful 웹 서비스의 컨트롤러임을 나타냅니다.
@RequestMapping("/api/qna") // 이 컨트롤러의 모든 핸들러 메서드에 대한 기본 경로를 설정합니다.
public class QnaController {

    @Autowired // QnaService를 자동으로 주입받습니다.
    private QnaService qnaService;

    @Autowired // JwtUtil을 자동으로 주입받습니다.
    private JwtUtil jwtUtil;

    /**
     * HTTP 요청(HttpServletRequest)에서 JWT 토큰을 추출하고 파싱하여 사용자 ID(int 타입)를 반환합니다.
     *
     * @param request 현재 HTTP 요청 객체.
     * @return 파싱된 사용자 ID (int 타입). 토큰이 없거나 유효하지 않으면 예외를 던집니다.
     * @throws IllegalArgumentException JWT 토큰이 없거나 유효하지 않을 경우 발생.
     */
    private int getUserIdFromRequest(HttpServletRequest request) {
        String token = ExtractToken.extractToken(request); // 요청에서 JWT 토큰을 추출합니다.
        if (token == null) {
            // 토큰이 없으면 로그인 필요 예외를 던집니다.
            throw new IllegalArgumentException("JWT 토큰이 없습니다. 로그인 상태를 확인해주세요.");
        }
        // JWT 토큰에서 사용자 ID를 추출하여 int 타입으로 반환합니다.
        return jwtUtil.extractUserId(token).intValue();
    }

    /**
     * 모든 Q&A 질문 목록을 조회합니다.
     * 이 API는 프론트엔드 호환성을 위해 POST 메서드를 사용하며, 요청 본문은 선택 사항입니다.
     *
     * @param reqBody (선택 사항) 요청 본문. 현재 사용되지 않지만 프론트엔드 요청 형식에 맞춤.
     * @return Q&A 질문 목록(QnaQuestionDTO 리스트)과 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("/questions-list") // POST 요청을 "/api/qna/questions-list" 경로로 매핑합니다.
    public ResponseEntity<List<QnaQuestionDTO>> getAllQuestionsPost(@RequestBody(required = false) Map<String, Object> reqBody) {
        // QnaService를 통해 모든 Q&A 질문 목록을 가져옵니다.
        List<QnaQuestionDTO> questions = qnaService.getAllQuestions();
        return ResponseEntity.ok(questions); // HTTP 200 OK 상태 코드와 함께 질문 목록을 반환합니다.
    }
    
    /**
     * 특정 ID를 가진 Q&A 질문의 상세 정보를 조회합니다.
     * 이 API는 프론트엔드 호환성을 위해 POST 메서드를 사용하며, 요청 본문은 선택 사항입니다.
     *
     * @param id 조회할 질문의 고유 ID (경로 변수).
     * @param reqBody (선택 사항) 요청 본문.
     * @return 조회된 질문 정보(QnaQuestionDTO) 또는 HTTP 404 Not Found 응답을 반환합니다.
     */
    @PostMapping("/questions-detail/{id}") // POST 요청을 "/api/qna/questions-detail/{id}" 경로로 매핑합니다.
    public ResponseEntity<QnaQuestionDTO> getQuestionByIdPost(@PathVariable("id") int id, @RequestBody(required = false) Map<String, Object> reqBody) {
        QnaQuestionDTO question = qnaService.getQuestionById(id); // QnaService를 통해 질문 상세 정보를 가져옵니다.
        if (question != null) {
            // 질문을 찾으면 HTTP 200 OK 상태 코드와 함께 질문 정보를 반환합니다.
            return ResponseEntity.ok(question);
        }
        // 질문을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
        return ResponseEntity.notFound().build();
    }

    /**
     * 새로운 Q&A 질문을 생성합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 질문 작성자를 확인하고 DTO에 설정합니다.
     *
     * @param questionDTO 생성할 질문 정보를 담고 있는 QnaQuestionDTO 객체 (요청 본문).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 질문 생성 성공 메시지와 함께 HTTP 201 Created 응답을 반환합니다.
     */
    @PostMapping("/questions") // POST 요청을 "/api/qna/questions" 경로로 매핑합니다.
    public ResponseEntity<String> createQuestion(@RequestBody QnaQuestionDTO questionDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            questionDTO.setUserId(userId); // DTO에 사용자 ID를 설정합니다.
            
            // QnaQuestionDTO에 nickname 필드가 프론트엔드로부터 넘어올 수 있으나,
            // 백엔드에서 userId를 통해 사용자 정보를 조회하여 닉네임을 설정하는 것이 일반적이므로
            // 여기서는 DTO에 nickname을 명시적으로 설정하지 않아도 됩니다. (선택적)

            qnaService.createQuestion(questionDTO); // QnaService를 통해 질문을 생성합니다.
            return ResponseEntity.status(HttpStatus.CREATED).body("질문이 성공적으로 생성되었습니다."); // 성공 응답
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // 질문 생성 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 기존 Q&A 질문을 수정합니다.
     * JWT 토큰에서 사용자 ID를 추출하고, 해당 사용자가 질문 작성자인지 확인하여 권한을 검사합니다.
     *
     * @param id 수정할 질문의 고유 ID (경로 변수).
     * @param questionDTO 수정된 질문 정보를 담고 있는 QnaQuestionDTO 객체 (요청 본문).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 질문 업데이트 성공 메시지와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PutMapping("/questions/{id}") // PUT 요청을 "/api/qna/questions/{id}" 경로로 매핑합니다.
    public ResponseEntity<String> updateQuestion(@PathVariable("id") int id, @RequestBody QnaQuestionDTO questionDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(id); // 기존 질문 정보를 가져옵니다.
            if (existingQuestion == null) {
                // 질문을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다.");
            }
            // 현재 로그인된 사용자가 질문 작성자가 아니면 권한 없음 응답을 반환합니다.
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("질문을 수정할 권한이 없습니다.");
            }

            questionDTO.setId(id); // 경로 변수에서 받은 ID를 DTO에 설정합니다.
            questionDTO.setUserId(userId); // DTO에 사용자 ID를 설정합니다. (작성자 유지)
            
            // DTO에 nickname 필드가 없으므로, 이 줄은 제거하거나 주석 처리합니다.
            // questionDTO.setNickname(existingQuestion.getNickname()); 

            qnaService.updateQuestion(questionDTO); // QnaService를 통해 질문을 업데이트합니다.
            return ResponseEntity.ok("질문이 성공적으로 업데이트되었습니다."); // 성공 응답
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // 질문 업데이트 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문 업데이트 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 Q&A 질문을 삭제합니다.
     * JWT 토큰에서 사용자 ID를 추출하고, 해당 사용자가 질문 작성자인지 확인하여 권한을 검사합니다.
     *
     * @param id 삭제할 질문의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 질문 삭제 성공 메시지와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @DeleteMapping("/questions/{id}") // DELETE 요청을 "/api/qna/questions/{id}" 경로로 매핑합니다.
    public ResponseEntity<String> deleteQuestion(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(id); // 기존 질문 정보를 가져옵니다.
            if (existingQuestion == null) {
                // 질문을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다.");
            }
            // 현재 로그인된 사용자가 질문 작성자가 아니면 권한 없음 응답을 반환합니다.
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("질문을 삭제할 권한이 없습니다.");
            }

            qnaService.deleteQuestion(id); // QnaService를 통해 질문을 삭제합니다.
            return ResponseEntity.ok("질문이 성공적으로 삭제되었습니다."); // 성공 응답
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // 질문 삭제 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문 삭제 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 질문에 대한 답변을 채택합니다.
     * JWT 토큰에서 사용자 ID를 추출하고, 해당 사용자가 질문 작성자인지 확인하여 채택 권한을 검사합니다.
     *
     * @param questionId 답변을 채택할 질문의 고유 ID (경로 변수).
     * @param answerId 채택할 답변의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 답변 채택 성공 메시지와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PutMapping("/questions/{questionId}/accept-answer/{answerId}") // PUT 요청을 "/api/qna/questions/{questionId}/accept-answer/{answerId}" 경로로 매핑합니다.
    public ResponseEntity<String> setAcceptedAnswer(@PathVariable("questionId") int questionId, @PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(questionId); // 기존 질문 정보를 가져옵니다.
            if (existingQuestion == null) {
                // 질문을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다.");
            }
            // 현재 로그인된 사용자가 질문 작성자가 아니면 채택 권한 없음 응답을 반환합니다.
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변을 채택할 권한이 없습니다. 질문 작성자만 채택할 수 있습니다.");
            }
            // 채택하려는 답변이 유효한지, 그리고 해당 질문에 속하는지 확인합니다.
            QnaAnswerDTO answerToAccept = qnaService.getAnswerById(answerId);
            if (answerToAccept == null || answerToAccept.getQuestionId() != questionId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않거나 해당 질문에 속하지 않는 답변입니다.");
            }

            qnaService.setAcceptedAnswer(questionId, answerId); // QnaService를 통해 답변을 채택합니다.
            return ResponseEntity.ok("답변이 성공적으로 채택되었습니다."); // 성공 응답
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // 답변 채택 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 채택 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 질문에 대한 채택된 답변을 해제합니다.
     * JWT 토큰에서 사용자 ID를 추출하고, 해당 사용자가 질문 작성자인지 확인하여 해제 권한을 검사합니다.
     *
     * @param questionId 답변 채택을 해제할 질문의 고유 ID (경로 변수).
     * @param answerId 채택 해제할 답변의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 답변 채택 해제 성공 메시지와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PutMapping("/questions/{questionId}/unset-accepted-answer/{answerId}") // PUT 요청을 "/api/qna/questions/{questionId}/unset-accepted-answer/{answerId}" 경로로 매핑합니다.
    public ResponseEntity<String> unsetAcceptedAnswer(@PathVariable("questionId") int questionId, @PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(questionId); // 기존 질문 정보를 가져옵니다.
            if (existingQuestion == null) {
                // 질문을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다.");
            }
            // 현재 로그인된 사용자가 질문 작성자가 아니면 해제 권한 없음 응답을 반환합니다.
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변 채택을 해제할 권한이 없습니다. 질문 작성자만 해제할 수 있습니다.");
            }
            // 채택 해제하려는 답변이 유효한지, 그리고 해당 질문에 속하는지 확인합니다.
            QnaAnswerDTO answerToUnset = qnaService.getAnswerById(answerId);
            if (answerToUnset == null || answerToUnset.getQuestionId() != questionId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않거나 해당 질문에 속하지 않는 답변입니다.");
            }

            qnaService.unsetAcceptedAnswer(questionId, answerId); // QnaService를 통해 답변 채택을 해제합니다.
            return ResponseEntity.ok("답변 채택이 성공적으로 해제되었습니다."); // 성공 응답
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // 답변 채택 해제 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 채택 해제 실패: " + e.getMessage());
        }
    }
    
    /**
     * 특정 질문에 대한 답변 목록을 조회합니다.
     * 이 API는 프론트엔드 호환성을 위해 POST 메서드를 사용하며, 요청 본문은 선택 사항입니다.
     *
     * @param questionId 답변을 조회할 질문의 고유 ID (경로 변수).
     * @param reqBody (선택 사항) 요청 본문.
     * @return 답변 목록(QnaAnswerDTO 리스트)과 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("/questions-answers/{questionId}") // POST 요청을 "/api/qna/questions-answers/{questionId}" 경로로 매핑합니다.
    public ResponseEntity<List<QnaAnswerDTO>> getAnswersByQuestionIdPost(@PathVariable("questionId") int questionId, @RequestBody(required = false) Map<String, Object> reqBody) {
        List<QnaAnswerDTO> answers = qnaService.getAnswersByQuestionId(questionId); // QnaService를 통해 답변 목록을 가져옵니다.
        return ResponseEntity.ok(answers); // HTTP 200 OK 상태 코드와 함께 답변 목록을 반환합니다.
    }
    
    /**
     * 특정 ID를 가진 Q&A 답변의 상세 정보를 조회합니다.
     *
     * @param id 조회할 답변의 고유 ID (경로 변수).
     * @return 조회된 답변 정보(QnaAnswerDTO) 또는 HTTP 404 Not Found 응답을 반환합니다.
     */
    @GetMapping("/answers/{id}") // GET 요청을 "/api/qna/answers/{id}" 경로로 매핑합니다.
    public ResponseEntity<QnaAnswerDTO> getAnswerById(@PathVariable("id") int id) {
        QnaAnswerDTO answer = qnaService.getAnswerById(id); // QnaService를 통해 답변 상세 정보를 가져옵니다.
        if (answer != null) {
            // 답변을 찾으면 HTTP 200 OK 상태 코드와 함께 답변 정보를 반환합니다.
            return ResponseEntity.ok(answer);
        }
        // 답변을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
        return ResponseEntity.notFound().build();
    }

    /**
     * 새로운 Q&A 답변을 생성합니다.
     * JWT 토큰에서 사용자 ID를 추출하고 답변 작성자를 확인하며 DTO에 설정합니다.
     *
     * @param answerDTO 생성할 답변 정보를 담고 있는 QnaAnswerDTO 객체 (요청 본문).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 답변 생성 성공 메시지와 함께 HTTP 201 Created 응답을 반환합니다.
     */
    @PostMapping("/answers") // POST 요청을 "/api/qna/answers" 경로로 매핑합니다.
    public ResponseEntity<String> createAnswer(@RequestBody QnaAnswerDTO answerDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            answerDTO.setUserId(userId); // DTO에 사용자 ID를 설정합니다.
            
            // QnaAnswerDTO에 nickname 필드가 프론트엔드로부터 넘어올 수 있으나,
            // 백엔드에서 userId를 통해 사용자 정보를 조회하여 닉네임을 설정하는 것이 일반적이므로
            // 여기서는 DTO에 nickname을 명시적으로 설정하지 않아도 됩니다. (선택적)
            // answerDTO.setNickname(어딘가에서 가져온 닉네임); // 이 줄은 제거합니다.

            qnaService.createAnswer(answerDTO); // QnaService를 통해 답변을 생성합니다.
            return ResponseEntity.status(HttpStatus.CREATED).body("답변이 성공적으로 생성되었습니다."); // 성공 응답
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // 답변 생성 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 기존 Q&A 답변을 수정합니다.
     * JWT 토큰에서 사용자 ID를 추출하고, 해당 사용자가 답변 작성자인지 확인하여 권한을 검사합니다.
     *
     * @param id 수정할 답변의 고유 ID (경로 변수).
     * @param answerDTO 수정된 답변 정보를 담고 있는 QnaAnswerDTO 객체 (요청 본문).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 답변 업데이트 성공 메시지와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PutMapping("/answers/{id}") // PUT 요청을 "/api/qna/answers/{id}" 경로로 매핑합니다.
    public ResponseEntity<String> updateAnswer(@PathVariable("id") int id, @RequestBody QnaAnswerDTO answerDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            QnaAnswerDTO existingAnswer = qnaService.getAnswerById(id); // 기존 답변 정보를 가져옵니다.
            if (existingAnswer == null) {
                // 답변을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("답변을 찾을 수 없습니다.");
            }
            // 현재 로그인된 사용자가 답변 작성자가 아니면 권한 없음 응답을 반환합니다.
            if (existingAnswer.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변을 수정할 권한이 없습니다.");
            }

            answerDTO.setId(id); // 경로 변수에서 받은 ID를 DTO에 설정합니다.
            answerDTO.setUserId(userId); // DTO에 사용자 ID를 설정합니다. (작성자 유지)
            // QnaAnswerDTO에 nickname 필드가 없으므로, 이 줄은 제거하거나 주석 처리해야 합니다.
            // answerDTO.setNickname(existingAnswer.getNickname()); // <-- 이 줄은 제거합니다.

            qnaService.updateAnswer(answerDTO); // QnaService를 통해 답변을 업데이트합니다.
            return ResponseEntity.ok("답변이 성공적으로 업데이트되었습니다."); // 성공 응답
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // 답변 업데이트 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 업데이트 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 Q&A 답변을 삭제합니다.
     * JWT 토큰에서 사용자 ID를 추출하고, 해당 사용자가 답변 작성자인지 확인하여 권한을 검사합니다.
     *
     * @param id 삭제할 답변의 고유 ID (경로 변수).
     * @param questionId 답변이 속한 질문의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 답변 삭제 성공 메시지와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @DeleteMapping("/answers/{id}/question/{questionId}") // DELETE 요청을 "/api/qna/answers/{id}/question/{questionId}" 경로로 매핑합니다.
    public ResponseEntity<String> deleteAnswer(@PathVariable("id") int id, @PathVariable("questionId") int questionId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            QnaAnswerDTO existingAnswer = qnaService.getAnswerById(id); // 기존 답변 정보를 가져옵니다.
            if (existingAnswer == null) {
                // 답변을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("답변을 찾을 수 없습니다.");
            }
            // 현재 로그인된 사용자가 답변 작성자가 아니면 권한 없음 응답을 반환합니다.
            if (existingAnswer.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변을 삭제할 권한이 없습니다.");
            }

            qnaService.deleteAnswer(id, questionId); // QnaService를 통해 답변을 삭제합니다.
            return ResponseEntity.ok("답변이 성공적으로 삭제되었습니다."); // 성공 응답
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // 답변 삭제 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 삭제 실패: " + e.getMessage());
        }
    }

    // --- 좋아요 관련 API ---

    /**
     * 특정 질문에 대한 좋아요를 토글(추가/취소)합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 좋아요를 처리합니다.
     *
     * @param questionId 좋아요를 토글할 질문의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 좋아요 상태(`true`는 좋아요 활성화, `false`는 좋아요 비활성화)와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("/questions/{questionId}/like") // POST 요청을 "/api/qna/questions/{questionId}/like" 경로로 매핑합니다.
    public ResponseEntity<Boolean> toggleQuestionLike(@PathVariable("questionId") int questionId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            boolean liked = qnaService.toggleQuestionLike(questionId, userId); // QnaService를 통해 좋아요를 토글합니다.
            return ResponseEntity.ok(liked); // 좋아요 상태 반환
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환하고 false를 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (Exception e) {
            // 좋아요 처리 중 기타 예외 발생 시 서버 내부 오류 응답을 반환하고 false를 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    /**
     * 특정 질문의 좋아요 수를 조회합니다.
     *
     * @param questionId 좋아요 수를 조회할 질문의 고유 ID (경로 변수).
     * @return 질문의 좋아요 수와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @GetMapping("/questions/{questionId}/like-count") // GET 요청을 "/api/qna/questions/{questionId}/like-count" 경로로 매핑합니다.
    public ResponseEntity<Integer> getQuestionLikeCount(@PathVariable("questionId") int questionId) {
        int likeCount = qnaService.getQuestionLikeCount(questionId); // QnaService를 통해 좋아요 수를 가져옵니다.
        return ResponseEntity.ok(likeCount); // 좋아요 수 반환
    }

    /**
     * 특정 사용자가 특정 질문에 좋아요를 눌렀는지 여부를 조회합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 확인합니다.
     *
     * @param questionId 좋아요 상태를 조회할 질문의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 좋아요 여부(`true` 또는 `false`)와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @GetMapping("/questions/{questionId}/like-status") // GET 요청을 "/api/qna/questions/{questionId}/like-status" 경로로 매핑합니다.
    public ResponseEntity<Boolean> isQuestionLikedByUser(@PathVariable("questionId") int questionId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            boolean isLiked = qnaService.isQuestionLikedByUser(questionId, userId); // QnaService를 통해 좋아요 여부를 확인합니다.
            return ResponseEntity.ok(isLiked); // 좋아요 여부 반환
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환하고 false를 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (Exception e) {
            // 좋아요 상태 조회 중 기타 예외 발생 시 서버 내부 오류 응답을 반환하고 false를 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    /**
     * 특정 답변에 대한 좋아요를 토글(추가/취소)합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 좋아요를 처리합니다.
     *
     * @param answerId 좋아요를 토글할 답변의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 좋아요 상태(`true`는 좋아요 활성화, `false`는 좋아요 비활성화)와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("/answers/{answerId}/like") // POST 요청을 "/api/qna/answers/{answerId}/like" 경로로 매핑합니다.
    public ResponseEntity<Boolean> toggleAnswerLike(@PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            boolean liked = qnaService.toggleAnswerLike(answerId, userId); // QnaService를 통해 좋아요를 토글합니다.
            return ResponseEntity.ok(liked); // 좋아요 상태 반환
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환하고 false를 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (Exception e) {
            // 좋아요 처리 중 기타 예외 발생 시 서버 내부 오류 응답을 반환하고 false를 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    /**
     * 특정 답변의 좋아요 수를 조회합니다.
     *
     * @param answerId 좋아요 수를 조회할 답변의 고유 ID (경로 변수).
     * @return 답변의 좋아요 수와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @GetMapping("/answers/{answerId}/like-count") // GET 요청을 "/api/qna/answers/{answerId}/like-count" 경로로 매핑합니다.
    public ResponseEntity<Integer> getAnswerLikeCount(@PathVariable("answerId") int answerId) {
        int likeCount = qnaService.getAnswerLikeCount(answerId); // QnaService를 통해 좋아요 수를 가져옵니다.
        return ResponseEntity.ok(likeCount); // 좋아요 수 반환
    }

    /**
     * 특정 사용자가 특정 답변에 좋아요를 눌렀는지 여부를 조회합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 확인합니다.
     *
     * @param answerId 좋아요 상태를 조회할 답변의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 좋아요 여부(`true` 또는 `false`)와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @GetMapping("/answers/{answerId}/like-status") // GET 요청을 "/api/qna/answers/{answerId}/like-status" 경로로 매핑합니다.
    public ResponseEntity<Boolean> isAnswerLikedByUser(@PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰에서 사용자 ID를 추출합니다.
            boolean isLiked = qnaService.isAnswerLikedByUser(answerId, userId); // QnaService를 통해 좋아요 여부를 확인합니다.
            return ResponseEntity.ok(isLiked); // 좋아요 여부 반환
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환하고 false를 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } catch (Exception e) {
            // 좋아요 상태 조회 중 기타 예외 발생 시 서버 내부 오류 응답을 반환하고 false를 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}