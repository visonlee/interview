package com.lws.interview.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

//用户上报经纬度, 然后同时调用三家地图api获得对应的地址信息,谁先返回就用谁的数据
//优点: 实时性较高, 缺点: 耗费较多资源

@RestController
@RequestMapping("address")
@Slf4j
@RestControllerAdvice
public class AddressController {

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    //这种方法不是很好, 因为当其中一个请求完成时, 可以通知其他请求去取消，但是会有可能取消失败的
    @GetMapping("/bad-solution")
    public String badSolution(Double la, Double lo) {
        log.info("===============================");
        BlockingQueue<String> completionQueue = new LinkedBlockingQueue<>(1);

        Future<String> gaodeFuture = executorService.submit(() -> getAddressFromGaode(la, lo));
        Future<String> baiduFuture = executorService.submit(() -> getAddressFromBaidu(la, lo));
        Future<String> tencentFuture = executorService.submit(() -> getAddressFromTencent(la, lo));

        executorService.execute(() -> {
            try {
                completionQueue.put(gaodeFuture.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.execute(() -> {
            try {
                completionQueue.put(baiduFuture.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.execute(() -> {
            try {
                completionQueue.put(tencentFuture.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        String result = "地址解析失败";

        try {
            result = completionQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            gaodeFuture.cancel(true);
            baiduFuture.cancel(true);
            tencentFuture.cancel(true);
        }
        return result;
    }
    @GetMapping("/good-solution")
    public String goodSolution(Double la, Double lo) {

        log.info("===============================");

        // 创建 CompletionService
        LinkedBlockingQueue<Future> completionQueue = new LinkedBlockingQueue<>(); //完成的任务会被丢到这里
        CompletionService<String> cs = new ExecutorCompletionService(executorService, completionQueue);
       // 用于保存 Future 对象
        List<Future<String>> futures = new ArrayList<>(3);
        // 提交异步任务，并保存 future 到 futures
        futures.add(cs.submit(()->getAddressFromGaode(la, lo)));
        futures.add(cs.submit(()->getAddressFromBaidu(la, lo)));
        futures.add(cs.submit(()->getAddressFromTencent(la, lo)));
        // 获取最快返回的任务执行结果
        String result = "地址解析失败";

        try {
            result = cs.take().get();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            for(Future<String> f : futures) {
                f.cancel(true);
            }
        }

        return result;
    }

    private String getAddressFromGaode(Double la, Double lo) {
        log.info("高德开始查询...");
        int number = generateRandomNumber(2000);
        try {
            TimeUnit.MILLISECONDS.sleep(number);
        } catch (InterruptedException e) {
            log.info("高德执行被打断了");
            throw new CancellationException("高德查询被打断了");
        }
        log.info("高德查询耗时: {}", number);
        return "高德查出来的地址";
    }

    private String getAddressFromBaidu(Double la, Double lo) {
        log.info("百度开始查询...");
        int number = generateRandomNumber(2000);
        try {
            TimeUnit.MILLISECONDS.sleep(number);
        } catch (InterruptedException e) {
            log.info("百度执行被打断了");
            throw new CancellationException("百度查询被打断了");
        }
        log.info("百度查询耗时: {}", number);
        return "百度查出来的地址";
    }

    private String getAddressFromTencent(Double la, Double lo) {
        log.info("腾讯开始查询...");
        int number = generateRandomNumber(2000);
        try {
            TimeUnit.MILLISECONDS.sleep(number);
        } catch (InterruptedException e) {
            log.info("腾讯执行被打断了");
            throw new CancellationException("腾讯查询被打断了");
        }
        log.info("腾讯查询耗时: {}", number);
        return "腾讯查出来的地址";
    }

    public static int generateRandomNumber(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }
}