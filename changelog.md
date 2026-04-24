# Changelog - LibreCalendar

All notable changes to the LibreCal project are documented in this file.

- Reformatted on-screen elements (month label, date grid) and added basic click interaction for calendar cells
- Replaced the original null layout system with BorderLayout and FlowLayout, allowing the UI to resize properly and behave consistently across different window sizes
- Implemented dynamic row height calculation based on available viewport height so the calendar scales cleanly when resized
- Implemented dynamic calendar generation logic, including calculating the correct number of rows and filling leading/trailing cells with dates from adjacent months
- Introduced negative values for non-current-month dates and updated the renderer to display them as inactive days with distinct styling
- Updated cell styling for today, weekends, selected cells, and inactive days to improve readability
- Implemented a dark mode toggle and persisted the setting using the Preferences API so the theme is restored on startup
- Refactored the program from a single file into multiple (LibreCal, ThemeManager, ReminderManager, CalendarUtils, Handlers, CalendarRenderer) to separate UI, logic, and data handling
- Reworked Abdullah’s reminder system to use persistent storage via the Preferences API, storing reminders as serialized strings by date as well as creating ReminderManager.java
- Stored reminders using the Preferences API with date-based keys so reminders persist between runs
- Enabled double-clicking a calendar cell to open the reminder dialog for that specific date
- Added time input controls (hour and minute spinners with AM/PM toggle) to support time-based reminders
- Implemented a custom JTable renderer (CalendarRenderer) to control alignment, coloring, and conditional formatting of each date cell
- Extended the renderer to display reminder indicators inside cells using HTML formatting, including a count of reminders for that day
- Implemented a background notifier using a Swing Timer that periodically checks the current time against stored reminders
- Triggered system tray notifications using the SystemTray API when a reminder time matches the current time
- Replaced string-based reminder handling with a structured Reminder object (hour, minute, AM/PM, message) to simplify parsing and improve reliability
- Added a calendar logo using given image asset
- Organized the project into a structured directory layout (src/, assets/, scripts/) for code, images, and build tools
- Created platform-specific build and run scripts for macOS and Windows, including a build.sh script to package/build LibreCal.app
- Added how many reminders missed notification
