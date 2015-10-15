PWM Sample Application
======================

This example demonstrates the usage of the PWM API. User is allowed to configure
and control all the available PWM channels in the device.

Demo requirements
-----------------

To run this example you need:

* One compatible device to host the application.
* A USB connection between the device and the host PC in order to transfer and
  launch the application.

Demo setup
----------

Make sure the hardware is set up correctly:

1. The device is powered on.
2. The device is connected directly to the PC by the micro USB cable.

Demo run
--------

The example is already configured, so all you need to do is to build and launch 
the project.

Once application starts, choose a PWM Channel and configure all its PWM
parameters:

* **PWM Channel**: PWM channel to configure and control.
* **Enable**: Enables or disables the selected PWM channel.
* **Frequency (Hz)**: Sets the PWM channel frequency in hertz.
* **Duty Cycle (%)**: Sets the PWM channel duty cycle as percentage (0-100).
* **Polarity**: Configures the selected PWM channel polarity between _Normal_
  and _Inverted_.

**\*\*Note**: Kernel 3.0 does not allow to modify some of the PWM channel
parameters, so they may appear as disabled in the application.

In the SBC board v1, PWM channels are mapped as follows:

* PWM1 signal is connected on the SBC to pin 13 of connector J18 (MIPI DSI) to
  control the backlight contrast of the MIPI DSI display.

In the SBC board v2, PWM channels are mapped as follows:

* PWM2 signal is connected on the SBC to pin 13 of connector J16 (MIPI DSI) to
  control the backlight contrast of the MIPI DSI display.

**\*\*Note**: Pull-up resistors may be required in order to measure PWM signals
from above pins.

Compatible with
---------------

* ConnectCore 6 SBC
* ConnectCore 6 SBC v2

License
-------

This software is open-source software. Copyright Digi International, 2014-2015.

This Source Code Form is subject to the terms of the Mozilla Public License,
v. 2.0. If a copy of the MPL was not distributed with this file, you can obtain
one at http://mozilla.org/MPL/2.0/.