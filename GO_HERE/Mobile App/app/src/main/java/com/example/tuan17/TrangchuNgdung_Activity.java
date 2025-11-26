package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.util.ArrayList;

public class TrangchuNgdung_Activity extends AppCompatActivity {
    private VideoView bannerVideo;
    private GridView grv1, grv2;
    private ArrayList<SanPham> mangSPgrv1;
    private ArrayList<NhomSanPham> mangNSPgrv2;
    private NhomSanPhamAdapter adapterGrv2;
    private SanPhamAdapter adapterGrv1;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_ngdung);

        // Khởi tạo views
        bannerVideo = findViewById(R.id.banner_video);
        grv1 = findViewById(R.id.grv1);
        grv2 = findViewById(R.id.grv2);
        ImageButton btnTrangChu = findViewById(R.id.btntrangchu);
        ImageButton btnTimKiem = findViewById(R.id.btntimkiem);
        ImageButton btnCart = findViewById(R.id.btncart);
        ImageButton btnDonHang = findViewById(R.id.btndonhang);
        ImageButton btnCaNhan = findViewById(R.id.btncanhan);
        EditText timkiem = findViewById(R.id.timkiem);
        TextView textTendn = findViewById(R.id.tendn);

        // Kiểm tra và thiết lập VideoView
        if (bannerVideo == null) {
            Log.e("VideoView", "Không tìm thấy VideoView!");
            Toast.makeText(this, "Lỗi: Không tìm thấy VideoView", Toast.LENGTH_SHORT).show();
        } else {
            // Thiết lập video từ res/raw
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.banner_video;
            // Hoặc dùng URL (thay bằng URL thật)
            // String videoPath = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
            try {
                bannerVideo.setVideoURI(Uri.parse(videoPath));
                Log.d("VideoView", "Đã thiết lập URI video: " + videoPath);
            } catch (Exception e) {
                Log.e("VideoView", "Lỗi thiết lập URI video: " + e.getMessage());
                Toast.makeText(this, "Lỗi URI video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Thiết lập vòng lặp và phát video
            bannerVideo.setOnPreparedListener(mp -> {
                Log.d("VideoView", "Video đã sẵn sàng, bắt đầu phát");
                mp.setLooping(true);
                bannerVideo.start();
            });

            // Xử lý lỗi
            bannerVideo.setOnErrorListener((mp, what, extra) -> {
                Log.e("VideoView", "Lỗi phát video: what=" + what + ", extra=" + extra);
                Toast.makeText(this, "Lỗi phát video: " + what + "/" + extra, Toast.LENGTH_LONG).show();
                return true;
            });
        }

        // Lấy tên đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPreferences.getString("tendn", null);

        // Kiểm tra đăng nhập
        if (tendn == null) {
            Intent intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
            finish();
            return;
        }
        textTendn.setText(tendn);
        Log.d("NguoiDung", "Username: " + tendn);

        // Khởi tạo danh sách và adapter
        mangSPgrv1 = new ArrayList<>();
        mangNSPgrv2 = new ArrayList<>();
        adapterGrv1 = new SanPhamAdapter(this, mangSPgrv1, false);
        adapterGrv2 = new NhomSanPhamAdapter(this, mangNSPgrv2, false);
        grv1.setAdapter(adapterGrv1);
        grv2.setAdapter(adapterGrv2);
        database = new Database(this, "banhang.db", null, 1);

        // Load dữ liệu GridView
        Loaddulieubacsigridview1();
        Loaddulieubacsigridview2();

        // Sự kiện click GridView
        grv2.setOnItemClickListener((parent, view, position, id) -> {
            NhomSanPham nhomSanPham = mangNSPgrv2.get(position);
            if (nhomSanPham != null) {
                Intent intent = new Intent(this, DanhMucSanPham_Activity.class);
                intent.putExtra("nhomSpId", nhomSanPham.getMa());
                startActivity(intent);
            }
        });

        // Sự kiện navigation
        btnTrangChu.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrangchuNgdung_Activity.class);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });

        btnTimKiem.setOnClickListener(v -> {
            Intent intent = new Intent(this, TimKiemSanPham_Activity.class);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, GioHang_Activity.class);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });

        btnDonHang.setOnClickListener(v -> {
            Intent intent = new Intent(this, DonHang_User_Activity.class);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });

        btnCaNhan.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrangCaNhan_nguoidung_Activity.class);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });

        timkiem.setOnClickListener(v -> {
            Intent intent = new Intent(this, TimKiemSanPham_Activity.class);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });
    }

    private void Loaddulieubacsigridview1() {
        Cursor cursor = database.GetData("SELECT * FROM sanpham ORDER BY RANDOM() LIMIT 8");
        mangSPgrv1.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String masp = cursor.getString(0);
                String tensp = cursor.getString(1);
                float dongia = cursor.getFloat(2);
                String mota = cursor.getString(3);
                String ghichu = cursor.getString(4);
                int soluongkho = cursor.getInt(5);
                String maso = cursor.getString(6);
                byte[] blob = cursor.getBlob(7);
                mangSPgrv1.add(new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, maso, blob));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, "Không tải được dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
        }
        adapterGrv1.notifyDataSetChanged();
    }

    private void Loaddulieubacsigridview2() {
        Cursor dataCongViec = database.GetData("SELECT * FROM nhomsanpham ORDER BY RANDOM() LIMIT 8");
        mangNSPgrv2.clear();
        while (dataCongViec.moveToNext()) {
            String ma = dataCongViec.getString(0);
            String ten = dataCongViec.getString(1);
            byte[] blob = dataCongViec.getBlob(2);
            mangNSPgrv2.add(new NhomSanPham(ma, ten, blob));
        }
        dataCongViec.close();
        adapterGrv2.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bannerVideo != null && bannerVideo.isPlaying()) {
            bannerVideo.pause();
            Log.d("VideoView", "Video đã tạm dừng");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bannerVideo != null && bannerVideo.isPlaying()) {
            bannerVideo.start();
            Log.d("VideoView", "Video đã tiếp tục");
        }
    }
}