package it.uninaswap.boundary;

import it.uninaswap.control.Controller;
import it.uninaswap.exceptions.BusinessException;
import it.uninaswap.exceptions.InfrastructureException;
import it.uninaswap.model.Annuncio;
import it.uninaswap.model.Offerta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StoricoOfferteFrame extends JFrame {
  private static final long serialVersionUID = 1L;

  private final Controller controller;
  private DefaultTableModel model;
  private JTable table;

  public StoricoOfferteFrame(Controller controller) {
    super("Storico Offerte");
    this.controller = controller;
    setSize(760, 420);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // Colonne: ID offerta, NOME ANNUNCIO, Tipo, Messaggio/Prezzo, Stato
    model = new DefaultTableModel(new Object[]{"ID", "Annuncio", "Tipo", "Messaggio/Prezzo", "Stato"}, 0) {
      private static final long serialVersionUID = 1L;
      @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    table = new JTable(model);
    add(new JScrollPane(table), BorderLayout.CENTER);

    JButton btnRefresh = new JButton("Aggiorna");
    JButton btnRitira  = new JButton("Ritira selezionata");
    JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    south.add(btnRefresh);
    south.add(btnRitira);
    add(south, BorderLayout.SOUTH);

    btnRefresh.addActionListener(e -> load());
    btnRitira.addActionListener(e -> onWithdraw());

    load();
  }

  private void load() {
    model.setRowCount(0);
    List<Offerta> list = controller.getStoricoOfferte();
    for (Offerta o : list) {
      // Ricavo nome annuncio (nome oggetto associato)
      String nomeAnnuncio = "N/D";
      Annuncio a = controller.getAnnuncio(o.getAnnuncioId());
      if (a != null) {
        String nome = controller.getNomeOggetto(a.getOggettoId());
        if (nome != null && !nome.isEmpty()) nomeAnnuncio = nome;
      }

      String val = (o.getPrezzoProposto() != null)
          ? String.format("%.2f €", o.getPrezzoProposto())
          : o.getMessaggioMotivazionale();

      model.addRow(new Object[]{
          o.getId(),
          nomeAnnuncio,
          o.getTipo(),
          val,
          o.getStato()
      });
    }
  }

  private void onWithdraw() {
    int row = table.getSelectedRow();
    if (row < 0) {
      JOptionPane.showMessageDialog(this, "Seleziona un'offerta");
      return;
    }
    int idOfferta = (Integer) model.getValueAt(row, 0);

    try {
      boolean ok = controller.ritiraOfferta(idOfferta);
      JOptionPane.showMessageDialog(
          this,
          ok ? "Offerta ritirata."
             : "Impossibile ritirare (forse non è 'InAttesa')."
      );
      load();
    } catch (BusinessException be) {
      JOptionPane.showMessageDialog(
          this,
          be.getMessage(),
          "Operazione non consentita",
          JOptionPane.WARNING_MESSAGE
      );
    } catch (InfrastructureException ie) {
      JOptionPane.showMessageDialog(
          this,
          "Problema tecnico. Riprova.",
          "Errore",
          JOptionPane.ERROR_MESSAGE
      );
      ie.printStackTrace();
    }
  }
}
