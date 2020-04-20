package skademy;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="LectureSystem_table")
public class LectureSystem {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String lectureName;
    private Integer studentNumber;

    @PostPersist
    public void onPostPersist(){
        LectureOpened lectureOpened = new LectureOpened();
        BeanUtils.copyProperties(this, lectureOpened);
        lectureOpened.publish();


    }

    @PostRemove
    public void onPostRemove(){
        OpenedCanceled openedCanceled = new OpenedCanceled();
        BeanUtils.copyProperties(this, openedCanceled);
        openedCanceled.publish();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }
    public Integer getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }




}
