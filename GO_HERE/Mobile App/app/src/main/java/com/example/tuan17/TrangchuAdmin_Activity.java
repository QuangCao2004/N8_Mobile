package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class TrangchuAdmin_Activity extends AppCompatActivity {

    private android.widget.VideoView bannerVideo;
    GridView grv2;
    GridView grv1;
    ArrayList<SanPham> mangSPgrv1; // Danh sách cho GridView

    ArrayList<NhomSanPham> mangNSPgrv2; // Danh sách cho ListView

    NhomSanPham_trangChuadmin_Adapter adapterGrv2;
    SanPham_TrangChuAdmin_Adapter adapterGrv1;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_admin);

        bannerVideo = findViewById(R.id.banner_video);

        // Kiểm tra và thiết lập VideoView
        if (bannerVideo == null) {
            android.util.Log.e("VideoView", "Không tìm thấy VideoView!");
            Toast.makeText(this, "Lỗi: Không tìm thấy VideoView", Toast.LENGTH_SHORT).show();
        } else {
            // Thiết lập video từ res/raw
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.banner_video;
            // Hoặc dùng URL (thay bằng URL thật)
            // String videoPath = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
            try {
                bannerVideo.setVideoURI(android.net.Uri.parse(videoPath));
                android.util.Log.d("VideoView", "Đã thiết lập URI video: " + videoPath);
            } catch (Exception e) {
                android.util.Log.e("VideoView", "Lỗi thiết lập URI video: " + e.getMessage());
                Toast.makeText(this, "Lỗi URI video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Thiết lập vòng lặp và phát video
            bannerVideo.setOnPreparedListener(mp -> {
                android.util.Log.d("VideoView", "Video đã sẵn sàng, bắt đầu phát");
                mp.setLooping(true);
                bannerVideo.start();
            });

            // Xử lý lỗi
            bannerVideo.setOnErrorListener((mp, what, extra) -> {
                android.util.Log.e("VideoView", "Lỗi phát video: what=" + what + ", extra=" + extra);
                Toast.makeText(this, "Lỗi phát video: " + what + "/" + extra, Toast.LENGTH_LONG).show();
                return true;
            });
        }

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
                //kiểm tra trạng thái đăng nhập của ng dùng
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if (!isLoggedIn) {
                    // Chưa đăng nhập, chuyển đến trang login
                    Intent intent = new Intent(getApplicationContext(),Login_Activity.class);
                    startActivity(intent);
                } else {
                    // Đã đăng nhập, chuyển đến trang 2
                    Intent intent = new Intent(getApplicationContext(), DonHang_admin_Activity.class);
                    startActivity(intent);
                }
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
        grv2=findViewById(R.id.grv2);
        grv1=findViewById(R.id.grv1);
        mangNSPgrv2= new ArrayList<>(); // Khởi tạo danh sách
        mangSPgrv1= new ArrayList<>(); // Khởi tạo danh sách
        adapterGrv2 = new NhomSanPham_trangChuadmin_Adapter(this, mangNSPgrv2, false) ;

         // false để hiển thị 4 thuộc tính
        grv2.setAdapter(adapterGrv2);

        adapterGrv1= new SanPham_TrangChuAdmin_Adapter(this, mangSPgrv1, false) ;

        grv1.setAdapter(adapterGrv1);


        database = new Database(this, "banhang.db", null, 1);

        Loaddulieubacsigridview2();
        Loaddulieubacsigridview1();
    }
    private void Loaddulieubacsigridview2() {
        Cursor dataCongViec = database.GetData("SELECT * from nhomsanpham order by random() limit 8 ");
        mangNSPgrv2.clear();

        while (dataCongViec.moveToNext()) {
            String ma = dataCongViec.getString(0);

            String ten = dataCongViec.getString(1);
            byte[] blob = dataCongViec.getBlob(2); // Lấy mảng byte từ cột chứa ảnh
            mangNSPgrv2.add(new NhomSanPham(ma,ten, blob));
        }

        adapterGrv2.notifyDataSetChanged(); // Cập nhật adapter
    }

    private void Loaddulieubacsigridview1() {
        Cursor cursor = database.GetData("SELECT * FROM sanpham order by random() limit 8");
        mangSPgrv1.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String masp = cursor.getString(0);
                String tensp = cursor.getString(1);
                float dongia = cursor.getFloat(2); // Giữ nguyên là float
                String mota = cursor.getString(3);
                String ghichu = cursor.getString(4);
                int soluongkho = cursor.getInt(5); // Giữ nguyên là int
                String maso = cursor.getString(6);
                byte[] blob = cursor.getBlob(7);
                mangSPgrv1.add(new SanPham(masp,tensp,dongia,mota,ghichu,soluongkho,maso,blob));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "Null load dữ liệu", Toast.LENGTH_SHORT).show();
        }

        adapterGrv1.notifyDataSetChanged();
    }
}