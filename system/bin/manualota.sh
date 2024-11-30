#!/system/bin/sh

# Clear the screen at the beginning
clear

# Show a warning about the reboot
echo "Warning: This process will reboot the device once it is completed."
echo "Press Enter to continue or CTRL+C to cancel."

# Wait for the user to press Enter
read -r

# Start phh-ota-make process
echo "Starting phh-ota-make..."
setprop ctl.start phh-ota-make

# Wait 10s
echo "Waiting for 10 seconds..."
sleep 10

# Copy the system.img to /dev/phh-ota
echo "Copying system.img to /dev/phh-ota..."
cp /storage/emulated/0/ManualOTA/system.img /dev/phh-ota

# Start phh-ota-switch process
echo "Starting phh-ota-switch..."
setprop ctl.start phh-ota-switch

# Wait 10s
echo "Waiting for 10 seconds..."
sleep 10

# Reboot the device
echo "Rebooting the device..."
reboot
