package com.bane.asynch.util;

public class RunnableExample implements Runnable {

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName());
            System.out.println("Let's sleep");
            Thread.sleep(2000);
            System.out.println("Sleep is over");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName());
        RunnableExample runnableExample = new RunnableExample();
        //no creation of a new thread, it will just run on the thread which is calling.
        runnableExample.run();

        System.out.println(Thread.currentThread().getName());
        RunnableExample runnableExample1 = new RunnableExample();
        Thread thread = new Thread(runnableExample);
        //thread.start() a new thread will be created.
        thread.start();
    }
}
