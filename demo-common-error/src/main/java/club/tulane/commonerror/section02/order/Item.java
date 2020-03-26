package club.tulane.commonerror.section02.order;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReentrantLock;

@Data
@RequiredArgsConstructor
public class Item {

    final String name; // 商品名
    int remaining = 1000; // 库存剩余

    ReentrantLock lock = new ReentrantLock();
}
