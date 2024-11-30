#!/system/bin/sh

# Clear the screen at the beginning
clear

# Start phh-ota-make process
echo "Starting phh-ota-make..."
setprop ctl.start phh-ota-make

# Wait 10s
echo "Waiting for 10 seconds..."
sleep 10

# Copy the system.img to /dev/phh-ota
echo "Copying system.img to /dev/phh-ota..."
cp /where/is/your/unsparsed/system.img /dev/phh-ota

# Start phh-ota-switch process
echo "Starting phh-ota-switch..."
setprop ctl.start phh-ota-switch

# Wait 10s
echo "Waiting for 10 seconds..."
sleep 10

# Ask for confirmation to reboot the device
echo "Do you want to reboot the device now? (Y/n)"
read -p "Choice: " choice
if [ "$choice" != "Y" ] && [ "$choice" != "y" ] && [ "$choice" != "" ]; then
    echo "Reboot canceled by user."
    exit 0
fi

# Reboot the device
echo "Rebooting the device..."
reboot
