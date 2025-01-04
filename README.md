# Lionfish
![ic_launcher](https://github.com/user-attachments/assets/98c0ed44-16c4-4d5b-88bb-5e3a15801a7b)

An app to manually update PHH-Based GSIs without fastbootd

# Installation
- Download the latest release of the app from my [releases page](https://github.com/Pokey8680/Lionfish/releases/), and install it.

- If a popup from Play Protect appears, you can scan it and install it. This appears because the app is brand new and Google dosen't know about it.

  # Requirements
- A device that launched with Android 10 or higher, and with Dynamic Partitions. You can check for this in the [Treble Info](https://f-droid.org/en/packages/tk.hack5.treblecheck/) app. This includes Samsung devices.
- Root access, preferably KernelSU or Apatch. Magisk *can* work, but it may interfere, see below
- A PHH/Trebledroid based GSI that you want to update. (Preferrably Android 12 or higher)
    

  # Screenshots
  ![photo_2025-01-04_15-35-32](https://github.com/user-attachments/assets/78bf6e69-1dfa-4fed-b8bc-ef6492239071) ![photo_2025-01-04_15-35-39](https://github.com/user-attachments/assets/cbaf242c-4d80-434b-881e-77298b13b404)

  # Usage
- Click the "Select File" button. You will be prompted to select the updated version of the GSI you want to flash.
- After that, click the "Start Update" button to proceed with the update.
- A message will appear while the update continues that tells you to wait.
- When the update is finished, you will get another message telling you so.
- You can then reboot your phone, and you should hopefully be in the updated version of your GSI!

  # Troubleshooting
- If your device no longer boots, go into a custom recovery, such as TWRP and delete `/metadata/phh/img.` Your device should return to the previous GSI it was on before.
 
  # Issues with Magisk
- You may have some problems using the update service if you have Magisk installed. For example:
  - The update may fail.
  - Your phone may get in a bootloop.
  - You may have to reinsall Magisk
  - etc...

# Other Stuff...
- The UI is a bit clunky at the moment, I will deal with it in a later release.
  
