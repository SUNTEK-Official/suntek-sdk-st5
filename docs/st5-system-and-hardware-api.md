# ST5 SdkApi

Android SDK for the ST5 embedded platform, providing system-level APIs for display control, hardware I/O, display scaler chip control, power management, and device administration.

## Overview

The **ST5 SdkApi** (`com.sdkapi`) is an Android library (AAR) that exposes low-level hardware and system capabilities of ST5-series smart terminal devices. It communicates with a privileged system service (`com.sdkapi.sdkapiservice`) via AIDL-based IPC, allowing third-party applications to access device-specific functionality that is not available through the standard Android SDK.

| Property          | Value                        |
| ----------------- | ---------------------------- |
| Version           | `4.1.0.260410.0350`          |
| Package           | `com.sdkapi`                 |
| Min SDK           | API 19 (Android 4.4)         |
| Target SDK        | API 30 (Android 11)          |
| Artifact          | `SdkApiJar_V1.260410.0350.aar` |
| System Service    | `com.sdkapi.sdkapiservice`   |

## Architecture

```
┌──────────────────────────────┐
│  Your Application            │
│  (uses com.sdkapi.*)         │
└─────────────┬────────────────┘
              │ AIDL IPC
┌─────────────▼────────────────┐
│  SdkApi Service              │
│  (com.sdkapi.sdkapiservice)  │
│  ─ system-privileged app     │
└─────────────┬────────────────┘
              │ JNI / sysfs / kernel
┌─────────────▼────────────────┐
│  ST5 Hardware & Peripherals  │
│  (Display, GPIO, Scale, ...) │
└──────────────────────────────┘
```

The SDK uses a **singleton** pattern. Obtain the instance via `SdkApi.newInstance(context)`, then access individual API modules through getter methods:

```java
SdkApi.newInstance(context);
SdkApi sdkApi = SdkApi.getInstance();

// Access API modules
sdkApi.Display().setBrightness(screen, brightness);
sdkApi.Hardware().setGpioOutput(pin, value, true);
sdkApi.SystemCtrl().deviceReboot();
```

## API Modules

### 1. Display (`ApiDisplay`)

Controls screen parameters including rotation, brightness, LVDS configuration, resolution, and backlight.

| Method                        | Description                            |
| ----------------------------- | -------------------------------------- |
| `setScreenRotate(screen, rotate)` | Set screen rotation angle          |
| `getScreenRotate(screen, cb)`     | Get current screen rotation        |
| `setScreenOnoff(screen, onoff)`   | Turn screen on/off                 |
| `setBrightness(screen, level)`    | Set backlight brightness           |
| `getBrightness(screen, cb)`       | Get current brightness             |
| `setBacklightInverse(screen, inv)`| Invert backlight polarity          |
| `setBacklightPWM(screen, freq, duty)` | Configure PWM backlight       |
| `setResolution(screen, res)`      | Change display resolution          |
| `getSupportedResolutionsList(screen)` | List supported resolutions    |
| `setLvdsMode(screen, mode)`       | Set LVDS mode                      |
| `setLvdsSwap(screen, swap)`       | Swap LVDS lanes                    |
| `setLvdsChannel(screen, channel)` | Set LVDS channel count             |
| `setCustomTiming(timing)`         | Apply custom display timings       |
| `setCustomMipiCmds(cmds, len)`    | Send custom MIPI commands          |
| `setScreenGraphicDisplay(...)`    | Configure graphic display params   |
| `switchLvdsToVbyOne(enable)`      | Switch between LVDS and V-by-One   |
| `setVbyOneSignal(enable)`         | Enable/disable V-by-One signal     |

### 2. Hardware (`ApiHardware`)

Provides GPIO, serial port, CAN bus, flash storage, USB power, and peripheral LED control.

