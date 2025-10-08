package it.uninaswap.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Enumeration;

public final class UITheme {

  // ========= Palette =========
  public static final Color INK          = new Color(27, 31, 39);     // testo
  public static final Color SUBTLE_INK   = new Color(98, 105, 117);   // testo attenuato
  public static final Color ACCENT       = new Color(63, 118, 234);   // blu primario
  public static final Color ACCENT_DARK  = new Color(47, 88, 178);
  public static final Color SUCCESS      = new Color(17, 165, 114);
  public static final Color WARN         = new Color(230, 149, 0);
  public static final Color DANGER       = new Color(210, 55, 72);

  public static final Color BG           = new Color(247, 249, 253);  // sfondo app
  public static final Color CARD         = Color.WHITE;               // sfondo card
  public static final Color BORDER       = new Color(222, 228, 238);
  public static final Color BORDER_SOFT  = new Color(234, 238, 246);
  public static final Color ROW_ALT      = new Color(251, 253, 255);
  public static final Color SELECTION    = new Color(227, 236, 255);

  private UITheme() {}

  // ========= L&F + font globale =========
  public static void installNimbusTheme() {
    try {
      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (Exception ignored) {}
    UIManager.put("control", CARD);
    UIManager.put("nimbusLightBackground", CARD);
    UIManager.put("text", INK);
    UIManager.put("nimbusFocus", ACCENT);
    UIManager.put("Table.alternateRowColor", ROW_ALT);
  }

  public static void applyGlobalFont(float size) {
    Font base = new Font("Segoe UI", Font.PLAIN, Math.round(size));
    Enumeration<Object> keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object k = keys.nextElement();
      Object v = UIManager.get(k);
      if (v instanceof Font) UIManager.put(k, base);
    }
  }

  // ========= Helpers layout =========
  public static void padded(JComponent c) {
    c.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
  }

  public static void hairline(JComponent c) {
    c.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, BORDER));
  }

  public static JPanel card() {
    JPanel p = new JPanel();
    p.setBackground(CARD);
    p.setBorder(new CompoundBorder(
        new MatteBorder(1,1,1,1, BORDER_SOFT),
        new EmptyBorder(12,14,12,14)
    ));
    return p;
  }

  public static JPanel appHeader(String title, String subtitle, Icon icon) {
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(new Color(242, 246, 255));
    header.setBorder(new CompoundBorder(
        new MatteBorder(0,0,1,0, BORDER_SOFT),
        new EmptyBorder(14,18,14,18)
    ));

    JLabel tl = new JLabel(title);
    tl.setFont(tl.getFont().deriveFont(Font.BOLD, 20f));
    tl.setForeground(INK);

    JLabel sub = new JLabel(subtitle);
    sub.setForeground(SUBTLE_INK);

    JPanel text = new JPanel();
    text.setOpaque(false);
    text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
    text.add(tl);
    text.add(Box.createVerticalStrut(2));
    text.add(sub);

    if (icon != null) {
      JLabel ic = new JLabel(icon);
      JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      left.setOpaque(false);
      left.add(ic);
      left.add(Box.createHorizontalStrut(10));

      JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      wrap.setOpaque(false);
      wrap.add(left);
      wrap.add(text);
      header.add(wrap, BorderLayout.WEST);
    } else {
      header.add(text, BorderLayout.WEST);
    }
    return header;
  }

  public static void muted(JLabel l) { l.setForeground(SUBTLE_INK); }

  // ========= Toolbar =========
  public static void styleToolbar(JToolBar tb) {
    tb.setFloatable(false);
    tb.setBackground(CARD);
    tb.setBorder(new CompoundBorder(
        new MatteBorder(0,0,1,0, BORDER_SOFT),
        new EmptyBorder(6,12,6,12)
    ));
  }

  // ========= Bottoni =========
  public static void primary(JButton b) {
    baseButton(b);
    b.setBackground(ACCENT);
    b.setForeground(Color.WHITE);
    b.setBorder(new LineBorder(ACCENT_DARK, 1, true));
  }

  public static void secondary(JButton b) {
    baseButton(b);
    b.setBackground(CARD);
    b.setForeground(INK);
    b.setBorder(new LineBorder(BORDER, 1, true));
  }

  public static void ghost(JButton b) {
    baseButton(b);
    b.setContentAreaFilled(false);
    b.setOpaque(false);
    b.setForeground(ACCENT);
    b.setBorder(new LineBorder(new Color(0,0,0,0), 1, true));
  }

  public static void danger(JButton b) {
    baseButton(b);
    b.setBackground(DANGER);
    b.setForeground(Color.WHITE);
    b.setBorder(new LineBorder(DANGER.darker(), 1, true));
  }

  private static void baseButton(AbstractButton b) {
    b.setFocusPainted(false);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    b.setMargin(new Insets(6, 12, 6, 12));
  }
}
