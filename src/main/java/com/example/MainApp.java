import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MainApp extends Application {

    private Hotel hotel = new Hotel();
    private TextArea display = new TextArea();

    @Override
    public void start(Stage stage) {

        Database.init();

        Label title = new Label("🏨 Hotel Management System");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Manage rooms, bookings and billing");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        VBox header = new VBox(5, title, subtitle);

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

        Button addBtn = new Button("Add Room");
        Button viewBtn = new Button("View Rooms");
        Button bookBtn = new Button("Book Room");
        Button checkoutBtn = new Button("Checkout");
        Button searchBtn = new Button("Search Room");

        //  Styling
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        bookBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        checkoutBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        viewBtn.setStyle("-fx-background-color: #555; -fx-text-fill: white;");
        searchBtn.setStyle("-fx-background-color: #673AB7; -fx-text-fill: white;");

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

        // ================= ADD ROOM =================
        addBtn.setOnAction(e -> {
            try {
                if (roomNoField.getText().isEmpty() || priceField.getText().isEmpty() || typeBox.getValue() == null) {
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

            } catch (NumberFormatException ex) {
                showAlert("Room number and price must be valid numbers!");
            } catch (Exception ex) {
                showAlert("Invalid input!");
            }
        });

        // ================= VIEW =================
        viewBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();

            for (Room r : hotel.getRooms()) {
                sb.append("Room ").append(r.getRoomNumber())
                        .append(" | ").append(r.getType())
                        .append(" | ₹").append(r.getPrice())
                        .append(" | ").append(r.isBooked() ? "Booked" : "Available");

                if (r.isBooked()) {
                    sb.append(" | ").append(r.getCustomerName())
                            .append(" | ").append(r.getPhone())
                            .append(" | ").append(r.getCheckIn())
                            .append(" → ").append(r.getCheckOut())
                            .append(" | ₹").append(r.getTotalAmount());
                }
                sb.append("\n");
            }

            if (sb.length() == 0) {
                display.setText("No rooms found in the hotel.");
            } else {
                display.setText(sb.toString());
            }
        });

        // ================= BOOK =================
        bookBtn.setOnAction(e -> {
            try {
                if (roomNoField.getText().isEmpty() || nameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
                    showAlert("Enter all booking details!");
                    return;
                }

                if (!isValidName(nameField.getText())) {
                    showAlert("Name should contain only letters!");
                    return;
                }

                if (!isValidPhone(phoneField.getText())) {
                    showAlert("Phone must be exactly 10 digits!");
                    return;
                }

                int roomNo = Integer.parseInt(roomNoField.getText());

                if (checkInDate.getValue() == null || checkOutDate.getValue() == null) {
                    showAlert("Select dates!");
                    return;
                }

                if (checkInDate.getValue().isBefore(LocalDate.now())) {
                    showAlert("Invalid check-in date! Cannot book in the past.");
                    return;
                }

                long days = ChronoUnit.DAYS.between(checkInDate.getValue(), checkOutDate.getValue());
                if (days <= 0) {
                    showAlert("Invalid date range! Check-out must be after check-in.");
                    return;
                }

                Room room = hotel.findRoom(roomNo);

                if (room == null) {
                    showAlert("Room does not exist!");
                    return;
                }

                if (room.isBooked()) {
                    showAlert("Room already booked!");
                    return;
                }

                double bill = room.getPrice() * days;

                boolean success = hotel.bookRoom(
                        roomNo,
                        nameField.getText(),
                        phoneField.getText(),
                        checkInDate.getValue().toString(),
                        checkOutDate.getValue().toString(),
                        bill
                );

                if (success) {
                    showAlert("Booking Successful!\nRoom: " + roomNo +
                            "\nDays: " + days +
                            "\nTotal: ₹" + bill);
                    clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate, nameField, phoneField);
                } else {
                    showAlert("Booking failed! Database error.");
                }

            } catch (NumberFormatException ex) {
                showAlert("Room number must be a valid integer!");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("An unexpected error occurred!");
            }
        });

        // ================= CHECKOUT =================
        checkoutBtn.setOnAction(e -> {
            try {
                if (roomNoField.getText().isEmpty()) {
                    showAlert("Enter the Room Number to checkout!");
                    return;
                }

                int no = Integer.parseInt(roomNoField.getText());
                Room r = hotel.findRoom(no);

                if (r == null || !r.isBooked()) {
                    showAlert("Room not found or not currently booked!");
                    return;
                }

                String receipt = "===== HOTEL RECEIPT =====\n" +
                        "Room No: " + r.getRoomNumber() + "\n" +
                        "Customer: " + r.getCustomerName() + "\n" +
                        "Phone: " + r.getPhone() + "\n" +
                        "Check-In: " + r.getCheckIn() + "\n" +
                        "Check-Out: " + r.getCheckOut() + "\n" +
                        "Total: ₹" + r.getTotalAmount() + "\n" +
                        "========================";

                showAlert(receipt);

                boolean outSuccess = hotel.checkoutRoom(no);
                if (outSuccess) {
                    clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate, nameField, phoneField);
                }

            } catch (NumberFormatException ex) {
                showAlert("Room number must be a valid integer!");
            } catch (Exception ex) {
                showAlert("An unexpected error occurred during checkout!");
            }
        });

        // ================= SEARCH =================
        searchBtn.setOnAction(e -> {
            try {
                if (roomNoField.getText().isEmpty()) {
                    showAlert("Enter a Room Number to search!");
                    return;
                }

                int no = Integer.parseInt(roomNoField.getText());
                Room r = hotel.findRoom(no);

                if (r != null) {
                    showAlert("Room: " + r.getRoomNumber() +
                            "\nType: " + r.getType() +
                            "\nPrice: ₹" + r.getPrice() +
                            "\nStatus: " + (r.isBooked() ? "Booked by " + r.getCustomerName() : "Available"));
                } else {
                    showAlert("Room Not found!");
                }

            } catch (NumberFormatException ex) {
                showAlert("Room number must be a valid integer!");
            } catch (Exception ex) {
                showAlert("Invalid input!");
            }
        });

        Scene scene = new Scene(root, 580, 650);
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
        alert.setTitle("System Message");
        alert.setHeaderText(null);
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
        launch(args);
    }
}