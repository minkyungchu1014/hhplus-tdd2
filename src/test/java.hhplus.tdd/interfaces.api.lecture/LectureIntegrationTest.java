package interfaces.api.lecture;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.NONE)
class LectureIntegrationTest {

    @Autowired
    private LectureFacade lectureFacade;

    @Autowired
    private ORMRepository ormRepository;

    @BeforeEach
    void setup() {
        Lectures lectures = Lectures.builder()
                .id(1L)
                .name("윈터와 노는 법")
                .instructor("카리나")
                .lectureDate(LocalDate.of(2024, 1, 18))  // 날짜를 LocalDate로 설정
                .capacity(30)
                .build();
        ormRepository.save(lectures);
    }

    @Test
    @DisplayName("지정한 날짜에 개설된 강의 목록이 정상적으로 반환되는지 확인")
    void testGetAvailableLectures() {
        String date = LocalDate.now().toString();

        List<LectureResponse> availableLectures = lectureFacade.getAvailableLectures(date);

        assertThat(availableLectures).isNotEmpty();
        assertThat(availableLectures.get(0).getName()).isEqualTo("윈터와 노는 법");
    }

    @Test
    @DisplayName("유저가 신청한 강의 목록이 정상적으로 반환되는지 확인하는 테스트")
    void testGetCompletedApplications() throws InterruptedException {
        String userId = "user01";
        Long lectureId = 1L;
        lectureFacade.applyLecture(userId, lectureId);

        // When
        List<LectureResponse> completedLectures = lectureFacade.getCompletedApplications(userId);

        // Then
        assertThat(completedLectures).isNotEmpty();
        assertThat(completedLectures.get(0).getName()).isEqualTo("Java Basics");
    }

    @Test
    @DisplayName("동시에 동일한 특강에 대해 40명이 신청했을 때, 30명만 성공하는 것을 검증하는 테스트")
    void testApplyLecture_Concurrent() throws InterruptedException {
        int threadCnt = 40;
        Long lectureId = 1L;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch latch = new CountDownLatch(threadCnt);
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 1; i <= threadCnt; i++) {
            String userId = "user0" + i;
            executorService.submit(() -> {
                try {
                    lectureFacade.applyLecture(userId, lectureId);
                } catch (IllegalArgumentException e) {
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        int successCount = ormRepository.countByLectureIdAndStatus(lectureId, "Y");

        assertThat(successCount).isEqualTo(30);  // 30명만 성공해야 함
        assertThat(failCount.get()).isEqualTo(10);  // 10명은 실패해야 함
    }

}
