# Setting up a Raspberry Pi to show Family Calendar

Note that this guide is for Raspberry Pi Zero W.

## Setup Java runtime

Default java installation does not start.

Run `sudo update-alternatives --config java`, and choose:
```
* 2            /usr/lib/jvm/java-8-openjdk-armhf/jre/bin/java   1081      manual mode
``` 

## Create Family Calendar service to run at startup

Build and copy quarkus jar to /home/pi/familycalendar

### Create run.sh

Create file `/home/pi/familycalendar/run.sh` with this contents:
```
#!/bin/bash
cd "$(dirname "$0")"
java -jar /home/pi/familycalendar/familycalendarquarkus-1.0.0-SNAPSHOT-runner.jar
```

Run `chmod +x run.sh`.

### Create systemd service

Create a file `/lib/systemd/system/familycalendar.service` with this contents:
```
[Unit]
Description=Family calendar server
After=network.target

[Service]
Type=simple
ExecStart=/home/pi/familycalendar/run.sh
Restart=always

[Install]
WantedBy=multi-user.target
```

Finally enable the service by running:
```
sudo systemctl enable familycalendar.service
```

## Run Chromium browser at startup

We want:
* No mouse cursor
* No idle screen blank
* Start Chromium browser

Run: `sudo nano ~/.config/lxsession/LXDE-pi/autostart` and add contents:
```
@xset s off
@xset -dpms
@xset s noblank
@chromium-browser --noerrdialogs --incognito --kiosk http://localhost:8080/index.html
```

