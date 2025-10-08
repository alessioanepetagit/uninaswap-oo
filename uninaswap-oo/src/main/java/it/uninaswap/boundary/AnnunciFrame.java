package it.uninaswap.boundary;

import it.uninaswap.control.Controller;
import it.uninaswap.model.Annuncio;
import it.uninaswap.model.Categoria;
import it.uninaswap.model.enums.TipoAnnuncio;
import it.uninaswap.ui.ModernTable;
import it.uninaswap.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AnnunciFrame extends JFrame {
  private static final long serialVersionUID = 1L;

  private final Controller controller;

  private JComboBox<Categoria> cboCategoria;
  private JComboBox<TipoAnnuncio> cboTipo;
  private JTable table;
  private DefaultTableModel model;
  private JLabel statusLbl;

  public AnnunciFrame(Controller controller) {
    super("UninaSwap – Annunci");
    this.controller = controller;
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    buildUi();
    loadFilters();
    search();

    setSize(1000, 620);
    setLocationRelativeTo(null);
  }

  private void buildUi() {
    // ====== HERO HEADER (titolo + sottotitolo) ======
    JPanel hero = UITheme.appHeader(
        "UninaSwap — Annunci",
        "Compra, scambia e regala in modo semplice tra studenti.",
        UIManager.getIcon("OptionPane.informationIcon")
    );

    // ====== TOOLBAR (solo azioni secondarie) ======
    JToolBar tb = new JToolBar();
    UITheme.styleToolbar(tb);
    tb.add(new JLabel("  Annunci"));
    tb.add(Box.createHorizontalGlue());

    JButton bStorico  = bigSecondaryButton("Storico");
    JButton bReport   = bigSecondaryButton("Report");
    JButton bRicevute = bigSecondaryButton("Offerte ricevute");

    tb.add(bStorico);
    tb.add(Box.createHorizontalStrut(6));
    tb.add(bReport);
    tb.add(Box.createHorizontalStrut(6));
    tb.add(bRicevute);

    // ====== FILTRI ======
    JPanel filters = UITheme.card();
    filters.setLayout(new GridBagLayout());
    GridBagConstraints gc = new GridBagConstraints();
    gc.insets = new Insets(6, 8, 6, 8);
    gc.gridy = 0; gc.gridx = 0; gc.anchor = GridBagConstraints.WEST;

    filters.add(new JLabel("Categoria"), gc);
    gc.gridx++;
    cboCategoria = new JComboBox<>();
    cboCategoria.setPreferredSize(new Dimension(240, 32));
    filters.add(cboCategoria, gc);

    gc.gridx++;
    filters.add(new JLabel("Tipo"), gc);
    gc.gridx++;
    cboTipo = new JComboBox<>(TipoAnnuncio.values());
    cboTipo.setPreferredSize(new Dimension(180, 32));
    filters.add(cboTipo, gc);

    gc.gridx++;
    JButton bCerca = bigSecondaryButton("Cerca");
    filters.add(bCerca, gc);

    // ====== TABELLA ======
    model = new DefaultTableModel(
        new Object[]{"IDAnnuncio","Tipo","Oggetto","Descrizione","Prezzo","Creato da"}, 0
    ) {
      private static final long serialVersionUID = 1L;
      @Override public boolean isCellEditable(int r,int c){ return false; }
      @Override public Class<?> getColumnClass(int ci){
        switch (ci) {
          case 0: return Integer.class;
          case 4: return Double.class;
          default: return String.class;
        }
      }
    };
    table = new JTable(model);
    ModernTable.beautify(table);
    JScrollPane center = new JScrollPane(table);

    // ====== BOTTONI IN BASSO ======
    JButton bApri = bigPrimaryButton("Dettaglio / Offerta");
    JButton bCrea = bigPrimaryButton("Crea annuncio"); // spostato in basso

    JPanel southButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    southButtons.setOpaque(false);
    southButtons.add(bApri);
    southButtons.add(bCrea);

    // ====== STATUS BAR ======
    statusLbl = new JLabel("  Pronto.");
    UITheme.muted(statusLbl);
    JPanel statusBar = new JPanel(new BorderLayout());
    statusBar.setBackground(Color.WHITE);
    statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_SOFT));
    statusBar.add(statusLbl, BorderLayout.WEST);

    // ====== PANNELLO BOTTOM ======
    JPanel bottom = new JPanel(new BorderLayout());
    bottom.setOpaque(false);
    bottom.add(southButtons, BorderLayout.NORTH);
    bottom.add(statusBar, BorderLayout.SOUTH);

    // ====== TOP STACK (hero + toolbar + filtri) ======
    JPanel top = new JPanel();
    top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
    top.setOpaque(false);
    top.add(hero);
    top.add(tb);
    top.add(Box.createVerticalStrut(6));
    top.add(filters);

    // ====== ASSEMBLAGGIO FRAME ======
    getContentPane().setLayout(new BorderLayout(0, 8));
    getContentPane().setBackground(UITheme.BG);
    add(top, BorderLayout.NORTH);
    add(center, BorderLayout.CENTER);
    add(bottom, BorderLayout.SOUTH);

    // ====== LISTENERS ======
    bCerca.addActionListener(e -> { search(); statusLbl.setText("  Ricerca aggiornata."); });
    bStorico.addActionListener(e -> new StoricoOfferteFrame(controller).setVisible(true));
    bReport.addActionListener(e -> new ReportFrame(controller).setVisible(true));
    bRicevute.addActionListener(e -> new OfferteRicevuteFrame(controller).setVisible(true));

    bCrea.addActionListener(e -> {
      CreaAnnuncioDialog dlg = new CreaAnnuncioDialog(this, controller);
      dlg.setVisible(true);
      // dopo la chiusura, ricarico la lista
      search();
    });

    bApri.addActionListener(e -> openSelected());

    // doppio click -> dettaglio
    table.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) openSelected();
      }
    });
  }

  /** Bottoni primari: grandi, leggibili, sempre visibili. */
  private JButton bigPrimaryButton(String text) {
    JButton b = new JButton(text);
    UITheme.primary(b);
    b.setFont(b.getFont().deriveFont(Font.BOLD, 13f));
    b.setPreferredSize(new Dimension(180, 36));
    b.setOpaque(true);
    b.setContentAreaFilled(true);
    b.setForeground(Color.WHITE); // niente testo "trasparente"
    return b;
  }

  /** Bottoni secondari: grandi ma con stile “outline/soft”. */
  private JButton bigSecondaryButton(String text) {
    JButton b = new JButton(text);
    UITheme.secondary(b);
    b.setFont(b.getFont().deriveFont(Font.BOLD, 13f));
    b.setPreferredSize(new Dimension(180, 36));
    b.setOpaque(true);
    b.setContentAreaFilled(true);
    b.setForeground(UITheme.INK); // contrasta su fondo chiaro
    return b;
  }

  private void loadFilters() {
    // Categoria: (tutte) + reali
    cboCategoria.addItem(new Categoria(0, "(tutte)"));
    List<Categoria> cat = controller.getCategorie();
    for (Categoria c : cat) cboCategoria.addItem(c);

    // Tipo opzionale: prima posizione null
    cboTipo.insertItemAt(null, 0);
    cboTipo.setSelectedIndex(0);
  }

  private void search() {
    Categoria sel = (Categoria) cboCategoria.getSelectedItem();
    String categoriaLike = (sel == null || sel.getId() == 0) ? null : sel.getNome();
    TipoAnnuncio tipo = (TipoAnnuncio) cboTipo.getSelectedItem();

    List<Annuncio> list = controller.cercaAnnunci(categoriaLike, tipo);
    model.setRowCount(0);
    for (Annuncio a : list) {
      String nomeOggetto = controller.getNomeOggetto(a.getOggettoId());
      String nomeUtente  = controller.getNomeUtenteById(a.getAutoreId());
      model.addRow(new Object[]{
          a.getId(),
          a.getTipo() != null ? a.getTipo().name() : "N/D",
          nomeOggetto,
          a.getDescrizione(),
          a.getPrezzoRichiesto(),
          nomeUtente
      });
    }
  }

  private void openSelected() {
    int row = table.getSelectedRow();
    if (row < 0) {
      JOptionPane.showMessageDialog(this, "Seleziona un annuncio.");
      return;
    }
    int id = (Integer) model.getValueAt(row, 0);
    Annuncio a = controller.getAnnuncio(id);
    if (a == null) {
      JOptionPane.showMessageDialog(this, "Annuncio non trovato.");
      return;
    }
    new DettaglioAnnuncioDialog(this, controller, a).setVisible(true);
  }
}
