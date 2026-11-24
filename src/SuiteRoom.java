public class SuiteRoom extends Room {
    public SuiteRoom(int roomNo, double price) { super(roomNo, price); }
    @Override public String getCategory() { return "Suite"; }
    @Override public String getAmenities() { return "AC, Living Area, King Bed, Jacuzzi, Free Breakfast"; }
}