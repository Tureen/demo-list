package club.tulane.commonerror;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/test1")
    public String test1() throws InterruptedException {
        Thread.sleep(1000);
        return "";
    }

    @GetMapping("/test2")
    public String test2() throws InterruptedException {
        Thread.sleep(500);
        return "";
    }

    @GetMapping("/test3")
    public String test3() throws InterruptedException {
        Thread.sleep(200);
        return "";
    }

    @GetMapping("/test4")
    public String test4() throws InterruptedException {
        Thread.sleep(100);
        return "";
    }
}
