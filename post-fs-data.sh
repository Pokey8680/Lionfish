#!/system/bin/sh

# Create the ManualOTA folder in /storage/emulated/0/ if it doesn't exist
if [ ! -d /storage/emulated/0/ManualOTA ]; then
    mkdir /storage/emulated/0/ManualOTA
    echo "Created ManualOTA folder in /storage/emulated/0/"
else
    echo "ManualOTA folder already exists."
fi
