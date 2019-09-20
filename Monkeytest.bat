adb kill-service
adb connect 192.168.1.155:5555
adb shell am force-stop com.whzxw.uface.ether
adb shell am start com.whzxw.uface.ether/com.whzxw.uface.ether.activity.SplashActivity


@REM 产生10个[4,12]间的随机数
　　@echo off
　　REM 启用延迟环境变量扩展
　　setlocal enabledelayedexpansion
　　REM 设置随机数的最小和最大值以及求模用的变量
　　set min=1
　　set max=3
　　set /a mod=!max!-!min!+1

for /l %%i in (1,1,10) do (
      :start
　　REM 产生[min,max]之间的随机数
　　set /a r=!random!%%!mod!+!min!
　　echo.
　　echo 随机数%%i：!r!
       if !r! equ 1 goto open
       if !r! EQU 2 goto pauseopen
       if !r! EQU 3  goto finalopen
　　)


:open
adb shell input tap 420 800
TIMEOUT /T 1
adb shell input tap 320 650
goto start

:pauseopen
adb shell input tap 420 860
TIMEOUT /T 1
adb shell input tap 320 650
goto start

:finalopen
adb shell input tap 420 900
TIMEOUT /T 1
adb shell input tap 320 650
goto start