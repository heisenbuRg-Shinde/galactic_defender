@echo off
echo =============================================
echo   Galactic Defender - Build and Run
echo =============================================
if not exist out mkdir out
echo Compiling sources...
powershell -Command "$f=(Get-ChildItem -Path 'src' -Recurse -Filter '*.java').FullName; & javac -encoding UTF-8 -d out $f 2>&1 | Write-Host"
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful! Starting game...
java -cp out com.galacticdefender.engine.GameEngine
