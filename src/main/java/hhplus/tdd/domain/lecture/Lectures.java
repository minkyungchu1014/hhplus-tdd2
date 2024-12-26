package hhplus.tdd.domain.lecture;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@Table(name = "Lectures")
public class Lectures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String instructor;
    @Column(name = "lecture_date")
    private LocalDate lectureDate;
    private Integer capacity = 30;


    public boolean canApply(int currentApplications) {
        return currentApplications < capacity;
    }
}
