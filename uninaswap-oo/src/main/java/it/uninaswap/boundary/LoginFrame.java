package it.uninaswap.boundary;

import it.uninaswap.control.Controller;
import it.uninaswap.model.Utente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Finestra di login con hero/intro e form a destra.
 * Semplificata per Windows: usa "Segoe UI Emoji" per le emoji nel riquadro sinistro.
 */
public class LoginFrame extends JFrame {
  private static final long serialVersionUID = 1L;

  private final Controller controller;

  private JTextField userTf;
  private JPasswordField passPf;
  private JLabel msgLbl;

  public LoginFrame(Controller controller) {
    super("UninaSwap – Login");
    this.controller = controller;
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(820, 520);
    setLocationRelativeTo(null);
    setContentPane(buildUi());
  }
  
  private void doLogin() {
	    String u = userTf.getText() == null ? "" : userTf.getText().trim();
	    String p = new String(passPf.getPassword());

	    if (u.isEmpty() || p.isEmpty()) {
	      msgLbl.setText("Inserisci username e password.");
	      return;
	    }

	    Utente logged = controller.doLogin(u, p);
	    if (logged != null) {
	      msgLbl.setText(" ");
	      dispose();
	      new AnnunciFrame(controller).setVisible(true);
	    } else {
	      msgLbl.setText("Credenziali non valide. Riprova.");
	    }
	  }

  private JComponent buildUi() {
    JPanel root = new JPanel(new BorderLayout());
    root.setBackground(new Color(246, 248, 252));

    // --- Header (benvenuto) ---
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(new Color(238, 242, 255));
    header.setBorder(new EmptyBorder(24, 28, 24, 28));

    JLabel title = new JLabel("Benvenuto in UninaSwap");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
    JLabel subtitle = new JLabel("<html>L’applicativo che consente compravendita, scambi e regali tra studenti."
        + "<br/>Accedi per esplorare gli annunci e inviare le tue offerte.</html>");
    subtitle.setFont(subtitle.getFont().deriveFont(14f));
    subtitle.setForeground(new Color(60, 65, 80));

    JPanel headerText = new JPanel(new GridLayout(0,1,0,6));
    headerText.setOpaque(false);
    headerText.add(title);
    headerText.add(subtitle);

    header.add(headerText, BorderLayout.WEST);
    root.add(header, BorderLayout.NORTH);

    // --- Corpo (due colonne) ---
    JPanel body = new JPanel(new GridBagLayout());
    body.setOpaque(false);
    body.setBorder(new EmptyBorder(24, 28, 24, 28));
    GridBagConstraints gc = new GridBagConstraints();
    gc.insets = new Insets(0, 0, 0, 0);
    gc.fill = GridBagConstraints.BOTH;

    // Colonna sinistra: hero panel/illustrazione
    JPanel hero = new JPanel(new BorderLayout());
    hero.setBackground(Color.WHITE);
    hero.setBorder(new RoundedBorder(18, new Color(225, 230, 245)));
    hero.add(makeHeroContent(), BorderLayout.CENTER);

    gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.55; gc.weighty = 1.0;
    body.add(hero, gc);

    // Colonna destra: pannello login
    JPanel formCard = new JPanel(new GridBagLayout());
    formCard.setBackground(Color.WHITE);
    formCard.setBorder(new RoundedBorder(18, new Color(225, 230, 245)));
    GridBagConstraints fg = new GridBagConstraints();
    fg.insets = new Insets(10, 16, 10, 16);
    fg.anchor = GridBagConstraints.WEST;
    fg.fill = GridBagConstraints.HORIZONTAL;
    fg.gridx = 0; fg.gridy = 0; fg.weightx = 1.0;

    JLabel formTitle = new JLabel("Accedi al tuo account");
    formTitle.setFont(formTitle.getFont().deriveFont(Font.BOLD, 18f));
    formCard.add(formTitle, fg);

    // Username
    fg.gridy++;
    formCard.add(new JLabel("Username"), fg);
    fg.gridy++;
    userTf = new JTextField();
    userTf.setPreferredSize(new Dimension(260, 34));
    formCard.add(userTf, fg);

    // Password + toggle
    fg.gridy++;
    formCard.add(new JLabel("Password"), fg);
    fg.gridy++;
    passPf = new JPasswordField();
    passPf.setPreferredSize(new Dimension(260, 34));
    formCard.add(passPf, fg);

    fg.gridy++;
    JCheckBox showPwd = new JCheckBox("Mostra password");
    showPwd.setOpaque(false);
    showPwd.addActionListener(e -> {
      if (showPwd.isSelected()) passPf.setEchoChar((char)0);
      else passPf.setEchoChar('\u2022'); // bullet
    });
    formCard.add(showPwd, fg);

    // Messaggi
    fg.gridy++;
    msgLbl = new JLabel(" ");
    msgLbl.setForeground(new Color(180, 40, 40));
    formCard.add(msgLbl, fg);

    // Bottoni
    fg.gridy++;
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttons.setOpaque(false);
    JButton btnAccedi = new JButton("Accedi");
    JButton btnAnnulla = new JButton("Esci");
    buttons.add(btnAnnulla);
    buttons.add(btnAccedi);
    formCard.add(buttons, fg);

    // Azioni
    btnAccedi.addActionListener(e -> doLogin());
    btnAnnulla.addActionListener(e -> System.exit(0));
    getRootPane().setDefaultButton(btnAccedi);

    // Piccolo testo note
    fg.gridy++;
    JLabel note = new JLabel("<html><span style='color:#7a7f8c'>Suggerimento:</span> usa l’account di popolamento, es. <b>AlessioAnepeta / ciao123</b>.</html>");
    formCard.add(note, fg);

    gc.gridx = 1; gc.gridy = 0; gc.weightx = 0.45; gc.insets = new Insets(0, 18, 0, 0);
    body.add(formCard, gc);

    root.add(body, BorderLayout.CENTER);
    return root;
  }

