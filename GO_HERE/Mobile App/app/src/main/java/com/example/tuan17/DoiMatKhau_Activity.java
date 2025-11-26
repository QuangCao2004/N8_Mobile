package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
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

public class DoiMatKhau_Activity extends AppCompatActivity {
    Database database;
    ArrayList<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;
    String spn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);

        // Khởi tạo VideoView
        VideoView videoView = findViewById(R.id.video_view);
        // Đặt đường dẫn đến file video (nằm trong res/raw)
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + com.example.tuan17.R.raw.login_video); // Thay "video" bằng tên file video của bạn
        videoView.setVideoURI(videoUri);

        // Cấu hình video
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true); // Lặp video
                videoView.start();   // Bắt đầu phát video
            }
        });

        // Các thành phần khác
        TextView ql = findViewById(R.id.ql);
        ql.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(DoiMatKhau_Activity.this, Login_Activity.class);
                startActivity(a);
            }
        });

        Button btndoimk = findViewById(R.id.btnDoi);
        EditText tendn = findViewById(R.id.tdn);
        EditText matkhau = findViewById(R.id.mk);
        EditText nhaplaimatkhau = findViewById(R.id.mk2);
        Spinner spinner = findViewById(R.id.quyen);

        ArrayList<String> ar = new ArrayList<>();
        ar.add("user");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ar);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spn = ar.get(i);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mangTK = new ArrayList<>();
        adapter = new TaiKhoanAdapter(getApplicationContext(), R.layout.ds_taikhoan, mangTK);
        database = new Database(this, "banhang.db", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS taikhoan(tendn VARCHAR(20) PRIMARY KEY, matkhau VARCHAR(50), quyen VARCHAR(50))");

        btndoimk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = tendn.getText().toString().trim();
                String password = matkhau.getText().toString().trim();
                String nhaplaimk = nhaplaimatkhau.getText().toString().trim();

                // Kiểm tra xem tên đăng nhập và mật khẩu có rỗng không
                if (username.isEmpty() || password.isEmpty() || nhaplaimk.isEmpty()) {
                    Toast.makeText(DoiMatKhau_Activity.this, "Tên đăng nhập và mật khẩu không được để trống!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem mật khẩu và mật khẩu xác nhận có trùng nhau không
                if (!password.equals(nhaplaimk)) {
                    Toast.makeText(DoiMatKhau_Activity.this, "Mật khẩu không khớp, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem username có tồn tại trong cơ sở dữ liệu không
                Cursor cursor = database.GetData("SELECT * FROM taikhoan WHERE tendn = '" + username + "'");
                if (cursor.getCount() <= 0) {
                    Toast.makeText(DoiMatKhau_Activity.this, "Tên đăng nhập không tồn tại, vui lòng nhập tên khác!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cập nhật tài khoản trong cơ sở dữ liệu
                database.QueryData("UPDATE taikhoan SET matkhau = '" + password + "', quyen = '" + spn + "' WHERE tendn = '" + username + "'");
                Toast.makeText(DoiMatKhau_Activity.this, "Đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoView videoView = findViewById(R.id.video_view);
        if (!videoView.isPlaying()) {
            videoView.start(); // Đảm bảo video chạy lại khi activity được resume
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoView videoView = findViewById(R.id.video_view);
        if (videoView.isPlaying()) {
            videoView.pause(); // Tạm dừng video khi activity bị pause
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoView videoView = findViewById(R.id.video_view);
        videoView.stopPlayback(); // Dừng và giải phóng tài nguyên khi activity bị destroy
    }
}