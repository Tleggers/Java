package com.Trekkit_Java.Controller;

import com.Trekkit_Java.DTO.QnaQuestionDTO; // Q&A 질문 데이터 전송 객체 (DTO) 임포트
import com.Trekkit_Java.DTO.QnaAnswerDTO; // Q&A 답변 데이터 전송 객체 (DTO) 임포트
import com.Trekkit_Java.Service.QnaService; // Q&A 비즈니스 로직을 처리하는 서비스 임포트
import com.Trekkit_Java.Util.ExtractToken; // HTTP 요청에서 JWT 토큰을 추출하는 유틸리티 임포트
import com.Trekkit_Java.Util.JwtUtil; // JWT 토큰 생성 및 검증, 정보 추출 유틸리티 임포트
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 정보를 가져오기 위한 서블릿 API 임포트
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입을 위한 어노테이션
import org.springframework.http.HttpStatus; // HTTP 상태 코드를 정의하는 ENUM 임포트
import org.springframework.http.ResponseEntity; // HTTP 응답을 커스터마이징하기 위한 클래스 임포트
import org.springframework.web.bind.annotation.*; // Spring Web의 REST 컨트롤러 관련 어노테이션 임포트

import java.util.List; // 리스트(컬렉션) 사용을 위해 임포트

@RestController // 이 클래스가 RESTful API를 제공하는 컨트롤러임을 나타냅니다.
@RequestMapping("/api/qna") // 이 컨트롤러의 모든 API 엔드포인트는 "/api/qna" 경로로 시작합니다.
public class QnaController {

    @Autowired // QnaService의 인스턴스를 자동으로 주입받습니다.
    private QnaService qnaService;

    @Autowired // JwtUtil의 인스턴스를 자동으로 주입받습니다.
    private JwtUtil jwtUtil; 

    /**
     * JWT 토큰에서 사용자 ID를 추출하는 헬퍼 메서드입니다.
     * 로그인 상태 확인 및 권한 부여 로직에 사용됩니다.
     *
     * 데이터 흐름:
     * 1. HTTP 요청 (Request) -> JWT 토큰 추출 (ExtractToken.extractToken)
     * 2. JWT 토큰 -> 사용자 ID 추출 (jwtUtil.extractUserId)
     * 3. 사용자 ID 반환
     *
     * @param request HTTP 요청 객체 (Authorization 헤더 또는 쿠키에서 토큰을 추출하기 위함)
     * @return JWT 토큰에서 추출된 사용자 ID (int)
     * @throws IllegalArgumentException JWT 토큰이 없거나 유효하지 않을 경우 발생
     */
    private int getUserIdFromRequest(HttpServletRequest request) {
        String token = ExtractToken.extractToken(request); // HTTP 요청에서 JWT 토큰을 추출합니다.
        if (token == null) {
            // 토큰이 없으면 로그인되지 않은 상태로 간주하여 예외를 발생시킵니다.
            // 클라이언트(프론트엔드)에서는 이 예외를 catch하여 "로그인이 필요합니다." 메시지를 표시할 수 있습니다.
            throw new IllegalArgumentException("JWT 토큰이 없습니다. 로그인 상태를 확인해주세요.");
        }
        return jwtUtil.extractUserId(token).intValue(); // 추출된 JWT 토큰에서 사용자 ID를 파싱하여 int 타입으로 반환합니다.
    }

    // --- Q&A 질문 관련 API ---

    /**
     * 모든 Q&A 질문 목록을 조회하는 API 엔드포인트입니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> GET /api/qna/questions 요청
     * 2. Controller -> QnaService.getAllQuestions() 호출
     * 3. QnaService -> DB에서 질문 목록 조회 (데이터 수신)
     * 4. Controller -> 조회된 질문 목록을 ResponseEntity.ok()로 클라이언트에게 응답 (JSON 형식)
     *
     * @return 질문 목록(List<QnaQuestionDTO>)을 포함하는 ResponseEntity
     */
    @GetMapping("/questions") // HTTP GET 요청을 처리하며, "/questions" 경로에 매핑됩니다.
    public ResponseEntity<List<QnaQuestionDTO>> getAllQuestions() {
        List<QnaQuestionDTO> questions = qnaService.getAllQuestions(); // QnaService를 통해 모든 질문을 조회합니다.
        return ResponseEntity.ok(questions); // HTTP 200 OK 상태 코드와 함께 질문 목록을 반환합니다.
    }

