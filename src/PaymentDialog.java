import javax.swing.*;
import java.awt.*;

// Payment simulation: asli paisa nahi katta, sirf demo hai
public class PaymentDialog extends JDialog {
    private boolean paid = false;
    private String method = "";
    private String selected = "Card";

    // Screen par jo naam dikhte hain aur booking mein jo save hota hai
    private final String[] shown = {"Card", "UPI", "Cash"};
    private final String[] saved = {"Card", "UPI", "Cash at Hotel"};

    private final Theme.RoundButton[] methodButtons = new Theme.RoundButton[3];
    private final JLabel detailLabel = new JLabel();
    private final JTextField detailField = new JTextField();
    private final Theme.RoundButton payButton = Theme.button("Pay Now", Theme.ACCENT);

    public PaymentDialog(JFrame parent, double amount) {
        super(parent, "Payment", true);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Theme.BG);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Heading
        JLabel head = Theme.label("Complete Payment", Theme.HEAD, Theme.TEXT);
        root.add(head);
        root.add(Box.createVerticalStrut(12));

        // Amount card
        Theme.Card amountCard = new Theme.Card(new BorderLayout());
        amountCard.add(Theme.label("Amount to pay", Theme.SMALL, Theme.MUTED), BorderLayout.NORTH);
        amountCard.add(Theme.label(Theme.money(amount), new Font("Segoe UI", Font.BOLD, 28), Theme.TEXT),
                BorderLayout.CENTER);
        amountCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(amountCard);
        root.add(Box.createVerticalStrut(14));

        // Payment method buttons
        root.add(Theme.label("Payment method", new Font("Segoe UI", Font.BOLD, 12), Theme.MUTED));
        root.add(Box.createVerticalStrut(6));
        JPanel methodRow = new JPanel(new GridLayout(1, 3, 8, 0));
        methodRow.setOpaque(false);
        methodRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        methodRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        for (int i = 0; i < 3; i++) {
            final String name = shown[i];
            methodButtons[i] = Theme.outlineButton(name, Theme.ACCENT);
            methodButtons[i].setBorder(BorderFactory.createEmptyBorder(9, 8, 9, 8));
            methodButtons[i].addActionListener(e -> selectMethod(name));
            methodRow.add(methodButtons[i]);
        }
        root.add(methodRow);
        root.add(Box.createVerticalStrut(14));

        // Card number / UPI ID ka box
        detailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailLabel.setForeground(Theme.MUTED);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(detailLabel);
        root.add(Box.createVerticalStrut(6));
        Theme.input(detailField);
        detailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        root.add(detailField);
        root.add(Box.createVerticalStrut(20));

        // Neeche ke buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        Theme.RoundButton cancelBtn = Theme.outlineButton("Cancel", Theme.MUTED);
        cancelBtn.addActionListener(e -> dispose());
        payButton.addActionListener(e -> processPayment());
        btnRow.add(cancelBtn);
        btnRow.add(payButton);
        root.add(btnRow);

        setContentPane(root);
        selectMethod("Card");

        pack();
        setSize(440, getHeight());
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    // Kaunsa method chuna hai, uske hisaab se buttons aur label badalta hai
    private void selectMethod(String name) {
        selected = name;
        for (int i = 0; i < 3; i++) {
            methodButtons[i].setFilled(shown[i].equals(name));
        }
        detailField.setText("");
        if (name.equals("Card")) {
            detailLabel.setText("Card number (16 digits)");
            detailField.setEnabled(true);
        } else if (name.equals("UPI")) {
            detailLabel.setText("UPI ID (example: rahul@upi)");
            detailField.setEnabled(true);
        } else {
            detailLabel.setText("No details needed - pay at the hotel");
            detailField.setEnabled(false);
        }
    }

    private String savedName() {
        for (int i = 0; i < 3; i++) {
            if (shown[i].equals(selected)) return saved[i];
        }
        return selected;
    }

    private void processPayment() {
        String detail = detailField.getText().trim();

        if (selected.equals("Card") && !detail.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this, "Card number must be exactly 16 digits.");
            return;
        }
        if (selected.equals("UPI") && !detail.matches("[\\w.\\-]+@[\\w]+")) {
            JOptionPane.showMessageDialog(this, "Enter a valid UPI ID, e.g. rahul@upi");
            return;
        }

        payButton.setEnabled(false);
        payButton.setText("Processing...");
        final String finalMethod = savedName();
        // 1.5 second ka fake processing
        Timer timer = new Timer(1500, e -> {
            paid = true;
            method = finalMethod;
            dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public boolean isPaid() { return paid; }
    public String getMethod() { return method; }
}