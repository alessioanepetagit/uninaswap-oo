package it.uninaswap.boundary;

import it.uninaswap.control.Controller;
import it.uninaswap.exceptions.BusinessException;
import it.uninaswap.model.Categoria;
import it.uninaswap.model.Oggetto;
import it.uninaswap.model.enums.TipoAnnuncio;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CreaAnnuncioDialog extends JDialog {
  private static final long serialVersionUID = 1L;

  private final Controller controller;

  private JComboBox<TipoAnnuncio> cboTipo;
  private JComboBox<Categoria> cboCategoria;
  private JComboBox<Oggetto> cboOggetto;
  private JTextField txtConsegna;
  private JTextField txtPrezzo;   // solo per vendita
  private JTextArea  txtDescr;    // opzionale

  public CreaAnnuncioDialog(Frame owner, Controller controller) {
    super(owner, "Crea annuncio", true);
    this.controller = controller;
    setSize(720, 560);
    setLocationRelativeTo(owner);
    buildUi();
    loadData();
  }

  // ---------- Helpers UI ----------
  private Font uiFont(float size, int style) {
    Font base = getFont();
    if (base == null) base = UIManager.getFont("Label.font");
    return base.deriveFont(style, size);
  }

  private JTextField bigTextField(int columns) {
    JTextField f = new JTextField(columns);
    f.setFont(uiFont(15f, Font.PLAIN));
    f.setPreferredSize(new Dimension(380, 40));
    f.setMinimumSize(new Dimension(280, 36));
    f.setMargin(new Insets(6, 10, 6, 10));
    return f;
  }

  private JTextArea bigTextArea(int rows, int cols) {
    JTextArea a = new JTextArea(rows, cols);
    a.setFont(uiFont(15f, Font.PLAIN));
    a.setLineWrap(true);
    a.setWrapStyleWord(true);
    a.setMargin(new Insets(8, 10, 8, 10));
    return a;
  }

  private JComboBox<?> growCombo(JComboBox<?> combo) {
    combo.setFont(uiFont(15f, Font.PLAIN));
    combo.setPreferredSize(new Dimension(380, 40));
    combo.setMinimumSize(new Dimension(300, 36));
    return combo;
  }

  private JButton bigButton(String text, boolean primary) {
    JButton b = new JButton(text);
    b.setFont(uiFont(15f, Font.BOLD));
    b.setPreferredSize(new Dimension(200, 44));
    if (primary) {
      b.setBackground(new Color(25,118,210));
      b.setForeground(Color.WHITE);
      b.setOpaque(true);
      b.setBorder(BorderFactory.createEmptyBorder(8,14,8,14));
    } else {
      b.setBackground(new Color(245,247,250));
      b.setForeground(Color.DARK_GRAY);
      b.setOpaque(true);
      b.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(new Color(220,225,232)),
          BorderFactory.createEmptyBorder(8,14,8,14)
      ));
    }
    return b;
  }

  // ---------- UI ----------
  private void buildUi() {
    JPanel root = new JPanel(new BorderLayout(12,12));
    root.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

    JLabel title = new JLabel("Nuovo annuncio");
    title.setFont(uiFont(20f, Font.BOLD));
    root.add(title, BorderLayout.NORTH);

    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints l = new GridBagConstraints(); // label
    GridBagConstraints f = new GridBagConstraints(); // field

    l.gridx = 0; l.weightx = 0; l.insets = new Insets(10,10,10,10); l.anchor = GridBagConstraints.EAST;
    f.gridx = 1; f.weightx = 1; f.insets = new Insets(10,10,10,10); f.fill = GridBagConstraints.HORIZONTAL;

    int row = 0;

    // Tipo
    l.gridy = f.gridy = row++;
    JLabel lTipo = new JLabel("Tipo:");
    lTipo.setFont(uiFont(15f, Font.PLAIN));
    form.add(lTipo, l);
    cboTipo = new JComboBox<>(TipoAnnuncio.values());
    growCombo(cboTipo);
    form.add(cboTipo, f);

    // Categoria
    l.gridy = f.gridy = row++;
    JLabel lCat = new JLabel("Categoria:");
    lCat.setFont(uiFont(15f, Font.PLAIN));
    form.add(lCat, l);
    cboCategoria = new JComboBox<>();
    growCombo(cboCategoria);
    cboCategoria.setRenderer(new DefaultListCellRenderer(){
    private static final long serialVersionUID = 1L; // <--- AGGIUNGI QUESTO

      @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Categoria c) setText(c.getNome());
        return this;
      }
    });
    form.add(cboCategoria, f);

    // Oggetto
    l.gridy = f.gridy = row++;
    JLabel lObj = new JLabel("Oggetto:");
    lObj.setFont(uiFont(15f, Font.PLAIN));
    form.add(lObj, l);
    cboOggetto = new JComboBox<>();
    growCombo(cboOggetto);
    cboOggetto.setRenderer(new DefaultListCellRenderer(){
    private static final long serialVersionUID = 1L;
      @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Oggetto o) setText(o.getNome());
        return this;
      }
    });
    form.add(cboOggetto, f);

    // Modalità consegna
    l.gridy = f.gridy = row++;
    JLabel lCons = new JLabel("Modalità consegna:");
    lCons.setFont(uiFont(15f, Font.PLAIN));
    form.add(lCons, l);
    txtConsegna = bigTextField(24);
    form.add(txtConsegna, f);

    // Prezzo (solo vendita)
    l.gridy = f.gridy = row++;
    JLabel lPrezzo = new JLabel("Prezzo (€):");
    lPrezzo.setFont(uiFont(15f, Font.PLAIN));
    form.add(lPrezzo, l);
    txtPrezzo = bigTextField(10);
    txtPrezzo.setPreferredSize(new Dimension(200, 40));
    form.add(txtPrezzo, f);

    // Descrizione opz.
    l.gridy = f.gridy = row++;
    JLabel lDesc = new JLabel("Descrizione (opz.):");
    lDesc.setFont(uiFont(15f, Font.PLAIN));
    form.add(lDesc, l);
    txtDescr = bigTextArea(6, 28);
    JScrollPane spDesc = new JScrollPane(txtDescr);
    spDesc.setPreferredSize(new Dimension(420, 140));
    form.add(spDesc, f);

    cboTipo.addActionListener(e -> togglePrezzo());

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
    JButton btnAnnulla = bigButton("Annulla", false);
    JButton btnCrea    = bigButton("Crea annuncio", true);
    buttons.add(btnAnnulla);
    buttons.add(btnCrea);

    btnAnnulla.addActionListener(e -> dispose());
    btnCrea.addActionListener(e -> onCreate());

    root.add(form, BorderLayout.CENTER);
    root.add(buttons, BorderLayout.SOUTH);
    setContentPane(root);

    togglePrezzo();
  }

  private void togglePrezzo() {
    TipoAnnuncio t = (TipoAnnuncio) cboTipo.getSelectedItem();
    boolean vendita = (t == TipoAnnuncio.AnnuncioVendita);
    txtPrezzo.setEnabled(vendita);
    if (!vendita) txtPrezzo.setText("");
  }

  private void loadData() {
    // Categorie
    List<Categoria> cats = controller.getCategorie();
    cboCategoria.removeAllItems();
    for (Categoria c : cats) cboCategoria.addItem(c);

    // Miei oggetti disponibili
    cboOggetto.removeAllItems();
    try {
      List<Oggetto> miei = controller.getMieiOggettiDisponibili();
      for (Oggetto o : miei) cboOggetto.addItem(o);
    } catch (BusinessException be) {
      JOptionPane.showMessageDialog(this, be.getMessage(), "Attenzione", JOptionPane.WARNING_MESSAGE);
      dispose();
    }
  }

  private void onCreate() {
    try {
      TipoAnnuncio tipo = (TipoAnnuncio) cboTipo.getSelectedItem();
      Categoria cat = (Categoria) cboCategoria.getSelectedItem();
      Oggetto   obj = (Oggetto)   cboOggetto.getSelectedItem();

      if (tipo == null) { JOptionPane.showMessageDialog(this, "Seleziona il tipo annuncio."); return; }
      if (cat  == null) { JOptionPane.showMessageDialog(this, "Seleziona una categoria.");    return; }
      if (obj  == null) { JOptionPane.showMessageDialog(this, "Seleziona un oggetto.");       return; }

      String consegna = (txtConsegna.getText() == null) ? "" : txtConsegna.getText().trim();
      String descrOpz = (txtDescr.getText() == null) ? "" : txtDescr.getText().trim();

      Double price = null;
      if (tipo == TipoAnnuncio.AnnuncioVendita) {
        String p = txtPrezzo.getText() == null ? "" : txtPrezzo.getText().trim();
        if (p.isEmpty()) { JOptionPane.showMessageDialog(this, "Inserisci un prezzo per la vendita."); return; }
        try { price = Double.valueOf(p); }
        catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Prezzo non valido."); return; }
      }

      boolean ok = controller.createAnnuncio(
          tipo,
          cat.getId(),
          obj.getId(),
          (consegna.isEmpty() ? "Consegna da concordare" : consegna),
          (descrOpz.isEmpty() ? null : descrOpz),
          price
      );

      JOptionPane.showMessageDialog(this,
          ok ? "Annuncio creato correttamente." : "Creazione non riuscita.",
          ok ? "Ok" : "Errore",
          ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
      );
      if (ok) dispose();

    } catch (BusinessException be) {
      JOptionPane.showMessageDialog(this, be.getMessage(), "Operazione non consentita", JOptionPane.WARNING_MESSAGE);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Errore imprevisto. " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
      ex.printStackTrace();
    }
  }
}
