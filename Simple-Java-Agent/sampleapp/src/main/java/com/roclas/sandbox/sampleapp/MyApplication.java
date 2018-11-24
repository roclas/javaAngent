package com.roclas.sandbox.sampleapp;


public class MyApplication {
    public static void main(String[] args) {
        long counter = 0l;
        while (!Thread.interrupted()) {
            try {
                System.out.println(counter++ % 2 == 0 ? "Tick" : "Tock");
                Thread.sleep(1000l);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}