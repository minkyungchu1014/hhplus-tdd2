package hhplus.tdd.domain.lecture.repository;

import hhplus.tdd.interfaces.api.dto.LectureResponse;

import java.time.LocalDate;
import java.util.List;

public interface LectureRepository {
    LectureResponse applyLecture(String userId, Long lectureId);

    List<LectureResponse> findAvailableLectures(LocalDate date);

    List<LectureResponse> findCompletedApplications(String userId);
}
