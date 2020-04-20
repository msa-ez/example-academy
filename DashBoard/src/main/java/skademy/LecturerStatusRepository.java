package skademy;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LecturerStatusRepository extends CrudRepository<LecturerStatus, Long> {


}