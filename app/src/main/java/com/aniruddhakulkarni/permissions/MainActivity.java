package com.aniruddhakulkarni.permissions;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.aniruddhakulkarni.handle_permissions.PermissionUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PermissionUtil.PermissionGrantListener {

    private PermissionUtil permissionUtil;
    private static final String TAG = "TAG";
    public static final int REQUEST_CAMERA_EXTERNAL_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionUtil = new PermissionUtil(MainActivity.this, false);

        Button btnCheck = findViewById(R.id.btn_check);
        Button btnOpenFragment = findViewById(R.id.btn_fragment);

        btnCheck.setOnClickListener(view -> checkForPermissions());

        btnOpenFragment.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SecondActivity.class));
        });

    }

    private void checkForPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);
        permissionUtil.checkPermission(permissions, getString(R.string.app_permission_camera_storage), REQUEST_CAMERA_EXTERNAL_STORAGE_PERMISSION);
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        Log.d(TAG, "onPermissionGranted: ");
        //Continue with the workflow
    }

    @Override
    public void onPartialPermissionGranted(int requestCode, ArrayList<String> grantedPermissions) {
        Log.d(TAG, "onPartialPermissionGranted: ");
    }

    @Override
    public void onPermissionDenied(int requestCode) {
        Log.d(TAG, "onPermissionDenied: ");
    }

    @Override
    public void onNeverAskAgain(int requestCode) {
        switch (requestCode) {
            case REQUEST_CAMERA_EXTERNAL_STORAGE_PERMISSION:
                PermissionUtil.showRationaleMessage(MainActivity.this, getString(R.string.external_storage_and_camera_permission), (dialogInterface, i) -> PermissionUtil.openAppSettingScreen(MainActivity.this));
                break;
        }
    }

    @Override
    public void fragmentOnRequestPermission(int requestCode, String[] grantedPermissions) {
        //Nothing to do here for activity. Useful only for fragments.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount > 0) {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }else {
            super.onBackPressed();
        }
    }
}
