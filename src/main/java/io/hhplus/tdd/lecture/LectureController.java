package io.hhplus.tdd.lecture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lecture")
public class LectureController {

    private static final Logger logger = LoggerFactory.getLogger(LectureController.class);
    private LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }




}