| Method                          | Description                          |
| ------------------------------- | ------------------------------------ |
| `getGpioInput(chip, pin)`       | Read GPIO input state                |
| `setGpioOutput(chip, pin, val)` | Set GPIO output level                |
| `flashRead(type, addr, cb)`     | Read from flash storage              |
| `flashWrite(type, addr, data, cb)` | Write to flash storage            |
| `flashReadAddr(type, type2, addr, cb)` | Read flash with address type  |
| `flashWriteAddr(type, type2, addr, data, cb)` | Write flash with address type |
| `setRunningLedOnoff(onoff, cb)` | Toggle running status LED            |
| `setKeyBoardLedROnoff(onoff, cb)` | Toggle keyboard red LED            |
| `setKeyBoardLedGOnoff(onoff, cb)` | Toggle keyboard green LED          |
| `setSpeakerConfigsSave(onoff, vol, mode)` | Configure speaker            |
| `openSerial(dev, baud, ...)`    | Open serial port                     |
| `closeSerial(fd)`               | Close serial port                    |
| `readSerial(fd, buf, len)`      | Read from serial port                |
| `writeSerial(fd, buf, len)`     | Write to serial port                 |
| `setSerialPortOnOff(dev, onoff)`| Enable/disable serial port           |
| `canBusConfigUp(channel)`       | Configure CAN bus                     |
| `canOpen(channel)`              | Open CAN bus channel                  |
| `canSend(fd, id, len, data, flag)` | Send CAN frame                    |
| `canRecv(fd, ids)`              | Receive CAN frame                     |
| `canClose(fd)`                  | Close CAN bus channel                 |
| `mcuRead(addr, buf, len)`       | Read from MCU                        |
| `mcuWrite(addr, buf, len)`      | Write to MCU                         |
| `setUsbPortPowerOnoff(port, onoff)` | Toggle USB port power            |
| `getUsbPortNumber(vid, pid)`    | Get USB port number by VID/PID       |
| `getRcuState()` / `setRcuState(state)` | Remote control unit state    |

### 3. Scale (`ApiScale`)

Interface for display scaler chip control (panel scaling & touchpoint controller events).

| Method                                              | Description                      |
| --------------------------------------------------- | -------------------------------- |
| `registerScaleListener(appId, listener)`             | Register display scaler event listener    |
| `unregisterScaleListener(appId)`                     | Unregister display scaler listener        |
| `setScaleEvent(appId, event, data, args, argsLen)`   | Send display scaler control event            |

### 4. System Control (`ApiSystemCtrl`)

System-level operations: reboot, screenshot, standby, silent APK install, UI bar control, network, camera, and touch configuration.

| Method                          | Description                          |
| ------------------------------- | ------------------------------------ |
| `deviceReboot()`                | Reboot the device                    |
| `setDeviceShallowOnoff(onoff)`  | Enter/exit shallow standby           |
| `setDeviceDeepStandby()`        | Enter deep standby mode              |
| `setDeviceDeepStandby(delay)`   | Deep standby with delay (seconds)    |
| `shotScreen(path, quality, cb)` | Capture screenshot                   |
| `shotHdmiInScreen(path, cb)`    | Capture HDMI input screenshot        |
| `silentInstallApk(path, reboot, cb)` | Silently install APK            |
| `silentUninstallApk(pkg, cb)`   | Silently uninstall APK               |
| `silentInstallApkForPackage(...)` | Install APK with package name      |
| `formatStorageDevice(type, cb)` | Format storage device                |
| `recoverySystem(mode)`          | Trigger system recovery              |
| `setFanOnoff(onoff)`            | Control cooling fan                  |
| `setBootVideoVolume(vol)`       | Set boot video volume                |
| `setStatusBarDisplay(show)`     | Show/hide status bar                 |
| `setStatusBarDisplay2(show)`    | Show/hide secondary status bar       |
| `setNavigationBar(show)`        | Show/hide navigation bar             |
| `setStatusBarDragDown(enable)`  | Enable/disable status bar pull-down  |
| `setNavigationBarSwipeUp(enable)` | Enable/disable nav bar swipe-up   |
| `setStatusBarState(mode, enable, duration)` | Configure status bar state |
| `setNavigationBarState(mode, enable, duration)` | Configure nav bar state |
| `setSystemBarDisplayNotStore(mode)` | Set bar display without persisting |
| `setNetworkPriority(ifaces[])`  | Set network interface priority       |
| `resetUpTime(time)`             | Reset system uptime                  |
| `setNtpServerAddress(addr)`     | Configure NTP server                 |
| `setCameraOrientation(deg)`     | Set camera orientation               |
| `setCameraFacing(dir)`          | Set camera facing direction          |
| `setTouchOrientation(deg)`      | Set touch panel orientation          |
| `setTouchState(id, cfg1, cfg2, state)` | Configure touch device         |
| `getTPLastTouchTime()`          | Get last touch panel event time      |

### 5. System Time (`ApiSystemTime`)

