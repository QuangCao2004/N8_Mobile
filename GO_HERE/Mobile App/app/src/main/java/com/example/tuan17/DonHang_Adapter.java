package com.example.tuan17;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DonHang_Adapter extends ArrayAdapter<Order> {
    private static final String TAG = "DonHang_Adapter";
    private final Context context;
    private final List<Order> orders;

    public DonHang_Adapter(Context context, List<Order> orders) {
        super(context, 0, orders);
        this.context = context;
        this.orders = orders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ds_donhang, parent, false);
            holder = new ViewHolder();
            holder.txtMahd = convertView.findViewById(R.id.txtMahd);
            holder.txtTenKh = convertView.findViewById(R.id.txtTenKh);
            holder.txtDiaChi = convertView.findViewById(R.id.txtDiaChi);
            holder.txtSdt = convertView.findViewById(R.id.txtSdt);
            holder.txtTongThanhToan = convertView.findViewById(com.example.tuan17.R.id.txtTongTien);
            holder.txtNgayDatHang = convertView.findViewById(R.id.txtNgayDatHang);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Order order = orders.get(position);
        holder.txtMahd.setText(String.valueOf(order.getId()));
        holder.txtTenKh.setText(order.getTenNguoiNhan());
        holder.txtDiaChi.setText(order.getDiaChi());
        holder.txtSdt.setText(order.getSdt());

        // Định dạng Tổng Thanh Toán
        float tongThanhToan = order.getTongTien();
        Log.d(TAG, "Tổng Thanh Toán (raw): " + tongThanhToan);
        if (tongThanhToan > 0) {
            DecimalFormat df = new DecimalFormat("#,### VNĐ");
            holder.txtTongThanhToan.setText(df.format(tongThanhToan));
        } else {
            holder.txtTongThanhToan.setText("Không xác định");
        }

        // Định dạng Ngày Đặt Hàng
        String ngayDatHang = order.getNgayDatHang();
        Log.d(TAG, "Ngày Đặt Hàng (raw): " + ngayDatHang);
        if (ngayDatHang != null && !ngayDatHang.isEmpty()) {
            try {
                SimpleDateFormat sqliteFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = sqliteFormat.parse(ngayDatHang);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                holder.txtNgayDatHang.setText(displayFormat.format(date));
            } catch (ParseException e) {
                Log.e(TAG, "Lỗi parse ngày: " + e.getMessage());
                holder.txtNgayDatHang.setText(ngayDatHang); // Hiển thị nguyên bản nếu có lỗi
            }
        } else {
            holder.txtNgayDatHang.setText("Không có ngày");
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtMahd;
        TextView txtTenKh;
        TextView txtDiaChi;
        TextView txtSdt;
        TextView txtTongThanhToan;
        TextView txtNgayDatHang;
    }
}