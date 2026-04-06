import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MainApp extends Application {

    Hotel hotel = new Hotel();
    TextArea display = new TextArea();

    @Override
    public void start(Stage stage) {

        Database.init();

        // 🔷 Title
        Label title = new Label("🏨 Hotel Management System");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Manage rooms, bookings and billing");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        VBox header = new VBox(5, title, subtitle);

        // 🔷 Inputs
        TextField roomNoField = new TextField();
        roomNoField.setPromptText("Room Number");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Single", "Double", "Deluxe", "Suite");
        typeBox.setPromptText("Select Room Type");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        DatePicker checkInDate = new DatePicker();
        DatePicker checkOutDate = new DatePicker();

        TextField nameField = new TextField();
        nameField.setPromptText("Customer Name");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        // 🔷 Buttons
        Button addBtn = new Button("Add Room");
        Button viewBtn = new Button("View Rooms");
        Button bookBtn = new Button("Book Room");
        Button checkoutBtn = new Button("Checkout");
        Button searchBtn = new Button("Search Room");

        // 🔥 Styling
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        bookBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        checkoutBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        viewBtn.setStyle("-fx-background-color: #555; -fx-text-fill: white;");
        searchBtn.setStyle("-fx-background-color: #673AB7; -fx-text-fill: white;");

        // 🔷 Layout
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);

        form.add(new Label("Room No:"), 0, 0);
        form.add(roomNoField, 1, 0);

        form.add(new Label("Type:"), 0, 1);
        form.add(typeBox, 1, 1);

        form.add(new Label("Price:"), 0, 2);
        form.add(priceField, 1, 2);

        form.add(new Label("Check-In:"), 0, 3);
        form.add(checkInDate, 1, 3);

        form.add(new Label("Check-Out:"), 0, 4);
        form.add(checkOutDate, 1, 4);

        form.add(new Label("Name:"), 0, 5);
        form.add(nameField, 1, 5);

        form.add(new Label("Phone:"), 0, 6);
        form.add(phoneField, 1, 6);

        HBox buttons = new HBox(10, addBtn, viewBtn, bookBtn, checkoutBtn, searchBtn);

        display.setPrefHeight(200);
        display.setStyle("-fx-font-family: monospace;");

        VBox card = new VBox(15, form, buttons);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd;");

        VBox root = new VBox(20, header, card, display);
        root.setPadding(new Insets(20));

        // ================== LOGIC ==================

        // ➕ Add Room
        addBtn.setOnAction(e -> {
            try {
                if (roomNoField.getText().isEmpty() ||
                        priceField.getText().isEmpty() ||
                        typeBox.getValue() == null) {

                    showAlert("Fill all room details!");
                    return;
                }

                int no = Integer.parseInt(roomNoField.getText());
                double price = Double.parseDouble(priceField.getText());

                if (hotel.findRoom(no) != null) {
                    showAlert("Room already exists!");
                    return;
                }

                hotel.addRoom(new Room(no, typeBox.getValue(), price));
                showAlert("Room Added!");

                clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate, nameField, phoneField);

            } catch (Exception ex) {
                showAlert("Invalid input!");
            }
        });

        // 👁 View Rooms
        viewBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();

            for (Room r : hotel.getRooms()) {
                sb.append("Room ").append(r.getRoomNumber())
                        .append(" | ").append(r.getType())
                        .append(" | ₹").append(r.getPrice())
                        .append(" | ")
                        .append(r.isBooked() ? "Booked" : "Available");

                if (r.isBooked()) {
                    sb.append(" | ").append(r.getCustomerName())
                            .append(" | ").append(r.getPhone());
                }

                sb.append("\n");
            }

            display.setText(sb.toString());
        });

        // 🏨 Book Room
        bookBtn.setOnAction(e -> {
            try {
                // 1. Basic empty field validation
                if (roomNoField.getText().isEmpty() || nameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
                    showAlert("Enter all booking details!");
                    return;
                }

                // 2. Format validation
                if (!isValidName(nameField.getText())) {
                    showAlert("Name should contain only letters!");
                    return;
                }

                if (!isValidPhone(phoneField.getText())) {
                    showAlert("Phone number must be exactly 10 digits!");
                    return;
                }

                int roomNo = Integer.parseInt(roomNoField.getText());

                // 3. Date validation
                if (checkInDate.getValue() == null || checkOutDate.getValue() == null) {
                    showAlert("Select dates!");
                    return;
                }

                if (checkInDate.getValue().isBefore(LocalDate.now())) {
                    showAlert("Invalid check-in date!");
                    return;
                }

                long days = ChronoUnit.DAYS.between(checkInDate.getValue(), checkOutDate.getValue());
                if (days <= 0) {
                    showAlert("Invalid date range!");
                    return;
                }

                // 4. Room availability validation
                Room room = hotel.findRoom(roomNo);
                if (room == null) {
                    showAlert("Room does not exist!");
                    return;
                }

                if (room.isBooked()) {
                    showAlert("Room already booked!");
                    return;
                }

                // 5. Execute booking
                boolean success = hotel.bookRoom(roomNo, nameField.getText(), phoneField.getText());

                if (success) {
                    double bill = room.getPrice() * days;
                    showAlert("Booking Successful!\n\n" +
                            "Room: " + roomNo + "\n" +
                            "Customer: " + nameField.getText() + "\n" +
                            "Days: " + days + "\n" +
                            "Total Bill: ₹" + bill);
                } else {
                    showAlert("Booking failed!");
                }

                // 6. Reset UI
                clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate, nameField, phoneField);

            } catch (Exception ex) {
                showAlert("Error!");
            }
        });

        // 🚪 Checkout
        checkoutBtn.setOnAction(e -> {
            try {
                int no = Integer.parseInt(roomNoField.getText());

                if (hotel.checkoutRoom(no)) {
                    showAlert("Checked out!");
                } else {
                    showAlert("Failed!");
                }

                clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate, nameField, phoneField);

            } catch (Exception ex) {
                showAlert("Error!");
            }
        });

        // 🔍 Search
        searchBtn.setOnAction(e -> {
            try {
                int no = Integer.parseInt(roomNoField.getText());

                Room r = hotel.findRoom(no);

                if (r != null) {
                    showAlert("Room: " + r.getRoomNumber()
                            + "\nType: " + r.getType()
                            + "\nStatus: " + (r.isBooked() ? "Booked" : "Available"));
                } else {
                    showAlert("Not found!");
                }

            } catch (Exception ex) {
                showAlert("Invalid input!");
            }
        });

        Scene scene = new Scene(root, 550, 600);
        stage.setTitle("Hotel Management");
        stage.setScene(scene);
        stage.show();
    }

    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z ]+");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    private void clear(TextField a, ComboBox<String> b, TextField c,
                       DatePicker d1, DatePicker d2,
                       TextField name, TextField phone) {
        a.clear();
        b.setValue(null);
        c.clear();
        d1.setValue(null);
        d2.setValue(null);
        name.clear();
        phone.clear();
    }

    public static void main(String[] args) {
        launch();
    }
}