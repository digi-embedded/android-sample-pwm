PWM Sample Application
======================

This example demonstrates the usage of the PWM API. User is allowed to 
configure and control all the available PWM channels in the device.

Demo requeriments
-----------------

To run this example you will need:
    - One compatible device to host the application.
    - Network connection between the device and the host PC in order to
      transfer and launch the application.
    - Establish remote target connection to your Digi hardware before running
      this application.

Demo setup
----------

 Make sure the hardware is set up correctly:
    - The device is powered on.
    - The device is connected directly to the PC or to the Local
      Area Network (LAN) by the Ethernet cable.

Demo run
--------

The example is already configured, so all you need to do is to build and launch 
the project.

Once application starts, you will be able to choose a PWM Channel and configure 
all the PWM parameters:
    - PWM Channel: Select the desired PWM channel to configure and control.
    - Enable: Enable or disable the selected PWM channel.
    - Frequency (Hz): Set the PWM channel frequency in hertz.
    - Duty Cycle (%): Set the PWM channel duty cycle as percentage (0-100).
    - Polarity: Configure the selected PWM channel polarity between Normal and 
	  Inversed.

(Note: Kernel 3.0 does not allow to modify some of the PWM channel parameters, 
so they may appear as disabled in the application)

In the SBC board v1, PWM channels are mapped as follows:
    - PWM1 signal is connected on the SBC to pin 13 of connector J18 (MIPI DSI) 
	  to control the backlight contrast of the MIPI DSI display.
In the SBC board v2, PWM channels are mapped as follows:
    - PWM2 signal is connected on the SBC to pin 13 of connector J16 (MIPI DSI) 
	  to control the backlight contrast of the MIPI DSI display.

(Note: Pull-up resistors may be required in order to measure PWM signals from 
above pins)

Tested on
---------

ConnectCard for i.MX28
ConnectCore 6 Adapter Board
ConnectCore 6 SBC
ConnectCore 6 SBC v2