    /**
     * 특정 Q&A 질문의 상세 정보를 조회하는 API 엔드포인트입니다.
     * 이 요청이 들어오면 해당 질문의 조회수가 1 증가합니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> GET /api/qna/questions/{id} 요청 (URL 경로 변수로 질문 ID 전송)
     * 2. Controller -> QnaService.getQuestionById() 호출 (질문 ID 전달, 내부적으로 조회수 증가)
     * 3. QnaService -> DB에서 질문 상세 정보 조회 (데이터 수신)
     * 4. Controller -> 조회된 QnaQuestionDTO 객체를 ResponseEntity.ok()로 클라이언트에게 응답 (JSON 형식)
     *
     * @param id 조회할 질문의 ID (URL 경로 변수)
     * @return 조회된 QnaQuestionDTO 객체를 포함하는 ResponseEntity 또는 404 Not Found
     */
    @GetMapping("/questions/{id}") // HTTP GET 요청을 처리하며, 경로의 {id} 부분을 id 변수에 매핑합니다.
    public ResponseEntity<QnaQuestionDTO> getQuestionById(@PathVariable("id") int id) {
        QnaQuestionDTO question = qnaService.getQuestionById(id); // QnaService를 통해 특정 질문을 조회합니다. (이때 조회수 증가)
        if (question != null) {
            return ResponseEntity.ok(question); // 질문이 존재하면 HTTP 200 OK와 함께 질문 객체를 반환합니다.
        }
        return ResponseEntity.notFound().build(); // 질문을 찾을 수 없으면 HTTP 404 Not Found를 반환합니다.
    }

