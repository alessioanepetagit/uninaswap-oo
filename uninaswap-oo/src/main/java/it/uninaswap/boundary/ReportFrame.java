package it.uninaswap.boundary;

import it.uninaswap.control.Controller;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class ReportFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private final Controller controller;

  public ReportFrame(Controller controller) {
    super("UninaSwap – Report");
    this.controller = controller;
    setSize(760, 520);
    setLocationRelativeTo(null);
    buildChart();
  }

  private void buildChart() {
    // ---- Dati dal controller (SOLO int / Double) ----
    int totVendita = controller.reportTotVendita();
    int totScambio = controller.reportTotScambio();
    int totRegalo  = controller.reportTotRegalo();

    int accVendita = controller.reportAccVendita();
    int accScambio = controller.reportAccScambio();
    int accRegalo  = controller.reportAccRegalo();

    int totaleOfferte = controller.reportTotaleOfferte();

    Double min = controller.reportVenditeAccettateMin();
    Double avg = controller.reportVenditeAccettateAvg();
    Double max = controller.reportVenditeAccettateMax();

    // ---- UI ----
    JPanel root = new JPanel(new BorderLayout(8,8));
    root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

    JLabel title = new JLabel("Report utente");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
    root.add(title, BorderLayout.NORTH);

    JTextArea txt = new JTextArea();
    txt.setEditable(false);
    txt.setBackground(new Color(248,250,253));
    txt.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    txt.setText(
        "Totale offerte inviate: " + totaleOfferte + "\n" +
        "Totali per tipologia:    Vendita=" + totVendita +
        "  Scambio=" + totScambio +
        "  Regalo=" + totRegalo + "\n" +
        "Accettate per tipologia: Vendita=" + accVendita +
        "  Scambio=" + accScambio +
        "  Regalo=" + accRegalo + "\n" +
        "Vendite accettate – min: " + n(min) +
        "  avg: " + n(avg) +
        "  max: " + n(max)
    );
    root.add(txt, BorderLayout.WEST);

    // Grafico 
    DefaultCategoryDataset ds = new DefaultCategoryDataset();
    ds.addValue(totVendita, "Totali (inviate)", "Vendita");
    ds.addValue(totScambio, "Totali (inviate)", "Scambio");
    ds.addValue(totRegalo,  "Totali (inviate)", "Regalo");

    ds.addValue(accVendita, "Accettate (inviate)", "Vendita");
    ds.addValue(accScambio, "Accettate (inviate)", "Scambio");
    ds.addValue(accRegalo,  "Accettate (inviate)", "Regalo");

    JFreeChart chart = ChartFactory.createBarChart(
        "Offerte per Tipologia",
        "Tipologia",
        "Numero",
        ds,
        PlotOrientation.VERTICAL,
        true, true, false
    );
    ChartPanel panel = new ChartPanel(chart);
    panel.setPreferredSize(new Dimension(520, 360));
    root.add(panel, BorderLayout.CENTER);

    setContentPane(root);
  }

  private String n(Double v){ return v==null? "-" : String.format("%.2f", v); }
}
