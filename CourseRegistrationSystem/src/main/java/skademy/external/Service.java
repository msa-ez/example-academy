
package skademy.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * Created by uengine on 2018. 11. 21..
 */

@FeignClient(name="", url="http://:8080")
public interface Service {

    @RequestMapping(method= RequestMethod.POST, path="/")
    public void makePayment(@RequestBody  );

}