| Method                    | Description                  |
| ------------------------- | ---------------------------- |
| `setSystemTime(millis)`   | Set system clock (epoch ms)  |
| `getRtcTime()`            | Get RTC time string          |
| `setAutoTimeOnoff(onoff)` | Enable/disable auto time sync|
| `getAutoTimeOnoff()`      | Get auto time sync state     |

### 6. Timer Switch (`ApiTimerOnoff`)

Scheduled power on/off control with support for weekly schedules and delayed actions.

| Method                                  | Description                       |
| --------------------------------------- | --------------------------------- |
| `setTimerSwitchOnoff(onoff)`            | Enable/disable timer switch        |
| `setTimerOnTime(time)` / `clearTimerOnTime()`  | Set/clear daily power-on time |
| `setTimerOffTime(time)` / `clearTimerOffTime()`| Set/clear daily power-off time|
| `setTimerOnYmdTime(ymdTime)`            | Set one-shot power-on date & time  |
| `setTimerOffYmdTime(ymdTime)`           | Set one-shot power-off date & time |
| `setTimerType(type)`                    | Set timer type (daily/once)        |
| `setPowerOnOffWithWeekly(onWeeks, offWeeks, hours)` | Weekly schedule     |
| `setDelayedPowerOffTime(millis)`        | Set delayed power-off countdown    |
| `setDelayedPowerOnTime(millis)`         | Set delayed power-on countdown     |
| `setTimerStandbyMode(mode)`             | Set standby mode for timer         |

### 7. Watchdog (`ApiWatchdog`)

Hardware watchdog timer for system reliability.

| Method                      | Description                        |
| --------------------------- | ---------------------------------- |
| `setWatchdogOnoff(onoff)`   | Enable/disable hardware watchdog   |
| `setWatchdogTimeout(sec)`   | Set watchdog timeout in seconds    |
| `feedWatchdog()`            | Feed the watchdog (reset timer)    |

### 8. Device Info (`ApiDeviceInfo`)

Read and configure device identification, firmware, and system properties.

| Method                        | Description                        |
| ----------------------------- | ---------------------------------- |
| `getApiVersion()`             | Get SDK API version                |
| `getApiSdkServiceVersion()`   | Get backend service version        |
| `getFirmwareVersion()`        | Get device firmware version        |
| `getChipName()`               | Get SoC/chipset name               |
| `getSerialNumber()`           | Get device serial number           |
| `setSerialNumber(sn)`         | Set device serial number           |
| `getDDRSize()` / `getEMMCSize()` | Get RAM / storage size          |
| `getDDRAvailableSize()` / `getEMMCAvailableSize()` | Get available RAM/storage |
| `getMcuVersion(cb)`           | Get MCU firmware version           |
| `setLcdDensity(dpi)` / `getLcdDensity()` | LCD density (DPI)          |
| `setProperty(key, val)` / `getProperty(key, def)` | System properties   |
| `setEthMac(mac)` / `getEthMac()` | Ethernet MAC address            |
| `getLN()` / `setLN(ln)`       | License number                     |
| `getAppRam(pkg)`              | Get app memory usage               |
| `getAppCpuUsage(pkg)`         | Get app CPU usage                  |
| `setProductInfo(id, val)` / `getProductInfo(id)` | Product info fields    |

### 9. Other (`ApiOther`)

Miscellaneous utilities including SIM/modem info, shell execution, and DRM overlay.

| Method                                  | Description                       |
| --------------------------------------- | --------------------------------- |
| `getSimICCID()`                         | Get SIM card ICCID                |
| `getModemIMEI()`                        | Get modem IMEI                    |
| `execShellCommand(cmd)`                 | Execute a shell command           |
| `setCTEI(val)` / `getCTEI()`            | CTEI configuration                |
| `getEmmcLeftTime()`                     | Get eMMC remaining lifespan       |
| `overlay_drm_open(w, h, fmt, num)`      | Open DRM overlay buffer           |
| `overlay_drm_close()`                   | Close DRM overlay                 |
| `overlay_drm_set_data(bitmap)`          | Set DRM overlay bitmap data       |
| `overlay_drm_transfer_data(bufIdx, data, len)` | Transfer data to overlay    |
| `overlay_drm_show(bufIdx, zOrder)`      | Display DRM overlay               |

### 10. Common Events (`ApiCommonEvent`)

Subscribe to system-wide events from other applications (e.g., display scaler events, network events).

