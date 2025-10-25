package it.uninaswap.boundary;

import it.uninaswap.control.Controller;
import it.uninaswap.exceptions.BusinessException;
import it.uninaswap.model.Annuncio;
import it.uninaswap.model.enums.TipoAnnuncio;

import javax.swing.*;
import java.awt.*;

public class DettaglioAnnuncioDialog extends JDialog {
  private static final long serialVersionUID = 1L;

  private final Controller controller;
  private final Annuncio annuncio;

  private JTextField prezzoTf;
  private JTextArea textArea;

  public DettaglioAnnuncioDialog(Frame owner, Controller controller, Annuncio annuncio) {
    super(owner, "Dettaglio Annuncio #" + annuncio.getId(), true);
    this.controller = controller;
    this.annuncio = annuncio;
    setSize(560, 420);
    setLocationRelativeTo(owner);
    buildUi();
  }

  // Handlers

  /** Invio offerta al ribasso (prezzo deve essere < richiesto). */
  private void onInviaOffertaVendita() {
    try {
      String s = prezzoTf != null ? prezzoTf.getText().trim() : "";
      double prezzo = Double.parseDouble(s);
      boolean ok = controller.inviaOffertaVendita(annuncio.getId(), prezzo);
      JOptionPane.showMessageDialog(this, ok ? "Offerta inviata." : "Invio non riuscito.");
      if (ok) dispose();
    } catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog(this, "Inserisci un prezzo valido.", "Dato non valido", JOptionPane.WARNING_MESSAGE);
    } catch (BusinessException be) {
      JOptionPane.showMessageDialog(this, be.getMessage(), "Operazione non consentita", JOptionPane.WARNING_MESSAGE);
    } 
  }

  /* invia un’offerta esattamente al prezzo richiesto. */
  private void onAccettaPrezzoRichiesto() {
    try {
      boolean ok = controller.accettaPrezzoRichiesto(annuncio.getId());
      JOptionPane.showMessageDialog(this, ok ? "Offerta inviata." : "Invio non riuscito.");
      if (ok) dispose();
    } catch (BusinessException be) {
      JOptionPane.showMessageDialog(this, be.getMessage(), "Operazione non consentita", JOptionPane.WARNING_MESSAGE);
    } 
  }

  private void onInviaOffertaScambio() {
    try {
      String proposta = (textArea != null) ? textArea.getText().trim() : "";
      boolean ok = controller.inviaOffertaScambio(annuncio.getId(), proposta);
      JOptionPane.showMessageDialog(this, ok ? "Proposta inviata." : "Invio non riuscito.");
      if (ok) dispose();
    } catch (BusinessException be) {
      JOptionPane.showMessageDialog(this, be.getMessage(), "Operazione non consentita", JOptionPane.WARNING_MESSAGE);
    } 
  }

  private void onInviaOffertaRegalo() {
    try {
      String msg = (textArea != null) ? textArea.getText().trim() : "";
      boolean ok = controller.inviaOffertaRegalo(annuncio.getId(), msg);
      JOptionPane.showMessageDialog(this, ok ? "Richiesta inviata." : "Invio non riuscito.");
      if (ok) dispose();
    } catch (BusinessException be) {
      JOptionPane.showMessageDialog(this, be.getMessage(), "Operazione non consentita", JOptionPane.WARNING_MESSAGE);
    } 
  }


  
  
  private void buildUi() {
    JPanel center = new JPanel(new BorderLayout(8,8));

    // Info principali + nome autore
    String nomeOggetto = controller.getNomeOggetto(annuncio.getOggettoId());
    String nomeAutore  = controller.getNomeUtenteById(annuncio.getAutoreId());

    StringBuilder sb = new StringBuilder();
    sb.append("Tipo: ").append(annuncio.getTipo()).append('\n');
    sb.append("Oggetto: ").append(nomeOggetto != null ? nomeOggetto : "(sconosciuto)").append('\n');
    sb.append("Descrizione: ").append(annuncio.getDescrizione()).append('\n');
    if (annuncio.getTipo() == TipoAnnuncio.AnnuncioVendita) {
      Double p = annuncio.getPrezzoRichiesto();
      sb.append("Prezzo richiesto: ").append(p != null ? String.format("%.2f €", p) : "N/D");
    }
    sb.append("   Utente: ").append(nomeAutore != null ? nomeAutore : "N/D").append('\n');

    JTextArea info = new JTextArea(sb.toString());
    info.setEditable(false);
    info.setLineWrap(true);
    info.setWrapStyleWord(true);
    center.add(new JScrollPane(info), BorderLayout.CENTER);

    // Azioni
    JPanel south = new JPanel(new GridBagLayout());
    GridBagConstraints gc = new GridBagConstraints();
    gc.insets = new Insets(4,4,4,4);
    gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.WEST;

    if (annuncio.getTipo() == TipoAnnuncio.AnnuncioVendita) {
      south.add(new JLabel("Prezzo proposto (€):"), gc);
      gc.gridx = 1;
      prezzoTf = new JTextField(10);
      south.add(prezzoTf, gc);

      // Accetta prezzo richiesto
      gc.gridx = 0; gc.gridy++;
      gc.gridwidth = 2;
      JButton btnAccettaPrezzo = new JButton("Accetta prezzo richiesto");
      btnAccettaPrezzo.addActionListener(e -> onAccettaPrezzoRichiesto());
      south.add(btnAccettaPrezzo, gc);
      gc.gridwidth = 1;

      // Offerta al ribasso
      gc.gridy++; gc.gridx = 0;
      JButton btnInvia = new JButton("Invia offerta al ribasso");
      btnInvia.addActionListener(e -> onInviaOffertaVendita());
      south.add(btnInvia, gc);

    } else if (annuncio.getTipo() == TipoAnnuncio.AnnuncioScambio) {
      south.add(new JLabel("Proposta di scambio (testo):"), gc);
      gc.gridx = 1;
      textArea = new JTextArea(4, 24);
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      south.add(new JScrollPane(textArea), gc);

      gc.gridx = 0; gc.gridy++;
      JButton btnInvia = new JButton("Invia proposta scambio");
      btnInvia.addActionListener(e -> onInviaOffertaScambio());
      south.add(btnInvia, gc);

    } else { // Regalo
      south.add(new JLabel("Messaggio motivazionale:"), gc);
      gc.gridx = 1;
      textArea = new JTextArea(4, 24);
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      south.add(new JScrollPane(textArea), gc);

      gc.gridx = 0; gc.gridy++;
      JButton btnInvia = new JButton("Richiedi regalo");
      btnInvia.addActionListener(e -> onInviaOffertaRegalo());
      south.add(btnInvia, gc);
    }

    setLayout(new BorderLayout());
    add(center, BorderLayout.CENTER);
    add(south, BorderLayout.SOUTH);
  }
}
