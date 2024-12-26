package hhplus.tdd.domain.lecture;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "lecture_applications", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "lecture_id"}))
public class LectureApplications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "lecture_id")
    private Long lectureId;

    private String status; // 'Y' or 'N'

}


