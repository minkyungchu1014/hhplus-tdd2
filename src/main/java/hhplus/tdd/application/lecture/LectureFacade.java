package hhplus.tdd.application.lecture;

import hhplus.tdd.interfaces.api.dto.LectureResponse;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LectureFacade {

    private final LectureService lectureService;

    public LectureFacade(LectureService lectureService1) {
        this.lectureService = lectureService1;
    }

    public LectureResponse applyLecture(String userId, Long lectureId) {
        return lectureService.applyLecture(userId, lectureId);
    }

    public List<LectureResponse> getAvailableLectures(LocalDate date) {
        return lectureService.getAvailableLectures(date);
    }

    public List<LectureResponse> getCompletedApplications(String userId) {
        return lectureService.getCompletedApplications(userId);
    }


}
