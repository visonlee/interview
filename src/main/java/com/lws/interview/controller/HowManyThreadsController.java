package com.lws.interview.controller;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
  创建多少线程合适？ 《来自极客时间 Java并发编程实战 的创建多少线程才是合适的》
     分不同情况:
     1: 如果是CPU密集型的, 比如3D渲染,天气预报计算,复杂数学计算等,对于 CPU 密集型的计算场景，
     理论上“线程的数量 =CPU 核数”就是最合适的。不过在工程上，线程的数量一般会设置为“CPU 核数 +1”，这样的话
     当线程因为偶尔的内存页失效或其他原因导致阻塞时，这个额外的线程可以顶上，从而保证 CPU 的利用率。
     2:对于 I/O 密集型的计算场景，比如前面我们的例子中，如果 CPU 计算和 I/O 操作的耗时是 1:1，那么 2 个线程是最合适的。
       如果 CPU 计算和 I/O 操作的耗时是 1:2，那多少个线程合适呢？是 3 个线程，
       根据上面规律: 最佳线程数 = (1 +（I/O 耗时 / CPU 耗时）) * CPU核数
 */

/**
 * ab -n 100 -c 10 http://localhost:8080/hello #tldr ab
 */
@RestController
@Slf4j
public class HowManyThreadsController {

    @GetMapping("/threads")
    public String hello() throws InterruptedException {
        // 创建一个 StopWatch 实例
        StopWatch sw = new StopWatch("耗时测试");
        sw.start("CPU操作");

        cpu();
        sw.stop();
        System.out.printf("CPU操作：%d%s.\n", sw.getLastTaskTimeMillis(), "ms");

        sw.start("IO操作");

        io();

        sw.stop();
        System.out.printf("IO操作：%d%s.\n", sw.getLastTaskTimeMillis(), "ms");


        System.out.println(sw.prettyPrint());
        return "ok";
    }

    private void cpu() {
        int val = 0;
        int count = 2;
        while (count > 0) {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
               if (val > 100000) {
                   val = 0;
               } else {
                   val++;
               }
            }
            count--;
        }
    }

    private void io() {
        readFile("D:\\test.md");
    }

    public static void readFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
