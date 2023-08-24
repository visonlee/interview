package com.lws.interview.questions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockCache<K,V> {

    private Map<K, V> cacheMap = new HashMap<>();
    private ReadWriteLock rwl = new ReentrantReadWriteLock();
    private Lock rl = rwl.readLock();
    private Lock wl = rwl.writeLock();

    public void put(K key, V value) {
        wl.lock();
        try {
            cacheMap.put(key, value);
        } finally {
            wl.unlock();
        }
    }

    public V get(K key) {
        rl.lock();
        try {
            return cacheMap.get(key);
        } finally {
            rl.unlock();
        }
    }


    public V lazyGet(K key) {
        V value = null;
        rl.lock();
        try {
            value = cacheMap.get(key);
        } finally {
            rl.unlock();
        }

        if (value != null) {
            return value;
        }

        wl.lock();
        try {
            value = cacheMap.get(key);
            if ( value == null) {
                value = readFromDb();
                cacheMap.put(key, value);
            }
        }finally {
            wl.unlock();
        }
        return value;
    }

    private V readFromDb() {
        return null;
    }
}
