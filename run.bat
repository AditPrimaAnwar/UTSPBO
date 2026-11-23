@echo off
rem Kompilasi lalu jalankan aplikasi Klinik Rawat Jalan
javac -encoding UTF-8 -d out src\klinik\adt\*.java src\klinik\model\*.java src\klinik\app\*.java
if errorlevel 1 (
    echo Kompilasi gagal.
    exit /b 1
)
java -cp out klinik.app.Main
