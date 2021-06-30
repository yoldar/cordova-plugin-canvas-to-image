package org.devgeeks.Canvas2ImagePlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.json.JSONArray;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

public class Canvas2ImagePlugin extends CordovaPlugin {
    public static final String ACTION = "saveImageDataToLibrary";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals(ACTION)) {
            return save(args, callbackContext);
        } else {
            return false;
        }
    }

    private Boolean save(JSONArray args, CallbackContext callbackContext) {
        String base64 = args.optString(0);
        String filename = args.optString(1);
        String quality = args.optString(2);
        String picfolder = args.optString(3);

        if (base64.isEmpty()) callbackContext.error("Missing base64 string");

        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        if (bmp == null) {
            callbackContext.error("The image could not be decoded");
        } else {
            Boolean saved = saveImage(bmp, filename, quality, picfolder);
            if (!saved) callbackContext.error("Error while saving image");

            callbackContext.success();
        }
        return true;
    }

    private int getQuality(String strQuality) {
        int result = 100;
        try {
            result = Integer.valueOf(strQuality);
            if (result > 100) result = 100;
            if (result < 1) result = 1;
        } catch (Exception e) {
        }
        return result;
    }

    private Boolean saveImage(Bitmap bitmap, String name, String strQuality, String picfolder) {
        int quality = getQuality(strQuality);
        boolean saved = false;
        OutputStream fos;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = cordova.getContext().getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + picfolder);
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(imageUri);
                bitmap.compress(CompressFormat.JPEG, quality, fos);
                fos.flush();
                fos.close();
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + picfolder;

                File file = new File(imagesDir);

                if (!file.exists()) {
                    file.mkdir();
                }

                File image = new File(imagesDir, name + ".jpg");
                fos = new FileOutputStream(image);
                bitmap.compress(CompressFormat.JPEG, quality, fos);
                fos.flush();
                fos.close();
                scanPhoto(image);
            }
            saved = true;
        } catch (Exception e) {
            Log.e("Canvas2ImagePlugin", "An exception occured while saving image: " + e.toString());
        }

        return saved;
    }

    private void scanPhoto(File imageFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        cordova.getActivity().sendBroadcast(mediaScanIntent);
    }
}
