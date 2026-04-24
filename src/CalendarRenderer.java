import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class CalendarRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean selected, boolean focused, int row, int column) {

        super.getTableCellRendererComponent(table, value, selected, focused, row, column);
        setHorizontalAlignment(SwingConstants.LEFT);
        setVerticalAlignment(SwingConstants.TOP);
        setBorder(null);

        if (value != null) {
            int dateVal = Integer.parseInt(value.toString());
            int displayDate = Math.abs(dateVal);

            List<ReminderManager.Reminder> reminders =
                ReminderManager.getReminders(
                    LibreCal.currentYear,
                    LibreCal.currentMonth,
                    displayDate
                );

            String tooltipText = null;
            StringBuilder cellText = new StringBuilder();
            cellText.append(displayDate);
            
            if (!reminders.isEmpty()) {
                tooltipText = "Reminders: " + reminders.size();
                cellText.append("<br>");
                for (ReminderManager.Reminder reminder : reminders) {
                    cellText.append(reminder.getTimeKey()).append(" - ").append(reminder.message).append("<br>");
                }
            }

            // Check if this date is a holiday
            boolean isHoliday = HolidayManager.isHoliday(
                LibreCal.currentYear,
                LibreCal.currentMonth,
                displayDate
            );
            
            if (isHoliday) {
                String holidayName = HolidayManager.getHoliday(
                    LibreCal.currentYear,
                    LibreCal.currentMonth,
                    displayDate
                );
                tooltipText = holidayName;
            }
            
            if (tooltipText != null) {
                setToolTipText(tooltipText);
            } else {
                setToolTipText(null);
            }
            
            // Display date and reminders as HTML
            setText("<html>" + cellText.toString() + "</html>");

            if (dateVal > 0) {
                setForeground(ThemeManager.text());

                // html used to allow multi-line text and smaller font for reminder count
                if (!reminders.isEmpty()) {
                    setText("<html>" + displayDate + 
                            "<br><span style='font-size:8px'>* " + reminders.size() + " reminder" +
                            (reminders.size() > 1 ? "s" : "") + "</span></html>");
                }

                if (selected) {
                    setBackground(ThemeManager.selected());
                } else if (isHoliday) {
                    setBackground(ThemeManager.holiday());
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

    @Override
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