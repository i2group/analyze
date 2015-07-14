@echo off

:: Copyright (c) 2014, 2015 IBM Corp.
:: All rights reserved. This program and the accompanying materials
:: are made available under the terms of the Eclipse Public License v1.0
:: which accompanies this distribution, and is available at
:: http://www.eclipse.org/legal/epl-v10.html
:: 
:: Contributors:
::    IBM Corp - initial API and implementation and initial documentation

:: Stores the current JAVA_HOME value.
set JAVA_HOME_OLD=%JAVA_HOME%

:: Sets JAVA_HOME without .. shortcuts in the path, which doesn't work on Windows.
for %%i in ("%~dp0..\..\..\IAP-Deployment-Toolkit") do SET JAVA_HOME=%%~fi\tools\java

:: Changes working directory to ensure that Groovy imports work when running this script from any directory.
pushd %~dp0

call %~dp0..\..\..\IAP-Deployment-Toolkit\tools\groovy\bin\groovy.bat %~dp0\build.groovy %*

:: Sets JAVA_HOME to its initial value.
set JAVA_HOME=%JAVA_HOME_OLD%

:: Restores the current working directory to the initial working directory.
popd

exit /b %ERRORLEVEL%
