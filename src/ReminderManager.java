import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

        public LocalTime toLocalTime() {
            int h = this.hour;
            if (amPm.equalsIgnoreCase("PM") && hour != 12) h += 12;
            else if (amPm.equalsIgnoreCase("AM") && hour == 12) h = 0;
            return LocalTime.of(h, minute);
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

        // chronologically sort reminders within dates
        list.sort(Comparator.comparing(Reminder::toLocalTime));
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
        list.sort(Comparator.comparing(Reminder::toLocalTime));
        saveReminders(year, month, day, list);
    }

    public static void deleteReminder(int year, int month, int day, int index) {
        List<Reminder> list = getReminders(year, month, day);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            saveReminders(year, month, day, list);
        }
    }

    
    // Checks for any reminders that are past due and sends a notification if found
    public static void checkMissedReminders() {
        int missedCount = 0;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

        try {
            String[] allKeys = prefs.keys();
            for (String key : allKeys) {
                if (key.startsWith(KEY_PREFIX)) {
                    String datePart = key.substring(KEY_PREFIX.length()); 
                    LocalDate reminderDate = LocalDate.parse(datePart);

                    List<Reminder> reminders = getReminders(
                        reminderDate.getYear(), 
                        reminderDate.getMonthValue() - 1, 
                        reminderDate.getDayOfMonth()
                    );

                    for (Reminder r : reminders) {
                        LocalTime reminderTime = LocalTime.parse(r.getTimeKey(), timeFormatter);
                        LocalDateTime combinedDT = LocalDateTime.of(reminderDate, reminderTime);

                        if (combinedDT.isBefore(now)) {
                            missedCount++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (missedCount > 0) {
            CalendarUtils.Notifier.send("Missed Reminders", 
                "You have " + missedCount + " past reminders waiting for you!");
        }
    }
}