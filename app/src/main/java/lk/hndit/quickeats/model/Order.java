package lk.hndit.quickeats.model;

import java.util.List;

public class Order {

    private String orderId;
    private List<Cart> cartList;
    private double totalPrice;
    private double discount;
    private String contactNo;
    private String address;
    private String userId;
    private String dateTime;
    private double latitude;
    private double longitude;
    private int status;

    public Order() {
    }

    public Order(String orderId, List<Cart> cartList, double totalPrice, double discount, String contactNo, String address, String userId, String dateTime, double latitude, double longitude, int status) {
        this.orderId = orderId;
        this.cartList = cartList;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.contactNo = contactNo;
        this.address = address;
        this.userId = userId;
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", cartList=" + cartList +
                ", totalPrice=" + totalPrice +
                ", discount=" + discount +
                ", contactNo='" + contactNo + '\'' +
                ", address='" + address + '\'' +
                ", userId='" + userId + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status=" + status +
                '}';
    }
}
