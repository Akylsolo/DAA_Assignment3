@echo off
setlocal
where mvn >nul 2>&1
if %ERRORLEVEL% equ 0 (
    mvn %*
) else (
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
        "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" %*
    ) else (
        echo "Error: Maven not found. Please install Maven or add it to PATH."
        exit /b 1
    )
)
