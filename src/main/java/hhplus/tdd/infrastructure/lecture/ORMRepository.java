package hhplus.tdd.infrastructure.lecture;

import hhplus.tdd.domain.lecture.LectureApplications;
import hhplus.tdd.interfaces.api.dto.LectureResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ORMRepository extends JpaRepository<LectureApplications, Integer> {

    boolean existsByUserIdAndLectureId(String userId, Long lectureId);

    @Query("SELECT COUNT(*) FROM LectureApplications a " +
            "WHERE a.lectureId = :lectureId AND a.status = :status")
    int countByLectureIdAndStatus(Long lectureId, String status);

    @Query("SELECT new hhplus.tdd.interfaces.api.dto.LectureResponse(l.id, l.name, l.instructor, l.lectureDate, " +
            "(l.capacity - COALESCE(COUNT(a), 0))) " +  // 신청자 수가 없는 경우를 처리
            "FROM Lectures l LEFT JOIN LectureApplications a ON l.id = a.lectureId AND a.status = 'Y' " +
            "WHERE l.lectureDate = :date GROUP BY l.id")
    List<LectureResponse> findAvailableLectures(LocalDate date);


    @Query("SELECT new hhplus.tdd.interfaces.api.dto.LectureResponse(l.id, l.name, l.instructor, l.lectureDate, l.capacity) " +
            "FROM LectureApplications a JOIN Lectures l ON a.lectureId = l.id " +
            "WHERE a.userId = :userId AND a.status = 'Y'")
    List<LectureResponse> findCompletedApplications(String userId);

}
