import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

// Dark mode theme: saare colors, fonts aur custom components yahan hain
public class Theme {

    // ---------- Colors (dark mode) ----------
    public static final Color ACCENT   = new Color(250, 190, 0);   // yellow
    public static final Color GOLD     = new Color(250, 190, 0);   // stars
    public static final Color GREEN    = new Color(52, 211, 153);
    public static final Color RED      = new Color(248, 113, 113);
    public static final Color BG       = new Color(17, 19, 26);    // sabse peeche ka dark
    public static final Color SURFACE  = new Color(28, 31, 41);    // cards, header
    public static final Color SURFACE2 = new Color(40, 44, 56);    // input boxes
    public static final Color TEXT     = new Color(236, 239, 244);
    public static final Color MUTED    = new Color(140, 148, 163);
    public static final Color BORDER   = new Color(56, 61, 76);

    public static final String RUPEE = "\u20B9";

    public static final Font FONT  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOLD  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font HEAD  = new Font("Segoe UI", Font.BOLD, 16);

    // Program shuru hote hi ek baar chalta hai: popup windows ko bhi dark banata hai
    public static void install() {
        UIManager.put("Panel.background", BG);
        UIManager.put("OptionPane.background", BG);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", BOLD);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("TextArea.foreground", TEXT);
        UIManager.put("TextField.inactiveBackground", SURFACE);
        UIManager.put("TextField.inactiveForeground", MUTED);
    }

    // ---------- Chhote helpers ----------
    public static String money(double v) {
        return RUPEE + String.format("%,.0f", v);
    }

    public static int starsFor(String category) {
        switch (category) {
            case "Suite":  return 5;
            case "Deluxe": return 4;
            default:       return 3;
        }
    }

    // Do colors ko mix karta hai (t = 0 pehla color, t = 1 doosra color)
    public static Color mix(Color a, Color b, float t) {
        int r = (int) (a.getRed() * (1 - t) + b.getRed() * t);
        int g = (int) (a.getGreen() * (1 - t) + b.getGreen() * t);
        int bl = (int) (a.getBlue() * (1 - t) + b.getBlue() * t);
        return new Color(r, g, bl);
    }

    // Button ke andar ka text: bright color (yellow) par dark text, warna white
    private static Color onColor(Color c) {
        double lum = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
        return lum > 160 ? new Color(25, 27, 34) : Color.WHITE;
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // Upar chhota caption, neeche input box
    public static JPanel labeled(String caption, JComponent c) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(caption);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(c);
        return p;
    }

