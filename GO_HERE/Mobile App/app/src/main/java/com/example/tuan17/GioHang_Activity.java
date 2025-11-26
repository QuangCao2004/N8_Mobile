package com.example.tuan17;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class GioHang_Activity extends AppCompatActivity {
    private ListView listView;
    private GioHangAdapter adapter;
    private GioHangManager gioHangManager;
    private Button thanhtoan;
    private Database database;
    private OrderManager orderManager;
    private TextView txtTongTien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        thanhtoan = findViewById(R.id.btnthanhtoan);
        listView = findViewById(R.id.listtk);
        TextView textTendn = findViewById(R.id.tendn);

        // Lấy tendn từ SharedPreferences
        SharedPreferences sharedPre = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPre.getString("tendn", null);

        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(GioHang_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }

        txtTongTien = findViewById(R.id.tongtien); // Khởi tạo TextView cho tổng tiền
        database = new Database(this, "banhang.db", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS Dathang (" +
                "id_dathang INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenkh TEXT, " + // Lưu tài khoản đặt hàng (taiKhoanDatHang)
                "tenNguoiNhan TEXT, " + // Tên người nhận
                "diachi TEXT, " +
                "sdt TEXT, " +
                "tongthanhtoan REAL, " +
                "ngaydathang DATETIME DEFAULT CURRENT_TIMESTAMP);");

        gioHangManager = GioHangManager.getInstance();
        orderManager = new OrderManager(this);

        // Lấy danh sách giỏ hàng và cập nhật giao diện
        List<GioHang> gioHangList = gioHangManager.getGioHangList();
        adapter = new GioHangAdapter(this, gioHangList, txtTongTien);
        listView.setAdapter(adapter);

        // Cập nhật tổng tiền ngay từ giỏ hàng
        txtTongTien.setText(String.valueOf(gioHangManager.getTongTien()));

        // Xử lý sự kiện click thanh toán
        thanhtoan.setOnClickListener(v -> showPaymentDialog());
        btncard.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            Intent intent = new Intent(getApplicationContext(), isLoggedIn ? GioHang_Activity.class : Login_Activity.class);
            startActivity(intent);
        });

        btntrangchu.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), TrangchuNgdung_Activity.class);
            startActivity(intent);
        });

        btndonhang.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            Intent intent = new Intent(getApplicationContext(), isLoggedIn ? DonHang_User_Activity.class : Login_Activity.class);
            if (isLoggedIn) {
                intent.putExtra("tendn", tendn);
            }
            startActivity(intent);
        });

        btncanhan.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            Intent intent = new Intent(getApplicationContext(), isLoggedIn ? TrangCaNhan_nguoidung_Activity.class : Login_Activity.class);
            startActivity(intent);
        });

        btntimkiem.setOnClickListener(view -> {
            Intent a = new Intent(getApplicationContext(), TimKiemSanPham_Activity.class);
            startActivity(a);
        });
    }

    private void showPaymentDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_thong_tin_thanh_toan);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        EditText edtTenNguoiNhan = dialog.findViewById(com.example.tuan17.R.id.tenNguoiNhan); // Đổi tên biến cho rõ nghĩa
        EditText edtDiaChi = dialog.findViewById(R.id.diachi);
        EditText edtSdt = dialog.findViewById(R.id.sdt);
        Button btnLuu = dialog.findViewById(R.id.btnxacnhandathang);
        TextView tvTongTien = dialog.findViewById(R.id.tienthanhtoan);

        // Lấy tendn từ SharedPreferences
        SharedPreferences sharedPre = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPre.getString("tendn", null);

        String tongTien = txtTongTien.getText().toString();
        tvTongTien.setText(tongTien);

        btnLuu.setOnClickListener(v -> {
            String tenNguoiNhan = edtTenNguoiNhan.getText().toString().trim(); // Tên người nhận
            String diaChi = edtDiaChi.getText().toString().trim();
            String sdt = edtSdt.getText().toString().trim();

            if (tenNguoiNhan.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                float tongThanhToan;
                try {
                    tongThanhToan = Float.parseFloat(tongTien.replace(",", ""));
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Có lỗi xảy ra với tổng tiền!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (orderManager != null && tendn != null) {
                    // Sử dụng tendn làm tenkh (taiKhoanDatHang)
                    long orderId = orderManager.addOrder(tendn, tenNguoiNhan, diaChi, sdt, tongThanhToan);
                    if (orderId > 0) {
                        // Lưu thông tin chi tiết đơn hàng
                        List<GioHang> gioHangList = gioHangManager.getGioHangList();
                        for (GioHang item : gioHangList) {
                            String masp = item.getSanPham().getMasp();
                            int soluong = item.getSoLuong();
                            float dongia = item.getSanPham().getDongia();
                            byte[] anhByteArray = item.getSanPham().getAnh();
                            orderManager.addOrderDetails((int) orderId, masp, soluong, dongia, anhByteArray);
                        }

                        Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                        gioHangManager.clearGioHang(); // Xóa giỏ hàng
                        txtTongTien.setText("0"); // Đặt tổng tiền về 0
                        adapter.notifyDataSetChanged(); // Cập nhật lại giao diện
                        Intent a = new Intent(GioHang_Activity.this, TrangchuNgdung_Activity.class);
                        startActivity(a);
                    } else {
                        Toast.makeText(this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Không thể xử lý đơn hàng, hãy thử lại!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss(); // Đóng dialog sau khi xử lý
            }
        });

        dialog.show();
    }
}