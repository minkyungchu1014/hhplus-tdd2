package hhplus.tdd.interfaces.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class LectureResponse {
    private Long id;
    private String name;
    private String instructor;
    private LocalDate lectureDate;
    //특강 정원이 30명이므로 int로 결정
    private int remainingSlots;
}
