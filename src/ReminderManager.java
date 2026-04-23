import java.util.*;
import java.util.prefs.Preferences;

public class ReminderManager {

    private static final Preferences prefs =
            Preferences.userNodeForPackage(ReminderManager.class);

    private static final String KEY_PREFIX = "reminder_";

    public static class Reminder {
        public int hour;
        public int minute;
        public String amPm;
        public String message;

        public Reminder(int hour, int minute, String amPm, String message) {
            this.hour = hour;
            this.minute = minute;
            this.amPm = amPm;
            this.message = message;
        }

        public String getTimeKey() {
            return String.format("%d:%02d %s", hour, minute, amPm);
        }

        public String serialize() {
            return getTimeKey() + " - " + message;
        }

        public static Reminder deserialize(String raw) {
            String[] parts = raw.split(" - ", 2);
            if (parts.length != 2) return null;

            String[] timeParts = parts[0].split(" ");
            if (timeParts.length != 2) return null;

            String[] hm = timeParts[0].split(":");
            if (hm.length != 2) return null;

            int hour = Integer.parseInt(hm[0]);
            int minute = Integer.parseInt(hm[1]);
            String amPm = timeParts[1];

            return new Reminder(hour, minute, amPm, parts[1]);
        }

        public String timeString() {
            return getTimeKey();
        }

        @Override
        public String toString() {
            return timeString() + " - " + message;
}
    }

    public static String key(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }

    public static List<Reminder> getReminders(int year, int month, int day) {
        String raw = prefs.get(KEY_PREFIX + key(year, month, day), "");
        List<Reminder> list = new ArrayList<>();

        if (!raw.isEmpty()) {
            String[] parts = raw.split("\\|");
            for (String p : parts) {
                Reminder r = Reminder.deserialize(p);
                if (r != null) list.add(r);
            }
        }

        return list;
    }

    public static void saveReminders(int year, int month, int day, List<Reminder> reminders) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < reminders.size(); i++) {
            sb.append(reminders.get(i).serialize());
            if (i < reminders.size() - 1) sb.append("|");
        }

        prefs.put(KEY_PREFIX + key(year, month, day), sb.toString());
    }

    public static void addReminder(int year, int month, int day, Reminder r) {
        List<Reminder> list = getReminders(year, month, day);
        list.add(r);
        saveReminders(year, month, day, list);
    }

    public static void deleteReminder(int year, int month, int day, int index) {
        List<Reminder> list = getReminders(year, month, day);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            saveReminders(year, month, day, list);
        }
    }
}