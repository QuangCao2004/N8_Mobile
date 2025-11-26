package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class ChiTietDonHang_Admin_Activity extends AppCompatActivity {

    DatabaseHelper dbdata;
    Database database;
    ListView listViewChiTiet; // Danh sách hiển thị chi tiết đơn hàng
    ChiTietDonHangAdapter chiTietAdapter; // Adapter để hiển thị chi tiết

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang_admin);

        // Khởi tạo cơ sở dữ liệu
        dbdata = new DatabaseHelper(this);
        database = new Database(this, "banhang.db", null, 1);

        createTableIfNotExists();

        // Khởi tạo ListView để hiển thị chi tiết đơn hàng
        listViewChiTiet = findViewById(R.id.listtk); // Đảm bảo rằng bạn đã định nghĩa ListView trong layout

        // Lấy ID đơn hàng từ Intent
        String donHangIdStr = getIntent().getStringExtra("donHangId");

        if (donHangIdStr != null) {
            try {
                // Chuyển đổi chuỗi donHangId thành kiểu int
                int donHangId = Integer.parseInt(donHangIdStr);

                // Lấy chi tiết đơn hàng từ database
                List<ChiTietDonHang> chiTietList = dbdata.getChiTietByOrderId(donHangId);

                // Kiểm tra danh sách chi tiết
                if (chiTietList != null && !chiTietList.isEmpty()) {
                    // Sử dụng adapter để hiển thị chi tiết đơn hàng
                    chiTietAdapter = new ChiTietDonHangAdapter(this, chiTietList);
                    listViewChiTiet.setAdapter(chiTietAdapter); // Gán adapter cho ListView
                } else {
                    Toast.makeText(this, "Không tìm thấy chi tiết cho đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "ID đơn hàng không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Nếu không có ID đơn hàng, lấy tất cả chi tiết đơn hàng
            List<ChiTietDonHang> allChiTietList = dbdata.getAllChiTietDonHang();
            if (allChiTietList != null && !allChiTietList.isEmpty()) {
                chiTietAdapter = new ChiTietDonHangAdapter(this, allChiTietList);
                listViewChiTiet.setAdapter(chiTietAdapter);
            } else {
                Toast.makeText(this, "Không tìm thấy bất kỳ chi tiết đơn hàng nào!", Toast.LENGTH_SHORT).show();
            }
        }

        ImageButton btntrangchu=findViewById(R.id.btntrangchu);
        btntrangchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),TrangchuAdmin_Activity.class);
                startActivity(a);
            }
        });
        ImageButton btncanhan=findViewById(R.id.btncanhan);
        btncanhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //kiểm tra trạng thái đăng nhập của ng dùng
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if (!isLoggedIn) {
                    // Chưa đăng nhập, chuyển đến trang login
                    Intent intent = new Intent(getApplicationContext(),Login_Activity.class);
                    startActivity(intent);
                } else {
                    // Đã đăng nhập, chuyển đến trang 2
                    Intent intent = new Intent(getApplicationContext(), TrangCaNhan_admin_Activity.class);
                    startActivity(intent);
                }
            }
        });
        ImageButton btndonhang=findViewById(R.id.btndonhang);
        btndonhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),DonHang_admin_Activity.class);
                startActivity(a);
            }
        });
        ImageButton btnsanpham    =findViewById(R.id.btnsanpham);
        btnsanpham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),Sanpham_admin_Activity.class);
                startActivity(a);
            }
        });
        ImageButton btnnhomsp   =findViewById(R.id.btnnhomsp);
        btnnhomsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),Nhomsanpham_admin_Actvity.class);
                startActivity(a);
            }
        });
        ImageButton btntaikhoan    =findViewById(R.id.btntaikhoan);
        btntaikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),Taikhoan_admin_Activity.class);
                startActivity(a);
            }
        });
    }

    private void createTableIfNotExists() {
        // Tạo bảng Chitietdonhang nếu chưa tồn tại
        database.QueryData("CREATE TABLE IF NOT EXISTS Chitietdonhang (" +
                "id_chitiet INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_dathang INTEGER, " +
                "masp INTEGER, " +
                "soluong INTEGER, " +
                "dongia REAL, " +
                "anh TEXT, " +
                "FOREIGN KEY(id_dathang) REFERENCES Dathang(id_dathang));");
    }
}