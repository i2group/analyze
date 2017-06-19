@echo off

:: Copyright (c) 2014, 2017 IBM Corp.
:: All rights reserved. This program and the accompanying materials
:: are made available under the terms of the Eclipse Public License v1.0
:: which accompanies this distribution, and is available at
:: http://www.eclipse.org/legal/epl-v10.html
:: 
:: Contributors:
::    IBM Corp - initial API and implementation and initial documentation

:: Stores the current JAVA_HOME and GROOVY_HOME values.
set JAVA_HOME_OLD=%JAVA_HOME%
SET GROOVY_HOME_OLD=%GROOVY_HOME%

:: Sets JAVA_HOME without .. shortcuts in the path, which doesn't work on Windows.
for %%i in ("%~dp0..\..\..\toolkit") do SET JAVA_HOME=%%~fi\tools\java
:: Clear GROOVY_HOME and allow Groovy to set it.
SET GROOVY_HOME=

:: Changes working directory to ensure that Groovy imports work when running this script from any directory.
pushd %~dp0

call %~dp0..\..\..\toolkit\tools\groovy\bin\groovy.bat %~dp0\build.groovy %*

:: Sets JAVA_HOME and GROOVY_HOME to their initial values.
set JAVA_HOME=%JAVA_HOME_OLD%
set GROOVY_HOME=%GROOVY_HOME_OLD%

:: Restores the current working directory to the initial working directory.
popd

exit /b %ERRORLEVEL%
