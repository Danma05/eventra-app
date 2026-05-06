package com.eventra.mobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {

    public static void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return;
        }

        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}