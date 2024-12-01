#!/system/bin/sh

# Add message saying creating ManualOTA folder in userdata
ui_print ""
ui_print "Creating ManualOTA folder in /storage/emulated/0/..."

# Wait for storage to mount
sleep 5

# Create the ManualOTA folder if it doesn't exist
if [ ! -d "/storage/emulated/0/ManualOTA" ]; then
    mkdir -p "/storage/emulated/0/ManualOTA"
    ui_print ""
    ui_print "ManualOTA folder created successfully."
else
    ui_print ""
    ui_print "ManualOTA folder already exists."
fi

# Add message reminding user on where to put system.img
ui_print ""
ui_print "-------------------------"
ui_print " Remember to put your"
ui_print " updated system.img in the"
ui_print " ManualOTA folder!"
ui_print "-------------------------"
