@echo off
setlocal
echo Compiling...
if not exist bin mkdir bin
if not exist data mkdir data
dir /s /b src\*.java > sources.txt
javac -cp "lib/itextpdf-5.5.13.3.jar;lib/sqlite-jdbc-3.46.0.0.jar;lib/slf4j-api-1.7.36.jar;lib/slf4j-simple-1.7.36.jar;src" -d bin @sources.txt
if exist sources.txt del sources.txt

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful. Running application...
    java -cp "bin;lib/itextpdf-5.5.13.3.jar;lib/sqlite-jdbc-3.46.0.0.jar;lib/slf4j-api-1.7.36.jar;lib/slf4j-simple-1.7.36.jar" com.billing.system.Main
) else (
    echo Compilation failed.
    pause
)
