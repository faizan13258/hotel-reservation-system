public class DeluxeRoom extends Room {
    public DeluxeRoom(int roomNo, double price) { super(roomNo, price); }
    @Override public String getCategory() { return "Deluxe"; }
    @Override public String getAmenities() { return "AC, King Bed, TV, Mini Fridge, Wi-Fi"; }
}