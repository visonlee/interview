package com.lws.interview.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@RestController
@RequestMapping("/health-check")
@Slf4j
public class HealthCheckReportController {

    Executor executor = Executors.newFixedThreadPool(3);

    @GetMapping("/report")
    public String report() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(3);

        //CRM 获取customer info  -> customer risk 分析
        final String[] customerInfo = {null};
        final String[] customerRiskInfo = {null};
        final String[] accountInfo = {null};
        final String[] holdingInfo = {null};
        final String[] assetAnalysisInfo = {null};
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                customerInfo[0] = "客户信息";

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                customerRiskInfo[0] = "风险等级信息";
                latch.countDown();
            }
        });

        //account service 获取账号信息
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                accountInfo[0] = "账号信息";
                latch.countDown();
            }
        });

        //dashboard service 获取holding信息  -> asset allocation 分析
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                holdingInfo[0] = "持仓信息";

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assetAnalysisInfo[0] = "持仓分析";
                latch.countDown();
            }
        });

        latch.await();

        // 根据上面的内容生成pdf文档
        String s = genPdf(customerInfo[0], customerRiskInfo[0], accountInfo[0], holdingInfo[0], assetAnalysisInfo[0]);
        // 异步保存一份到document center
        executor.execute(new Runnable() {
            @Override
            public void run() {
                savePdf("财产健康检查信息");
            }
        });

        // 返回pdf 给前端

        return s;
    }
    @GetMapping("/report-v2")
    public String reportV2() {
        CompletableFuture<String> f1 =
                CompletableFuture.supplyAsync(() -> {
                    log.info("获取客户信息");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "客户名: 张三丰";
                }).thenApply(ret -> {
                    log.info("根据客户信息获取风险等级信息");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return ret + ", 客户风险等级: 5";
                });

        CompletableFuture<String> f2 =
                CompletableFuture.supplyAsync(() -> {
                    log.info("获取账户信息");
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "账户信息: 10086";
                });

        CompletableFuture<String> f3 =
                CompletableFuture.supplyAsync(() -> {
                    log.info("持仓信息");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "持仓信息: 你有很多钱";
                }).thenApply(ret -> {
                    log.info("根据持仓信息来分析客户的财产健康状况");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return ret + ", 财产健康状况: 优秀";
                });

        CompletableFuture<String> f4 = f3
                .thenCombine(f2, (cur, tf) -> "\n" + cur + "\n" + tf).
                thenCombine(f1, (cur, tf) -> cur + "\n" + tf)
                .thenApply(s -> {
                    log.info("根据内容生成pdf文件");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                })
                .thenApplyAsync(combineResult -> {
                    log.info("保存到数据库中去: {}", combineResult);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return combineResult;
                });

        return f4.join();
    }

    private String genPdf(String customerInfo, String customerRiskInfo, String accountInfo, String holdingInfo, String assetAnalysis) {
        return "customerInfo:" + customerInfo + "\n" +
                "customerRiskInfo: " + customerRiskInfo + "\n" +
                "accountInfo: " + accountInfo + "\n" +
                "holdingInfo: " + holdingInfo + "\n" +
                "assetAnalysis: " + assetAnalysis;
    }

    private void savePdf(String s) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("保存完成了: " + s);
    }
}
