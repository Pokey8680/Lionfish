#!/system/bin/sh

setprop ctl.start phh-ota-make
# Wait 10s
sleep 10
cp /storage/emulated/0/ManualOTA/system.img /dev/phh-ota
setprop ctl.start phh-ota-switch
# Wait 10s
sleep 10
reboo
t
