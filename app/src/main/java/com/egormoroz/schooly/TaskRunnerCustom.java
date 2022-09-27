package com.egormoroz.schooly;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunnerCustom {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback<FacePart> {
        void onComplete(FacePart result);
    }

    public <Clothes> void executeAsync(Callable<FacePart> callable, TaskRunner.Callback<FacePart> callback) {
        executor.execute(() -> {
            FacePart result = null;
            try {
                result = callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FacePart finalResult = result;
            handler.post(() -> {
                try {
                    callback.onComplete(finalResult);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
