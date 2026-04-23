import javax.swing.*;
import java.awt.*;

public class ThemeManager {

    // Light mode palette
    static final Color LIGHT_BG           = Color.white;
    static final Color LIGHT_TEXT         = Color.black;
    static final Color LIGHT_TODAY        = new Color(255, 220, 220);
    static final Color LIGHT_WEEKEND      = new Color(235, 235, 255);
    static final Color LIGHT_SELECTED     = new Color(204, 255, 204);
    static final Color LIGHT_INACTIVE_FG  = new Color(180, 180, 180);
    static final Color LIGHT_INACTIVE_WKD = new Color(245, 245, 255);
    static final Color LIGHT_INACTIVE_WKD2= new Color(250, 250, 250);
    static final Color LIGHT_SEL_INACTIVE = new Color(235, 255, 235);
    static final Color LIGHT_HEADER       = new Color(238, 238, 238);

    // Dark mode palette
    static final Color DARK_BG            = new Color(30, 30, 30);
    static final Color DARK_TEXT          = new Color(220, 220, 220);
    static final Color DARK_TODAY         = new Color(120, 50, 50);
    static final Color DARK_WEEKEND       = new Color(40, 40, 70);
    static final Color DARK_SELECTED      = new Color(40, 80, 40);
    static final Color DARK_INACTIVE_FG   = new Color(90, 90, 90);
    static final Color DARK_INACTIVE_WKD  = new Color(35, 35, 55);
    static final Color DARK_INACTIVE_WKD2 = new Color(28, 28, 28);
    static final Color DARK_SEL_INACTIVE  = new Color(30, 55, 30);
    static final Color DARK_PANEL         = new Color(45, 45, 45);
    static final Color DARK_HEADER        = new Color(40, 40, 40);

    // Convenience helpers so callers don't need to branch on darkMode themselves
    static Color bg()         { return LibreCal.darkMode ? DARK_BG           : LIGHT_BG;           }
    static Color text()       { return LibreCal.darkMode ? DARK_TEXT         : LIGHT_TEXT;         }
    static Color today()      { return LibreCal.darkMode ? DARK_TODAY        : LIGHT_TODAY;        }
    static Color weekend()    { return LibreCal.darkMode ? DARK_WEEKEND      : LIGHT_WEEKEND;      }
    static Color selected()   { return LibreCal.darkMode ? DARK_SELECTED     : LIGHT_SELECTED;     }
    static Color inactiveFg() { return LibreCal.darkMode ? DARK_INACTIVE_FG  : LIGHT_INACTIVE_FG;  }
    static Color inactiveWkd(){ return LibreCal.darkMode ? DARK_INACTIVE_WKD : LIGHT_INACTIVE_WKD; }
    static Color inactiveDay(){ return LibreCal.darkMode ? DARK_INACTIVE_WKD2: LIGHT_INACTIVE_WKD2;}
    static Color selInactive(){ return LibreCal.darkMode ? DARK_SEL_INACTIVE : LIGHT_SEL_INACTIVE; }
    static Color header()     { return LibreCal.darkMode ? DARK_HEADER       : LIGHT_HEADER;       }
    static Color panel()      { return LibreCal.darkMode ? DARK_PANEL        : null;               }

    // Repaints all non-table UI components to match the current darkMode state
    public static void applyTheme() {
        LibreCal.panelCal.setBackground(panel());
        LibreCal.panelCal.setOpaque(LibreCal.darkMode);

        LibreCal.headerPanel.setBackground(LibreCal.darkMode ? DARK_HEADER : null);
        LibreCal.headerPanel.setOpaque(LibreCal.darkMode);

        LibreCal.LabelMonth.setForeground(LibreCal.darkMode ? DARK_TEXT : null);
        LibreCal.LabelMonth.setBackground(LibreCal.darkMode ? DARK_HEADER : null);
        LibreCal.LabelMonth.setOpaque(LibreCal.darkMode);

        // Force the scroll pane's viewport background to match
        LibreCal.theScrollPane.getViewport().setBackground(bg());
        LibreCal.tabelCal.setBackground(bg());
        LibreCal.tabelCal.setGridColor(LibreCal.darkMode ? new Color(60, 60, 60) : new Color(225, 225, 225));
        LibreCal.theScrollPane.setBorder(LibreCal.darkMode
            ? BorderFactory.createLineBorder(DARK_PANEL)
            : BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Keep "Calendar" at the top-left readable regardless of theme
        LibreCal.panelCal.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LibreCal.darkMode ? DARK_PANEL : Color.gray),
            "Calendar", 0, 0, null,
            LibreCal.darkMode ? DARK_TEXT : null
        ));

        LibreCal.mainFrame.getContentPane().setBackground(panel());
        LibreCal.tabelCal.getTableHeader().setOpaque(true);
        LibreCal.tabelCal.getTableHeader().setBackground(header());
        LibreCal.tabelCal.getTableHeader().setForeground(text());
        LibreCal.tabelCal.getTableHeader().repaint();
        LibreCal.tabelCal.repaint();
    }

    // colors for the reminder dialog based on user preference
    public static void styleReminderDialog(JDialog dialog, JList<ReminderManager.Reminder> list, 
        JTextField field, JPanel bottom, JPanel btnRow, JScrollPane scrollPane, 
        JPanel timeRow, JSpinner hourSpinner, JSpinner minSpinner, JToggleButton amPmToggle) {
            
            if (LibreCal.darkMode) {
                // 1. Fix the Mac Native Title Bar
                // This tells macOS to use the dark mode version of the window frame
                dialog.getRootPane().putClientProperty("apple.awt.windowAppearance", "NSAppearanceNameDarkAqua");
    
                // main window frame
                dialog.getContentPane().setBackground(bg());
                bottom.setBackground(bg());
                btnRow.setBackground(bg());
                timeRow.setBackground(bg());
    
                // text list and background
                list.setBackground(bg());
                list.setForeground(text());
                scrollPane.getViewport().setBackground(bg());
                scrollPane.setBorder(BorderFactory.createLineBorder(header()));
    
                // 2. Lighten up the Text Field
                // Creates a distinct, lighter gray so the input field doesn't disappear into the background
                Color fieldBg = new Color(65, 65, 65); 
                field.setBackground(fieldBg);
                field.setForeground(text());
                field.setCaretColor(text());
                // Add a subtle border and some padding so the text doesn't hit the absolute edge
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 100, 100)),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
                ));
    
                // spinner text
                JTextField hEditor = ((JSpinner.DefaultEditor) hourSpinner.getEditor()).getTextField();
                hEditor.setBackground(fieldBg);
                hEditor.setForeground(text());
                hEditor.setBorder(BorderFactory.createEmptyBorder());
    
                JTextField mEditor = ((JSpinner.DefaultEditor) minSpinner.getEditor()).getTextField();
                mEditor.setBackground(fieldBg);
                mEditor.setForeground(text());
                mEditor.setBorder(BorderFactory.createEmptyBorder());
    
                // 3. Fix the AM/PM Toggle
                amPmToggle.setBackground(fieldBg);
                amPmToggle.setForeground(text());
                // macOS buttons are transparent by default; this forces the color to actually paint
                amPmToggle.setOpaque(true); 
                amPmToggle.setBorderPainted(false);
            }
        }
}