package org.devgeeks.Canvas2ImagePlugin;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class Canvas2ImagePlugin extends CordovaPlugin {
    public static final String ACTION = "saveImageDataToLibrary";
    private static final int REQ_CODE = 9000;
    private static final String STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private JSONArray requestArgs;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {

        this.callbackContext = callbackContext;
        this.requestArgs = args;

        if (action.equals(ACTION)) {
            return save(args);
        } else {
            return false;
        }
    }

    private Boolean save(JSONArray args) {
        String base64 = args.optString(0);
        String filename = args.optString(1);
        String quality = args.optString(2);
        String picfolder = args.optString(3);
        Boolean add2Galery = Boolean.valueOf(args.optString(4));

        if (base64.isEmpty()) callbackContext.error("Missing base64 string");

        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        if (bmp == null) {
            callbackContext.error("The image could not be decoded");
        } else {
            if(cordova.hasPermission(STORAGE)) {
                File imageFile = savePhoto(bmp, filename, quality, picfolder);
                if (imageFile == null)
                    callbackContext.error("Error while saving image");

                if (add2Galery) scanPhoto(imageFile);

                Log.i("Canvas2ImagePlugin", "imageFile.toString(): " + imageFile.toString());

                callbackContext.success(imageFile.toString());
            } else {
                cordova.requestPermission(this, REQ_CODE, STORAGE);
            }
        }
        return true;
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        for(int r:grantResults) {
            if(r == PackageManager.PERMISSION_DENIED) {
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "No permission"));
                return;
            }
        }
        if (requestCode == REQ_CODE) {
            save(this.requestArgs);
        }
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

    private File savePhoto(Bitmap bmp, String filename, String strQuality, String picfolder) {
        int quality = getQuality(strQuality);
        File retVal = null;

        try {
            File folder = new File(Environment.getExternalStorageDirectory(), picfolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File imageFile = new File(folder, filename);
            CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
            FileOutputStream out = new FileOutputStream(imageFile);
            bmp.compress(compressFormat, quality, out);
            out.flush();
            out.close();

            retVal = imageFile;
        } catch (Exception e) {
            Log.e("Canvas2ImagePlugin", "An exception occured while saving image: " + e.toString());
        }
        return retVal;
    }

    private void scanPhoto(File imageFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        cordova.getActivity().sendBroadcast(mediaScanIntent);
    }
}
