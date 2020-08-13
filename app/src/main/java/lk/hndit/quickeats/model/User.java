package lk.hndit.quickeats.model;

public class User {

    private String id;
    private String name;
    private String contactNo;
    private String address;
    private String imageUrl;
    private String userType;

    public User() {
    }

    public User(String id, String name, String contactNo, String address, String imageUrl, String userType) {
        this.id = id;
        this.name = name;
        this.contactNo = contactNo;
        this.address = address;
        this.imageUrl = imageUrl;
        this.userType = userType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", address='" + address + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
