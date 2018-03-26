package com.aniruddhakulkarni.permissions;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aniruddhakulkarni.handle_permissions.PermissionUtil;

import java.util.ArrayList;


public class PermissionFragment extends Fragment{

    private PermissionUtil permissionUtil;
    private static final String TAG = "TAG";
    private Context mContext;

    public static PermissionFragment getInstance(){
        return new PermissionFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permission, container, false);

        permissionUtil = new PermissionUtil(mContext, true);

        Button btnPermission = view.findViewById(R.id.btn_permission);
        btnPermission.setOnClickListener(view1 -> checkForPermissions());
        return view;
    }

    private void checkForPermissions(){
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);
        permissionUtil.checkPermissionInFragment(permissions, getString(R.string.app_permission_camera_storage), MainActivity.REQUEST_CAMERA_EXTERNAL_STORAGE_PERMISSION, new PermissionUtil.PermissionGrantListener() {
            @Override
            public void onPermissionGranted(int requestCode) {
                Log.d(TAG, "onPermissionGranted: ");
            }

            @Override
            public void onPartialPermissionGranted(int requestCode, ArrayList<String> grantedPermissions) {
                Log.d(TAG, "onPartialPermissionGranted: ");
            }

            @Override
            public void onPermissionDenied(int requestCode) {
                Log.d(TAG, "onPermissionDenied: " + requestCode);
            }

            @Override
            public void onNeverAskAgain(int requestCode) {
                switch (requestCode) {
                    case MainActivity.REQUEST_CAMERA_EXTERNAL_STORAGE_PERMISSION:
                        PermissionUtil.showRationaleMessage(mContext, getString(R.string.external_storage_and_camera_permission), (dialogInterface, i) -> PermissionUtil.openAppSettingScreen(mContext));
                        break;
                }
            }

            @Override
            public void fragmentOnRequestPermission(int requestCode, String[] grantedPermissions) {
                requestPermissions(grantedPermissions, requestCode); // this line is important in fragment to call 'onRequestPermissionsResult' method
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
