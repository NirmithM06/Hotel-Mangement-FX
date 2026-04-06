public class Room {
    private int roomNumber;
    private String type;
    private double price;
    private boolean isBooked;
    private String customerName;
    private String phone;

    public Room(int roomNumber, String type, double price) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.isBooked = false;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public boolean isBooked() { return isBooked; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }

    public void bookRoom(String name, String phone) {
        this.isBooked = true;
        this.customerName = name;
        this.phone = phone;
    }

    public void checkoutRoom() {
        this.isBooked = false;
        this.customerName = null;
        this.phone = null;
    }
}