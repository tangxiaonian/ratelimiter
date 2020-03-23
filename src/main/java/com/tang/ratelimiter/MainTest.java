package com.tang.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @Classname MainTest
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/3/22 19:22
 * @Created by ASUS
 */
public class MainTest {

    private final static Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) {



    }

    @Test
    public void test02(){

        // 桶容量为5 每秒生成5个令牌
        RateLimiter limiter = RateLimiter.create(5);

        // 打印取到令牌的时间
        System.out.println(limiter.acquire(1));
        System.out.println(limiter.acquire(1));

        // 能取到，等待时间推迟到下一次获取
        System.out.println(limiter.acquire(10));

        System.out.println(limiter.acquire(10));

    }


    @Test
    public void test01(){

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {

            executorService.execute(new TestThread(semaphore, i + 1));

        }

        executorService.shutdown();
    }

}

class TestThread implements Runnable{

    private Semaphore semaphore = null;

    private Integer number = 0;

    TestThread(Semaphore semaphore,Integer number) {
        this.semaphore = semaphore;
        this.number = number;
    }

    @Override
    public void run() {

        try {
            semaphore.acquire();

            System.out.println( "业务开始....." + number );

            TimeUnit.SECONDS.sleep(5);

            System.out.println( "业务结束....."+ number  );

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            semaphore.release();
        }

    }
}