package com.lws.interview.questions;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 三个线程分别打印三个字符'A','B','C',要求顺序是按照ABC打印, 使用多种方法实现
 */
public class ThreadPrintString {

    public static void main(String[] args) {
        m5();
    }

    // synchronized + wait/notify
    private static void m1() {

        Object objA = new Object();
        Object objB = new Object();
        Object objC = new Object();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
               printString("A", objA, objB);
            }
        }, "t1");

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                printString("B", objB, objC);
            }
        }, "t2");

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                printString("C", objC, objA);
            }
        }, "t3");

        t1.start();
        t2.start();
        t3.start();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (objA) {
            objA.notify();
        }

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printString(String str, Object current, Object next) {
        synchronized (current) {
            try {
                current.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(str);
            synchronized (next) {
                next.notify();
            }
        }
    }

    // Lock api + Condition
    private static void m2() {
        Lock lock = new ReentrantLock();
        Condition c1 = lock.newCondition();
        Condition c2 = lock.newCondition();
        Condition c3 = lock.newCondition();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                printString("A",lock, c1, c2);
            }
        }, "t1");

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                printString("B", lock, c2, c3);
            }
        }, "t2");

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                printString("C", lock, c3, c1);
            }
        }, "t3");

        t1.start();
        t2.start();
        t3.start();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            lock.lock();
            c1.signal();
        }finally {
            lock.unlock();
        }

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printString(String str,Lock lock, Condition current, Condition next) {
        try {
            lock.lock();
            current.await();
            System.out.println(str);
            next.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    static class PrintThread extends Thread {
        private String text;
        private Thread nextRunThread;

        @Override
        public void run() {
            LockSupport.park();
            System.out.println(this.text);
            LockSupport.unpark(nextRunThread);
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Thread getNextRunThread() {
            return nextRunThread;
        }

        public void setNextRunThread(Thread nextRunThread) {
            this.nextRunThread = nextRunThread;
        }
    }

    // LockSupport
    private static void m3() {

        PrintThread t1 = new PrintThread();
        PrintThread t2 = new PrintThread();
        PrintThread t3 = new PrintThread();

        t1.setText("A");
        t1.setName("T1");
        t1.setNextRunThread(t2);

        t2.setText("B");
        t2.setName("T2");
        t2.setNextRunThread(t3);

        t3.setText("C");
        t3.setName("T3");
        t3.setNextRunThread(t1);

        t1.start();
        t2.start();
        t3.start();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LockSupport.unpark(t1);

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //防止循环引用
        t1.setNextRunThread(null);
        t2.setNextRunThread(null);
        t3.setNextRunThread(null);
    }


    // Thread.join()
    private static void m4() {
        Thread t1 = new Thread(() -> {
            System.out.println("A");
        });

        Thread t2 = new Thread(() -> {
            try {
                t1.join(); // Wait for T1 to complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Code for thread T2
            System.out.println("B");
        });

        Thread t3 = new Thread(() -> {
            try {
                t2.join(); // Wait for T2 to complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Code for thread T3
            System.out.println("C");
        });

        t1.start();
        t2.start();
        t3.start();

    }

//    CountDownLatch
    private static void m5() {
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            // Code for thread T1
            System.out.println("A");
            latch1.countDown(); // Signal that T1 has completed
        });

        Thread t2 = new Thread(() -> {
            try {
                latch1.await(); // Wait for T1 to complete
                // Code for thread T2
                System.out.println("B");
                latch2.countDown(); // Signal that T2 has completed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread t3 = new Thread(() -> {
            try {
                latch2.await(); // Wait for T2 to complete
                // Code for thread T3
                System.out.println("C");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }

}
