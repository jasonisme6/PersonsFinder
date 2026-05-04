@echo off
REM Database Seeding Script for Windows
REM Seeds MongoDB with 10,000 sample persons for testing and benchmarking

echo.
echo 🌱 MongoDB Database Seeding Script
echo ==================================
echo.

REM Check if MongoDB is running
docker ps | findstr mongodb >nul 2>&1
if errorlevel 1 (
    echo ❌ MongoDB container is not running
    echo    Starting MongoDB...
    docker-compose up -d
    echo    Waiting for MongoDB to be ready...
    timeout /t 5 /nobreak >nul
)

REM Check if OPENAI_API_KEY is set
if "%OPENAI_API_KEY%"=="" (
    echo ⚠️  OPENAI_API_KEY not set ^(not required for seeding^)
)

REM Default to 10,000 records
if "%SEED_COUNT%"=="" set SEED_COUNT=10000

echo 📊 Configuration:
echo    Records to seed: %SEED_COUNT%
echo.

echo 🚀 Starting seed process...
echo.

REM Run application with seed profile
gradlew.bat bootRun --args="--spring.profiles.active=seed" -Dorg.gradle.jvmargs="-DSEED_COUNT=%SEED_COUNT%"

echo.
echo ✅ Seeding complete!
echo.
echo 📈 Next steps:
echo    1. Run the application: gradlew.bat bootRun
echo    2. Test nearby search with 10k records
echo    3. Benchmark query performance
