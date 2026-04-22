import java.awt.event.*;
import java.util.prefs.Preferences;

public class Handlers {

    public static class ButtonPrevAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (LibreCal.currentMonth == 0) {
                LibreCal.currentMonth = 11;
                LibreCal.currentYear -= 1;
            } else {
                LibreCal.currentMonth -= 1;
            }
            CalendarUtils.refreshCalendar(LibreCal.currentMonth, LibreCal.currentYear);
        }
    }

    public static class ButtonNextAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (LibreCal.currentMonth == 11) {
                LibreCal.currentMonth = 0;
                LibreCal.currentYear += 1;
            } else {
                LibreCal.currentMonth += 1;
            }
            CalendarUtils.refreshCalendar(LibreCal.currentMonth, LibreCal.currentYear);
        }
    }

    public static class ButtonDarkModeAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            LibreCal.darkMode = !LibreCal.darkMode;
            LibreCal.buttonDarkMode.setText(LibreCal.darkMode ? "\u2600" : "\u263E"); // ☀ or ☾

            ThemeManager.applyTheme();

            Preferences prefs = Preferences.userNodeForPackage(LibreCal.class);
            prefs.putBoolean("darkMode", LibreCal.darkMode);
        }
    }

    public static class ComboYearAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (LibreCal.comboYear.getSelectedItem() != null) {
                String b = LibreCal.comboYear.getSelectedItem().toString();
                LibreCal.currentYear = Integer.parseInt(b);
                CalendarUtils.refreshCalendar(LibreCal.currentMonth, LibreCal.currentYear);
            }
        }
    }
}