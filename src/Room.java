// Abstract (parent) class: har room ki common cheezein yahan hain
public abstract class Room {
    private final int roomNo;
    private final double pricePerNight;

    protected Room(int roomNo, double pricePerNight) {
        this.roomNo = roomNo;
        this.pricePerNight = pricePerNight;
    }

    public int getRoomNo() { return roomNo; }
    public double getPricePerNight() { return pricePerNight; }

    // Har child class apna category aur amenities batayegi
    public abstract String getCategory();
    public abstract String getAmenities();
}