public class StandardRoom extends Room {
    public StandardRoom(int roomNo, double price) { super(roomNo, price); }
    @Override public String getCategory() { return "Standard"; }
    @Override public String getAmenities() { return "Double Bed, Fan, TV, Wi-Fi"; }
}