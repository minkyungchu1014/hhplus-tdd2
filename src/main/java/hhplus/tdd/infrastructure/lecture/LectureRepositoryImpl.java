package hhplus.tdd.infrastructure.lecture;

import hhplus.tdd.domain.lecture.LectureApplications;
import hhplus.tdd.domain.lecture.Lectures;
import hhplus.tdd.domain.lecture.repository.LectureRepository;
import hhplus.tdd.interfaces.api.dto.LectureResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class LectureRepositoryImpl implements LectureRepository {

    private final EntityManager entityManager;
    private final ORMRepository ormRepository;

    public LectureRepositoryImpl(EntityManager entityManager, ORMRepository ormRepository) {
        this.entityManager = entityManager;
        this.ormRepository = ormRepository;
    }

    @Transactional
    @Override
    public LectureResponse applyLecture(String userId, Long lectureId) {
        Lectures lectures = entityManager.find(Lectures.class, lectureId, LockModeType.PESSIMISTIC_WRITE);
        if (lectures == null) {
            throw new IllegalArgumentException("해당 강의를 찾을 수 없습니다.");
        }

        // 정원 초과 여부를 확인한다.
        int currentApplications = countApplications(lectureId);
        if (!lectures.canApply(currentApplications)) {
            throw new IllegalArgumentException("특강 신청 정원이 마감되었습니다.");
        }

        // 신청 정보를 저장한다.
        saveApplication(userId, lectureId);

        int remainingSlots = lectures.getCapacity() - currentApplications - 1;

        // LectureResponse로 변환하여 값을 반환한다.
        return convertToResponse(lectures, remainingSlots);
    }

    @Override
    public List<LectureResponse> findAvailableLectures(LocalDate date) {
        return ormRepository.findAvailableLectures(date);
    }

    @Override
    public List<LectureResponse> findCompletedApplications(String userId) {
        return ormRepository.findCompletedApplications(userId);
    }

    private int countApplications(Long lectureId) {
        return ormRepository.countByLectureIdAndStatus(lectureId, "Y");
    }

    private void saveApplication(String userId, Long lectureId) {
        LectureApplications application = new LectureApplications();
        application.setUserId(userId);
        application.setLectureId(lectureId);
        application.setStatus("Y");
        ormRepository.save(application);
    }

    private LectureResponse convertToResponse(Lectures lectures, int remainingSlots) {
        return new LectureResponse(
                lectures.getId(),
                lectures.getName(),
                lectures.getInstructor(),
                lectures.getLectureDate(),
                remainingSlots
        );
    }
}
