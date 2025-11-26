package com.example.tuan17;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TrangCaNhan_nguoidung_Activity extends AppCompatActivity {
    private static final String TAG = "TrangCaNhanActivity";
    private String tendn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_ca_nhan_nguoidung);

        // Khởi tạo views
        Button dangxuat = findViewById(R.id.btndangxuat);
        TextView textTendn = findViewById(R.id.tendn_display); // Sửa thành tendn_display
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);

        // Lấy giá trị tendn từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        tendn = sharedPreferences.getString("tendn", null);

        // Nếu SharedPreferences không có, lấy từ Intent
        if (tendn == null) {
            tendn = getIntent().getStringExtra("tendn");
        }

        // Kiểm tra giá trị tendn
        if (tendn != null && !tendn.isEmpty()) {
            textTendn.setText(tendn);
            Log.d(TAG, "Đã gán tên đăng nhập: " + tendn);
        } else {
            Log.e(TAG, "Không tìm thấy tên đăng nhập trong SharedPreferences hoặc Intent");
            Intent intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Xử lý các mục điều hướng
        findViewById(R.id.qd).setOnClickListener(v -> {
            // TODO: Mở activity hoặc dialog Quy định sử dụng
        });
        findViewById(R.id.cs).setOnClickListener(v -> {
            // TODO: Mở activity hoặc dialog Chính sách và bảo mật
        });
        findViewById(R.id.dk).setOnClickListener(v -> {
            // TODO: Mở activity hoặc dialog Điều khoản và dịch vụ
        });
        findViewById(R.id.td).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:19002325"));
            startActivity(intent);
        });

        // Xử lý nút giỏ hàng
        btncard.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            Intent intent = new Intent(this, isLoggedIn ? GioHang_Activity.class : Login_Activity.class);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });

        // Xử lý nút trang chủ
        btntrangchu.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrangchuNgdung_Activity.class);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });

        // Xử lý nút đơn hàng
        btndonhang.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            Intent intent = new Intent(this, isLoggedIn ? DonHang_User_Activity.class : Login_Activity.class);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });

        // Xử lý nút cá nhân (sửa lỗi: đang chuyển sai đến DonHang_User_Activity)
        btncanhan.setOnClickListener(v -> {
            // Đã ở trang cá nhân, không cần chuyển
        });

        // Xử lý nút tìm kiếm
        btntimkiem.setOnClickListener(v -> {
            Intent intent = new Intent(this, TimKiemSanPham_Activity.class);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });

        // Xử lý đăng xuất
        dangxuat.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng Xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", false);
                        editor.putString("tendn", null);
                        editor.apply();
                        Intent intent = new Intent(this, TrangchuNgdung_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }
}