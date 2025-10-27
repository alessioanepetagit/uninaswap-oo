package it.uninaswap;

import javax.swing.SwingUtilities;

import it.uninaswap.boundary.LoginFrame;
import it.uninaswap.control.Controller;
import it.uninaswap.ui.UITheme;

public class Main {

  public static void main(String[] args) {
    // Tema + font globale
    UITheme.installNimbusTheme();
    UITheme.applyGlobalFont(14f);

    SwingUtilities.invokeLater(() -> {
      Controller controller = new Controller();
      new LoginFrame(controller).setVisible(true);
    });
  }
}
