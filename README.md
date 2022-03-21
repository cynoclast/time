# time

## Does things like:

1. Calculate time to leave for a day:

`tm 8:30-12:30 12:40-`

`Time to leave: 16:40`

2. Calculate the hours for a day:

`tm 8:30-12:30 12:40-6:40`

`8:30-12:30 12:40-6:40 > 10.0`

3. Calculate when to leave on Friday targeting 40.0 hours:

`tm 8.5 9.27 8.83 8.87 9:45-1:23 1:33-`

`14:11`

`54 minutes`

## Configure (optional)

Edit gradle.properties `deploy.dir` to something on your path. Default is `/users/cynoclast/bin`.

## Build:

`./gradlew clean fatJar deploy`

## Run:

``java -classpath /Users/`whoami`/bin/time-1.0.jar com.cynoclast.time.Time``

Best used as an alias like:

``alias tm="java -classpath /Users/`whoami`/bin/time-1.0.jar com.cynoclast.time.Time"``


