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

        //  Title
        Label title = new Label("🏨 Hotel Management System");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Manage rooms, bookings and billing");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        VBox header = new VBox(5, title, subtitle);

        //  Inputs
        TextField roomNoField = new TextField();
        roomNoField.setPromptText("Room Number");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Single", "Double", "Deluxe", "Suite");
        typeBox.setPromptText("Select Room Type");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        DatePicker checkInDate = new DatePicker();
        DatePicker checkOutDate = new DatePicker();

        //  Buttons
        Button addBtn = new Button("Add Room");
        Button viewBtn = new Button("View Rooms");
        Button bookBtn = new Button("Book Room");
        Button checkoutBtn = new Button("Checkout");
        Button searchBtn = new Button("Search Room");

        //  Button Styling
        searchBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        bookBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        checkoutBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        viewBtn.setStyle("-fx-background-color: #555; -fx-text-fill: white;");

        addBtn.setPrefWidth(120);
        searchBtn.setPrefWidth(120);
        bookBtn.setPrefWidth(120);
        checkoutBtn.setPrefWidth(120);
        viewBtn.setPrefWidth(120);

        //  Form Layout
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

        //  Buttons Layout
        HBox buttons = new HBox(10, addBtn, viewBtn, bookBtn, checkoutBtn, searchBtn);

        //  Output area
        display.setPrefHeight(200);
        display.setStyle("-fx-font-family: monospace;");

        //  Card Style (box)
        VBox card = new VBox(15, form, buttons);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 10;");

        //  Main Layout
        VBox root = new VBox(20, header, card, display);
        root.setPadding(new Insets(20));

        addBtn.setOnAction(e -> {
            try {

                //  EMPTY CHECK
                if (roomNoField.getText().isEmpty() ||
                        priceField.getText().isEmpty() ||
                        typeBox.getValue() == null) {
                    if (roomNoField.getText().isEmpty()) {
                        showAlert("Enter Room Number");
                        return;
                    }

                    if (typeBox.getValue() == null) {
                        showAlert("Select Room Type");
                        return;
                    }

                    if (priceField.getText().isEmpty()) {
                        showAlert("Enter Price");
                        return;
                    }
                }

                int no = Integer.parseInt(roomNoField.getText());
                double price = Double.parseDouble(priceField.getText());
                String type = typeBox.getValue();

                //  TYPE CHECK
                if (type == null) {
                    showAlert("Select room type!");
                    return;
                }

                //  VALUE CHECK
                if (no <= 0 || price <= 0) {
                    showAlert("Invalid room number or price!");
                    return;
                }

                //  DUPLICATE CHECK
                if (hotel.findRoom(no) != null) {
                    showAlert("Room already exists!");
                    return;
                }

                hotel.addRoom(new Room(no, type, price));
                showAlert("Room Added!");
                clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate);

            } catch (NumberFormatException ex) {
                showAlert("Enter valid numbers!");
            } catch (Exception ex) {
                showAlert("Error occurred!");
            }
        });

        bookBtn.setOnAction(e -> {
            try {

                //  EMPTY CHECK
                if (roomNoField.getText().isEmpty()) {
                    showAlert("Enter room number!");
                    return;
                }

                int no = Integer.parseInt(roomNoField.getText());

                //  DATE CHECK
                if (checkInDate.getValue() == null || checkOutDate.getValue() == null) {
                    showAlert("Select both dates!");
                    return;
                }

                //  PAST DATE CHECK
                if (checkInDate.getValue().isBefore(java.time.LocalDate.now())) {
                    showAlert("Check-in cannot be in past!");
                    return;
                }

                long days = ChronoUnit.DAYS.between(
                        checkInDate.getValue(),
                        checkOutDate.getValue()
                );

                //  INVALID RANGE
                if (days <= 0) {
                    showAlert("Check-out must be after check-in!");
                    return;
                }

                //  ROOM EXIST CHECK
                Room r = hotel.findRoom(no);
                if (r == null) {
                    showAlert("Room does not exist!");
                    return;
                }

                //  ALREADY BOOKED CHECK
                if (r.isBooked()) {
                    showAlert("Room already booked!");
                    return;
                }

                if (hotel.bookRoom(no)) {

                    double bill = r.getPrice() * days;

                    String message = "Booking Successful!\n\n" +
                            "Room: " + r.getRoomNumber() + "\n" +
                            "Type: " + r.getType() + "\n" +
                            "Days: " + days + "\n" +
                            "Price/day: ₹" + r.getPrice() + "\n" +
                            "Total Bill: ₹" + bill;

                    showAlert(message);

                } else {
                    showAlert("Room unavailable");
                }

                clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate);

            } catch (NumberFormatException ex) {
                showAlert("Enter valid room number!");
            } catch (Exception ex) {
                showAlert("Error occurred!");
            }
        });
        
        viewBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("===== ROOM LIST =====\n\n");

            for (Room r : hotel.getRooms()) {
                sb.append("Room No: ").append(r.getRoomNumber()).append("\n")
                        .append("Type: ").append(r.getType()).append("\n")
                        .append("Price: ₹").append(r.getPrice()).append("\n")
                        .append("Status: ").append(r.isBooked() ? "Booked" : "Available")
                        .append("\n----------------------\n");
            }

            display.setText(sb.toString());
        });


        checkoutBtn.setOnAction(e -> {
            try {

                //  EMPTY CHECK
                if (roomNoField.getText().isEmpty()) {
                    showAlert("Enter room number!");
                    return;
                }

                int no = Integer.parseInt(roomNoField.getText());

                Room r = hotel.findRoom(no);

                //  ROOM EXIST CHECK
                if (r == null) {
                    showAlert("Room does not exist!");
                    return;
                }

                //  NOT BOOKED CHECK
                if (!r.isBooked()) {
                    showAlert("Room is not currently booked!");
                    return;
                }

                if (hotel.checkoutRoom(no)) {
                    showAlert("Checked out successfully!");
                } else {
                    showAlert("Checkout failed!");
                }

                clear(roomNoField, typeBox, priceField, checkInDate, checkOutDate);

            } catch (NumberFormatException ex) {
                showAlert("Enter valid room number!");
            } catch (Exception ex) {
                showAlert("Error occurred!");
            }
        });

        searchBtn.setOnAction(e -> {
            try {

                //  EMPTY CHECK
                if (roomNoField.getText().isEmpty()) {
                    showAlert("Enter room number!");
                    return;
                }

                int no = Integer.parseInt(roomNoField.getText());

                //  FIND ROOM
                Room r = hotel.findRoom(no);

                if (r != null) {
                    String message = "Room Found!\n\n" +
                            "Room: " + r.getRoomNumber() + "\n" +
                            "Type: " + r.getType() + "\n" +
                            "Price: ₹" + r.getPrice() + "\n" +
                            "Status: " + (r.isBooked() ? "Booked" : "Available");

                    showAlert(message);
                } else {
                    showAlert("Room not found!");
                }

            } catch (NumberFormatException ex) {
                showAlert("Enter valid room number!");
            } catch (Exception ex) {
                showAlert("Error occurred!");
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