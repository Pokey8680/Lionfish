#!/system/bin/sh

# Add message saying creating Lionfish folder in userdata
ui_print ""
ui_print "Creating Lionfish folder in /storage/emulated/0/..."

# Wait for storage to mount
sleep 5

# Create the Lionfish folder if it doesn't exist
if [ ! -d "/storage/emulated/0/Lionfish" ]; then
    mkdir -p "/storage/emulated/0/Lionfish"
    ui_print ""
    ui_print "Lionfish folder created successfully."
else
    ui_print ""
    ui_print "Lionfish folder already exists."
fi

# Add message reminding user on where to put system.img
ui_print ""
ui_print "-------------------------"
ui_print " Remember to put your"
ui_print " updated system.img in the"
ui_print " Lionfish folder!"
ui_print "-------------------------"
