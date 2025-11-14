@echo off
:: Gradle Wrapper for Windows
set DIR=%~dp0
set DEFAULT_JVM_OPTS=-Duser.home=%DIR%\.gradle
set DEFAULT_GRADLE_OPTS=-Dgradle.home=%DIR%\gradle
set DEFAULT_GROOVY_OPTS=-Dgroovy.home=%DIR%\groovy
set DEFAULT_PATH=%DIR%\gradle;%DIR%in;%PATH%
set JAVA_HOME=%JAVA_HOME%
:: Running gradle tasks
%DIR%\gradlein\gradle %*