| Method                                                      | Description                      |
| ----------------------------------------------------------- | -------------------------------- |
| `registerCommonListener(appId, listener)`                    | Register event listener          |
| `unregisterCommonListener(appId, listener)`                  | Unregister event listener        |
| `setCommonEvent(appId, event, data, args, argsLen)`          | Send/broadcast event             |

## Key Data Types

### Enums & Constants (in `ApiTool`)

| Inner Class            | Description                                |
| ---------------------- | ------------------------------------------ |
| `ApiTool.Screen`       | Screen identifiers (MAIN, HDMI, etc.)      |
| `ApiTool.BacklightType`| Backlight control modes                    |
| `ApiTool.Can`          | CAN bus channel identifiers                |
| `ApiTool.CommonAppId`  | Application IDs for common event routing   |
| `ApiTool.CommonEvent`  | Common event type codes                    |
| `ApiTool.CommonEventConstant` | Event constant values               |
| `ApiTool.FlashRWType`  | Flash read/write storage partitions        |
| `ApiTool.Format`       | Storage format types                       |
| `ApiTool.GpioPin`      | GPIO pin definitions                       |
| `ApiTool.NetEventType` | Network event types                        |
| `ApiTool.NetworkType`  | Network interface types                    |
| `ApiTool.ScaleAppId`   | Display scaler application IDs                      |
| `ApiTool.ScaleEvent`   | Display scaler event type codes                     |
| `ApiTool.ScaleEventResult` | Display scaler event result codes               |
| `ApiTool.TimingType`   | Timer mode types                           |
| `ApiTool.USBPort`      | USB port identifiers                       |

### Parcelable Objects

| Class              | Description                        |
| ------------------ | ---------------------------------- |
| `BitmapWrapper`    | Bitmap data for AIDL transfer      |
| `CustomTiming`     | Custom display timing parameters   |
| `ApInfo`           | Wi-Fi access point information     |
| `EthSwitchInfo`    | Ethernet switch configuration      |
| `NetInfo`          | Network interface information      |
| `StaticIPInfo`     | Static IP configuration            |

### Callbacks & Listeners

| Interface             | Description                                  |
| --------------------- | -------------------------------------------- |
| `ResultCallback<T>`   | Generic async callback wrapping `ResultInfo`  |
| `ResultCallback1<T>`  | Alternative async callback variant            |
| `ResultInfo<T>`       | Result container with status, data, message   |
| `CommonListener`      | Listener for common events                    |
| `ScaleListener`       | Listener for display scaler events                     |

## Getting Started

### 1. Add the AAR to Your Project

Place `SdkApiJar_V1.260410.0350.aar` in your project's `libs/` directory and add the dependency in `build.gradle`:

```groovy
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.aar'])
}
```

### 2. Platform Signing

The SDK requires **platform-level** privileges. Sign your application with the platform keys provided in the `System Signature File` directory:

- `platform.pk8` — Platform private key
- `platform.x509.pem` — Platform certificate

Configure signing in `build.gradle`:

```groovy
android {
    signingConfigs {
        platform {
            storeFile file("path/to/platform.keystore")
            storePassword "android"
            keyAlias "platform"
            keyPassword "android"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.platform
        }
    }
}
```

### 3. Add Service Query

Add the SDK service package to your `AndroidManifest.xml` (required for Android 11+):

```xml
<queries>
    <package android:name="com.sdkapi.sdkapiservice" />
</queries>
```

### 4. Initialize the SDK

```java
import com.sdkapi.api.SdkApi;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SdkApi.newInstance(this);
    }
}
```

### 5. Use API Modules

```java
SdkApi api = SdkApi.getInstance();

// Check connection
if (api.getServiceConnectionStatus()) {
    // Control display brightness
    api.Display().setBrightness(0, 200);

    // Read a GPIO input
    int state = api.Hardware().getGpioInput(0, 1);

    // Reboot device
    api.SystemCtrl().deviceReboot();

    // Get device serial number
    String sn = api.DeviceInfo().getSerialNumber();
}
```

### 6. Cleanup

Unbind the service when done:

```java
api.unBindApiSdkService();
```

## API Patterns

### Synchronous vs. Asynchronous

- **Synchronous** methods return values directly (e.g., `getGpioInput`, `getSerialNumber`)
- **Asynchronous** methods accept a `ResultCallback<T>` parameter:

