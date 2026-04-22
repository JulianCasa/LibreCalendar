import java.util.*;
import java.util.prefs.Preferences;

public class ReminderManager {

    private static final Preferences prefs =
            Preferences.userNodeForPackage(ReminderManager.class);

    private static final String KEY_PREFIX = "reminder_";


    public static String key(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }

    public static List<String> getReminders(int year, int month, int day) {
        String key = KEY_PREFIX + ReminderManager.key(year, month, day);
        String raw = prefs.get(key, "");

        List<String> list = new ArrayList<>();
        if (!raw.isEmpty()) {
            String[] parts = raw.split("\\|");
            for (String p : parts) {
                list.add(p);
            }
        }
        return list;
    }

    public static void saveReminders(int year, int month, int day, List<String> reminders) {
        String key = KEY_PREFIX + ReminderManager.key(year, month, day);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reminders.size(); i++) {
            sb.append(reminders.get(i));
            if (i < reminders.size() - 1) sb.append("|");
        }

        prefs.put(key, sb.toString());
    }

    public static void addReminder(int year, int month, int day, String text) {
        List<String> list = getReminders(year, month, day);
        list.add(text);
        saveReminders(year, month, day, list);
    }

    public static void deleteReminder(int year, int month, int day, int index) {
        List<String> list = getReminders(year, month, day);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            saveReminders(year, month, day, list);
        }
    }
}