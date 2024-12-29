# Lionfish
A tool for updating PHH-Based GSIs on dynamic devices without going to fastbootd

# 🔎 About this tool 
- This is a module designed to easily put PHH's manual OTA script into /system/bin/sh/, so that it can be run in a Terminal Emulator, such as Termux.
- It allows an easy way to point to the updated image that PHH's internal mechanisms will use, and my modification also includes strings that put the user in the know of what's happening during the OTA process.

# ❓ Why? 
It's useful in some cases. For example, a small ammount of GSIs, which don't come with PHH's OTA updater UI, but you might still want to use their GSI without the hassle of using fastbootd every time you want to update it. This tool makes that easier, you don't have to use fastbootd.

# 💿 Module Installation 
- Download the module from my releases page, and install it using Magisk, KernelSU or APatch.

- After installation, it will make a folder in your userdata called Lionfish.

# 📦 Usage

- Transfer the updated image of your GSI (unparsed) to the Lionfish folder, and rename it to "system.img".

- Go into a terminal emulator app & type "su -c lionfish.sh".

- The script will start the phh-ota-make process, which prepares the system to take an update.
- It will then copy your system.img to /dev/phh-ota, so that it can begin the phh-ota-switch process, which will use internal mechanisms to switch over to the updated GSI image.

- After this, your phone should reboot, hopefully in the updated version of your GSI!

# 👨‍💻 Troubleshooting 
- If for whatever reason, the system no longer boots properly, you need to go to TWRP or another custom recovery, and delete "/metadata/phh/img". When you do this and reboot, You should hopefully return to the previous version of the GSI you were using.

# ⚠️ Warnings 

- You can only use this with a device that supports Dynamic Partitions.
  - You can check for this in the *Treble Info* app.
- The OTA mechanism may conflict with Magisk.
  - You may have less issues with KernelSU or APatch.
- This tool was not designed to be used in DSU mode.

# Special Thanks
- Special thanks to phhusson for creating the OTA mechanism, and publishing the manual OTA script.
