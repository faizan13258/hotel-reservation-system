import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Ek booking ka poora data
public class Booking {
    private final int id;
    private final int roomNo;
    private final String category;
    private final String guestName;
    private final String phone;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double totalAmount;
    private final String paymentMethod;
    private String status; // CONFIRMED ya CANCELLED

    public Booking(int id, int roomNo, String category, String guestName, String phone,
                   LocalDate checkIn, LocalDate checkOut, double totalAmount,
                   String paymentMethod, String status) {
        this.id = id;
        this.roomNo = roomNo;
        this.category = category;
        this.guestName = guestName.replace(",", " ");
        this.phone = phone;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public int getId() { return id; }
    public int getRoomNo() { return roomNo; }
    public String getCategory() { return category; }
    public String getGuestName() { return guestName; }
    public String getPhone() { return phone; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isConfirmed() { return "CONFIRMED".equals(status); }

    public long getNights() { return ChronoUnit.DAYS.between(checkIn, checkOut); }

    // Kya yeh booking di gayi dates se takrati hai?
    public boolean overlaps(LocalDate in, LocalDate out) {
        return isConfirmed() && in.isBefore(checkOut) && out.isAfter(checkIn);
    }

    // File mein save karne ke liye ek line
    public String toCsv() {
        return id + "," + roomNo + "," + category + "," + guestName + "," + phone + ","
                + checkIn + "," + checkOut + "," + totalAmount + "," + paymentMethod + "," + status;
    }

    // File se line padh ke Booking object banana
    public static Booking fromCsv(String line) {
        String[] f = line.split(",");
        return new Booking(Integer.parseInt(f[0]), Integer.parseInt(f[1]), f[2], f[3], f[4],
                LocalDate.parse(f[5]), LocalDate.parse(f[6]), Double.parseDouble(f[7]), f[8], f[9]);
    }

    public String getDetails() {
        return "Booking ID   : " + id + "\n"
             + "Guest Name   : " + guestName + "\n"
             + "Phone        : " + phone + "\n"
             + "Room         : " + roomNo + " (" + category + ")\n"
             + "Check-in     : " + checkIn + "\n"
             + "Check-out    : " + checkOut + "\n"
             + "Nights       : " + getNights() + "\n"
             + "Total Amount : \u20B9" + String.format("%.2f", totalAmount) + "\n"
             + "Payment      : " + paymentMethod + "\n"
             + "Status       : " + status;
    }
}