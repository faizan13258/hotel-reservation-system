import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Theme.install(); // dark mode popups ke liye
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}