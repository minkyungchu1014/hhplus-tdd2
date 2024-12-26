package application;

import hhplus.tdd.application.lecture.LectureFacade;
import hhplus.tdd.domain.lecture.Lectures;
import hhplus.tdd.infrastructure.lecture.ORMRepository;
import hhplus.tdd.interfaces.api.dto.LectureResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.NONE)
class LectureServiceTest {

    @Autowired
    private LectureFacade lectureFacade;

    @Autowired
    private ORMRepository ormRepository;

    @BeforeEach
    void setup() {
        Lectures lectures = Lectures.builder()
                .name("윈터와 노는 법")
                .instructor("카리나")
                .lectureDate(LocalDate.of(2024, 1, 18))
                .capacity(30)
                .build();
        ormRepository.save(lectures);
    }

    @Test
    @DisplayName("강의 신청이 정상적으로 이루어지는지 확인하는 테스트")
    void testApplyLecture_Success() {
        String userId = "user001";
        Long lectureId = 1L;

        LectureResponse response = lectureFacade.applyLecture(userId, lectureId);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("카리나와 노는 법");
        assertThat(response.getInstructor()).isEqualTo("윈터");
    }

    @Test
    @DisplayName("이미 신청한 유저가 같은 강의를 다시 신청할 때 예외가 발생하는지 확인")
    void testApplyLecture_Duplicate() {
        String userId = "user001";
        Long lectureId = 1L;

        lectureFacade.applyLecture(userId, lectureId);

        assertThatThrownBy(() -> lectureFacade.applyLecture(userId, lectureId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 신청한 강의입니다.");
    }

    @Test
    @DisplayName("강의 신청자가 30명 이상일 때 더 이상 신청이 불가능한지 확인")
    void testApplyLecture_FullCapacity() {
        Long lectureId = 1L;

        for (int i = 1; i <= 30; i++) {
            lectureFacade.applyLecture("user" + i, lectureId);
        }

        assertThatThrownBy(() -> lectureFacade.applyLecture("user31", lectureId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("신청이 마감되었습니다.");
    }
}

