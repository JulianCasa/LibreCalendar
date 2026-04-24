import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.prefs.Preferences;

public class LibreCal {//Authored by Vaibhav Thakkar, Ariane Quenum, Michael Woelfel
    // Modified by Arek Gubala

    static JLabel LabelMonth, calendarLogoLabel;
    static JButton buttonPrev, buttonNext, buttonDarkMode;
    static JTable tabelCal;
    static JComboBox<String> comboYear;
    static JFrame mainFrame;
    static Container pane;
    static JScrollPane theScrollPane;
    static JPanel panelCal, headerPanel;
    static int todayYear, todayMonth, todayDay, currentYear, currentMonth;
    static DefaultTableModel mtabelCal;
    static boolean darkMode = false;


    public static void main(String[] args) {
        loadPreferences();
        initFrame();
        initComponents();
        initTable();
        initListeners();
        initLayout();
        initData();
        CalendarUtils.Notifier.startNotifier();
        finalizeUI();
    }


    // Loads saved user settings (e.g., dark mode) from persistent storage
    static void loadPreferences() {
        Preferences prefs = Preferences.userNodeForPackage(LibreCal.class);
        darkMode = prefs.getBoolean("darkMode", false);
    }

    // Creates main JFrame and sets base window layout and behavior
    static void initFrame() {
        mainFrame = new JFrame("LibreCalendar");
        pane = mainFrame.getContentPane();
        pane.setLayout(new BorderLayout()); // Set the main window to BorderLayout to allow for dynamic component placement
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Initializes all UI components (buttons, table, panels, combo box)
    static void initComponents() {
        LabelMonth = new JLabel("January", SwingConstants.CENTER); // Set month label with centered text
        LabelMonth.setFont(new Font("TimesNewRoman", Font.BOLD, 32));
        LabelMonth.setPreferredSize(new Dimension(190, 40));
    
        comboYear = new JComboBox<>();
        buttonPrev = new JButton("<<");
        buttonNext = new JButton(">>");
        buttonDarkMode = new JButton("\u263E");
        buttonDarkMode.setFont(new Font("Serif", Font.PLAIN, 18));
        buttonDarkMode.setToolTipText("Toggle dark mode");
    
        mtabelCal = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelCal = new JTable(mtabelCal);
        theScrollPane = new JScrollPane(tabelCal);
        theScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        theScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelCal = new JPanel(new BorderLayout());
        panelCal.setOpaque(false);
        panelCal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 

        calendarLogoLabel = new JLabel();
        try {
            java.net.URL imgURL = LibreCal.class.getResource("/assets/CroppedLogo.png");
            if (imgURL != null) {
                Image img = new ImageIcon(imgURL).getImage().getScaledInstance(105, 28, Image.SCALE_SMOOTH);
                calendarLogoLabel.setIcon(new ImageIcon(img));
            }
        } catch (Exception e) {
            calendarLogoLabel.setText("calendar"); // fallback text if logo fails
        }
    }

    // Configures calendar table structure, headers, and renderers
    static void initTable() {
        String[] headers = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (int i = 0; i < 7; i++) {
            mtabelCal.addColumn(headers[i]);
        }
    
        tabelCal.getParent().setBackground(tabelCal.getBackground());
        tabelCal.getTableHeader().setResizingAllowed(false);
        tabelCal.getTableHeader().setReorderingAllowed(false);
        tabelCal.setColumnSelectionAllowed(true);
        tabelCal.setRowSelectionAllowed(true);
        tabelCal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelCal.setShowGrid(true);
    
        tabelCal.setDefaultRenderer(Object.class, new CalendarRenderer());
        tabelCal.getTableHeader().setDefaultRenderer(new HeaderRenderer());
    }

    // Attaches all event listeners (buttons, mouse clicks, keyboard actions, resizing)
    static void initListeners() {
        buttonPrev.addActionListener(new Handlers.ButtonPrevAction());
        buttonNext.addActionListener(new Handlers.ButtonNextAction());
        buttonDarkMode.addActionListener(new Handlers.ButtonDarkModeAction());
        comboYear.addActionListener(new Handlers.ComboYearAction());
    
        tabelCal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW) // Clear selection w/ light green cell when Escape key is pressed
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearHighlight");
    
        tabelCal.getActionMap().put("clearHighlight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                tabelCal.clearSelection();
            }
        });
    
        // Handle verticle scaling of calendar cells when the window is resized
        theScrollPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                CalendarUtils.updateRowHeights(theScrollPane, tabelCal);
            }
        });

        tabelCal.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
        
                    int row = tabelCal.rowAtPoint(e.getPoint());
                    int col = tabelCal.columnAtPoint(e.getPoint());
        
                    Object val = mtabelCal.getValueAt(row, col);
        
                    if (val != null) {
                        int dateVal = Integer.parseInt(val.toString());
        
                        if (dateVal > 0) {
                            CalendarUtils.showReminderDialog(
                                mainFrame,
                                currentYear,
                                currentMonth,
                                dateVal
                            );
                        }
                    }
                }
            }
        });
    }

    // Arranges UI components into the main frame using layout managers
    static void initLayout() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setOpaque(false); 
        navPanel.add(buttonPrev);
        navPanel.add(LabelMonth);
        navPanel.add(comboYear);
        navPanel.add(buttonNext);
        navPanel.add(buttonDarkMode);

        // Logo row 
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        logoArea.setOpaque(false); 
        logoArea.add(calendarLogoLabel);

        // Header panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // ThemeManager will change this to true in dark mode
        headerPanel.add(logoArea, BorderLayout.NORTH);
        headerPanel.add(navPanel, BorderLayout.CENTER);

        panelCal.add(headerPanel, BorderLayout.NORTH);
        panelCal.add(theScrollPane, BorderLayout.CENTER);        
        pane.add(panelCal, BorderLayout.CENTER);
    }

    // Loads current date, sets initial calendar state, and populates year selector
    static void initData() {
        GregorianCalendar cal = new GregorianCalendar();
        todayDay   = cal.get(GregorianCalendar.DAY_OF_MONTH);
        todayMonth = cal.get(GregorianCalendar.MONTH);
        todayYear  = cal.get(GregorianCalendar.YEAR);
    
        currentMonth = todayMonth;
        currentYear  = todayYear;
    
        for (int i = todayYear - 100; i <= todayYear + 100; i++) {
            comboYear.addItem(String.valueOf(i));
        }
    
        CalendarUtils.refreshCalendar(todayMonth, todayYear);
    }

    // Final UI setup: sizing, theme application, and displaying the window
    static void finalizeUI() {
        mainFrame.setMinimumSize(new Dimension(620, 450));
        mainFrame.pack();
        buttonDarkMode.setText(darkMode ? "\u2600" : "\u263E");
        ThemeManager.applyTheme();
        mainFrame.setVisible(true);
    }
}