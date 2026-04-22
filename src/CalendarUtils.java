import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.*;
import java.awt.*;


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
    
        List<String> dayReminders = getReminders(year, month, day);
    
        JDialog dialog = new JDialog(parent,
                months[month] + " " + day + ", " + year,
                true);
    
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setMinimumSize(new Dimension(350, 280));
    
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String r : dayReminders) {
            listModel.addElement(r);
        }
    
        JList<String> list = new JList<>(listModel);
        dialog.add(new JScrollPane(list), BorderLayout.CENTER);
    
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    
        JTextField field = new JTextField();
        JButton addBtn = new JButton("Add");
        JButton delBtn = new JButton("Delete");
    
        addBtn.addActionListener(e -> {
            String text = field.getText().trim();
            if (!text.isEmpty()) {
                listModel.addElement(text);
                dayReminders.add(text);
                saveReminders(year, month, day, dayReminders);
                field.setText("");
            }
        });
    
        field.addActionListener(e -> addBtn.doClick());
    
        delBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx != -1) {
                listModel.remove(idx);
                dayReminders.remove(idx);
                saveReminders(year, month, day, dayReminders);
            }
        });
    
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnRow.add(delBtn);
        btnRow.add(addBtn);
    
        bottom.add(field, BorderLayout.CENTER);
        bottom.add(btnRow, BorderLayout.SOUTH);
    
        dialog.add(bottom, BorderLayout.SOUTH);
    
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    static Preferences prefs =
        Preferences.userNodeForPackage(CalendarUtils.class);

    static String key(int y, int m, int d) {
        return String.format("%04d-%02d-%02d", y, m + 1, d);
    }

    static List<String> getReminders(int y, int m, int d) {
        String raw = prefs.get(key(y, m, d), "");
        List<String> list = new ArrayList<>();

        if (!raw.isEmpty()) {
            String[] parts = raw.split("\\|");
            for (String s : parts) {
                list.add(s);
            }
        }
        return list;
    }

    static void saveReminders(int y, int m, int d, List<String> list) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append("|");
        }

        prefs.put(key(y, m, d), sb.toString());
    }

    static void addReminder(int y, int m, int d, String text) {
        List<String> list = getReminders(y, m, d);
        list.add(text);
        saveReminders(y, m, d, list);
    }

    static void deleteReminder(int y, int m, int d, int idx) {
        List<String> list = getReminders(y, m, d);
        if (idx >= 0 && idx < list.size()) {
            list.remove(idx);
            saveReminders(y, m, d, list);
        }
    }
}