package com.bane.asynch.util;

public class DemoExample {

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName());
        RunnableExample runnableExample = new RunnableExample();
        runnableExample.run();
    }
}
