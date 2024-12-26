package hhplus.tdd.interfaces.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureRequest {
    private String userId;
    private Long lectureId;
}