    public static void input(JTextField f) {
        f.setFont(FONT);
        f.setBackground(SURFACE2);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 36));
    }

    public static void spinner(JSpinner sp, int w) {
        sp.setPreferredSize(new Dimension(w, 36));
        sp.setBackground(SURFACE2);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        if (sp.getEditor() instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor ed = (JSpinner.DefaultEditor) sp.getEditor();
            ed.setBackground(SURFACE2);
            JTextField tf = ed.getTextField();
            tf.setFont(FONT);
            tf.setBackground(SURFACE2);
            tf.setForeground(TEXT);
            tf.setCaretColor(TEXT);
            tf.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 4));
        }
    }

    // Scroll area ko dark aur patla scrollbar deta hai
    public static void styleScroll(JScrollPane sp) {
        sp.setBorder(null);
        sp.setBackground(BG);
        sp.getViewport().setBackground(BG);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        sp.getVerticalScrollBar().setUnitIncrement(18);
    }

    // ---------- Dark scrollbar ----------
    public static class DarkScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            trackColor = BG;
            thumbColor = new Color(90, 96, 112);
        }

        @Override protected JButton createDecreaseButton(int orientation) { return zeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return zeroButton(); }

        private JButton zeroButton() {
            JButton b = new JButton();
            Dimension d = new Dimension(0, 0);
            b.setPreferredSize(d);
            b.setMinimumSize(d);
            b.setMaximumSize(d);
            return b;
        }

        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(BG);
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
            g2.dispose();
        }

        @Override public Dimension getPreferredSize(JComponent c) {
            return new Dimension(10, super.getPreferredSize(c).height);
        }
    }

    // ---------- Dark rounded card ----------
    public static class Card extends JPanel {
        public Card(LayoutManager lm) {
            super(lm);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 16, 14));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(SURFACE);
            g2.fillRoundRect(0, 0, w - 4, h - 6, 18, 18);
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, w - 5, h - 7, 18, 18);
            g2.dispose();
        }
    }

    // ---------- Rounded button (filled ya outline) ----------
    public static class RoundButton extends JButton {
        private final Color color;
        private boolean filled;

        public RoundButton(String text, Color color, boolean filled) {
            super(text);
            this.color = color;
            this.filled = filled;
            setFont(BOLD);
            setForeground(filled ? onColor(color) : color);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        }

        public void setFilled(boolean f) {
            filled = f;
            setForeground(f ? onColor(color) : color);
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            boolean over = getModel().isRollover();
            boolean down = getModel().isPressed();
            if (filled) {
                Color c = color;
                if (!isEnabled()) c = new Color(70, 75, 88);
                else if (down) c = mix(color, Color.BLACK, 0.15f);
                else if (over) c = mix(color, Color.WHITE, 0.15f);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, w, h, 12, 12);
            } else {
                g2.setColor(over ? mix(SURFACE, color, 0.18f) : SURFACE);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static RoundButton button(String text, Color color) {
        return new RoundButton(text, color, true);
    }

    public static RoundButton outlineButton(String text, Color color) {
        return new RoundButton(text, color, false);
    }

    // ---------- Chhota rangeen tag (jaise CONFIRMED) ----------
    public static class Chip extends JLabel {
        private final Color color;

        public Chip(String text, Color color) {
            super(text, SwingConstants.CENTER);
            this.color = color;
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(color);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 45));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------- Stars (font se nahi, drawing se - isliye box nahi dikhega) ----------
    public static class Stars extends JComponent {
        private final int count;

        public Stars(int count) {
            this.count = count;
            Dimension d = new Dimension(count * 17, 16);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(GOLD);
            for (int i = 0; i < count; i++) {
                double cx = i * 17 + 8.5;
                double cy = 8;
                Path2D.Double star = new Path2D.Double();
                for (int k = 0; k < 10; k++) {
                    double r = (k % 2 == 0) ? 7.5 : 3.2;
                    double a = Math.PI / 5 * k - Math.PI / 2;
                    double x = cx + r * Math.cos(a);
                    double y = cy + r * Math.sin(a);
                    if (k == 0) star.moveTo(x, y); else star.lineTo(x, y);
                }
                star.closePath();
                g2.fill(star);
            }
            g2.dispose();
        }
    }

    // ---------- Room ki photo (file ho to wo, warna drawing) ----------
    public static class RoomPhoto extends JPanel {
        private static final Map<String, BufferedImage> CACHE = new HashMap<>();

        private final String category;
        private final BufferedImage photo;

        public RoomPhoto(String category, int w, int h) {
            this.category = category;
            this.photo = loadPhoto(category);
            setOpaque(false);
            setPreferredSize(new Dimension(w, h));
            setMinimumSize(new Dimension(w, h));
            setMaximumSize(new Dimension(w, h));
        }

        // Ek baar load karke yaad rakhta hai, baar-baar file nahi padhta
        private static BufferedImage loadPhoto(String category) {
            if (CACHE.containsKey(category)) return CACHE.get(category);
            BufferedImage img = findPhoto(category);
            CACHE.put(category, img);
            return img;
        }

        // images folder mein standard.jpg / deluxe.png / suite.jpeg dhundhta hai
        private static BufferedImage findPhoto(String category) {
            String key = category.toLowerCase();
            File[] dirs = {new File("images"), new File("..", "images")};
            for (File dir : dirs) {
                File[] files = dir.listFiles();
                if (files == null) continue;
                for (File f : files) {
                    String n = f.getName().toLowerCase();
                    boolean okExt = n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png");
                    if (okExt && n.startsWith(key + ".")) {
                        try {
                            BufferedImage img = ImageIO.read(f);
                            if (img != null) return shrink(img, 420);
                            System.out.println("Photo padh nahi paya: " + f.getAbsolutePath());
                        } catch (Exception e) {
                            System.out.println("Photo error: " + f.getAbsolutePath());
                        }
                    }
                }
            }
            System.out.println("Photo nahi mili: " + category
                    + "  | yahan dhundha: " + new File("images").getAbsolutePath());
            return null;
        }

        // Badi photo ko chhota karta hai taaki app tez rahe
        private static BufferedImage shrink(BufferedImage src, int maxW) {
            if (src.getWidth() <= maxW) return src;
            int w = maxW;
            int h = src.getHeight() * maxW / src.getWidth();
            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = out.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(src, 0, 0, w, h, null);
            g.dispose();
            return out;
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            int w = getWidth(), h = getHeight();
            g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, 14, 14));
            if (photo != null) {
                double scale = Math.max((double) w / photo.getWidth(), (double) h / photo.getHeight());
                int dw = (int) (photo.getWidth() * scale);
                int dh = (int) (photo.getHeight() * scale);
                g2.drawImage(photo, (w - dw) / 2, (h - dh) / 2, dw, dh, null);
            } else {
                drawIllustration(g2, w, h);
            }
            g2.dispose();
        }

        private void drawIllustration(Graphics2D g2, int w, int h) {
            Color wall, accent;
            switch (category) {
                case "Deluxe":
                    wall = new Color(52, 40, 78);
                    accent = new Color(167, 139, 250);
                    break;
                case "Suite":
                    wall = new Color(74, 58, 30);
                    accent = new Color(251, 191, 36);
                    break;
                default:
                    wall = new Color(36, 52, 82);
                    accent = new Color(96, 165, 250);
                    break;
            }
            g2.setColor(wall);
            g2.fillRect(0, 0, w, h);
            g2.setColor(mix(wall, Color.BLACK, 0.3f));
            g2.fillRect(0, h * 72 / 100, w, h);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillRoundRect(w * 68 / 100, h * 8 / 100, w * 22 / 100, h * 24 / 100, 6, 6);
            g2.setColor(mix(accent, Color.BLACK, 0.35f));
            g2.fillRoundRect(w * 12 / 100, h * 28 / 100, w * 76 / 100, h * 34 / 100, 10, 10);
            g2.setColor(new Color(235, 238, 245));
            g2.fillRoundRect(w * 8 / 100, h * 50 / 100, w * 84 / 100, h * 22 / 100, 12, 12);
            g2.setColor(accent);
            g2.fillRoundRect(w * 8 / 100, h * 58 / 100, w * 84 / 100, h * 20 / 100, 12, 12);
            g2.setColor(new Color(250, 250, 250));
            g2.fillRoundRect(w * 16 / 100, h * 42 / 100, w * 28 / 100, h * 14 / 100, 8, 8);
            g2.fillRoundRect(w * 52 / 100, h * 42 / 100, w * 28 / 100, h * 14 / 100, 8, 8);
            g2.setColor(mix(accent, Color.BLACK, 0.35f));
            g2.fillRect(w * 11 / 100, h * 77 / 100, 6, h * 9 / 100);
            g2.fillRect(w * 85 / 100, h * 77 / 100, 6, h * 9 / 100);
        }
    }

    // ---------- Neeche wale navigation ka button (icon + naam) ----------
    public static class NavButton extends JButton {
        private final int icon; // 0 = home, 1 = list
        private boolean active;

        public NavButton(String text, int icon) {
            super(text);
            this.icon = icon;
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setVerticalAlignment(SwingConstants.BOTTOM);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(6, 10, 8, 10));
            setPreferredSize(new Dimension(120, 62));
            setActive(false);
        }

        public void setActive(boolean a) {
            active = a;
            setForeground(a ? ACCENT : MUTED);
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(active ? ACCENT : MUTED);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = getWidth() / 2;
            int y = 8;
            if (icon == 0) {
                g2.drawLine(cx - 11, y + 11, cx, y);
                g2.drawLine(cx, y, cx + 11, y + 11);
                g2.drawRect(cx - 8, y + 11, 16, 10);
                g2.drawRect(cx - 3, y + 15, 6, 6);
            } else {
                for (int i = 0; i < 3; i++) {
                    g2.fillOval(cx - 11, y + 2 + i * 7, 4, 4);
                    g2.drawLine(cx - 4, y + 4 + i * 7, cx + 11, y + 4 + i * 7);
                }
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------- Scroll hone wali list (width window ke barabar rehti hai) ----------
    public static class ListPanel extends JPanel implements Scrollable {
        public ListPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
        }

        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 24; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 120; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
}