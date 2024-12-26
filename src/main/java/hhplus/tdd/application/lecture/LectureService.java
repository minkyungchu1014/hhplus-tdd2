package hhplus.tdd.application.lecture;

import hhplus.tdd.domain.lecture.repository.LectureRepository;
import hhplus.tdd.interfaces.api.dto.LectureResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    @Transactional
    public LectureResponse applyLecture(String userId, Long lectureId) {
        return lectureRepository.applyLecture(userId, lectureId);
    }

    public List<LectureResponse> getAvailableLectures(LocalDate date) {
        return lectureRepository.findAvailableLectures(date);
    }

    public List<LectureResponse> getCompletedApplications(String userId) {
        return lectureRepository.findCompletedApplications(userId);
    }
}
