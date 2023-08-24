package com.lws.interview.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class Allocator {
    private static /*volatile*/ Allocator instance; // 这里不用volatile,新版JDK保证了new Allocator()操作是原子性

    private List<Object> resources = new ArrayList<>();

    private Allocator() {}

    public static Allocator getInstance() {
        if (instance == null) {
            synchronized (Allocator.class) {
                if (instance == null) {
                    instance = new Allocator();
                }
            }
        }
        return instance;
    }

    public boolean apply(Object from, Object to) {
        synchronized (this) {
            if (resources.contains(from) || resources.contains(to)) {
                return false;
            }else {
                resources.add(from);
                resources.add(to);
                return true;
            }
        }
    }

    public void free(Object from, Object to) {
        synchronized (this) {
            resources.remove(from);
            resources.remove(to);
        }
    }
}

class Account {
    private long id;
    private long balance;

    private final Lock lock = new ReentrantLock();

    public Account(long id, long amount) {
        this.id = id;
        this.balance = amount;
    }

    // concurrency issue
    public void transfer_wrong1(Account target, long amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            target.balance += amount;
        }
    }

    // one lock can't protect 2 resources
    public void transfer_wrong2(Account target, long amount) {
        synchronized (this) {
            if (this.balance >= amount) {
                this.balance -= amount;
                target.balance += amount;
            }
        }
    }

    // may cause dead lock
    public void transfer_wrong3(Account target, long amount) {
        synchronized (this) {
            synchronized (target) {
                if (this.balance >= amount) {
                    this.balance -= amount;
                    target.balance += amount;
                }
            }
        }
    }

    // correct, but with bad performance, all the operation will sequentially
    public void transfer_bad_performance(Account target, long amount) {
        synchronized (Account.class) {
            if (this.balance >= amount) {
                this.balance -= amount;
                target.balance += amount;
            }
        }
    }

    // 个人认为这种方案是最好的,性能好, 并且容易理解
    public void transfer_correct1(Account target, long amount) {
        Account left = this;
        Account right = target;
        // reorder to avoid dead lock
        if(left.getId() > right.getId()) {
            left = target;
            right = this;
        }
        synchronized (left) {
            synchronized (right) {
                if (this.balance >= amount) {
                    this.balance -= amount;
                    target.balance += amount;
                }
            }
        }
    }

    public void transfer_correct2(Account target, long amount) {
        //do not use synchronized keyword, instead use the Lock api from JDK
        boolean tryLock1 = this.lock.tryLock();
        boolean tryLock2 = target.lock.tryLock();
        if(tryLock1 && tryLock2) {
            try {
                if (this.balance >= amount) {
                    this.balance -= amount;
                    target.balance += amount;
                }
            } finally {
                this.lock.unlock();
                target.lock.unlock();
            }
        } else {
            //log error
            System.out.println("transfer error");
        }
    }

    public void transfer_correct3(Account target, long amount) {
        //do not use synchronized keyword, instead use the Lock api from JDK
        boolean tryLock1 = false;
        boolean tryLock2 = false;
        try {
            tryLock1 = this.lock.tryLock(2000, TimeUnit.MILLISECONDS);
            tryLock2 = target.lock.tryLock(2000, TimeUnit.MILLISECONDS);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }

        if(tryLock1 && tryLock2) {
            try {
                if (this.balance >= amount) {
                    this.balance -= amount;
                    target.balance += amount;
                }
            } finally {
                this.lock.unlock();
                target.lock.unlock();
            }
        } else {
            //log error
            System.out.println("transfer timeout error");
            if(tryLock1) {
                this.lock.unlock();
            }
            if(tryLock2) {
                target.lock.unlock();
            }
        }
    }

    public void transfer_correct4(Account target, long amount) {
        // 一次性申请转出账户和转入账户，直到成功, 不过看起来有点绕
        while(!Allocator.getInstance().apply(this, target)); // 死循环了,这里最好有超时的设计,
        try{
            // 锁定转出账户
            synchronized(this){
                // 锁定转入账户
                synchronized(target){
                    if (this.balance >= amount) {
                        this.balance -= amount;
                        target.balance += amount;
                    }
                }
            }
        } finally {
            Allocator.getInstance().free(this, target);
        }
    }

    public long getId() {
        return id;
    }

    public long getBalance() {
        return balance;
    }
}

// 死锁产生原因分析和解决办法
public class DeadLockSample {

    public static int generateRandomNumber(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }

    public static void main(String[] args) {

        Account account1 = new Account(1,1000000);
        Account account2 = new Account(2, 1000000);
        Account account3 = new Account(3, 1000000);

        List<Account> accounts = Arrays.asList(account1, account2, account3);

        int threadCount = 200;
        List<Thread> threads = new ArrayList<>(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Account accountFrom = accounts.get(generateRandomNumber(3));
                    Account accountTo = accounts.get(generateRandomNumber(3));
                    if (accountFrom != accountTo) {
                        accountFrom.transfer_correct1(accountTo, 100);
                    }
                }
            }, "myThread" + i);
            threads.add(thread);
        }

        for (int i = 0; i < threadCount; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String msg = "account1: " + account1.getBalance() + ", account2: " + account2.getBalance() + ", account3: " + account3.getBalance() +
                ", total: " + (account1.getBalance() + account2.getBalance() + account3.getBalance());
        System.out.println(msg);
    }
}


