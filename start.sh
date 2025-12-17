#!/bin/bash

echo "Starting Blog Analytics Backend Service..."
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 8 or higher and try again"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven and try again"
    exit 1
fi

echo "Building project..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Error: Build failed"
    exit 1
fi

echo
echo "Starting application..."
echo "Application will be available at: http://localhost:9050"
echo "API documentation: http://localhost:9050/api/analytics/info"
echo "Health check: http://localhost:9050/api/analytics/health"
echo "Swagger UI: http://localhost:9050/swagger-ui/"
echo "API JSON: http://localhost:9050/v2/api-docs"
echo
echo "Press Ctrl+C to stop the server"
echo

java -jar target/blog-analytics-1.0.0.jar