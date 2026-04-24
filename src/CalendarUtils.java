import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CalendarUtils {

    public static void refreshCalendar(int month, int year) {
        String[] months = {"January","February","March","April","May","June",
                           "July","August","September","October","November","December"};
        int nod, som;

        LibreCal.mtabelCal.setRowCount(0);
        LibreCal.buttonPrev.setEnabled(true);
        LibreCal.buttonNext.setEnabled(true);

        if (month == 0  && year <= LibreCal.todayYear - 100) { LibreCal.buttonPrev.setEnabled(false); }
        if (month == 11 && year >= LibreCal.todayYear + 100) { LibreCal.buttonNext.setEnabled(false); }

        LibreCal.LabelMonth.setText(months[month]);
        LibreCal.comboYear.setSelectedItem(String.valueOf(year));

        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        som = cal.get(GregorianCalendar.DAY_OF_WEEK);

        // Calculate the number of days in the PREVIOUS month (to fill leading cells)
        GregorianCalendar prevCal = new GregorianCalendar(year, month - 1, 1);
        int prevNod = prevCal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        int numOfRows = ((nod + som - 2) / 7) + 1;
        LibreCal.mtabelCal.setRowCount(numOfRows);

        // Fill leading cells with previous month dates (stored as negative)
        for (int i = 0; i < som - 1; i++) {
            int prevDate = prevNod - (som - 2 - i);
            LibreCal.mtabelCal.setValueAt(-prevDate, 0, i);
        }
        // Fill current month
        for (int i = 1; i <= nod; i++) {
            int row    = (i + som - 2) / 7;
            int column = (i + som - 2) % 7;
            LibreCal.mtabelCal.setValueAt(i, row, column);
        }
        // Fill trailing cells with next month dates (stored as negative)
        int nextMonthDay = 1;
        for (int i = nod + som - 1; i < numOfRows * 7; i++) {
            int row    = i / 7;
            int column = i % 7;
            LibreCal.mtabelCal.setValueAt(-nextMonthDay, row, column);
            nextMonthDay++;
        }
        updateRowHeights(LibreCal.theScrollPane, LibreCal.tabelCal);
    }

    // rather than always setting a fixed height, this method determines row height 
    // based on the number of rows and the available vertical space in the calendar
    public static void updateRowHeights(javax.swing.JScrollPane scrollPane, javax.swing.JTable table) {
        int availableHeight = scrollPane.getViewport().getHeight();
        if (availableHeight > 0 && table.getRowCount() > 0) {
            int newRowHeight = availableHeight / table.getRowCount();
            table.setRowHeight(newRowHeight);
        }
    }

    public static void showReminderDialog(JFrame parent, int year, int month, int day) {
        String[] months = {
            "January","February","March","April","May","June",
            "July","August","September","October","November","December"
        };

        List<ReminderManager.Reminder> dayReminders =
                ReminderManager.getReminders(year, month, day);

        JDialog dialog = new JDialog(parent, months[month] + " " + day + ", " + year, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setMinimumSize(new Dimension(380, 320));

        DefaultListModel<ReminderManager.Reminder> listModel = new DefaultListModel<>();

        for (ReminderManager.Reminder r : dayReminders) {
            listModel.addElement(r);
        }

        JList<ReminderManager.Reminder> list = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(list);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Input Area
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Time row: hour spinner + AM/PM toggle + reminder text field
        JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        // Hour spinner
        SpinnerNumberModel hourModel = new SpinnerNumberModel(12, 1, 12, 1);
        JSpinner hourSpinner = new JSpinner(hourModel);

        // Minute spinner 0-59
        SpinnerNumberModel minModel = new SpinnerNumberModel(0, 0, 59, 1);
        JSpinner minSpinner = new JSpinner(minModel);
        minSpinner.setPreferredSize(new Dimension(55, 26));
        
        JSpinner.NumberEditor minEditor = new JSpinner.NumberEditor(minSpinner, "00");
        minSpinner.setEditor(minEditor);

        // AM/PM toggle button — pressing it flips between AM and PM
        JToggleButton amPmToggle = new JToggleButton("AM");
        amPmToggle.addActionListener(e ->
            amPmToggle.setText(amPmToggle.isSelected() ? "PM" : "AM")
        );

        timeRow.add(new JLabel("Time:"));
        timeRow.add(hourSpinner);
        timeRow.add(new JLabel(":"));
        timeRow.add(minSpinner);
        timeRow.add(amPmToggle);

        // Reminder text field
        JTextField field = new JTextField();

        // Add / Delete buttons
        JButton addBtn = new JButton("Add");
        JButton delBtn = new JButton("Delete");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnRow.add(delBtn);
        btnRow.add(addBtn);

        bottom.add(timeRow, BorderLayout.NORTH);
        bottom.add(field, BorderLayout.CENTER);
        bottom.add(btnRow, BorderLayout.SOUTH);

        dialog.add(bottom, BorderLayout.SOUTH);

        // Listeners for user-input
        Runnable doAdd = () -> {
            String note = field.getText().trim();
            if (!note.isEmpty()) {

                int hour = (int) hourSpinner.getValue();
                int minute = (int) minSpinner.getValue();
                String amPm = amPmToggle.getText();

                ReminderManager.Reminder r =
                        new ReminderManager.Reminder(hour, minute, amPm, note);

                listModel.addElement(r);
                dayReminders.add(r);

                ReminderManager.saveReminders(year, month, day, dayReminders);

                field.setText("");
                LibreCal.tabelCal.repaint();
            }
        };

        addBtn.addActionListener(e -> doAdd.run());  field.addActionListener(e -> doAdd.run());

        delBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx != -1) {
                listModel.remove(idx);
                dayReminders.remove(idx);
                ReminderManager.saveReminders(year, month, day, dayReminders);
                LibreCal.tabelCal.repaint();
            }
        });

        ThemeManager.styleReminderDialog(
            dialog, list, field, bottom, btnRow, scrollPane, 
            timeRow, hourSpinner, minSpinner, amPmToggle
        );

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static class Notifier {

        private static Set<String> fired = new HashSet<>();

        public static void startNotifier() {
            javax.swing.Timer timer = new javax.swing.Timer(5000, e -> {

                GregorianCalendar cal = new GregorianCalendar();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int d = cal.get(Calendar.DAY_OF_MONTH);

                List<ReminderManager.Reminder> reminders =
                        ReminderManager.getReminders(y, m, d);

                String nowKey = LocalTime.now()
                        .format(DateTimeFormatter.ofPattern("h:mm a"));

                for (ReminderManager.Reminder r : reminders) {
                    if (r.getTimeKey().equals(nowKey) && !fired.contains(r.serialize())) {
                        
                        send("Reminder!", r.message); // Pass title and message
                        fired.add(r.serialize());
                    }
                }

                // CRITICAL: Clear the fired list at midnight. 
                // If the app stays open for days, this ensures tomorrow's reminders can fire!
                if (nowKey.equals("12:00 AM")) {
                    fired.clear();
                }
            });

            timer.start();
        }


        // send notifications to user using SystemTray API
        public static void send(String title, String message) {
            if (!SystemTray.isSupported()) {
                System.out.println("SystemTray is not supported");
                return;
            }
        
            try {
                SystemTray tray = SystemTray.getSystemTray();
                
                java.net.URL imgURL = CalendarUtils.class.getResource("/assets/SquareLogo.png");
                Image image = new ImageIcon(imgURL).getImage();
        
                TrayIcon trayIcon = new TrayIcon(image, "Reminder!");
                trayIcon.setImageAutoSize(true);
                tray.add(trayIcon);

                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        
                // Clean up try icon after 5 seconds
                new javax.swing.Timer(5000, e -> tray.remove(trayIcon)).start();
        
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}