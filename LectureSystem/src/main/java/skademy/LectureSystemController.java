package skademy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

 @RestController
 public class LectureSystemController {

@RequestMapping(value = "/",
        method = RequestMethod.DELETE,
        produces = "application/json;charset=UTF-8")

public void lectureCancellation(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        System.out.println("##### /lectureSystem/lectureCancellation  called #####");
        }

@RequestMapping(value = "/",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8")

public void lectureOpen(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        System.out.println("##### /lectureSystem/lectureOpen  called #####");
        }
 }
