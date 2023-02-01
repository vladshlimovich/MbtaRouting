@rem helper to start local instance on windows

SETLOCAL

SET BOOT_DIR=.
SET BOOT_PORT=1984
SET LOG_DIR=C:\temp\logs
SET LOG_FILE=${LOG_DIR}/newApp.log

java -Xms512m -Xmx2048m -jar %BOOT_DIR%/NewApp-0.0.1-SNAPSHOT.jar


