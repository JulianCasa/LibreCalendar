@echo off
cd /d "%~dp0.."

rmdir /s /q output 2>nul
rmdir /s /q input 2>nul
del /q *.jar 2>nul
del /q src\*.class 2>nul

echo Generating your calendar...
javac src\*.java


jar cfe LibreCalendar.jar LibreCal -C src . assets

mkdir input
copy LibreCalendar.jar input\

echo Building Windows Executable...
jpackage ^
  --type exe ^
  --name "LibreCalendar" ^
  --input input ^
  --main-jar LibreCalendar.jar ^
  --main-class LibreCal ^
  --icon assets\LibreCal.ico ^
  --dest output ^
  --win-shortcut ^
  --win-menu


del /q LibreCalendar.jar
rmdir /s /q input
del /q src\*.class

echo Check the output folder for your executable calendar!
pause