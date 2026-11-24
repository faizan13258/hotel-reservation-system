import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

// File I/O: rooms aur bookings ko data/ folder ki CSV files mein save/load karta hai
public class DataStore {
    private static final String DIR = "data";
    private static final Path ROOMS_FILE = Paths.get(DIR, "rooms.csv");
    private static final Path BOOKINGS_FILE = Paths.get(DIR, "bookings.csv");

    public static List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        try {
            Files.createDirectories(Paths.get(DIR));
            if (!Files.exists(ROOMS_FILE)) {
                Files.write(ROOMS_FILE, defaultRooms());
            }
            for (String line : Files.readAllLines(ROOMS_FILE)) {
                if (line.isBlank()) continue;
                String[] f = line.split(",");
                rooms.add(createRoom(f[1].trim(), Integer.parseInt(f[0].trim()),
                        Double.parseDouble(f[2].trim())));
            }
        } catch (IOException e) {
            System.out.println("Rooms file error: " + e.getMessage());
        }
        return rooms;
    }

    public static List<Booking> loadBookings() {
        List<Booking> list = new ArrayList<>();
        try {
            if (Files.exists(BOOKINGS_FILE)) {
                for (String line : Files.readAllLines(BOOKINGS_FILE)) {
                    if (!line.isBlank()) list.add(Booking.fromCsv(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Bookings file error: " + e.getMessage());
        }
        return list;
    }

    public static void saveBookings(List<Booking> bookings) {
        List<String> lines = new ArrayList<>();
        for (Booking b : bookings) lines.add(b.toCsv());
        try {
            Files.createDirectories(Paths.get(DIR));
            Files.write(BOOKINGS_FILE, lines);
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    // Category ke hisaab se sahi child class ka object banata hai
    private static Room createRoom(String category, int no, double price) {
        switch (category) {
            case "Deluxe": return new DeluxeRoom(no, price);
            case "Suite":  return new SuiteRoom(no, price);
            default:       return new StandardRoom(no, price);
        }
    }

    // Pehli baar chalane par yeh rooms ban jayenge (roomNo,category,price)
    private static List<String> defaultRooms() {
        List<String> l = new ArrayList<>();
        for (int i = 101; i <= 105; i++) l.add(i + ",Standard,2000");
        for (int i = 201; i <= 204; i++) l.add(i + ",Deluxe,3500");
        for (int i = 301; i <= 302; i++) l.add(i + ",Suite,6000");
        return l;
    }
}