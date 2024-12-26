package hhplus.tdd.interfaces.api.lecture;

import hhplus.tdd.application.lecture.LectureFacade;
import hhplus.tdd.interfaces.api.dto.LectureRequest;
import hhplus.tdd.interfaces.api.dto.LectureResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/lecture")
public class LectureController {

    private final LectureFacade lectureFacade;

    public LectureController(LectureFacade lectureFacade) {
        this.lectureFacade = lectureFacade;
    }

    /**
     * 특강을 신청한다.
     *
     * @requestBody userId 신청하는 사용자 ID, lectureId 신청할 강의 ID
     * @return ResponseEntity<LectureResponse> 강의 신청 결과를 담은 응답 반환
     * @throws IllegalArgumentException 이미 신청한 강의에 다시 신청 시 예외 발생
     * @description 사용자가 강의를 신청하는 기능, 이미 신청된 강의에는 예외 발생
     */
    @PostMapping("/apply-lectures")
    public ResponseEntity<LectureResponse> applyLecture(@RequestBody LectureRequest lectureRequest) {
        LectureResponse response = lectureFacade.applyLecture(lectureRequest.getUserId(), lectureRequest.getLectureId());
        return ResponseEntity.ok(response);

    }

    /**
     * 특강 신청 가능 목록을 조회한다.
     *
     * @param date 조회하고자 하는 날짜
     * @return ResponseEntity<List<LectureResponse>> 강의 목록과 함께 HTTP 응답을 반환
     * @description 해당 날짜에 개설된 모든 강의를 조회하여 반환하는 기능
     */
    @GetMapping("/available-lectures")
    public ResponseEntity<?> getAvailableLectures(@RequestParam String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            List<LectureResponse> availableLectures = lectureFacade.getAvailableLectures(parsedDate);
            return ResponseEntity.ok(availableLectures);
        } catch (DateTimeParseException e) {
            // 잘못된 날짜 형식 처리
            return ResponseEntity.badRequest().body("잘못된 날짜 형식입니다. yyyy-MM-dd 형식으로 입력해주세요.");
        }
    }

    /**
     * 사용자가 신청한 강의 목록을 조회한다.
     *
     * @param userId 사용자 ID
     * @return ResponseEntity<List<LectureResponse>> 사용자가 신청한 강의 목록
     * @description 사용자가 신청한 강의를 조회하여 반환하는 기능
     */
    @GetMapping("/completed-lectures")
    public ResponseEntity<List<LectureResponse>> getCompletedApplications(@RequestParam String userId) {
        List<LectureResponse> completedLectures = lectureFacade.getCompletedApplications(userId);
        return ResponseEntity.ok(completedLectures);
    }
}