  // --- HERO content: emoji semplificate per Windows ---
  private JComponent makeHeroContent() {
    JPanel p = new JPanel(new GridBagLayout());
    p.setOpaque(false);
    GridBagConstraints gc = new GridBagConstraints();
    gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.CENTER;

    JLabel big = new JLabel("🛍️  🔄  🎁");
    big.setFont(emojiFontWin(48f)); // usa Segoe UI Emoji su Windows

    JLabel line1 = new JLabel("Compra • Scambia • Regala");
    line1.setFont(line1.getFont().deriveFont(Font.BOLD, 20f));

    JLabel line2 = new JLabel("<html><div style='text-align:center;color:#3b4255'>"
        + "Gestisci annunci, invia offerte e tieni traccia delle tue attività in pochi clic."
        + "</div></html>");
    line2.setFont(line2.getFont().deriveFont(14f));

    JPanel inner = new JPanel();
    inner.setOpaque(false);
    inner.setLayout(new GridLayout(0,1,0,8));
    inner.add(big);
    inner.add(line1);
    inner.add(line2);

    p.add(inner, gc);
    return p;
  }

  // Font emoji per Windows (fallback al font corrente)
  private Font emojiFontWin(float size) {
    try {
      return new Font("Segoe UI Emoji", Font.PLAIN, Math.round(size));
    } catch (Exception e) {
      Font base = getFont();
      if (base == null) base = UIManager.getFont("Label.font");
      return base.deriveFont(size);
    }
  }



  /** Bordo arrotondato semplice per le card. */
  static class RoundedBorder extends javax.swing.border.LineBorder {
    private static final long serialVersionUID = 1L;
    private final int arc;

    public RoundedBorder(int radius, Color color) {
      super(color, 1, true);
      this.arc = radius;
    }

    @Override
    public Insets getBorderInsets(Component c) { return new Insets(16,16,16,16); }

    @Override
    public boolean isBorderOpaque() { return false; }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(lineColor != null ? lineColor : new Color(220, 226, 240));
      g2.drawRoundRect(x, y, width-1, height-1, arc, arc);
      g2.dispose();
    }
  }
}
