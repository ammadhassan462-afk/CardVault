package com.example.cardvault;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for managing app-private internal storage for card photos.
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static final String CARDS_DIR = "cards";

    /**
     * Copies an image from a content URI into the app's private files directory.
     * @return the relative filename/path (e.g. "cards/card_1698765432.jpg"), or null on failure.
     */
    public static String copyUriToInternalStorage(Context context, Uri uri) {
        if (context == null || uri == null) return null;

        try {
            File dir = new File(context.getFilesDir(), CARDS_DIR);
            if (!dir.exists() && !dir.mkdirs()) {
                Log.e(TAG, "Failed to create cards directory");
                return null;
            }

            String fileName = "card_" + System.currentTimeMillis() + ".jpg";
            File destinationFile = new File(dir, fileName);

            try (InputStream in = context.getContentResolver().openInputStream(uri);
                 OutputStream out = new FileOutputStream(destinationFile)) {
                if (in == null) return null;
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }

            return CARDS_DIR + File.separator + fileName;
        } catch (Exception e) {
            Log.e(TAG, "Error copying image to internal storage", e);
            return null;
        }
    }

    /**
     * Resolves a relative path to a local File in the app's files directory.
     */
    public static File getFileFromRelativePath(Context context, String relativePath) {
        if (context == null || relativePath == null || relativePath.trim().isEmpty()) {
            return null;
        }
        File file = new File(context.getFilesDir(), relativePath);
        return file.exists() ? file : null;
    }

    /**
     * Safely loads a bitmap from a relative path with subsampling to prevent OOM errors.
     */
    public static Bitmap loadBitmap(Context context, String relativePath, int reqWidth, int reqHeight) {
        File file = getFileFromRelativePath(context, relativePath);
        if (file == null) return null;

        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        } catch (Throwable t) {
            Log.e(TAG, "Error decoding bitmap from " + relativePath, t);
            return null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Deletes a single image file if it exists.
     */
    public static boolean deleteImage(Context context, String relativePath) {
        if (context == null || relativePath == null) return false;
        try {
            File file = new File(context.getFilesDir(), relativePath);
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting image file", e);
        }
        return false;
    }

    /**
     * Deletes all files in the cards directory.
     */
    public static void deleteAllStoredImages(Context context) {
        if (context == null) return;
        try {
            File dir = new File(context.getFilesDir(), CARDS_DIR);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        //noinspection ResultOfMethodCallIgnored
                        f.delete();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting all card photos", e);
        }
    }
}
