package com.example.tuan17;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id; // ID đơn hàng
    private String tenKh;
    private String tenNguoiNhan;
    private String diaChi;
    private String sdt;
    private float tongTien;
    private String ngayDatHang;
    private List<ChiTietDonHang> chiTietList; // Danh sách chi tiết đơn hàng

    public Order(int id, String tenKh, String diaChi, String sdt, String tongthanhtoan, float tongTien, String ngayDatHang) {
        this.id = id;
        this.tenKh = tenKh;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.tongTien = tongTien;
        this.ngayDatHang = ngayDatHang;
        this.chiTietList = new ArrayList<>(); // Khởi tạo danh sách
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getTenKh() {
        return tenKh;
    }
    public String getTenNguoiNhan() { return tenNguoiNhan; }

    public String getDiaChi() {
        return diaChi;
    }

    public String getSdt() {
        return sdt;
    }

    public float getTongTien() {
        return tongTien;
    }

    public String getNgayDatHang() {
        return ngayDatHang;
    }

    public List<ChiTietDonHang> getChiTietList() {
        return chiTietList; // Getter cho danh sách chi tiết
    }

    public void setChiTietList(List<ChiTietDonHang> chiTietList) {
        this.chiTietList = chiTietList; // Setter cho danh sách chi tiết
    }
}