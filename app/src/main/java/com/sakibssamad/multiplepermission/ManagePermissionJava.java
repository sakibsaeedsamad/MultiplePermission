package com.sakibssamad.multiplepermission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class ManagePermissionJava {
    Activity activity;
    List<String> list;
    int code;
    Context context;
    String packageName;

    public ManagePermissionJava(Activity activity, List<String> list, int code, Context context, String packageName) {
        this.activity = activity;
        this.list = list;
        this.code = code;
        this.context = context;
        this.packageName = packageName;
    }

    public boolean checkPermissions() {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            rationaleCheck();
            return false;
        } else {
            return true;
        }

    }

    private int isPermissionsGranted() {
        int counter = 0;
        for (String permission:
                list ) {
            counter += ContextCompat.checkSelfPermission(activity, permission);
        }
        return counter;
    }


    private String deniedPermission() {

        for (String permission:
                list ) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    == PackageManager.PERMISSION_DENIED
            ) return permission;
        }

        return "";
    }


    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setTitle("Need permission(s)")
                .setMessage("Some permissions are required to do the task.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void rationaleCheck() {
        String permission = deniedPermission();
        boolean rationaleStatus =
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);

        boolean isFirst = MainActivity.sharedPreferences.getBoolean("isFirst", false);

        if (!isFirst) {
            if (rationaleStatus) {
                showAlert();
            } else {
                showSettingDialog();
            }

        } else {
            requestPermission();
            SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
            editor.putBoolean("isFirst", false);
            editor.apply();
            editor.commit();

        }
    }

    private void showSettingDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setTitle("Need permission(s)")
                .setMessage("Some permissions are required to do the task.")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)
                        );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(activity, list.toArray(new String[list.size()]), code);
    }


    public boolean processPermissionsResult(
            int requestCode, String[] permissions,
            int[] grantResults
    ) {
        int result = 0;
        if (grantResults.length > 0) {
            for (int item :
                    grantResults) {
                result += item;
            }

        }
        if (result == PackageManager.PERMISSION_GRANTED) return true;
        return false;
    }
}
