package skademy;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LectureStatusRepository extends CrudRepository<LectureStatus, Long> {

    List<LectureStatus> findByLectureId(Long lectureId);
    List<LectureStatus> findByLectureId(Long lectureId);

        void deleteByLectureId(Long lectureId);
}