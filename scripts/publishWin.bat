REM free some space

DIR c:\java8
DIR c:\java8\java-1.8.0-openjdk-1.8.0.191-1.b12.ojdkbuild.windows.x86_64
SET JAVA_HOME=c:\java8\java-1.8.0-openjdk-1.8.0.191-1.b12.ojdkbuild.windows.x86_64

REM CALL refreshenv
REM SET GRADLE_ERROR_LEVEL=%errorlevel%
REM CALL gradlew.bat --stop
REM exit /b %GRADLE_ERROR_LEVEL%

CALL gradlew.bat --no-daemon -s publishMingwPublicationToMavenRepository -PSONATYPE_USERNAME="%SONATYPE_USERNAME%" -PSONATYPE_PASSWORD="%SONATYPE_PASSWORD%"
