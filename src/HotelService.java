import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// Saara business logic: search, book, cancel
public class HotelService {
    private final List<Room> rooms;
    private final List<Booking> bookings;

    public HotelService() {
        rooms = DataStore.loadRooms();
        bookings = DataStore.loadBookings();
    }

    public List<Booking> getBookings() { return bookings; }

    public boolean isAvailable(Room room, LocalDate in, LocalDate out) {
        for (Booking b : bookings) {
            if (b.getRoomNo() == room.getRoomNo() && b.overlaps(in, out)) return false;
        }
        return true;
    }

    // category "All" ho to saari categories dikhao
    public List<Room> searchAvailable(String category, LocalDate in, LocalDate out) {
        List<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            boolean categoryOk = category.equals("All") || r.getCategory().equals(category);
            if (categoryOk && isAvailable(r, in, out)) result.add(r);
        }
        return result;
    }

    public Booking book(Room room, String name, String phone, LocalDate in, LocalDate out, String payMethod) {
        if (!isAvailable(room, in, out)) {
            throw new IllegalStateException("Room " + room.getRoomNo() + " is no longer available.");
        }
        long nights = ChronoUnit.DAYS.between(in, out);
        double total = nights * room.getPricePerNight();
        Booking b = new Booking(nextId(), room.getRoomNo(), room.getCategory(), name, phone,
                in, out, total, payMethod, "CONFIRMED");
        bookings.add(b);
        DataStore.saveBookings(bookings);
        return b;
    }

    // Cancel karta hai aur refund amount return karta hai (-1 = cancel nahi ho saka)
    // Rule: check-in se 2+ din pehle = 100% refund, warna 50%
    public double cancel(int bookingId) {
        for (Booking b : bookings) {
            if (b.getId() == bookingId && b.isConfirmed()) {
                b.setStatus("CANCELLED");
                DataStore.saveBookings(bookings);
                long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), b.getCheckIn());
                return daysLeft >= 2 ? b.getTotalAmount() : b.getTotalAmount() * 0.5;
            }
        }
        return -1;
    }

    private int nextId() {
        int max = 1000;
        for (Booking b : bookings) max = Math.max(max, b.getId());
        return max + 1;
    }
}