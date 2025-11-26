package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class DangKiTaiKhoan_Activity extends AppCompatActivity {

    Database database;
    ArrayList<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;
    String spn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki_tai_khoan);

        // Khởi tạo VideoView
        VideoView videoView = findViewById(R.id.video_view);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.login_video;
        videoView.setVideoPath(videoPath);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);
        });
        videoView.start();

        // Khởi tạo các thành phần
        Button btnDangKi = findViewById(R.id.btnDangki); // Fixed from btnadd
        EditText tendn = findViewById(R.id.tdn);
        EditText matkhau = findViewById(R.id.mk);
        EditText nhaplaimatkhau = findViewById(R.id.nhaplaimk);
        Spinner spinner = findViewById(R.id.quyen);
        TextView quayLai = findViewById(R.id.ql);

        // Cấu hình Spinner
        ArrayList<String> ar = new ArrayList<>();
        ar.add("user");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ar);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spn = ar.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spn = "user"; // Default value
            }
        });

        // Khởi tạo database và adapter
        mangTK = new ArrayList<>();
        adapter = new TaiKhoanAdapter(this, R.layout.ds_taikhoan, mangTK);
        database = new Database(this, "banhang.db", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS taikhoan(tendn VARCHAR(20) PRIMARY KEY, matkhau VARCHAR(50), quyen VARCHAR(50))");

        // Xử lý nút Đăng Kí
        btnDangKi.setOnClickListener(v -> {
            String username = tendn.getText().toString().trim();
            String password = matkhau.getText().toString().trim();
            String nhaplaimk = nhaplaimatkhau.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || nhaplaimk.isEmpty()) {
                Toast.makeText(DangKiTaiKhoan_Activity.this, "Tên đăng nhập và mật khẩu không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(nhaplaimk)) {
                Toast.makeText(DangKiTaiKhoan_Activity.this, "Mật khẩu không khớp, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = database.GetData("SELECT * FROM taikhoan WHERE tendn = '" + username + "'");
            if (cursor.getCount() > 0) {
                Toast.makeText(DangKiTaiKhoan_Activity.this, "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!", Toast.LENGTH_SHORT).show();
                return;
            }

            database.QueryData("INSERT INTO taikhoan VALUES('" + username + "', '" + password + "', '" + spn + "')");
            Toast.makeText(DangKiTaiKhoan_Activity.this, "Đăng kí tài khoản thành công", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DangKiTaiKhoan_Activity.this, Login_Activity.class);
            startActivity(intent);
        });

        // Xử lý liên kết Quay Lại
        quayLai.setOnClickListener(v -> {
            Intent intent = new Intent(DangKiTaiKhoan_Activity.this, Login_Activity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoView videoView = findViewById(R.id.video_view);
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoView videoView = findViewById(R.id.video_view);
        if (!videoView.isPlaying()) {
            videoView.start();
        }
    }
}