```java
api.Display().getBrightness(0, new ResultCallback<ResultInfo<Integer>>() {
    @Override
    public void onResult(ResultInfo<Integer> result) {
        if (result.isSuccess()) {
            int brightness = result.getData();
        }
    }
});
```

### Listener Registration

For event-driven APIs (ApiScale, CommonEvent), register a listener and receive callbacks:

```java
api.Scale().registerScaleListener(ApiTool.ScaleAppId.XXXX, new ScaleListener() {
    @Override
    public void onScaleData(int event, String data, int[] args) {
        // Handle display scaler event data
    }
});
```

## Version History

| Version             | Date       | Notes                          |
| ------------------- | ---------- | ------------------------------ |
| 4.1.0.260410.0350   | 2026-04-10 | Current release                |

## Requirements

- ST5-series device with `com.sdkapi.sdkapiservice` system service installed
- Android 4.4 (API 19) or higher
- Platform-signed application

## File Inventory

| File                                              | Description                         |
| ------------------------------------------------- | ----------------------------------- |
| `SdkApiJar_V1.260410.0350.aar`                    | Android Archive library             |
| `SdkApi Guidance document_V1.20260617.docx`       | API guidance document (English)     |
| `SdkApi -20260617.docx` | API guidance document (Chinese) |
| `System Signature File(signature file)/`          | Platform signing keys               |

## License

Proprietary. For use with ST5 platform devices only.


---

# ST5 Android SDK & Hardware API Reference

SdkApi Guidance document

| Current Version | V1.20260410 |
| :--- | :--- |
| Update Date | 2026-06-17 |
| Confidentiality Level | [] Internal[√] Public |
| Status | [] Draft [√] Officially released[] Under revision |

Overview
APIs supported by motherboards running Android 10 and above, along with their corresponding descriptions.
Target Audience
This document is mainly intended for the following engineers:
Software Development Engineers
Technical Support Engineers
Sales Engineers
Product Version
Supported Motherboard Models

| Motherboard Model | Supported Versions |
| :--- | :--- |
| ST5-3576 | Android14 |

Catalogue
Overview1
Target Audience1
Product Version1
API Detailed Description6
API Import Method6
API Registration Flowchart7
API Module Classification7

## 1. Initialize API8


## 1.1 Bind SdkApi8


## 1.2 Obtain the SdkApi instance8


## 1.3 Get the SDK service connection status8


## 1.4 Unbind SDK API9


## 2. Facility information API9


## 2.1 Get the API aar library version9


## 2.2 Get API SDK service version10


## 2.3 Get the motherboard system version10


## 2.4 Obtain the mainboards main chip name11


## 2.5 Get the motherboard serial number11


## 2.6 Get motherboard memory size12


## 2.7 Get motherboard storage size12


## 2.8 Set system screen density13


## 2.9 Get system screen density13


## 2.10 Get the system prop attribute14


## 2.11 Get the motherboards Ethernet MAC address14


## 2.12 Get Motherboard Available Memory Size15


## 2.13 Get Motherboard Available Storage Size15


## 2.14 Set Device Product Information15


## 2.15 Get Device Product Information16


## 3. Show relevant API17


## 3.1 Set screen orientation17


## 3.2 Get screen orientation18


## 3.3 Set screen switch18


## 3.4 Get screen switch19


## 3.5 Set screen brightness20


## 3.6 Get screen brightness20


## 3.7 Get screen resolution21


## 4. System Control API21


## 4.1 Restart the device22


## 4.2 Screenshot22


## 4.3 Hide or show the navigation bar and status bar23


## 4.4 Access the navigation bar and status bar23


## 4.5 Hide or show the status bar24


## 4.6 Get status bar status24


## 4.7 Hide or show the navigation bar25


## 4.8 Get navigation bar status25


## 4.9 General standby mode25


## 4.10 Get device standby status26


## 4.11 Low-power standby mode26


## 4.12 Low-power timed standby for devices27


## 4.13 Silent App Installation27


## 4.14 Silent uninstallation of the app28


## 4.15 Restore factory settings29


## 4.16 Set Touch Function State29


## 4.17 Get Touch Function State30


## 5. Timer API31


## 5.1 Set timer switch31


## 5.2 Get the timer switch status31


## 5.3 Set a scheduled startup time32


## 5.4 Clear scheduled startup time32


## 5.5 Set scheduled startup time33


