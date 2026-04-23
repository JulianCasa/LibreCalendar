import java.util.*;
import java.util.prefs.Preferences;
// import static ReminderManager.*; // Removed due to unresolved import error


public class HolidayManager {

    private static final Preferences prefs =
            Preferences.userNodeForPackage(HolidayManager.class);

    private static final String KEY_PREFIX = "holiday_";
    private static final Set<Integer> loadedYears = new HashSet<>();

    /**
     * Generate a unique key for a holiday date (format: YYYY-MM-DD)
     */
    public static String key(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }

    /**
     * Get holiday name for a specific date, or null if no holiday
     */
    public static String getHoliday(int year, int month, int day) {
        String key = KEY_PREFIX + HolidayManager.key(year, month, day);
        return prefs.get(key, null);
    }

    /**
     * Check if a date has a holiday
     */
    public static boolean isHoliday(int year, int month, int day) {
        return getHoliday(year, month, day) != null;
    }

    /**
     * Add a holiday to a specific date
     */
    public static void addHoliday(int year, int month, int day, String holidayName) {
        String key = KEY_PREFIX + HolidayManager.key(year, month, day);
        prefs.put(key, holidayName);
        
        // Automatically add holiday as a reminder so it displays in the calendar cell
        // Only add if it doesn't already exist
        String reminderKey = "🎉 " + holidayName;
        List<String> existingReminders = ReminderManager.getReminders(year, month, day);
        if (!existingReminders.contains(reminderKey)) {
            ReminderManager.addReminder(year, month, day, reminderKey);
        }
    }

    /**
     * Remove a holiday from a specific date
     */
    public static void removeHoliday(int year, int month, int day) {
        String key = KEY_PREFIX + HolidayManager.key(year, month, day);
        prefs.remove(key);
    }

    /**
     * Load preset holidays for a given year (common US holidays)
     */
    public static void loadPresetHolidays(int year) {
        // Check if this year's holidays have already been loaded
        if (loadedYears.contains(year)) {
            return;
        }
        
        loadedYears.add(year);
        
        // New Year's Day
        addHoliday(year, 0, 1, "New Year's Day");
        
        // MLK Day (third Monday in January)
        addHoliday(year, 0, getThirdMonday(year, 0), "MLK Jr. Day");
        
        // Presidents Day (third Monday in February)
        addHoliday(year, 1, getThirdMonday(year, 1), "Presidents Day");
        
        // Memorial Day (last Monday in May)
        addHoliday(year, 4, getLastMonday(year, 4), "Memorial Day");
        
        // Independence Day
        addHoliday(year, 6, 4, "Independence Day");
        
        // Labor Day (first Monday in September)
        addHoliday(year, 8, getFirstMonday(year, 8), "Labor Day");
        
        // Columbus Day (second Monday in October)
        addHoliday(year, 9, getSecondMonday(year, 9), "Indigenous Peoples' Day");
        
        // Veterans Day
        addHoliday(year, 10, 11, "Veterans Day");
        
        // Thanksgiving (fourth Thursday in November)
        addHoliday(year, 10, getFourthThursday(year, 10), "Thanksgiving");
        
        // Christmas
        addHoliday(year, 11, 25, "Christmas");
    }

    // Helper methods to calculate specific dates
    private static int getFirstMonday(int year, int month) {
        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        int dayOfWeek = cal.get(GregorianCalendar.DAY_OF_WEEK);
        if (dayOfWeek == GregorianCalendar.MONDAY) return 1;
        return 1 + (9 - dayOfWeek);
    }

    private static int getSecondMonday(int year, int month) {
        return getFirstMonday(year, month) + 7;
    }

    private static int getThirdMonday(int year, int month) {
        return getFirstMonday(year, month) + 14;
    }

    private static int getFourthThursday(int year, int month) {
        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        int dayOfWeek = cal.get(GregorianCalendar.DAY_OF_WEEK);
        int thursday = 5; // GregorianCalendar.THURSDAY
        int daysUntilThursday = (thursday - dayOfWeek + 7) % 7;
        return 1 + daysUntilThursday + 21; // 4th Thursday
    }

    private static int getLastMonday(int year, int month) {
        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        int maxDay = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        
        cal.set(year, month, maxDay);
        int dayOfWeek = cal.get(GregorianCalendar.DAY_OF_WEEK);
        
        if (dayOfWeek == GregorianCalendar.MONDAY) return maxDay;
        return maxDay - (dayOfWeek - GregorianCalendar.MONDAY + 7) % 7;
    }
}
