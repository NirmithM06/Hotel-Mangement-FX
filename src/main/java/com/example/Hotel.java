import java.sql.*;
import java.util.ArrayList;

public class Hotel {

    public void addRoom(Room r) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO rooms (roomNumber, type, price, isBooked, customerName, phone) VALUES (?, ?, ?, 0, NULL, NULL)"
             )) {

            ps.setInt(1, r.getRoomNumber());
            ps.setString(2, r.getType());
            ps.setDouble(3, r.getPrice());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean bookRoom(int roomNo, String name, String phone) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE rooms SET isBooked=1, customerName=?, phone=? WHERE roomNumber=? AND isBooked=0"
             )) {

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setInt(3, roomNo);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkoutRoom(int roomNo) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE rooms SET isBooked=0, customerName=NULL, phone=NULL WHERE roomNumber=?"
             )) {

            ps.setInt(1, roomNo);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Room findRoom(int roomNo) {
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM rooms WHERE roomNumber=?")) {

            ps.setInt(1, roomNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Room r = new Room(
                        rs.getInt("roomNumber"),
                        rs.getString("type"),
                        rs.getDouble("price")
                );

                if (rs.getInt("isBooked") == 1) {
                    r.bookRoom(rs.getString("customerName"), rs.getString("phone"));
                }

                return r;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Room> getRooms() {
        ArrayList<Room> list = new ArrayList<>();

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms")) {

            while (rs.next()) {
                Room r = new Room(
                        rs.getInt("roomNumber"),
                        rs.getString("type"),
                        rs.getDouble("price")
                );

                if (rs.getInt("isBooked") == 1) {
                    r.bookRoom(rs.getString("customerName"), rs.getString("phone"));
                }

                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}