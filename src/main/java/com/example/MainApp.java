import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
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

        // 🔷 Buttons
        Button addBtn = new Button("Add Room");
        Button viewBtn = new Button("View Rooms");
        Button bookBtn = new Button("Book Room");
        Button checkoutBtn = new Button("Checkout");

        // 🔥 Button Styling
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        bookBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        checkoutBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        viewBtn.setStyle("-fx-background-color: #555; -fx-text-fill: white;");

        addBtn.setPrefWidth(120);
        bookBtn.setPrefWidth(120);
        checkoutBtn.setPrefWidth(120);
        viewBtn.setPrefWidth(120);

        // 🔷 Form Layout
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

        // 🔷 Buttons Layout
        HBox buttons = new HBox(10, addBtn, viewBtn, bookBtn, checkoutBtn);

        // 🔷 Output area
        display.setPrefHeight(200);
        display.setStyle("-fx-font-family: monospace;");

        // 🔷 Card Style (box)
        VBox card = new VBox(15, form, buttons);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 10;");

        // 🔷 Main Layout
        VBox root = new VBox(20, header, card, display);
        root.setPadding(new Insets(20));

        // ===============================
        // KEEP YOUR EXISTING LOGIC BELOW
        // ===============================

        addBtn.setOnAction(e -> {
            try {
                int no = Integer.parseInt(roomNoField.getText());
                String type = typeBox.getValue();
                double price = Double.parseDouble(priceField.getText());

                if (type == null) {
                    showAlert("Select room type!");
                    return;
                }

                hotel.addRoom(new Room(no, type, price));
                showAlert("Room Added!");
                clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate);

            } catch (Exception ex) {
                showAlert("Invalid input!");
            }
        });

        viewBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Room r : hotel.getRooms()) {
                sb.append("Room ").append(r.getRoomNumber())
                        .append(" | ").append(r.getType())
                        .append(" | ₹").append(r.getPrice())
                        .append(" | ")
                        .append(r.isBooked() ? "Booked" : "Available")
                        .append("\n");
            }
            display.setText(sb.toString());
        });

        bookBtn.setOnAction(e -> {
            try {
                int no = Integer.parseInt(roomNoField.getText());
                if (checkInDate.getValue() == null || checkOutDate.getValue() == null) {
                    showAlert("Select both dates!");
                    return;
                }

                long days = java.time.temporal.ChronoUnit.DAYS.between(
                        checkInDate.getValue(),
                        checkOutDate.getValue()
                );

                if (days <= 0) {
                    showAlert("Check-out must be after check-in!");
                    return;
                }

                if (hotel.bookRoom(no)) {
                    Room r = hotel.findRoom(no);
                    double bill = r.getPrice() * days;

                    showAlert("Booked! Bill: ₹" + bill);
                } else {
                    showAlert("Room unavailable");
                }
                clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate);

            } catch (Exception ex) {
                showAlert("Invalid input!");
            }
        });

        checkoutBtn.setOnAction(e -> {
            try {
                int no = Integer.parseInt(roomNoField.getText());

                if (hotel.checkoutRoom(no)) {
                    showAlert("Checked out!");
                } else {
                    showAlert("Failed!");
                }
                clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate);

            } catch (Exception ex) {
                showAlert("Invalid input!");
            }
        });

        Scene scene = new Scene(root, 500, 550);
        stage.setTitle("Hotel Management");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    private void clear(TextField a, ComboBox<String> b, TextField c,
                       DatePicker d1, DatePicker d2) {
        a.clear();
        b.setValue(null);
        c.clear();
        d1.setValue(null);
        d2.setValue(null);
    }

    public static void main(String[] args) {
        launch();
    }
}