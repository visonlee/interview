package com.lws.interview.questions.blocking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 实现简单的阻塞队列
 */

/**
 * 错误动作示范,运行这段代码会报数组越界异常,这里演示了wait()方法放在if条件判断里面会导致异常,
 * 导致异常的最关键原因是,Object.wait()方法调用后,会释放锁,也就是释放CPU使用权,同时该线程会条件加入到等待队列中,而当调用了Object.notify(),会唤醒等待队列中的其中一个线程加入到入口就绪队列,具体是队列中的哪个线程是不确定的,具体参考Object代码的注释,使用了if语句判断,调用了wait方法后,
 * 线程被唤醒后,会继续从wait()语句后面的代码执行,而用了while语句,
 * 还会继续判断循环条件是否成立,如果成立会继续执行wait()方法继续等待
 *
 */
public class MySimpleBlockingQueue2<T> {

    private List<T> list = new ArrayList();

    private int MAX_SIZE = 10; // 最大只能10个元素

    private Lock lock = new ReentrantLock();
    Condition fullCondition = lock.newCondition();
    Condition emptyCondition = lock.newCondition();

    private boolean isEmpty() {
        return list.isEmpty();
    }

    private boolean isFull() {
        return list.size() >= MAX_SIZE;
    }

    public void put(T item) {
        //TODO 不能用if判断,而要用while循环判断
        lock.lock();
        try {
            while (isFull()) {
                System.out.println("队列已满");
                try {
                    fullCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            this.list.add(item);
            emptyCondition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public T take() {
        lock.lock();
        T item = null;
        try {
            //TODO 不能用if判断,而要用while循环判断
            while(isEmpty()) {
                System.out.println("队列为空");
                try {
                    emptyCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            item = list.remove(0);
            fullCondition.signalAll();
        } finally {
            lock.unlock();
        }

        return item;
    }

    public static void main(String[] args) {
        MySimpleBlockingQueue2<String> myQueue = new MySimpleBlockingQueue2();

        new Thread(() -> { //消费者线程1
            String take = myQueue.take();
            System.out.println("线程1拿到的数据为:" + take);
        }).start();

        new Thread(() -> {//消费者线程1

            String take = myQueue.take();
            System.out.println("线程2拿到的数据为:" + take);

        }).start();

        new Thread(() -> {//生产者线程睡3秒后产生数据

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + "生产者开始插入数据");
            myQueue.put("hello");

        },"生产者线程").start();
    }
}
