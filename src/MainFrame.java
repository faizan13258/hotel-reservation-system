import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainFrame extends JFrame {
    private final HotelService service = new HotelService();

    // Pages ke beech switch karne ke liye
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);
    private Theme.NavButton exploreNav, bookingsNav;

    // Explore page
    private JSpinner inSpinner, outSpinner;
    private final String[] categories = {"All", "Standard", "Deluxe", "Suite"};
    private final Theme.RoundButton[] chips = new Theme.RoundButton[4];
    private String selectedCategory = "All";
    private final JLabel countLabel = new JLabel();
    private Theme.ListPanel roomList;
    private List<Room> results = new ArrayList<>();

    // Bookings page
    private Theme.ListPanel bookingList;
    private final JLabel summaryLabel = new JLabel();

    public MainFrame() {
        setTitle("Grand Stay - Hotel Reservation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Screen se lambi window na ho, isliye height screen ke hisaab se
        int h = Math.min(840, Toolkit.getDefaultToolkit().getScreenSize().height - 90);
        setSize(480, h);
        setMinimumSize(new Dimension(430, 500));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);

        pages.setOpaque(false);
        pages.add(buildExplorePage(), "explore");
        pages.add(buildBookingsPage(), "bookings");
        root.add(pages, BorderLayout.CENTER);
        root.add(buildNavBar(), BorderLayout.SOUTH);
        setContentPane(root);

        searchRooms();
        refreshBookings();
        showPage("explore");
    }

    // ================= NAVIGATION BAR =================
    private JPanel buildNavBar() {
        JPanel bar = new JPanel(new GridLayout(1, 2));
        bar.setBackground(Theme.SURFACE);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, Theme.BORDER));

        exploreNav = new Theme.NavButton("Explore", 0);
        bookingsNav = new Theme.NavButton("My Bookings", 1);
        exploreNav.addActionListener(e -> showPage("explore"));
        bookingsNav.addActionListener(e -> {
            refreshBookings();
            showPage("bookings");
        });
        bar.add(exploreNav);
        bar.add(bookingsNav);
        return bar;
    }

    private void showPage(String name) {
        cardLayout.show(pages, name);
        exploreNav.setActive(name.equals("explore"));
        bookingsNav.setActive(name.equals("bookings"));
    }

    // ================= PAGE 1: EXPLORE =================
    private JPanel buildExplorePage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(false);

        // ---- Upar ka header ----
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Theme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER), new EmptyBorder(16, 18, 14, 18)));

        header.add(Theme.label("Grand Stay", Theme.TITLE, Theme.TEXT));
        header.add(Theme.label("Find your perfect room", Theme.FONT, Theme.MUTED));
        header.add(Box.createVerticalStrut(12));

        // Dates
        inSpinner = makeDateSpinner(LocalDate.now());
        outSpinner = makeDateSpinner(LocalDate.now().plusDays(1));
        Theme.spinner(inSpinner, 100);
        Theme.spinner(outSpinner, 100);
        inSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        outSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel dates = new JPanel(new GridLayout(1, 2, 12, 0));
        dates.setOpaque(false);
        dates.add(Theme.labeled("Check-in", inSpinner));
        dates.add(Theme.labeled("Check-out", outSpinner));
        dates.setAlignmentX(Component.LEFT_ALIGNMENT);
        dates.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        header.add(dates);
        header.add(Box.createVerticalStrut(12));

        // Category chips
        JPanel chipRow = new JPanel(new GridLayout(1, 4, 8, 0));
        chipRow.setOpaque(false);
        chipRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        chipRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        for (int i = 0; i < 4; i++) {
            final String cat = categories[i];
            chips[i] = Theme.outlineButton(cat, Theme.ACCENT);
            chips[i].setBorder(new EmptyBorder(7, 4, 7, 4));
            chips[i].addActionListener(e -> {
                selectedCategory = cat;
                updateChips();
                searchRooms();
            });
            chipRow.add(chips[i]);
        }
        header.add(chipRow);
        header.add(Box.createVerticalStrut(12));

        // Search button
        Theme.RoundButton searchBtn = Theme.button("Search Rooms", Theme.ACCENT);
        searchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        searchBtn.addActionListener(e -> searchRooms());
        header.add(searchBtn);
        updateChips();

        page.add(header, BorderLayout.NORTH);

        // ---- Beech mein room cards ki list ----
        roomList = new Theme.ListPanel();
        roomList.setBorder(new EmptyBorder(8, 16, 12, 16));
        JScrollPane scroll = new JScrollPane(roomList);
        Theme.styleScroll(scroll);

        countLabel.setFont(Theme.BOLD);
        countLabel.setForeground(Theme.MUTED);
        countLabel.setBorder(new EmptyBorder(10, 20, 0, 20));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(countLabel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);

        return page;
    }

    private void updateChips() {
        for (int i = 0; i < 4; i++) {
            chips[i].setFilled(categories[i].equals(selectedCategory));
        }
    }

    private void searchRooms() {
        LocalDate in = toLocalDate(inSpinner), out = toLocalDate(outSpinner);
        if (!datesValid(in, out)) return;

        results = service.searchAvailable(selectedCategory, in, out);
        long nights = ChronoUnit.DAYS.between(in, out);

        roomList.removeAll();
        for (Room r : results) {
            roomList.add(buildRoomCard(r, nights));
            roomList.add(Box.createVerticalStrut(12));
        }
        if (results.isEmpty()) {
            JLabel empty = Theme.label("No rooms available for these dates", Theme.HEAD, Theme.MUTED);
            empty.setBorder(new EmptyBorder(40, 0, 0, 0));
            roomList.add(empty);
        }
        countLabel.setText(results.size() + (results.size() == 1 ? " room" : " rooms")
                + " available  |  " + nights + (nights == 1 ? " night" : " nights"));
        roomList.revalidate();
        roomList.repaint();
    }

    // Ek room ka card (photo + naam + stars + price + button)
    private Theme.Card buildRoomCard(Room room, long nights) {
        Theme.Card card = new Theme.Card(new BorderLayout(12, 0));
        card.add(new Theme.RoomPhoto(room.getCategory(), 112, 128), BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        // Naam + stars
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nameRow.setOpaque(false);
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameRow.add(Theme.label(room.getCategory() + " Room " + room.getRoomNo(), Theme.HEAD, Theme.TEXT));
        nameRow.add(Box.createHorizontalStrut(8));
        nameRow.add(new Theme.Stars(Theme.starsFor(room.getCategory())));
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, nameRow.getPreferredSize().height));

        // Amenities
        JLabel amenities = new JLabel("<html><div style='width:210px'>" + room.getAmenities() + "</div></html>");
        amenities.setFont(Theme.SMALL);
        amenities.setForeground(Theme.MUTED);
        amenities.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Price
        JPanel priceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        priceRow.setOpaque(false);
        priceRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceRow.add(Theme.label(Theme.money(room.getPricePerNight() * nights),
                new Font("Segoe UI", Font.BOLD, 20), Theme.GREEN));
        priceRow.add(Box.createHorizontalStrut(8));
        priceRow.add(Theme.label(Theme.money(room.getPricePerNight()) + " x " + nights
                + (nights == 1 ? " night" : " nights"), Theme.SMALL, Theme.MUTED));
        priceRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, priceRow.getPreferredSize().height));

        // Book now button
        Theme.RoundButton bookBtn = Theme.button("Book now", Theme.ACCENT);
        bookBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        bookBtn.addActionListener(e -> startBooking(room));

        info.add(nameRow);
        info.add(Box.createVerticalStrut(2));
        info.add(amenities);
        info.add(Box.createVerticalStrut(8));
        info.add(priceRow);
        info.add(Box.createVerticalStrut(8));
        info.add(bookBtn);

        card.add(info, BorderLayout.CENTER);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        return card;
    }

    // ================= BOOKING FLOW =================
    private void startBooking(Room room) {
        LocalDate in = toLocalDate(inSpinner), out = toLocalDate(outSpinner);
        if (!datesValid(in, out)) return;
        long nights = ChronoUnit.DAYS.between(in, out);
        double total = nights * room.getPricePerNight();

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        Theme.input(nameField);
        Theme.input(phoneField);

        JPanel nameBox = Theme.labeled("Guest name", nameField);
        JPanel phoneBox = Theme.labeled("Phone (10 digits)", phoneField);
        nameBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        phoneBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(Theme.label(room.getCategory() + " Room " + room.getRoomNo(), Theme.HEAD, Theme.TEXT));
        form.add(Theme.label(in + " to " + out + "  (" + nights + (nights == 1 ? " night)" : " nights)"),
                Theme.SMALL, Theme.MUTED));
        form.add(Theme.label("Total: " + Theme.money(total), Theme.BOLD, Theme.GREEN));
        form.add(Box.createVerticalStrut(12));
        form.add(nameBox);
        form.add(Box.createVerticalStrut(10));
        form.add(phoneBox);
        form.setPreferredSize(new Dimension(320, 215));

        String name = "";
        String phone = "";
        while (true) {
            int choice = JOptionPane.showOptionDialog(this, form, "Guest details",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    new Object[]{"Continue to payment", "Cancel"}, "Continue to payment");
            if (choice != 0) return;

            name = nameField.getText().trim();
            phone = phoneField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter guest name.");
                continue;
            }
            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits.");
                continue;
            }
            break;
        }

        PaymentDialog pay = new PaymentDialog(this, total);
        pay.setVisible(true);
        if (!pay.isPaid()) return; // user ne payment cancel kar diya

        try {
            Booking b = service.book(room, name, phone, in, out, pay.getMethod());
            showText("Booking Confirmed", "Payment successful! Your booking is confirmed.\n\n" + b.getDetails());
            refreshBookings();
            refreshRoomsQuietly();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            refreshRoomsQuietly();
        }
    }

    // ================= PAGE 2: MY BOOKINGS =================
    private JPanel buildBookingsPage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(false);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Theme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER), new EmptyBorder(16, 18, 14, 18)));
        header.add(Theme.label("My Bookings", Theme.TITLE, Theme.TEXT));
        summaryLabel.setFont(Theme.FONT);
        summaryLabel.setForeground(Theme.MUTED);
        summaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(summaryLabel);
        page.add(header, BorderLayout.NORTH);

        bookingList = new Theme.ListPanel();
        bookingList.setBorder(new EmptyBorder(14, 16, 12, 16));
        JScrollPane scroll = new JScrollPane(bookingList);
        Theme.styleScroll(scroll);
        page.add(scroll, BorderLayout.CENTER);

        return page;
    }

    private void refreshBookings() {
        bookingList.removeAll();
        List<Booking> all = service.getBookings();

        int confirmed = 0;
        double revenue = 0;
        for (Booking b : all) {
            if (b.isConfirmed()) {
                confirmed++;
                revenue += b.getTotalAmount();
            }
        }
        // Nayi booking sabse upar
        for (int i = all.size() - 1; i >= 0; i--) {
            bookingList.add(buildBookingCard(all.get(i)));
            bookingList.add(Box.createVerticalStrut(12));
        }
        if (all.isEmpty()) {
            JLabel empty = Theme.label("No bookings yet", Theme.HEAD, Theme.MUTED);
            empty.setBorder(new EmptyBorder(40, 0, 0, 0));
            bookingList.add(empty);
        }
        summaryLabel.setText(all.size() + " bookings  |  " + confirmed + " confirmed  |  Revenue "
                + Theme.money(revenue));
        bookingList.revalidate();
        bookingList.repaint();
    }

    private Theme.Card buildBookingCard(Booking b) {
        Theme.Card card = new Theme.Card(new BorderLayout(12, 0));
        card.add(new Theme.RoomPhoto(b.getCategory(), 92, 118), BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        // Booking ID + status tag
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(Theme.label("Booking #" + b.getId(), Theme.HEAD, Theme.TEXT));
        top.add(Box.createHorizontalStrut(8));
        top.add(new Theme.Chip(b.getStatus(), b.isConfirmed() ? Theme.GREEN : Theme.RED));
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, top.getPreferredSize().height));

        JLabel line1 = Theme.label(b.getCategory() + " Room " + b.getRoomNo() + "  |  " + b.getGuestName(),
                Theme.FONT, Theme.TEXT);
        JLabel line2 = Theme.label(b.getCheckIn() + "  to  " + b.getCheckOut() + "  ("
                + b.getNights() + (b.getNights() == 1 ? " night)" : " nights)"), Theme.SMALL, Theme.MUTED);
        JLabel line3 = Theme.label(Theme.money(b.getTotalAmount()) + "  |  " + b.getPaymentMethod(),
                Theme.BOLD, b.isConfirmed() ? Theme.GREEN : Theme.MUTED);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(Component.LEFT_ALIGNMENT);
        Theme.RoundButton detailsBtn = Theme.outlineButton("Details", Theme.ACCENT);
        detailsBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        detailsBtn.addActionListener(e -> viewDetails(b));
        btns.add(detailsBtn);
        if (b.isConfirmed()) {
            Theme.RoundButton cancelBtn = Theme.outlineButton("Cancel", Theme.RED);
            cancelBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
            cancelBtn.addActionListener(e -> cancelBooking(b));
            btns.add(Box.createHorizontalStrut(8));
            btns.add(cancelBtn);
        }
        btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, btns.getPreferredSize().height));

        info.add(top);
        info.add(Box.createVerticalStrut(4));
        info.add(line1);
        info.add(Box.createVerticalStrut(2));
        info.add(line2);
        info.add(Box.createVerticalStrut(4));
        info.add(line3);
        info.add(Box.createVerticalStrut(8));
        info.add(btns);

        card.add(info, BorderLayout.CENTER);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        return card;
    }

    private void viewDetails(Booking b) {
        showText("Booking Details", b.getDetails());
    }

    private void cancelBooking(Booking b) {
        int ok = JOptionPane.showConfirmDialog(this,
                "Cancel booking " + b.getId() + "?\n(Refund: 100% if check-in is 2+ days away, else 50%)",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        double refund = service.cancel(b.getId());
        JOptionPane.showMessageDialog(this, "Booking cancelled.\nRefund amount: " + Theme.money(refund));
        refreshBookings();
        refreshRoomsQuietly();
    }

    // ================= HELPERS =================
    private void showText(String title, String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setOpaque(false);
        area.setForeground(Theme.TEXT);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, area, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Dates theek hon to rooms ki list dobara bana deta hai (bina popup ke)
    private void refreshRoomsQuietly() {
        LocalDate in = toLocalDate(inSpinner), out = toLocalDate(outSpinner);
        if (!in.isBefore(LocalDate.now()) && out.isAfter(in)) {
            searchRooms();
        }
    }

    private boolean datesValid(LocalDate in, LocalDate out) {
        if (in.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Check-in date cannot be in the past.");
            return false;
        }
        if (!out.isAfter(in)) {
            JOptionPane.showMessageDialog(this, "Check-out must be after check-in.");
            return false;
        }
        return true;
    }

    private JSpinner makeDateSpinner(LocalDate date) {
        Date d = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        JSpinner sp = new JSpinner(new SpinnerDateModel(d, null, null, Calendar.DAY_OF_MONTH));
        sp.setEditor(new JSpinner.DateEditor(sp, "yyyy-MM-dd"));
        return sp;
    }

    private LocalDate toLocalDate(JSpinner sp) {
        return ((Date) sp.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}