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
public class CourseRegistrationStatusViewHandler {


    @Autowired
    private CourseRegistrationStatusRepository courseRegistrationStatusRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCourseRegistered_then_CREATE_1 (@Payload CourseRegistered courseRegistered) {
        try {
            if (courseRegistered.isMe()) {
                // view 객체 생성
                CourseRegistrationStatus courseRegistrationStatus = new CourseRegistrationStatus();
                // view 객체에 이벤트의 Value 를 set 함
                courseRegistrationStatus.setCourseId(courseRegistered.getId);
                // view 레파지 토리에 save
                courseRegistrationStatusRepository.save(courseRegistrationStatus);
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
                List<CourseRegistrationStatus> courseRegistrationStatusList = courseRegistrationStatusRepository.findByCourseStatus(courseRegistrationCompleted.getStatus);
                for(CourseRegistrationStatus courseRegistrationStatus : courseRegistrationStatusList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    courseRegistrationStatus.setCourseId(courseRegistrationCompleted.getId);
                    // view 레파지 토리에 save
                    courseRegistrationStatusRepository.save(courseRegistrationStatus);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCourseCanceled_then_DELETE_1(@Payload CourseCanceled courseCanceled) {
        try {
            if (courseCanceled.isMe()) {
                // view 레파지 토리에 삭제 쿼리
                courseRegistrationStatusRepository.deleteByCourseId(courseCanceled.getId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}