package com.example.tuan17;

import android.os.Parcel;
import android.os.Parcelable;

public class ChiTietDonHang implements Parcelable {
    private int id_chitiet; // ID chi tiết đơn hàng
    private int id_dathang; // ID đơn hàng
    private int masp; // Mã sản phẩm
    private int soLuong; // Số lượng
    private float donGia; // Đơn giá
    private byte[] anh; // Hình ảnh (dưới dạng byte array)

    // Constructor với tất cả các tham số
    public ChiTietDonHang(int id_chitiet, int id_dathang, int masp, int soLuong, float donGia, byte[] anh) {
        this.id_chitiet = id_chitiet;
        this.id_dathang = id_dathang;
        this.masp = masp;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.anh = anh; // Đặt thuộc tính ảnh là kiểu byte[]
    }

    public int getId_chitiet() {
        return id_chitiet;
    }

    public void setId_chitiet(int id_chitiet) {
        this.id_chitiet = id_chitiet;
    }

    public int getId_dathang() {
        return id_dathang;
    }

    public void setId_dathang(int id_dathang) {
        this.id_dathang = id_dathang;
    }

    public int getMasp() {
        return masp;
    }

    public void setMasp(int masp) {
        this.masp = masp;
    }

    public int getSoLuong() {
        return soLuong; // Getter cho số lượng
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public float getDonGia() {
        return donGia; // Getter cho đơn giá
    }

    public void setDonGia(float donGia) {
        this.donGia = donGia;
    }

    public byte[] getAnh() {
        return anh; // Getter cho ảnh (byte array)
    }

    public void setAnh(byte[] anh) {
        this.anh = anh; // Setter cho ảnh
    }

    // Constructor từ Parcel
    protected ChiTietDonHang(Parcel in) {
        id_chitiet = in.readInt();
        masp = in.readInt();
        soLuong = in.readInt();
        donGia = in.readFloat();
        anh = in.createByteArray(); // Đọc ảnh từ Parcel dưới dạng byte array
        id_dathang = in.readInt(); // Đọc ID đơn hàng từ Parcel
    }

    // Creator cho Parcelable
    public static final Creator<ChiTietDonHang> CREATOR = new Creator<ChiTietDonHang>() {
        @Override
        public ChiTietDonHang createFromParcel(Parcel in) {
            return new ChiTietDonHang(in);
        }

        @Override
        public ChiTietDonHang[] newArray(int size) {
            return new ChiTietDonHang[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_chitiet);
        dest.writeInt(masp);
        dest.writeInt(soLuong);
        dest.writeFloat(donGia);
        dest.writeByteArray(anh); // Ghi ảnh vào Parcel dưới dạng byte array
        dest.writeInt(id_dathang); // Ghi ID đơn hàng vào Parcel
    }
}
