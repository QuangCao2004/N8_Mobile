package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class DonHang_admin_Activity extends AppCompatActivity {

    private Database database;
    private ListView listView;
    private DonHang_Adapter donHangAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang_admin);

        // Khởi tạo các thành phần
        listView = findViewById(R.id.listViewChiTiet);
        database = new Database(this, "banhang.db", null, 1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = donHangAdapter.getItem(position);

                if (order != null) {
                    // Hiển thị Toast với ID đơn hàng
                    Toast.makeText(DonHang_admin_Activity.this, "ID đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();

                    // Gửi thông tin đơn hàng qua Intent
                    Intent intent = new Intent(DonHang_admin_Activity.this, ChiTietDonHang_Admin_Activity.class);
                    intent.putExtra("donHangId", String.valueOf(order.getId())); // Đảm bảo rằng ID là chuỗi
                    startActivity(intent);
                }
            }


        });
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

        // Tạo bảng nếu chưa tồn tại
        createTableIfNotExists();



        loadDonHang(); // Gọi phương thức loadDonHang với tenDN
    }

    private void createTableIfNotExists() {
        // Tạo bảng đơn hàng nếu chưa tồn tại
        database.QueryData("CREATE TABLE IF NOT EXISTS Dathang (" +
                "id_dathang INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenkh TEXT, " +
                "diachi TEXT, " +
                "sdt TEXT, " +
                "tongthanhtoan REAL, " +
                "ngaydathang DATETIME DEFAULT CURRENT_TIMESTAMP);");
    }

    private void loadDonHang() {

        // Lấy danh sách đơn hàng từ cơ sở dữ liệu
        List<Order> orders = database.getAllDonHang();
        if (orders.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy đơn hàng nào!", Toast.LENGTH_SHORT).show();
        } else {
            // Sử dụng DonHangAdapter để hiển thị danh sách đơn hàng
            donHangAdapter = new DonHang_Adapter(this, orders);
            listView.setAdapter(donHangAdapter); // Gán adapter cho ListView
        }
    }
}