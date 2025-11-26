package com.example.tuan17;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ChiTietDonHangAdapter extends ArrayAdapter<ChiTietDonHang> {
    public ChiTietDonHangAdapter(Context context, List<ChiTietDonHang> details) {
        super(context, 0, details);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ChiTietDonHang detail = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_chitietdonhang, parent, false);
        }

        TextView tvID_dathang = convertView.findViewById(R.id.txt_Iddathang);
        TextView tvMaSp = convertView.findViewById(R.id.txtMasp);
        TextView tvTenSp = convertView.findViewById(R.id.txtTensp); // Thêm TextView cho tên sản phẩm
        TextView tvSoLuong = convertView.findViewById(R.id.txtSoLuong);
        TextView tvDonGia = convertView.findViewById(R.id.txtGia);
        ImageView ivAnh = convertView.findViewById(R.id.imgsp);

        // Hiển thị ID đơn hàng
        tvID_dathang.setText(String.valueOf(detail.getId_dathang()));

        // Hiển thị mã sản phẩm
        int masp = detail.getMasp();
        tvMaSp.setText(String.valueOf(masp)); // Hiển thị mã sản phẩm

        // Lấy tên sản phẩm từ DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        String tenSanPham = dbHelper.getTenSanPhamByMaSp(masp);
        tvTenSp.setText(tenSanPham != null ? tenSanPham : "Không tìm thấy tên sản phẩm");

        // Hiển thị số lượng và đơn giá
        tvSoLuong.setText(String.valueOf(detail.getSoLuong()));
        tvDonGia.setText(String.valueOf(detail.getDonGia()));

        // Tải ảnh từ byte[]
        if (detail.getAnh() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(detail.getAnh(), 0, detail.getAnh().length);
            ivAnh.setImageBitmap(bitmap);
        } else {
            ivAnh.setImageResource(R.drawable.vest); // Hình ảnh mặc định nếu không có
        }

        return convertView;
    }



    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream input = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }
}