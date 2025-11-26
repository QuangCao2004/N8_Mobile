package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Taikhoan_admin_Activity extends AppCompatActivity {


    Database database;
    ListView lv;
    int vitri;
    ArrayList<TaiKhoan> mangTK;
    TaiKhoanAdapter adapter;
    FloatingActionButton dauconggocphai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taikhoan_admin);
        dauconggocphai = findViewById(R.id.btnthem);
        lv = findViewById(R.id.listtk);
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


        dauconggocphai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),ThemTaiKhoan_Activity.class);
                startActivity(a);
            }
        });
        mangTK = new ArrayList<>();
        adapter = new TaiKhoanAdapter(getApplicationContext(), R.layout.ds_taikhoan, mangTK);
        lv.setAdapter(adapter);
        database = new Database(this, "banhang.db", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS taikhoan(tendn VARCHAR(20) PRIMARY KEY, matkhau VARCHAR(50), quyen VARCHAR(50))");
        // Thêm 2 dòng dữ liệu
//        database.QueryData("INSERT  INTO taikhoan VALUES ('admin', '1234', 'admin')");
//        database.QueryData("INSERT  INTO taikhoan VALUES ('bac2', '1111', 'user')");
//        database.QueryData("INSERT  INTO taikhoan VALUES ('bac3', '1111', 'user')");
//        database.QueryData("INSERT  INTO taikhoan VALUES ('bac4', '1111', 'bacsi')");
        Loaddulieutaikhoan();


    }
    private void Loaddulieutaikhoan() {
        Cursor dataCongViec = database.GetData("SELECT * FROM taikhoan");
        mangTK.clear();
        while (dataCongViec.moveToNext()) {
            String tdn = dataCongViec.getString(0);
            String mk= dataCongViec.getString(1);
            String q = dataCongViec.getString(2);
            mangTK.add(new TaiKhoan(tdn, mk, q));
        }
        adapter.notifyDataSetChanged();
    }
}