# ManualOTA
A tool for updating PHH-Based GSIs without going to fastbootd

# Instructions
- Download the module from my releases page, and install it using Magisk or KernelSU.

- After installation, it will make a folder in your userdata called ManualOTA.

  - If it doesn't make the folder, you can make it yourself. Remember to place it in storage/emulated/0/

- Transfer the updated image of your GSI (unparsed) to the ManualOTA folder, and rename it to "system.img".

- Go into a terminal emulator app (For example: Termux) and give it root access by typing "su".

  - For KernelSU users, go into the KernelSU manager app > Superuser, click on your terminal emulator, enable superuser & then restart your terminal emulator.

- Go into Termux (or another terminal emulator app) and type "su -c manualota.sh".

- It won't say anything, but it's actually doing stuff in the background. (Will fix later.) after a couple seconds your phone will reboot.

- Hopefully you should now be in the updated version of your GSI!


I apologize for anything unpolished, this was a tool developed by me in haste.
I could probably develop an elegant app with a nicer UI if I wasn't lazy.

# Troubleshooting
- If for whatever reason, the system no longer boots properly, you need to go to TWRP or another custom recovery, and delete "/metadata/phh". When you do this and reboot, You should hopefully return to the previous version of the GSI you were using.
