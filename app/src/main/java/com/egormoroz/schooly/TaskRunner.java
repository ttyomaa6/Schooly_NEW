package com.egormoroz.schooly;

import android.os.Handler;
import android.os.Looper;

import com.egormoroz.schooly.ui.profile.Wardrobe.WardrobeFragment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback<Clothes> {
        void onComplete(Clothes result) throws IOException, URISyntaxException;
    }

    public <Clothes> void executeAsync(Callable<Clothes> callable, TaskRunner.Callback<Clothes> callback) {
        executor.execute(() -> {
            Clothes result = null;
            try {
                result = callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Clothes finalResult = result;
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