## 5.6 Set scheduled shutdown time33


## 5.7 Clear scheduled shutdown time34


## 5.8 Set scheduled shutdown time34


## 6. Dog-related API35


## 6.1 Set the dog feeder35


## 6.2 Get dog feeder35


## 6.3 Set the watchdog timeout36


## 6.4 Feed the Dog36


## 7. System time-related API37


## 7.1 Set system time37


## 7.2 Get RTC Time37


## 7.3 Set automatic calibration time38


## 7.4 Get the auto-calibration time switch status38


## 8. Hardware-related API39


## 8.1 Get GPIO input level39


## 8.2 Configure GPIO Output Status39


## 8.3 Open serial port device 140


## 8.4 Open Serial Port 241


## 8.5 Close serial port41


## 8.6 Read serial port data42


## 8.7 Write serial port data42


## 8.8 Obtain the serial port power switch43


## 8.9 Set serial port power switch43


## 8.10 Query USB Port Number44


## 8.11 Set the USB port power switch45


## 9. Other related APIs45


## 9.1 Obtain the ICCID number of the SIM card45


## 9.2 Get IMEI46


## 9.3 Execute shell commands46


## 9.4 Read emmc lifespan47


## 10. CommonEvent-related API47


## 10.1 Register a Common listener47


## 10.2 Unregister the Common listener48


## 10.3 Set Common Execution Events49


## 10.4 ISdkCommonListener Interface Description49


## 10.5 CommonEvent Usage50


## 10.6 Interface Call Usage Example51


## 10.7 actionId command set51


## 10.7.1 WiFi function switch51


## 10.7.2 Get WiFi MAC52


## 10.7.3 Bluetooth function switch52


## 10.7.4 Ethernet Function Switch53


## 10.7.5 AP Function Switch54


## 10.7.6 Get Ethernet Status55


## 10.7.7 Set Ethernet IP55


## 10.7.8 Access Network Information56


## 10.7.9 OTA update57


## 10.7.10 Update startup animation57

API constant59
LOGO file import error code table59
Network driver Type parameter table60
GPIO Value Definition60
API Detailed Description
API Import Method
Import aar package into Android Studio
Copy the araar package [SdkApiJar.aar] to the project directory /app/libs.
Add references to build.gradle files in the app.
Register SdkApi.
Use the functions of the SdkApi-related class interface.
API Registration Flowchart
API Module Classification

| module | Class Name | meaning |
| :--- | :--- | :--- |
| facility information | ApiDeviceInfo | Device Basic Info Interface |
| display control | ApiDisplay | Display relevant control interfaces |
| System control | ApiSystemCtrl | System control interface |
| Schedule | ApiTimerOnoff | Timer interface |
| Dog Settings | ApiWatchdog | Dog survival-related interface |
| Time Settings | ApiSystemTime | System time-related interfaces |
| hardware control | ApiHardware | Hardware control interface |
| Other Settings | ApiOther | Other related interfaces |
| Scale set up | ApiScale | Touchpoint chip control interface |
| General Settings | ApiCommonEvent | General Function Interface |


## 1. Initialize API


## 1.1 Bind SdkApi

Interface name:
void newInstance(Context context)
Parameter :
--context: context
Returned value :
-- None
Code example:
SdkApi.newInstance(this);SdkApi.newInstance(this);
Remarks :
-- None

## 1.2 Obtain the SdkApi instance

Interface name:
SdkApi getInstance()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | class | SdkApi living example |

Code example:
SdkApi api = SdkApi.getInstance(); SdkApi api = SdkApi.getInstance();
Remarks :
-- None

## 1.3 Get the SDK service connection status

Interface name:
boolean  getServiceConnectionStatus()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | boolean | true: Connection successful false: Connection failed |

Code example:
boolean ret = SdkApi.getInstance().getServiceConnectionStatus(); boolean ret = SdkApi.getInstance().getServiceConnectionStatus();
Remarks :
After the--newInstance function completes, wait approximately 2 seconds for the service to connect successfully.

## 1.4 Unbind SDK API

Interface name:
void  unBindApiSdkService()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | void | - |

Code example:
SdkApi.getInstance().unBindApiSdkService(); SdkApi.getInstance().unBindApiSdkService();
Remarks :
--After unbinding, you need to rebind the SDK API
Facility information API
--Interface call method: SdkApi.getInstance(). DeviceInfo(). Interface

