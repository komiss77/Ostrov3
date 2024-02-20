ChCp 65001

call gradlew build


timeout 2
::cd /d "%~dp0"
start /d "C:\server\paper" paper.lnk
::call C:\server\paper\start_paper.bat

::start /d "C:\server\paper" start_paper.bat



::pause