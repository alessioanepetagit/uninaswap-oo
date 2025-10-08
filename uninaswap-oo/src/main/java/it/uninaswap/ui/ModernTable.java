package it.uninaswap.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ModernTable extends JTable {
  private static final long serialVersionUID = 1L;

  public static void beautify(JTable table) {
    table.setRowHeight(26);
    table.setShowGrid(false);
    table.setFillsViewportHeight(true);

    table.setSelectionBackground(new Color(232, 238, 252));
    table.setSelectionForeground(UITheme.INK);

    JTableHeader header = table.getTableHeader();
    if (header != null) {
      header.setReorderingAllowed(false);
      header.setFont(header.getFont().deriveFont(Font.BOLD));
      header.setForeground(UITheme.INK);
      header.setBackground(new Color(245, 247, 252));
      header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
    }

    final Color alt = (Color) UIManager.get("Table.alternateRowColor");
    final Color altSafe = (alt != null) ? alt : new Color(250, 252, 255);

    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      private static final long serialVersionUID = 1L;

      @Override
      public Component getTableCellRendererComponent(
          JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
          c.setBackground((row % 2 == 0) ? Color.WHITE : altSafe);
          setForeground(UITheme.INK);
        }
        setBorder(new EmptyBorder(0, 6, 0, 6));
        setHorizontalAlignment(column == 0 ? CENTER : LEFT);
        return c;
      }
    });
  }
}
