package domain.lecture.repository;

import hhplus.tdd.domain.lecture.LectureApplications;
import hhplus.tdd.infrastructure.lecture.ORMRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.NONE)
class LectureRepositoryTest {

    @Autowired
    private ORMRepository ormRepository;

    @Test
    @DisplayName("강의에 대한 신청 수가 최대 인원을 초과하지 않는지 확인하는 테스트")
    void testCountApplications_OverCapacity() {
        // Given
        Long lectureId = 1L;
        for (int i = 1; i <= 30; i++) {
            LectureApplications application = new LectureApplications();
            application.setUserId("user0" + i);
            application.setLectureId(lectureId);
            application.setStatus("Y");
            ormRepository.save(application);
        };

        int currentApplications = ormRepository.countByLectureIdAndStatus(lectureId, "Y");
        assertThat(currentApplications).isEqualTo(30);

        LectureApplications application = new LectureApplications();
        application.setUserId("user031");
        application.setLectureId(lectureId);
        application.setStatus("Y");
        assertThatThrownBy(() -> ormRepository.save(application))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("특강 신청 정원이 마감되었습니다.");
    }

    @Test
    @DisplayName("중복된 유저가 강의에 신청하지 않았는지 확인하는 테스트")
    void testExistsApplication_Duplicate() {
        String userId = "user001";
        Long lectureId = 1L;
        LectureApplications application = new LectureApplications();
        application.setUserId(userId);
        application.setLectureId(lectureId);
        application.setStatus("Y");
        ormRepository.save(application);

        boolean exists = ormRepository.existsByUserIdAndLectureId(userId, lectureId);
        assertThat(exists).isTrue();

        assertThatThrownBy(() -> ormRepository.save(application))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 신청한 강의입니다.");
    }
}
