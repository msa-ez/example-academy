package skademy;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="LecturerStatus_table")
public class LecturerStatus {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

}
