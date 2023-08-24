package com.lws.interview.questions.blocking;

import java.util.ArrayList;
import java.util.List;

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
public class MySimpleBlockingQueue1<T> {

    private List<T> list = new ArrayList();

    private int MAX_SIZE = 10; // 最大只能10个元素

    private boolean isEmpty() {
        return list.isEmpty();
    }

    private boolean isFull() {
        return list.size() >= MAX_SIZE;
    }

    public synchronized void put(T item) {
        //TODO 不能用if判断,而要用while循环判断
        while (isFull()) {
            System.out.println("队列已满");
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        this.list.add(item);
        this.notifyAll();
    }

    public synchronized T take() {
        //TODO 不能用if判断,而要用while循环判断
        while(isEmpty()) {
            System.out.println("队列为空");
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        T item = list.remove(0);
        this.notifyAll();
        return item;
    }



    public static void main(String[] args) {
        MySimpleBlockingQueue1<String> myQueue = new MySimpleBlockingQueue1();

/**
 * 一个生产者线程,两个消费者线程
 * 当我开启了两个消费者线程,和一个生产者线程(睡眠3秒后生产数据),一开始没有书队列为空,
 * 消费者拿数据(执行take方法),
 *  条件判断队列为空,所以两个消费者线程都进入wait状态。三秒过后, 生产者线程产生了数据,
 * 无论生产者线程调用notify还是notifyAll方法,都会产生异常。
 *  情况一😳:当生产者线程调用notify,生产者会随机唤醒其中一个消费者线程,
 *          如果使用了if来判断,线程被唤醒后会继续执行wait后面的删除数据的方法,
 *          然后执行notify()方法,此时该消费者线程会唤醒另外一个正在wait的消费者线程,
 *          被该消费者唤醒的另外一个消费者线程,被唤醒后会继续执行wait
 *          后面的删除数据的方法,这个时候就完蛋了,因为数组为空,会产生越界
 *
 *  情况二😳:当生产者线程调用notifyAll,生产者会唤醒其中所有的正在wait的者线程,
 *          如果使用了if来判断,两个同时被唤醒的消费者线程会继续执行wait后面的删除数据的方法,
 *          此时数据元素只有一个执行两次删除操作,产生越界异常
 */
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

        }).start();
    }
}
