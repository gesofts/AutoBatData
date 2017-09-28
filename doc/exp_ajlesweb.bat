set "CURRENT_DIR=%cd%"
cd "%CURRENT_DIR%"
if exist "%CURRENT_DIR%"\"data-%date:~0,4%%date:~5,2%%date:~8,2%" goto expExe
md "%CURRENT_DIR%"\"data-%date:~0,4%%date:~5,2%%date:~8,2%"
:expExe
EXP ajlesweb/ajlesweb@192.168.20.132:1521/orcl BUFFER=64000 FILE="data-%date:~0,4%%date:~5,2%%date:~8,2%\ajlesweb.DMP" OWNER=ajlesweb

