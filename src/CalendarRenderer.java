import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class CalendarRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean selected, boolean focused, int row, int column) {

        super.getTableCellRendererComponent(table, value, selected, focused, row, column);
        setHorizontalAlignment(SwingConstants.LEFT);
        setVerticalAlignment(SwingConstants.TOP);
        setBorder(null);

        if (value != null) {
            int dateVal = Integer.parseInt(value.toString());
            int displayDate = Math.abs(dateVal);
            setText(String.valueOf(displayDate));

            // 👇 ADD THIS BLOCK HERE
            List<ReminderManager.Reminder> reminders =
                ReminderManager.getReminders(
                    LibreCal.currentYear,
                    LibreCal.currentMonth,
                    displayDate
                );

            if (!reminders.isEmpty()) {
                setToolTipText("Reminders: " + reminders.size());
            } else {
                setToolTipText(null);
            }


            if (dateVal > 0) {
                setForeground(ThemeManager.text());

                if (selected) {
                    setBackground(ThemeManager.selected());
                } else if (displayDate == LibreCal.todayDay
                        && LibreCal.currentMonth == LibreCal.todayMonth
                        && LibreCal.currentYear == LibreCal.todayYear) {
                    setBackground(ThemeManager.today());
                } else if (column == 0 || column == 6) {
                    setBackground(ThemeManager.weekend());
                } else {
                    setBackground(ThemeManager.bg());
                }
            } else {
                setForeground(ThemeManager.inactiveFg());

                if (selected) {
                    setBackground(ThemeManager.selInactive());
                } else if (column == 0 || column == 6) {
                    setBackground(ThemeManager.inactiveWkd());
                } else {
                    setBackground(ThemeManager.inactiveDay());
                }
            }
        } else {
            setText("");
            setBackground(ThemeManager.bg());
        }

        return this;
    }
}


class HeaderRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean selected, boolean focused, int row, int column) {

        super.getTableCellRendererComponent(table, value, selected, focused, row, column);

        setHorizontalAlignment(SwingConstants.LEFT);
        setBackground(ThemeManager.header());
        setForeground(ThemeManager.text());

        setBorder(BorderFactory.createMatteBorder(
            0,
            column == 0 ? 1 : 0,
            1,
            1,
            LibreCal.darkMode ? new Color(40, 40, 40)
                              : new Color(200, 200, 200)
        ));

        return this;
    }
}