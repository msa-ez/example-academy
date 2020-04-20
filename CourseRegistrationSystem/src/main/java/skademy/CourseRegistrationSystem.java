package skademy;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="CourseRegistrationSystem_table")
public class CourseRegistrationSystem {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long lectureId;
    private Integer studentId;
    private String status;

    @PostPersist
    public void onPostPersist(){
        CourseRegistered courseRegistered = new CourseRegistered();
        BeanUtils.copyProperties(this, courseRegistered);
        courseRegistered.publish();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        skademy.external.CourseRegistrationSystem courseRegistrationSystem = new skademy.external.CourseRegistrationSystem();
        // mappings goes here
        Application.applicationContext.getBean(skademy.external.CourseRegistrationSystemService.class)
            .makePayment(courseRegistrationSystem);


    }

    @PostUpdate
    public void onPostUpdate(){
        CourseRegistrationCompleted courseRegistrationCompleted = new CourseRegistrationCompleted();
        BeanUtils.copyProperties(this, courseRegistrationCompleted);
        courseRegistrationCompleted.publish();


    }

    @PostRemove
    public void onPostRemove(){
        CourseCanceled courseCanceled = new CourseCanceled();
        BeanUtils.copyProperties(this, courseCanceled);
        courseCanceled.publish();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getLectureId() {
        return lectureId;
    }

    public void setLectureId(Long lectureId) {
        this.lectureId = lectureId;
    }
    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
