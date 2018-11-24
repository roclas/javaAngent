package com.roclas.sandbox.simpleagent;

import java.lang.instrument.Instrumentation;

public class MyAgent {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("premain:Starting Agent");
    }

    public static void agentmain(String agentArgs, Instrumentation inst){
        System.out.println("agentmain: Starting Agent");
        
        Thread th = new Thread(){
        	@Override
        	public void run() {
        		super.run();
        		while(true){
        			System.out.println("running code");
        			try { 
        				sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
        	}
        };

        th.start();
        
    }

}
