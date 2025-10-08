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

public class OfferteRicevuteFrame extends JFrame {
  private static final long serialVersionUID = 1L;

  private final Controller controller;
  private DefaultTableModel model;
  private JTable table;

  public OfferteRicevuteFrame(Controller controller) {
    super("Offerte ricevute sui miei annunci");
    this.controller = controller;
    setSize(860, 440);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(8, 8));

    // Colonne: IDOfferta, Annuncio (nome oggetto), Tipo, Messaggio/Prezzo, Stato, Offerente (username)
    model = new DefaultTableModel(new Object[]{
        "ID Offerta", "Annuncio", "Tipo", "Messaggio/Prezzo", "Stato", "Offerente"
    }, 0) {
      private static final long serialVersionUID = 1L;
      @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    table = new JTable(model);
    add(new JScrollPane(table), BorderLayout.CENTER);

    JButton btnAccetta = new JButton("Accetta");
    JButton btnRifiuta = new JButton("Rifiuta");
    JButton btnRefresh = new JButton("Aggiorna");

    JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    south.add(btnRefresh);
    south.add(btnRifiuta);
    south.add(btnAccetta);
    add(south, BorderLayout.SOUTH);

    btnRefresh.addActionListener(e -> load());
    btnAccetta.addActionListener(e -> change(true));
    btnRifiuta.addActionListener(e -> change(false));

    load();
  }

  private void load() {
    model.setRowCount(0);
    List<Offerta> list = controller.getOfferteRicevute();
    for (Offerta o : list) {
      // Nome annuncio = nome dell’oggetto collegato
      String nomeAnnuncio = "N/D";
      Annuncio a = controller.getAnnuncio(o.getAnnuncioId());
      if (a != null) {
        String nome = controller.getNomeOggetto(a.getOggettoId());
        if (nome != null && !nome.isEmpty()) nomeAnnuncio = nome;
      }

      // Nome offerente
      String offerente = controller.getNomeUtenteById(o.getOfferenteId());
      if (offerente == null || offerente.isEmpty()) {
        offerente = "#" + o.getOfferenteId();
      }

      String val = (o.getPrezzoProposto() != null)
          ? String.format("%.2f €", o.getPrezzoProposto())
          : o.getMessaggioMotivazionale();

      model.addRow(new Object[]{
          o.getId(),
          nomeAnnuncio,
          o.getTipo(),
          val,
          o.getStato(),
          offerente
      });
    }
  }

  private void change(boolean accept) {
    int row = table.getSelectedRow();
    if (row < 0) {
      JOptionPane.showMessageDialog(this, "Seleziona un'offerta");
      return;
    }
    int id = (Integer) model.getValueAt(row, 0);
    try {
      boolean ok = accept
          ? controller.accettaOffertaRicevuta(id)
          : controller.rifiutaOffertaRicevuta(id);

      JOptionPane.showMessageDialog(
          this,
          ok ? "Operazione riuscita."
             : "Impossibile eseguire (forse non è 'InAttesa' o non è un tuo annuncio)."
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
