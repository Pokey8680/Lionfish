#!/system/bin/sh

# Define the path to the OTA script
OTA_SCRIPT="/system/bin/manualota.sh"

# Check if the OTA script exists and remove it
if [ -f "$OTA_SCRIPT" ]; then
    rm -f "$OTA_SCRIPT"
fi