## 2.1 Get the API aar library version

Interface name:
String getApiVersion()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | String | aar library version |

Code example:
String version =  SdkApi.getInstance().DeviceInfo().getApiVersion(); Version: YYMMdd.hhmmInstance : 250813.0759String version =  SdkApi.getInstance().DeviceInfo().getApiVersion(); Version: YYMMdd.hhmmInstance : 250813.0759
Remarks :
-- None

## 2.2 Get API SDK service version

Interface name:
String getApiSdkServiceVersion()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | String | SDK service version |

Code example:
String version =  SdkApi.getInstance().DeviceInfo().getApiSdkServiceVersion(); Version: V2.YYMMdd.hhmm ssExample: V2.20 250813.0759 00String version =  SdkApi.getInstance().DeviceInfo().getApiSdkServiceVersion(); Version: V2.YYMMdd.hhmm ssExample: V2.20 250813.0759 00
Remarks :
-- None

## 2.3 Get the motherboard system version

Interface name:
String getFirmwareVersion()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | String | System version |

Code example:
String version =  SdkApi.getInstance().DeviceInfo().getFirmwareVersion();Example: EC3576_RK3576_Android14_A_20250813.143037String version =  SdkApi.getInstance().DeviceInfo().getFirmwareVersion();Example: EC3576_RK3576_Android14_A_20250813.143037
Remarks :
-- None

## 2.4 Obtain the mainboards main chip name

Interface name:
String getChipName()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | String | Main chip name |

Code example:
String chip = SdkApi.getInstance().DeviceInfo().getChipName()Instance : rk3576String chip = SdkApi.getInstance().DeviceInfo().getChipName()Instance : rk3576
Remarks :
-- None

## 2.5 Get the motherboard serial number

Interface name:
String getSerialNumber()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | String | Mainboard unique serial number |

Code example:
String sn = SdkApi.getInstance().DeviceInfo().getSerialNumber()Instance : 12345678String sn = SdkApi.getInstance().DeviceInfo().getSerialNumber()Instance : 12345678
Remarks :
--The data length is usually 8 or 21 bits

## 2.6 Get motherboard memory size

Interface name:
String getDDRSize()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | String | Total memory size |

Code example:
String ddrSize = SdkApi.getInstance().DeviceInfo().getDDRSize()Instance : 4GBString ddrSize = SdkApi.getInstance().DeviceInfo().getDDRSize()Instance : 4GB
Remarks :
-- None

## 2.7 Get motherboard storage size

Interface name:
String getEMMCSize()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | String | Total storage size |

Code example:
String ddrSize = SdkApi.getInstance().DeviceInfo().getEMMCSize()Instance : 32GBString ddrSize = SdkApi.getInstance().DeviceInfo().getEMMCSize()Instance : 32GB
Remarks :
-- None

## 2.8 Set system screen density

Interface name:
void setLcdDensity(int density)
Parameter :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| syntony | int | Screen density Value range: 100-480 Default: 160 |

Returned value :
-- None
Code example:
SdkApi.getInstance().DeviceInfo().setLcdDensity ( density); SdkApi.getInstance().DeviceInfo().setLcdDensity ( density);
Remarks :
Some systems need to restart to take effect

## 2.9 Get system screen density

Interface name:
int getLcdDensity()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | int | Screen density |

Code example:
int density = SdkApi.getInstance().DeviceInfo().getLcdDensity()int density = SdkApi.getInstance().DeviceInfo().getLcdDensity()
Remarks :
-- None

## 2.10 Get the system prop attribute

Interface name:
String getProperty(String key, String default)
Parameter :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| key | String | Prop key name |
| default | String | Prop key default value |

Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| parameter values | String | parameter value corresponding to the system prop attribute |

Code example:
String prop = SdkApi.getInstance().DeviceInfo().getProperty(" persist.sys.timezone" , "Asia/Shanghai" )String prop = SdkApi.getInstance().DeviceInfo().getProperty(" persist.sys.timezone" , "Asia/Shanghai" )
Remarks :
-- None

## 2.11 Get the motherboards Ethernet MAC address

Interface name:
String getEthMac()
Parameter :
-- None
Returned value :

| Parameter name | Type | Explain |
| :--- | :--- | :--- |
| Returned value | String | Ethernet MAC address, format: "0C63FC112233" |

Code example:

> *End of extracted summary — see full revision history on SUNTEK Developer Portal.*

