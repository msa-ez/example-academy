package skademy;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRegistrationStatusRepository extends CrudRepository<CourseRegistrationStatus, Long> {

    List<CourseRegistrationStatus> findByCourseStatus(String courseStatus);

        void deleteByCourseId(Long courseId);
}