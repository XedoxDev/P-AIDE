package com.xedox.paide.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskExecutor {
    private static ExecutorService es = Executors.newSingleThreadExecutor();
    
    public static void execute(ExexutorCode ec) {
        Future<?> f = es.submit(ec::start);
    }
    
    public static interface ExexutorCode {
        void start();
    }
}
