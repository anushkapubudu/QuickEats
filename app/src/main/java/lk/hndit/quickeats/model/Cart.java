package lk.hndit.quickeats.model;

public class Cart {

    private String uId;
    private String productId;
    private int quantity;
    private String dateTime;

    public Cart() {
    }

    public Cart(String uId, String productId, int quantity, String dateTime) {
        this.uId = uId;
        this.productId = productId;
        this.quantity = quantity;
        this.dateTime = dateTime;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "uId='" + uId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}
