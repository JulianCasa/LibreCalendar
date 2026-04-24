@echo off
cd /d "%~dp0.."

del /q LibreCalendar.jar 2>nul
del /q src\*.class 2>nul

echo Compiling Java code...
javac src\*.java

echo Packaging JAR with assets...
jar cfe LibreCalendar.jar LibreCal -C src . assets

echo Launching LibreCalendar...
java -jar LibreCalendar.jar