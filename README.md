# Grand Stay - Hotel Reservation System

A desktop hotel booking application built with **Java Swing**. Search rooms by date and category, book them with a simulated payment, and manage or cancel reservations. All data is stored using **File I/O** (CSV files).

## Features

- Room categories: **Standard**, **Deluxe** and **Suite**
- Search available rooms by check-in and check-out dates
- Filter rooms by category
- Make reservations with guest details validation
- Payment simulation (Card / UPI / Cash at Hotel)
- Booking details view
- Cancel bookings with a refund rule (100% if check-in is 2+ days away, otherwise 50%)
- Dark mode UI with room cards, prices in Indian Rupees (₹)
- Bookings are saved to a file and stay available after restarting the app

## OOP Concepts Used

| Concept | Where |
|---|---|
| Abstraction | `Room` is an abstract class |
| Inheritance | `StandardRoom`, `DeluxeRoom`, `SuiteRoom` extend `Room` |
| Polymorphism | Each room type gives its own category and amenities |
| Encapsulation | Private fields with getters in `Booking` and `Room` |

## Project Structure

```
HotelReservationSystem/
├── src/
│   ├── Main.java            - entry point
│   ├── MainFrame.java       - main UI (Explore and My Bookings pages)
│   ├── PaymentDialog.java   - payment simulation window
│   ├── Theme.java           - colors and custom UI components
│   ├── HotelService.java    - search, book and cancel logic
│   ├── DataStore.java       - File I/O (CSV read/write)
│   ├── Booking.java         - booking data
│   ├── Room.java            - abstract parent class
│   ├── StandardRoom.java
│   ├── DeluxeRoom.java
│   └── SuiteRoom.java
├── images/                  - room photos (standard, deluxe, suite)
└── data/                    - rooms.csv and bookings.csv (auto-created)
```

## How to Run

**Requirements:** JDK 17 or higher

**In VS Code:** open the project folder, open `src/Main.java` and click **Run**.

**From the terminal** (inside the project folder):

```bash
javac -d bin src/*.java
java -cp bin Main
```

## Author

Made as part of an internship task: *Hotel Reservation System*.