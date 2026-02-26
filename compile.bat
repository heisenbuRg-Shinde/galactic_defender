@echo off
if not exist out mkdir out
if exist sourcelist.txt del sourcelist.txt
for /r src %%f in (*.java) do echo "%%f">> sourcelist.txt
javac -encoding UTF-8 -d out @sourcelist.txt
if %errorlevel% equ 0 (
    echo BUILD SUCCESS
) else (
    echo BUILD FAILED - check errors above
)
del sourcelist.txt 2>nul
