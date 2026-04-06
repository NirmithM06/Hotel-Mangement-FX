public class Room {
    private int roomNumber;
    private String type;
    private double price;
    private boolean isBooked;
    private String customerName;
    private String phone;
    private String checkIn;
    private String checkOut;
    private double totalAmount;

    public Room(int roomNumber, String type, double price) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.isBooked = false;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void bookRoom(String name, String phone, String checkIn, String checkOut, double total) {
        this.isBooked = true;
        this.customerName = name;
        this.phone = phone;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = total;
    }

    public void checkoutRoom() {
        this.isBooked = false;
        this.customerName = null;
        this.phone = null;
        this.checkIn = null;
        this.checkOut = null;
        this.totalAmount = 0.0;
    }
}