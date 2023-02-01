export BOOT_PORT=1984

curl -X POST localhost:${BOOT_PORT}/actuator/shutdown
