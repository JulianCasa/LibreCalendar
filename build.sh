#!/bin/bash

rm -rf output input *.jar src/*.class LibreCal.icns

echo "Generating your calendar..."
javac src/*.java

# include assets in jar
jar cfe LibreCalendar.jar LibreCal -C src . assets

# macOS Icon
iconutil -c icns assets/LibreCal.iconset -o LibreCal.icns

# package as macOS app
mkdir -p input
cp LibreCalendar.jar input/

jpackage \
  --type app-image \
  --name "LibreCalendar" \
  --mac-package-identifier com.arek.librecalendar \
  --input input \
  --main-jar LibreCalendar.jar \
  --main-class LibreCal \
  --icon LibreCal.icns \
  --dest output

rm LibreCal.icns
rm LibreCalendar.jar
rm -rf input
rm src/*.class

read -p "Would you like to install LibreCalendar to your user Applications folder? (y/n): " install_choice

if [[ "$install_choice" == "y" || "$install_choice" == "Y" ]]; then
  rm -rf "$HOME/Applications/LibreCalendar.app"
  cp -R output/LibreCalendar.app "$HOME/Applications/" || \
echo "Installation failed! Check LibreCalendar/output and drap the app to your Applications folder manually."
  rm -rf output
  echo "LibreCalendar has been installed to your user Applications folder!"
else
  echo "Your app is in: output/LibreCalendar.app!"
fi