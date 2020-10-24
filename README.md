![](https://github.com/steffeleffe/family-calendar/workflows/CI/badge.svg)

# family-calendar

![Example](docs/screenshot.png)

## Building

Run this gradle task to build the uber-jar:
```
./gradlew quarkusBuild --uber-jar
``` 

## Setup

Enable Google calendar API; see https://developers.google.com/calendar/quickstart/java.

Download the credentials.json and save it in /src/main/resources/credentials.json.

## Google calendar event format

```
billede:<url>
hvem:<participants>
```

Example:
```
billede:https://www.flaticon.com/svg/static/icons/svg/921/921664.svg
hvem:ada
```

![Karate](docs/karate.png)


## Configuration

### Users

TODO 

### Google calenders

TODO

# Putting it together

[Raspberry Pi setup guide](docs/raspberrypi.md)

