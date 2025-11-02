# RVZ-to-WBF-converter
This programm allows users to convert RVZ files directly do WBF without having to go manually through 2 different applications.

before being able to run the programm, you need to have both dolphin (for RVZ to ISO conversion) and WIT (for ISO to WBF conversion) downloaded and have access to their commands using the cmd.
After downloading them you can check if you have access to them with the commands "DolphinTool.exe --help" and "wit.exe --help" in the command prompt.
If you get a mistake that means the files haven't been automatically added to a Path. You can fix that by going into the "Enviromental Variables" from the Windows button, then clicking on Path (for the User or Global depeneds on what you want, but will work either way), then EDIT -> NEW and adding the filepaths for Dolphin and Wit. After that you can close and relaunch the cmd and you should have access to the commands of the 2 apps.

Now that you are done with the setup, you can open the java project and run the project with Maven (on the right side you should have Maven -> Plugins -> javafx ->javafx:run

After a few seconds (depending on computer speed and file size) you should get a Wbf version of the file you chose in the same folder, ready to be used
