package com.example.tuan17;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SanPham_DanhMuc_Adapter extends BaseAdapter {
    private Context context;
    private Uri selectedImageUri;
    private ArrayList<SanPham> spList;
    private boolean showFullDetails;
    private Database database;

    public SanPham_DanhMuc_Adapter(Context context, ArrayList<SanPham> spList, boolean showFullDetails) {
        this.context = context;
        this.spList = spList;
        this.showFullDetails = showFullDetails;
        this.database = new Database(context, "banhang.db", null, 1);
    }

    @Override
    public int getCount() {
        return spList.size();
    }

    @Override
    public Object getItem(int position) {
        return spList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (showFullDetails) {
            return getViewWith8Properties(position, convertView, parent);
        } else {
            return getViewWith4Properties(position, convertView, parent);
        }
    }

    private View getViewWith8Properties(int i, View view, ViewGroup parent) {
        View viewtemp;
        if (view == null) {
            viewtemp = LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_sanpham, parent, false);
        } else {
            viewtemp = view;
        }

        SanPham tt = spList.get(i);
        TextView masp = viewtemp.findViewById(R.id.masp);
        TextView tensp = viewtemp.findViewById(R.id.tensp);
        TextView dongia = viewtemp.findViewById(R.id.dongia);
        TextView mota = viewtemp.findViewById(R.id.mota);
        TextView ghichu = viewtemp.findViewById(R.id.ghichu);
        TextView soluongkho = viewtemp.findViewById(R.id.soluongkho);
        TextView manhomsanpham = viewtemp.findViewById(R.id.manhomsanpham);
        ImageView anh = viewtemp.findViewById(R.id.imgsp);


        masp.setText(tt.getMasp());
        tensp.setText(tt.getTensp());
        dongia.setText(String.valueOf(tt.getDongia()));
        mota.setText(tt.getMota());
        ghichu.setText(tt.getGhichu());
        soluongkho.setText(String.valueOf(tt.getSoluongkho()));
        manhomsanpham.setText(tt.getMansp());

        byte[] anhByteArray = tt.getAnh();
        if (anhByteArray != null && anhByteArray.length > 0) {
            Bitmap imganhbs = BitmapFactory.decodeByteArray(anhByteArray, 0, anhByteArray.length);
            anh.setImageBitmap(imganhbs);
        } else {
            anh.setImageResource(R.drawable.vest);
        }

        // Thay đổi ở đây: Truyền thêm thông tin sản phẩm khi người dùng nhấn vào sản phẩm
        viewtemp.setOnClickListener(v -> {
            Intent intent = new Intent(parent.getContext(), ChiTietSanPham_Activity.class);
            ChiTietSanPham chiTietSanPham = new ChiTietSanPham(
                    tt.getMasp(),
                    tt.getTensp(),
                    tt.getDongia(),
                    tt.getMota(),
                    tt.getGhichu(),
                    tt.getSoluongkho(),
                    tt.getMansp(),
                    tt.getAnh()
            );
            intent.putExtra("chitietsanpham", chiTietSanPham); // Truyền đối tượng ChiTietSanPham
            parent.getContext().startActivity(intent);
        });

        return viewtemp;
    }

    private View getViewWith4Properties(int i, View view, ViewGroup parent) {
        View viewtemp;
        if (view == null) {
            viewtemp = LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_hienthi_gridview1_nguoidung, parent, false);
        } else {
            viewtemp = view;
        }

        SanPham tt = spList.get(i);
        TextView masp = viewtemp.findViewById(R.id.masp);
        TextView tensp = viewtemp.findViewById(R.id.tensp);
        TextView dongia = viewtemp.findViewById(R.id.dongia);
        TextView mota = viewtemp.findViewById(R.id.mota);
        TextView ghichu = viewtemp.findViewById(R.id.ghichu);
        TextView soluongkho = viewtemp.findViewById(R.id.soluongkho);
        TextView manhomsanpham = viewtemp.findViewById(R.id.manhomsanpham);
        ImageView anh = viewtemp.findViewById(R.id.imgsp);

        masp.setText(tt.getMasp());
        tensp.setText(tt.getTensp());
        dongia.setText(String.valueOf(tt.getDongia()));
        mota.setText(tt.getMota());
        ghichu.setText(tt.getGhichu());
        soluongkho.setText(String.valueOf(tt.getSoluongkho()));
        manhomsanpham.setText(tt.getMansp());

        byte[] anhByteArray = tt.getAnh();
        if (anhByteArray != null && anhByteArray.length > 0) {
            Bitmap imganhbs = BitmapFactory.decodeByteArray(anhByteArray, 0, anhByteArray.length);
            anh.setImageBitmap(imganhbs);
        } else {
            anh.setImageResource(R.drawable.vest);
        }

        viewtemp.setOnClickListener(v -> {
            Intent intent = new Intent(parent.getContext(), ChiTietSanPham_Activity.class);
            ChiTietSanPham chiTietSanPham = new ChiTietSanPham(
                    tt.getMasp(),
                    tt.getTensp(),
                    tt.getDongia(),
                    tt.getMota(),
                    tt.getGhichu(),
                    tt.getSoluongkho(),
                    tt.getMansp(),
                    tt.getAnh()
            );
            intent.putExtra("chitietsanpham", chiTietSanPham); // Truyền đối tượng ChiTietSanPham
            parent.getContext().startActivity(intent);
        });
        return viewtemp;
    }
}
