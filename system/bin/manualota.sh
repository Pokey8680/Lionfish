#!/system/bin/sh

# Clear the screen at the beginning
clear

# Show a warning about the reboot
echo "Warning: This process will reboot the device once it is completed."
echo " "
echo "Press Enter to continue or CTRL+C to cancel."
echo " "

# Wait for the user to press Enter
read -r

# Clear screen
clear

# Check for Magisk presence
if [ -f "/system/bin/magisk" ]; then
    ui_print "Warning: Magisk is installed."
    ui_print " "
    ui_print "If you proceed with the installation, you may lose Magisk!"
    ui_print " "
    ui_print "Press Enter to continue or Ctrl+C to cancel."
    ui_print " "
    read
fi

# Clear screen
clear

# Start phh-ota-make process
echo "Preparing for update..."
echo " "
setprop ctl.start phh-ota-make

# Wait 10s
echo "Please wait..."
echo " "
sleep 10

# Copy the system.img to /dev/phh-ota
echo "Copying system.img"
echo " "
cp /storage/emulated/0/ManualOTA/system.img /dev/phh-ota

# Start phh-ota-switch process
echo "Starting update process..."
echo " "
setprop ctl.start phh-ota-switch

# Wait 10s
echo "Please wait..."
echo " "
sleep 10

# Clear screen
clear

# Reboot the device
echo "Done! Rebooting..."
echo " "
echo "If you encounter errors, delete /metadata/phh/img from recovery!"
sleep 6
reboot
