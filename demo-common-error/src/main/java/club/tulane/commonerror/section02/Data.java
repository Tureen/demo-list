package club.tulane.commonerror.section02;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

@RestController
@RequestMapping("/data")
public class Data {

    @GetMapping("wrong")
    public int wrong(@RequestParam(value = "count", defaultValue = "1000000") int count){
        Data.reset();
        // 多线程循环一定次数调用 Data 类不同实例的 wrong 方法
        IntStream.rangeClosed(1, count).parallel().forEach(i -> new Data().wrong());
        return Data.getCounter();
    }

    @GetMapping("right")
    public int right(@RequestParam(value = "count", defaultValue = "1000000") int count){
        Data.reset();
        // 多线程循环一定次数调用 Data 类不同实例的 wrong 方法
        IntStream.rangeClosed(1, count).parallel().forEach(i -> new Data().right());
        return Data.getCounter();
    }

    @Getter
    private static int counter = 0;

    public static int reset(){
        counter = 0;
        return counter;
    }

    public synchronized void wrong(){
        counter++;
    }

    private static Object locker = new Object();

    /**
     * 类级别锁
     */
    public void right(){
        synchronized (locker){
            counter++;
        }
    }
}