    /**
     * 새로운 Q&A 질문을 생성하는 API 엔드포인트입니다. (로그인된 사용자만 가능)
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> POST /api/qna/questions 요청 (요청 본문에 QnaQuestionDTO 객체(JSON) 전송)
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출 (getUserIdFromRequest 호출)
     * 3. Controller -> QnaQuestionDTO에 작성자 ID 설정
     * 4. Controller -> QnaService.createQuestion() 호출 (QnaQuestionDTO 전달)
     * 5. QnaService -> DB에 질문 삽입
     * 6. Controller -> HTTP 201 Created 상태 코드와 함께 성공 메시지 응답
     *
     * @param questionDTO 생성할 질문 데이터 (요청 본문에서 JSON으로 받음)
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 성공 메시지 또는 오류 메시지를 포함하는 ResponseEntity
     */
    @PostMapping("/questions") // HTTP POST 요청을 처리하며, "/questions" 경로에 매핑됩니다.
    public ResponseEntity<String> createQuestion(@RequestBody QnaQuestionDTO questionDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰을 통해 사용자 ID를 추출합니다. (로그인 여부 확인)
            questionDTO.setUserId(userId); // 질문 DTO에 작성자 ID를 설정합니다.
            // TODO: 닉네임은 user_id로 user 테이블에서 조회하여 설정하는 것이 더 안전하고 일관적입니다.
            // 현재는 프론트엔드에서 받은 닉네임 사용 (임시)
            qnaService.createQuestion(questionDTO); // QnaService를 통해 질문을 생성합니다.
            return ResponseEntity.status(HttpStatus.CREATED).body("질문이 성공적으로 생성되었습니다."); // HTTP 201 Created와 성공 메시지 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // 토큰 없음 등 (401 Unauthorized)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문 생성 실패: " + e.getMessage()); // 기타 서버 에러 (500 Internal Server Error)
        }
    }

    /**
     * 기존 Q&A 질문을 수정하는 API 엔드포인트입니다. (로그인된 질문 작성자만 가능)
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> PUT /api/qna/questions/{id} 요청 (URL 경로 변수로 질문 ID, 요청 본문에 QnaQuestionDTO 객체(JSON) 전송)
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> 수정하려는 질문의 기존 정보 조회 (QnaService.getQuestionById)
     * 4. Controller -> 기존 질문 작성자와 현재 사용자 ID 일치 여부 확인 (권한 확인)
     * 5. Controller -> QnaService.updateQuestion() 호출 (QnaQuestionDTO 전달)
     * 6. QnaService -> DB에 질문 업데이트
     * 7. Controller -> HTTP 200 OK 상태 코드와 함께 성공 메시지 응답
     *
     * @param id 수정할 질문의 ID (URL 경로 변수)
     * @param questionDTO 수정할 질문 데이터 (요청 본문에서 JSON으로 받음)
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 성공 메시지 또는 오류 메시지를 포함하는 ResponseEntity
     */
    @PutMapping("/questions/{id}") // HTTP PUT 요청을 처리하며, 경로의 {id} 부분을 id 변수에 매핑합니다.
    public ResponseEntity<String> updateQuestion(@PathVariable("id") int id, @RequestBody QnaQuestionDTO questionDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // 사용자 ID 추출
            // 권한 확인: 현재 로그인한 사용자가 질문의 작성자인지 확인합니다.
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(id); // 기존 질문 정보를 불러옵니다.
            if (existingQuestion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다."); // 질문이 없으면 404
            }
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("질문을 수정할 권한이 없습니다."); // 작성자가 아니면 403 Forbidden
            }

            questionDTO.setId(id); // URL 경로 변수의 ID를 DTO의 ID에 설정합니다.
            // 닉네임과 사용자 ID는 수정 요청에서 변경하지 않고, 기존 값(DB에 있는 값)을 유지합니다.
            // DTO에 userId를 다시 설정하여 서비스 계층으로 전달 (필수 아님, 명시적 설정)
            questionDTO.setUserId(userId); 
            questionDTO.setNickname(existingQuestion.getNickname()); // 닉네임도 기존 값으로 유지

            qnaService.updateQuestion(questionDTO); // QnaService를 통해 질문을 업데이트합니다.
            return ResponseEntity.ok("질문이 성공적으로 업데이트되었습니다."); // HTTP 200 OK와 성공 메시지 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // JWT 토큰 없음 (401 Unauthorized)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문 업데이트 실패: " + e.getMessage()); // 기타 서버 에러 (500 Internal Server Error)
        }
    }

    /**
     * 특정 Q&A 질문을 삭제하는 API 엔드포인트입니다. (로그인된 질문 작성자만 가능)
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> DELETE /api/qna/questions/{id} 요청 (URL 경로 변수로 질문 ID 전송)
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> 삭제하려는 질문의 기존 정보 조회
     * 4. Controller -> 기존 질문 작성자와 현재 사용자 ID 일치 여부 확인 (권한 확인)
     * 5. Controller -> QnaService.deleteQuestion() 호출 (질문 ID 전달)
     * 6. QnaService -> DB에서 질문 삭제 (관련 답변, 좋아요 등은 DB CASCADE 설정에 따라 삭제되거나, 서비스에서 명시적 삭제)
     * 7. Controller -> HTTP 200 OK 상태 코드와 함께 성공 메시지 응답
     *
     * @param id 삭제할 질문의 ID (URL 경로 변수)
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 성공 메시지 또는 오류 메시지를 포함하는 ResponseEntity
     */
    @DeleteMapping("/questions/{id}") // HTTP DELETE 요청을 처리하며, 경로의 {id} 부분을 id 변수에 매핑합니다.
    public ResponseEntity<String> deleteQuestion(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // 사용자 ID 추출
            // 권한 확인: 현재 로그인한 사용자가 질문의 작성자인지 확인합니다.
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(id); // 기존 질문 정보를 불러옵니다.
            if (existingQuestion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다."); // 질문이 없으면 404
            }
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("질문을 삭제할 권한이 없습니다."); // 작성자가 아니면 403 Forbidden
            }

            qnaService.deleteQuestion(id); // QnaService를 통해 질문을 삭제합니다.
            return ResponseEntity.ok("질문이 성공적으로 삭제되었습니다."); // HTTP 200 OK와 성공 메시지 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // JWT 토큰 없음 (401 Unauthorized)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("질문 삭제 실패: " + e.getMessage()); // 기타 서버 에러 (500 Internal Server Error)
        }
    }

    /**
     * 특정 Q&A 질문에 대해 답변을 채택하는 API 엔드포인트입니다. (로그인된 질문 작성자만 가능)
     * 질문의 is_solved 상태를 true로, accepted_answer_id를 해당 답변의 ID로 설정합니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> PUT /api/qna/questions/{questionId}/accept-answer/{answerId} 요청
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> 질문의 기존 정보 조회 및 질문 작성자와 현재 사용자 ID 일치 여부 확인 (권한 확인)
     * 4. Controller -> 채택하려는 답변이 해당 질문에 속하는지 확인
     * 5. Controller -> QnaService.setAcceptedAnswer() 호출 (질문 ID, 답변 ID 전달)
     * 6. QnaService -> DB 업데이트 (질문 상태, 답변 상태)
     * 7. Controller -> HTTP 200 OK 상태 코드와 함께 성공 메시지 응답
     *
     * @param questionId 채택할 답변이 속한 질문의 ID
     * @param answerId 채택할 답변의 ID
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 성공 메시지 또는 오류 메시지를 포함하는 ResponseEntity
     */
    @PutMapping("/questions/{questionId}/accept-answer/{answerId}") // HTTP PUT 요청 처리
    public ResponseEntity<String> setAcceptedAnswer(@PathVariable("questionId") int questionId, @PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // 사용자 ID 추출
            // 권한 확인: 현재 로그인한 사용자가 질문의 작성자인지 확인합니다.
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(questionId);
            if (existingQuestion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다.");
            }
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변을 채택할 권한이 없습니다. 질문 작성자만 채택할 수 있습니다.");
            }
            // 채택하려는 답변이 해당 질문에 속하는지 추가적으로 확인하여 안전성을 높입니다.
            QnaAnswerDTO answerToAccept = qnaService.getAnswerById(answerId);
            if (answerToAccept == null || answerToAccept.getQuestionId() != questionId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않거나 해당 질문에 속하지 않는 답변입니다.");
            }

            qnaService.setAcceptedAnswer(questionId, answerId); // QnaService를 통해 답변을 채택합니다.
            return ResponseEntity.ok("답변이 성공적으로 채택되었습니다."); // HTTP 200 OK와 성공 메시지 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 채택 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 Q&A 질문의 답변 채택을 해제하는 API 엔드포인트입니다. (로그인된 질문 작성자만 가능)
     * 질문의 is_solved 상태를 false로, accepted_answer_id를 null로 설정합니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> PUT /api/qna/questions/{questionId}/unset-accepted-answer/{answerId} 요청
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> 질문의 기존 정보 조회 및 질문 작성자와 현재 사용자 ID 일치 여부 확인 (권한 확인)
     * 4. Controller -> 채택 해제하려는 답변이 해당 질문에 속하는지 확인
     * 5. Controller -> QnaService.unsetAcceptedAnswer() 호출 (질문 ID, 답변 ID 전달)
     * 6. QnaService -> DB 업데이트 (질문 상태, 답변 상태)
     * 7. Controller -> HTTP 200 OK 상태 코드와 함께 성공 메시지 응답
     *
     * @param questionId 채택 해제할 답변이 속한 질문의 ID
     * @param answerId 채택 해제할 답변의 ID
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 성공 메시지 또는 오류 메시지를 포함하는 ResponseEntity
     */
    @PutMapping("/questions/{questionId}/unset-accepted-answer/{answerId}") // HTTP PUT 요청 처리
    public ResponseEntity<String> unsetAcceptedAnswer(@PathVariable("questionId") int questionId, @PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // 사용자 ID 추출
            // 권한 확인: 현재 로그인한 사용자가 질문의 작성자인지 확인합니다.
            QnaQuestionDTO existingQuestion = qnaService.getQuestionById(questionId);
            if (existingQuestion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문을 찾을 수 없습니다.");
            }
            if (existingQuestion.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변 채택을 해제할 권한이 없습니다. 질문 작성자만 해제할 수 있습니다.");
            }
            // 채택 해제하려는 답변이 해당 질문에 속하는지 확인
            QnaAnswerDTO answerToUnset = qnaService.getAnswerById(answerId);
            if (answerToUnset == null || answerToUnset.getQuestionId() != questionId) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않거나 해당 질문에 속하지 않는 답변입니다.");
            }

            qnaService.unsetAcceptedAnswer(questionId, answerId); // QnaService를 통해 답변 채택을 해제합니다.
            return ResponseEntity.ok("답변 채택이 성공적으로 해제되었습니다."); // HTTP 200 OK와 성공 메시지 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 채택 해제 실패: " + e.getMessage());
        }
    }

    // --- Q&A 답변 관련 API ---

    /**
     * 특정 질문에 대한 모든 답변 목록을 조회하는 API 엔드포인트입니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> GET /api/qna/questions/{questionId}/answers 요청
     * 2. Controller -> QnaService.getAnswersByQuestionId() 호출 (질문 ID 전달)
     * 3. QnaService -> DB에서 답변 목록 조회 (데이터 수신)
     * 4. Controller -> 조회된 답변 목록을 ResponseEntity.ok()로 클라이언트에게 응답 (JSON 형식)
     *
     * @param questionId 답변을 조회할 질문의 ID (URL 경로 변수)
     * @return 답변 목록(List<QnaAnswerDTO>)을 포함하는 ResponseEntity
     */
    @GetMapping("/questions/{questionId}/answers") // HTTP GET 요청을 처리하며, 경로에 매핑됩니다.
    public ResponseEntity<List<QnaAnswerDTO>> getAnswersByQuestionId(@PathVariable("questionId") int questionId) {
        List<QnaAnswerDTO> answers = qnaService.getAnswersByQuestionId(questionId); // QnaService를 통해 특정 질문의 모든 답변을 조회합니다.
        return ResponseEntity.ok(answers); // HTTP 200 OK와 함께 답변 목록을 반환합니다.
    }

    /**
     * 특정 Q&A 답변의 상세 정보를 조회하는 API 엔드포인트입니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> GET /api/qna/answers/{id} 요청
     * 2. Controller -> QnaService.getAnswerById() 호출 (답변 ID 전달)
     * 3. QnaService -> DB에서 답변 상세 정보 조회 (데이터 수신)
     * 4. Controller -> 조회된 QnaAnswerDTO 객체를 ResponseEntity.ok()로 클라이언트에게 응답
     *
     * @param id 조회할 답변의 ID (URL 경로 변수)
     * @return 조회된 QnaAnswerDTO 객체를 포함하는 ResponseEntity 또는 404 Not Found
     */
    @GetMapping("/answers/{id}") // HTTP GET 요청을 처리하며, 경로의 {id} 부분을 id 변수에 매핑합니다.
    public ResponseEntity<QnaAnswerDTO> getAnswerById(@PathVariable("id") int id) {
        QnaAnswerDTO answer = qnaService.getAnswerById(id); // QnaService를 통해 특정 답변을 조회합니다.
        if (answer != null) {
            return ResponseEntity.ok(answer); // 답변이 존재하면 HTTP 200 OK와 함께 답변 객체를 반환합니다.
        }
        return ResponseEntity.notFound().build(); // 답변을 찾을 수 없으면 HTTP 404 Not Found를 반환합니다.
    }

    /**
     * 새로운 Q&A 답변을 생성하는 API 엔드포인트입니다. (로그인된 사용자만 가능)
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> POST /api/qna/answers 요청 (요청 본문에 QnaAnswerDTO 객체(JSON) 전송)
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> QnaAnswerDTO에 작성자 ID, 질문 ID 설정
     * 4. Controller -> QnaService.createAnswer() 호출 (QnaAnswerDTO 전달)
     * 5. QnaService -> DB에 답변 삽입 및 해당 질문의 답변 수 증가
     * 6. Controller -> HTTP 201 Created 상태 코드와 함께 성공 메시지 응답
     *
     * @param answerDTO 생성할 답변 데이터 (요청 본문에서 JSON으로 받음)
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 성공 메시지 또는 오류 메시지를 포함하는 ResponseEntity
     */
    @PostMapping("/answers") // HTTP POST 요청을 처리하며, "/answers" 경로에 매핑됩니다.
    public ResponseEntity<String> createAnswer(@RequestBody QnaAnswerDTO answerDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // JWT 토큰을 통해 사용자 ID를 추출합니다.
            answerDTO.setUserId(userId); // 답변 DTO에 작성자 ID를 설정합니다.
            // TODO: 닉네임은 user_id로 user 테이블에서 조회하여 설정하는 것이 더 안전하고 일관적입니다.
            // 현재는 프론트엔드에서 받은 닉네임 사용 (임시)
            qnaService.createAnswer(answerDTO); // QnaService를 통해 답변을 생성합니다.
            return ResponseEntity.status(HttpStatus.CREATED).body("답변이 성공적으로 생성되었습니다."); // HTTP 201 Created와 성공 메시지 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // 토큰 없음 등 (401 Unauthorized)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 생성 실패: " + e.getMessage()); // 기타 서버 에러 (500 Internal Server Error)
        }
    }

    /**
     * 기존 Q&A 답변을 수정하는 API 엔드포인트입니다. (로그인된 답변 작성자만 가능)
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> PUT /api/qna/answers/{id} 요청 (URL 경로 변수로 답변 ID, 요청 본문에 QnaAnswerDTO 객체(JSON) 전송)
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> 수정하려는 답변의 기존 정보 조회
     * 4. Controller -> 기존 답변 작성자와 현재 사용자 ID 일치 여부 확인 (권한 확인)
     * 5. Controller -> QnaService.updateAnswer() 호출 (QnaAnswerDTO 전달)
     * 6. QnaService -> DB에 답변 업데이트
     * 7. Controller -> HTTP 200 OK 상태 코드와 함께 성공 메시지 응답
     *
     * @param id 수정할 답변의 ID (URL 경로 변수)
     * @param answerDTO 수정할 답변 데이터 (요청 본문에서 JSON으로 받음)
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 성공 메시지 또는 오류 메시지를 포함하는 ResponseEntity
     */
    @PutMapping("/answers/{id}") // HTTP PUT 요청을 처리하며, 경로의 {id} 부분을 id 변수에 매핑합니다.
    public ResponseEntity<String> updateAnswer(@PathVariable("id") int id, @RequestBody QnaAnswerDTO answerDTO, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // 사용자 ID 추출
            // 권한 확인: 현재 로그인한 사용자가 답변의 작성자인지 확인합니다.
            QnaAnswerDTO existingAnswer = qnaService.getAnswerById(id); // 기존 답변 정보를 불러옵니다.
            if (existingAnswer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("답변을 찾을 수 없습니다."); // 답변이 없으면 404
            }
            if (existingAnswer.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변을 수정할 권한이 없습니다."); // 작성자가 아니면 403 Forbidden
            }

            answerDTO.setId(id); // URL 경로 변수의 ID를 DTO의 ID에 설정합니다.
            // 닉네임과 사용자 ID는 수정 요청에서 변경하지 않고, 기존 값을 유지합니다.
            answerDTO.setUserId(userId); // DTO에 userId를 다시 설정 (필수 아님, 명시적 설정)
            answerDTO.setNickname(existingAnswer.getNickname()); // 닉네임도 기존 값으로 유지

            qnaService.updateAnswer(answerDTO); // QnaService를 통해 답변을 업데이트합니다.
            return ResponseEntity.ok("답변이 성공적으로 업데이트되었습니다."); // HTTP 200 OK와 성공 메시지 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // JWT 토큰 없음 (401 Unauthorized)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 업데이트 실패: " + e.getMessage()); // 기타 서버 에러 (500 Internal Server Error)
        }
    }

    /**
     * 특정 Q&A 답변을 삭제하는 API 엔드포인트입니다. (로그인된 답변 작성자만 가능)
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> DELETE /api/qna/answers/{id}/question/{questionId} 요청
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> 삭제하려는 답변의 기존 정보 조회
     * 4. Controller -> 기존 답변 작성자와 현재 사용자 ID 일치 여부 확인 (권한 확인)
     * 5. Controller -> QnaService.deleteAnswer() 호출 (답변 ID, 질문 ID 전달)
     * 6. QnaService -> DB에서 답변 삭제 및 해당 질문의 답변 수 감소
     * 7. Controller -> HTTP 200 OK 상태 코드와 함께 성공 메시지 응답
     *
     * @param id 삭제할 답변의 ID (URL 경로 변수)
     * @param questionId 답변이 속한 질문의 ID (URL 경로 변수, 답변 수 감소를 위해 필요)
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 성공 메시지 또는 오류 메시지를 포함하는 ResponseEntity
     */
    @DeleteMapping("/answers/{id}/question/{questionId}") // HTTP DELETE 요청을 처리하며, 경로에 매핑됩니다.
    public ResponseEntity<String> deleteAnswer(@PathVariable("id") int id, @PathVariable("questionId") int questionId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // 사용자 ID 추출
            // 권한 확인: 현재 로그인한 사용자가 답변의 작성자인지 확인합니다.
            QnaAnswerDTO existingAnswer = qnaService.getAnswerById(id); // 기존 답변 정보를 불러옵니다.
            if (existingAnswer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("답변을 찾을 수 없습니다."); // 답변이 없으면 404
            }
            if (existingAnswer.getUserId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("답변을 삭제할 권한이 없습니다."); // 작성자가 아니면 403 Forbidden
            }

            qnaService.deleteAnswer(id, questionId); // QnaService를 통해 답변을 삭제합니다.
            return ResponseEntity.ok("답변이 성공적으로 삭제되었습니다."); // HTTP 200 OK와 성공 메시지 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // JWT 토큰 없음 (401 Unauthorized)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("답변 삭제 실패: " + e.getMessage()); // 기타 서버 에러 (500 Internal Server Error)
        }
    }

    // --- 좋아요 관련 API ---

    /**
     * Q&A 질문의 좋아요 상태를 토글(추가/취소)하는 API 엔드포인트입니다. (로그인된 사용자만 가능)
     * 좋아요가 없으면 추가하고, 있으면 취소합니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> POST /api/qna/questions/{questionId}/like 요청
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> QnaService.toggleQuestionLike() 호출 (질문 ID, 사용자 ID 전달)
     * 4. QnaService -> DB에서 좋아요 상태 확인 및 업데이트 (좋아요 추가 또는 삭제)
     * 5. Controller -> HTTP 200 OK 상태 코드와 함께 좋아요 추가/취소 여부를 boolean 값으로 응답
     *
     * @param questionId 좋아요를 토글할 질문의 ID
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 좋아요 추가 여부 (true: 추가됨, false: 취소됨)를 포함하는 ResponseEntity
     */
    @PostMapping("/questions/{questionId}/like") // HTTP POST 요청 처리
    public ResponseEntity<Boolean> toggleQuestionLike(@PathVariable("questionId") int questionId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // 사용자 ID 추출 (로그인 여부 확인)
            boolean liked = qnaService.toggleQuestionLike(questionId, userId); // QnaService를 통해 좋아요 상태를 토글합니다.
            return ResponseEntity.ok(liked); // HTTP 200 OK와 함께 좋아요 추가/취소 여부(boolean)를 반환합니다.
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // 인증되지 않은 경우 401 Unauthorized와 false 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false); // 서버 에러 시 500 Internal Server Error와 false 반환
        }
    }

    /**
     * 특정 Q&A 질문의 현재 좋아요 수를 조회하는 API 엔드포인트입니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> GET /api/qna/questions/{questionId}/like-count 요청
     * 2. Controller -> QnaService.getQuestionLikeCount() 호출 (질문 ID 전달)
     * 3. QnaService -> DB에서 좋아요 수 조회
     * 4. Controller -> 조회된 좋아요 수를 ResponseEntity.ok()로 클라이언트에게 응답
     *
     * @param questionId 좋아요 수를 조회할 질문의 ID
     * @return 좋아요 수를 포함하는 ResponseEntity
     */
    @GetMapping("/questions/{questionId}/like-count") // HTTP GET 요청 처리
    public ResponseEntity<Integer> getQuestionLikeCount(@PathVariable("questionId") int questionId) {
        int likeCount = qnaService.getQuestionLikeCount(questionId); // QnaService를 통해 좋아요 수를 조회합니다.
        return ResponseEntity.ok(likeCount); // HTTP 200 OK와 함께 좋아요 수를 반환합니다.
    }

    /**
     * 특정 사용자가 특정 Q&A 질문에 좋아요를 눌렀는지 상태를 확인하는 API 엔드포인트입니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> GET /api/qna/questions/{questionId}/like-status 요청
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> QnaService.isQuestionLikedByUser() 호출 (질문 ID, 사용자 ID 전달)
     * 4. QnaService -> DB에서 좋아요 상태 확인
     * 5. Controller -> HTTP 200 OK 상태 코드와 함께 좋아요 여부를 boolean 값으로 응답
     *
     * @param questionId 좋아요 상태를 확인할 질문의 ID
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 좋아요 여부 (true/false)를 포함하는 ResponseEntity
     */
    @GetMapping("/questions/{questionId}/like-status") // HTTP GET 요청 처리
    public ResponseEntity<Boolean> isQuestionLikedByUser(@PathVariable("questionId") int questionId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // 사용자 ID 추출 (로그인 여부 확인)
            boolean isLiked = qnaService.isQuestionLikedByUser(questionId, userId); // QnaService를 통해 좋아요 상태를 확인합니다.
            return ResponseEntity.ok(isLiked); // HTTP 200 OK와 함께 좋아요 여부(boolean)를 반환합니다.
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // 인증되지 않은 경우 401 Unauthorized와 false 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false); // 서버 에러 시 500 Internal Server Error와 false 반환
        }
    }


    /**
     * Q&A 답변의 좋아요 상태를 토글(추가/취소)하는 API 엔드포인트입니다. (로그인된 사용자만 가능)
     * 좋아요가 없으면 추가하고, 있으면 취소합니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> POST /api/qna/answers/{answerId}/like 요청
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> QnaService.toggleAnswerLike() 호출 (답변 ID, 사용자 ID 전달)
     * 4. QnaService -> DB에서 좋아요 상태 확인 및 업데이트
     * 5. Controller -> HTTP 200 OK 상태 코드와 함께 좋아요 추가/취소 여부를 boolean 값으로 응답
     *
     * @param answerId 좋아요를 토글할 답변의 ID
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 좋아요 추가 여부 (true: 추가됨, false: 취소됨)를 포함하는 ResponseEntity
     */
    @PostMapping("/answers/{answerId}/like") // HTTP POST 요청 처리
    public ResponseEntity<Boolean> toggleAnswerLike(@PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // 사용자 ID 추출 (로그인 여부 확인)
            boolean liked = qnaService.toggleAnswerLike(answerId, userId); // QnaService를 통해 좋아요 상태를 토글합니다.
            return ResponseEntity.ok(liked); // HTTP 200 OK와 함께 좋아요 추가/취소 여부(boolean)를 반환합니다.
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // 인증되지 않은 경우 401 Unauthorized와 false 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false); // 서버 에러 시 500 Internal Server Error와 false 반환
        }
    }

    /**
     * 특정 Q&A 답변의 현재 좋아요 수를 조회하는 API 엔드포인트입니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> GET /api/qna/answers/{answerId}/like-count 요청
     * 2. Controller -> QnaService.getAnswerLikeCount() 호출 (답변 ID 전달)
     * 3. QnaService -> DB에서 좋아요 수 조회
     * 4. Controller -> 조회된 좋아요 수를 ResponseEntity.ok()로 클라이언트에게 응답
     *
     * @param answerId 좋아요 수를 조회할 답변의 ID
     * @return 좋아요 수를 포함하는 ResponseEntity
     */
    @GetMapping("/answers/{answerId}/like-count") // HTTP GET 요청 처리
    public ResponseEntity<Integer> getAnswerLikeCount(@PathVariable("answerId") int answerId) {
        int likeCount = qnaService.getAnswerLikeCount(answerId); // QnaService를 통해 좋아요 수를 조회합니다.
        return ResponseEntity.ok(likeCount); // HTTP 200 OK와 함께 좋아요 수를 반환합니다.
    }

    /**
     * 특정 사용자가 특정 Q&A 답변에 좋아요를 눌렀는지 상태를 확인하는 API 엔드포인트입니다.
     *
     * 데이터 흐름:
     * 1. 클라이언트 -> GET /api/qna/answers/{answerId}/like-status 요청
     * 2. Controller -> JWT 토큰 검증 및 사용자 ID 추출
     * 3. Controller -> QnaService.isAnswerLikedByUser() 호출 (답변 ID, 사용자 ID 전달)
     * 4. QnaService -> DB에서 좋아요 상태 확인
     * 5. Controller -> HTTP 200 OK 상태 코드와 함께 좋아요 여부를 boolean 값으로 응답
     *
     * @param answerId 좋아요 상태를 확인할 답변의 ID
     * @param request HTTP 요청 객체 (JWT 토큰 추출용)
     * @return 좋아요 여부 (true/false)를 포함하는 ResponseEntity
     */
    @GetMapping("/answers/{answerId}/like-status") // HTTP GET 요청 처리
    public ResponseEntity<Boolean> isAnswerLikedByUser(@PathVariable("answerId") int answerId, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request); // 사용자 ID 추출 (로그인 여부 확인)
            boolean isLiked = qnaService.isAnswerLikedByUser(answerId, userId); // QnaService를 통해 좋아요 상태를 확인합니다.
            return ResponseEntity.ok(isLiked); // HTTP 200 OK와 함께 좋아요 여부(boolean)를 반환합니다.
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false); // 인증되지 않은 경우 401 Unauthorized와 false 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false); // 서버 에러 시 500 Internal Server Error와 false 반환
        }
    }
}
