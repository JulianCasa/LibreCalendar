import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TheCal {//Authored by Vaibhav Thakkar, Ariane Quenum, Michael Woelfel
    // Modified by Arek Gubala
    
    static JLabel LabelMonth;
    static JButton buttonPrev, buttonNext;
    static JTable tabelCal;
    static JComboBox comboYear;
    static JFrame mainFrame;
    static Container pane;
    static JScrollPane theScrollPane;
    static JPanel panelCal;
    static int todayYear, todayMonth, todayDay, currentYear, currentMonth;
    static DefaultTableModel mtabelCal;

    public static void main (String args[]){
        mainFrame = new JFrame ("LibreCalendar");
        pane = mainFrame.getContentPane();
        pane.setLayout(new BorderLayout()); // Set the main window to BorderLayout to allow for dynamic component placement
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        LabelMonth = new JLabel ("January", SwingConstants.CENTER); // Set month label with centered text
        LabelMonth.setFont(new Font("TimesNewRoman", Font.BOLD, 32)); 
        LabelMonth.setPreferredSize(new Dimension(190, 40));

        comboYear = new JComboBox();
        buttonPrev = new JButton ("<<");
        buttonNext = new JButton (">>");
        mtabelCal = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
        tabelCal = new JTable(mtabelCal);
        theScrollPane = new JScrollPane(tabelCal);
        panelCal = new JPanel(new BorderLayout());
        
        panelCal.setBorder(BorderFactory.createTitledBorder("Calendar"));
        
        buttonPrev.addActionListener(new buttonPrev_Action());
        buttonNext.addActionListener(new buttonNext_Action());
        comboYear.addActionListener(new comboYear_Action());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        headerPanel.add(buttonPrev);
        headerPanel.add(LabelMonth);
        headerPanel.add(comboYear);
        headerPanel.add(buttonNext);
                

        panelCal.add(headerPanel, BorderLayout.NORTH);
        panelCal.add(theScrollPane, BorderLayout.CENTER);
        pane.add(panelCal, BorderLayout.CENTER);
        
        mainFrame.setResizable(true);
        
        GregorianCalendar cal = new GregorianCalendar(); 
        todayDay = cal.get(GregorianCalendar.DAY_OF_MONTH);
        todayMonth = cal.get(GregorianCalendar.MONTH);
        todayYear = cal.get(GregorianCalendar.YEAR);
        currentMonth = todayMonth;
        currentYear = todayYear;
            
        String[] headers = {"Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i=0; i<7; i++){
            mtabelCal.addColumn(headers[i]);
        }

        tabelCal.getParent().setBackground(tabelCal.getBackground());
        tabelCal.getTableHeader().setResizingAllowed(true);
        tabelCal.getTableHeader().setReorderingAllowed(true);
        tabelCal.setColumnSelectionAllowed(true);
        tabelCal.setRowSelectionAllowed(true);
        tabelCal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabelCal.setShowGrid(true);
        tabelCal.setDefaultRenderer(Object.class, new tabelCalRenderer());
        tabelCal.setGridColor(new Color(225, 225, 225));
        
        
        for (int i=todayYear-100; i<=todayYear+100; i++){
            comboYear.addItem(String.valueOf(i));
        }

         // Clear selection w/ light green cell when Escape key is pressed
        tabelCal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearHighlight");
                
        tabelCal.getActionMap().put("clearHighlight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                tabelCal.clearSelection(); 
            }
        });

        // Handle verticle scaling of calendar cells when the window is resized
        theScrollPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateRowHeights();
            }
        });

        refreshCalendar (todayMonth, todayYear);
        mainFrame.setMinimumSize(new Dimension(600, 450));
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    
    public static void refreshCalendar(int month, int year){
        String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int nod, som;
        
        mtabelCal.setRowCount(0);
        buttonPrev.setEnabled(true);
        buttonNext.setEnabled(true);
        if (month == 0 && year <= todayYear-100){buttonPrev.setEnabled(false);}
        if (month == 11 && year >= todayYear+100){buttonNext.setEnabled(false);}
        LabelMonth.setText(months[month]);
        
        comboYear.setSelectedItem(String.valueOf(year));

        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        som = cal.get(GregorianCalendar.DAY_OF_WEEK);


        // Calculate the number of days in the PREVIOUS month
        GregorianCalendar prevCal = new GregorianCalendar(year, month - 1, 1);
        int prevNod = prevCal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        int numOfRows = ((nod + som - 2) / 7) + 1;
        mtabelCal.setRowCount(numOfRows);

        // Fill the leading empty cells with dates from previous month
        for (int i = 0; i < som - 1; i++) {
            int prevDate = prevNod - (som - 2 - i);
            mtabelCal.setValueAt(-prevDate, 0, i);
        }
        // Fill the current month
        for (int i=1; i<=nod; i++){
            int row = (i+som-2)/7;
            int column  =  (i+som-2)%7;
            mtabelCal.setValueAt(i, row, column);
        }
        // Fill the trailing empty cells with dates for the following month
        int nextMonthDay = 1;
        for (int i = nod + som - 1; i < numOfRows * 7; i++) {
            int row = i / 7;
            int column = i % 7;
            mtabelCal.setValueAt(-nextMonthDay, row, column);
            nextMonthDay++;
        }
        updateRowHeights();
    }
    
    static class tabelCalRenderer extends DefaultTableCellRenderer{
        public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            setHorizontalAlignment(SwingConstants.LEFT);
            setVerticalAlignment(SwingConstants.TOP);
            setBorder(null);

            if (value != null) {
                int dateVal = Integer.parseInt(value.toString());
                int displayDate = Math.abs(dateVal); // Strips the negative sign
                setText(String.valueOf(displayDate)); 
                
                if (dateVal > 0) { // Current month dates
                    setForeground(Color.black); // Strong black text
                    
                    if (selected) {
                        setBackground(new Color(204, 255, 204)); // Green highlight
                    } else if (displayDate == todayDay && currentMonth == todayMonth && currentYear == todayYear) { 
                        setBackground(new Color(255, 220, 220)); // Pink today
                    } else if (column == 0 || column == 6) { 
                        setBackground(new Color(235, 235, 255)); // Blue weekend
                    } else { 
                        setBackground(Color.white); // Normal white day
                    }
                } else { // Inactive dates from adjacent months
                    setForeground(new Color(180, 180, 180)); // Very faint gray text
                    
                    if (selected) {
                        setBackground(new Color(235, 255, 235)); // Faint green if clicked
                    } else if (column == 0 || column == 6) {
                        // Washed-out, faint pink for inactive weekends
                        setBackground(new Color(245, 245, 255)); 
                    } else {
                        // Plain off-white background for weekdays
                        setBackground(new Color(250, 250, 250)); 
                    }
                }
            } else {
                setText("");
                setBackground(Color.white);
            }
            
            return this;
        }
    }
    
    static class buttonPrev_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 0) {
                currentMonth = 11;
                currentYear -= 1;
            }
            else {
                currentMonth -= 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    
    static class buttonNext_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 11){
                currentMonth = 0;
                currentYear += 1;
            }
            else{
                currentMonth += 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    
    static class comboYear_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (comboYear.getSelectedItem() != null){
                String b = comboYear.getSelectedItem().toString();
                currentYear = Integer.parseInt(b);
                refreshCalendar(currentMonth, currentYear);
            }
        }
    }

    // rather than always setting a fixed height, this method determines row height 
    // based on the number of rows and the available vertical space in the calendar
    public static void updateRowHeights() {
        int availableHeight = theScrollPane.getViewport().getHeight();
        if (availableHeight > 0 && tabelCal.getRowCount() > 0) {
            int newRowHeight = availableHeight / tabelCal.getRowCount();
            tabelCal.setRowHeight(newRowHeight);
        }
    }
}