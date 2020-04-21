package skademy;

import skademy.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class LectureStatusViewHandler {


    @Autowired
    private LectureStatusRepository lectureStatusRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenLectureOpened_then_CREATE_1 (@Payload LectureOpened lectureOpened) {
        try {
            if (lectureOpened.isMe()) {
                // view 객체 생성
                LectureStatus lectureStatus = new LectureStatus();
                // view 객체에 이벤트의 Value 를 set 함
                lectureStatus.setLectureId(lectureOpened.getId);
                // view 레파지 토리에 save
                lectureStatusRepository.save(lectureStatus);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenCourseRegistrationCompleted_then_UPDATE_1(@Payload CourseRegistrationCompleted courseRegistrationCompleted) {
        try {
            if (courseRegistrationCompleted.isMe()) {
                // view 객체 조회
                List<LectureStatus> lectureStatusList = lectureStatusRepository.findByLectureId(courseRegistrationCompleted.getLectureId);
                for(LectureStatus lectureStatus : lectureStatusList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    lectureStatus.setLectureStatus(courseRegistrationCompleted.getStatus);
                    // view 레파지 토리에 save
                    lectureStatusRepository.save(lectureStatus);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenCourseCanceled_then_UPDATE_2(@Payload CourseCanceled courseCanceled) {
        try {
            if (courseCanceled.isMe()) {
                // view 객체 조회
                List<LectureStatus> lectureStatusList = lectureStatusRepository.findByLectureId(courseCanceled.getLectureId);
                for(LectureStatus lectureStatus : lectureStatusList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    lectureStatus.setLectureStatus(courseCanceled.getStatus);
                    // view 레파지 토리에 save
                    lectureStatusRepository.save(lectureStatus);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOpenedCanceled_then_DELETE_1(@Payload OpenedCanceled openedCanceled) {
        try {
            if (openedCanceled.isMe()) {
                // view 레파지 토리에 삭제 쿼리
                lectureStatusRepository.deleteByLectureId(openedCanceled.getId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}