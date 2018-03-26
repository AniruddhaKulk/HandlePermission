package com.aniruddhakulkarni.handle_permissions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PermissionUtil {
    private Context mContext;
    private Activity mActivity;
    private ArrayList<String> permissionList = new ArrayList<>();
    private ArrayList<String> listPermissionsNeeded = new ArrayList<>();
    private String message = "";
    private PermissionGrantListener mListener;
    private boolean isFragment = false;


    public PermissionUtil(Context context, boolean isFragment) {
        mContext = context;
        if(!isFragment)
            mListener = (PermissionGrantListener) context;
        mActivity = (Activity) context;
        this.isFragment = isFragment;
    }


    public void checkPermissionInFragment(ArrayList<String> permissions, String message, int requestCode, PermissionGrantListener listener) {
        this.permissionList = permissions;
        this.message = message;
        mListener = listener;

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissions, requestCode)) {
                mListener.onPermissionGranted(requestCode);
            }
        } else {
            mListener.onPermissionGranted(requestCode);
        }
    }

    public void checkPermission(ArrayList<String> permissions, String message, int requestCode) {
        this.permissionList = permissions;
        this.message = message;

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissions, requestCode)) {
                mListener.onPermissionGranted(requestCode);
            }
        } else {
            mListener.onPermissionGranted(requestCode);
        }
    }

    private boolean checkAndRequestPermissions(ArrayList<String> permissions, int request_code) {

        if (permissions.size() > 0) {
            listPermissionsNeeded = new ArrayList<>();

            for (int i = 0; i < permissions.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(mActivity, permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }

            }

            if (!listPermissionsNeeded.isEmpty()) {
                if (isFragment) {
                    mListener.fragmentOnRequestPermission(request_code, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]));
                } else {
                    ActivityCompat.requestPermissions(mActivity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), request_code);
                }
                return false;
            }
        }

        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (grantResults.length > 0) {
            Map<String, Integer> perms = new HashMap<>();

            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }

            final ArrayList<String> pendingPermissions = new ArrayList<>();

            for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, listPermissionsNeeded.get(i)))
                        pendingPermissions.add(listPermissionsNeeded.get(i));
                    else {
                        mListener.onNeverAskAgain(requestCode);
                        return;
                    }
                }

            }

            if (pendingPermissions.size() > 0) {
                showMessageOKCancel(message,
                        (dialog, which) -> {

                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    checkPermission(permissionList, message, requestCode);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    if (permissionList.size() == pendingPermissions.size())
                                        mListener.onPermissionDenied(requestCode);
                                    else
                                        mListener.onPartialPermissionGranted(requestCode, pendingPermissions);
                                    break;
                            }
                        });
            } else {
                mListener.onPermissionGranted(requestCode);
            }
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton(mContext.getString(R.string.ok), okListener)
                .setNegativeButton(mContext.getString(R.string.cancel), okListener)
                .create()
                .show();
    }

    public interface PermissionGrantListener {
        void onPermissionGranted(int requestCode);

        void onPartialPermissionGranted(int requestCode, ArrayList<String> grantedPermissions);

        void onPermissionDenied(int requestCode);

        void onNeverAskAgain(int requestCode);

        void fragmentOnRequestPermission(int requestCode, String[] grantedPermissions);
    }


    public static void showRationaleMessage(Context context, String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.settings), okListener)
                .setNegativeButton(context.getString(R.string.cancel), null)
                .create()
                .show();
    }

    public static void openAppSettingScreen(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
