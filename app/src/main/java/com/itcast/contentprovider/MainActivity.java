package com.itcast.contentprovider;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mLookmsg;
    private List<SmsInfo> smsInfos;
    private LinearLayout mLin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mLookmsg = (Button) findViewById(R.id.lookmsg);
        mLookmsg.setOnClickListener(this);
        smsInfos = new ArrayList<>();
        mLin = (LinearLayout) findViewById(R.id.lin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.lookmsg:
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 1);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    getSms();
                } else {
                    Toast.makeText(this, "权限" + permissions[i] + "申请失败,不能读取系统短信", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getSms() {
        Uri uri = Uri.parse("content://sms/");
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id", "address", "body"}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (smsInfos != null) smsInfos.clear();
            while (cursor.moveToNext()) {
                SmsInfo smsInfo = new SmsInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                smsInfos.add(smsInfo);
            }
            cursor.close();
        }
        addView();
    }

    private void addView() {
        mLin.removeAllViews();
        for (SmsInfo smsInfo : smsInfos) {
            View view=LinearLayout.inflate(MainActivity.this,R.layout.add,null);
            TextView mPhone = view.findViewById(R.id.phone);
            TextView mMsg = view.findViewById(R.id.msg);
            mPhone.setText(smsInfo.getAddress());
            mMsg.setText(smsInfo.getBody());
            mLin.addView(view);
        }
    }

}
