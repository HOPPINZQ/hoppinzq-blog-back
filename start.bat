@echo off
echo Starting Blog Analytics Backend Service...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 8 or higher and try again
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven and try again
    pause
    exit /b 1
)

echo Building project...
mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo Error: Build failed
    pause
    exit /b 1
)

echo.
echo Starting application...
echo Application will be available at: http://localhost:9050
echo API documentation: http://localhost:9050/api/analytics/info
echo Health check: http://localhost:9050/api/analytics/health
echo Swagger UI: http://localhost:9050/swagger-ui/
echo API JSON: http://localhost:9050/v2/api-docs
echo.
echo Press Ctrl+C to stop the server
echo.

java -jar target/hoppinzq-blog-back-1.0.0-SNAPSHOT.jar

pause