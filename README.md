PWM Sample Application
======================

This example demonstrates the usage of the PWM API. User is allowed to
configure and control all the available PWM channels in the device.

Demo requirements
-----------------

To run this example you need:

* A compatible development board to host the application.
* A USB connection between the board and the host PC in order to transfer and
  launch the application.

Demo setup
----------

Make sure the hardware is set up correctly:

1. The development board is powered on.
2. The board is connected directly to the PC by the micro USB cable.

Demo run
--------

The example is already configured, so all you need to do is to build and launch
the project.

Once application starts, choose a PWM Channel and configure its parameters:

* **PWM Channel**: PWM channel to configure and control.
* **Duty Cycle (%)**: Sets the PWM channel duty cycle as percentage (0-100).

Depending on the platform:
* On the ConnectCore 6 SBC board v1, PWM channels are mapped as follows:
  * PWM1 signal is connected on the SBC to pin 13 of connector J18 (MIPI DSI) to
    control the backlight contrast of the MIPI DSI display.

* On the ConnectCore 8X SBC Pro the following MCA PWM channels are available:
  * MCA PWM0 (pwmchip0) channel 4 (MCA_IO17) is available on J20-16.
  * MCA PWM2 (pwmchip8) channels 0 (MCA_IO7) and 1 (MCA_IO8) are available on
    J20-6 and J20-7.

* On the ConnectCore 8M Mini Development Kit:
  * PWM2 (pwmchip6) channel 0 (GPIO1_IO13) is available on J46-7.
  * MCA PWM0 (pwmchip0):
    * Channel 0 (SWD_DIO) is available on J22-2.
    * Channel 1 is connected to USER_LED1.
    * Channel 2 (XBEE1_RESET_N.) is available on J39-5.
    * Channel 3 (XBEE1_ON/SLEEP_N) is available on J40-8.
    * Channel 4 (LVDS_PWM_OUT) is available on J32-16.
    * Channel 5 is connected to USER_LED2.
  * PWM1 is available at EXP_I2C_SDA on the J48 expansion connector.
    PWM1 is disabled by default due to conflicts with I2C4.
  * PWM3 is available on J46-9 (GPIO1_IO14)
    PWM3 is disabled by default due to conflicts with USB-OTG2 power enable.
  * PWM4 is available on J46-4 (GPIO1_IO15)
    PWM4 is disabled by default due to conflicts with USB-OTG2 overcurrent detect.

Compatible with
---------------

* ConnectCore 6 SBC
* ConnectCore 8X SBC Pro
* ConnectCore 8M Mini Development Kit

License
-------

Copyright (c) 2014-2025, Digi International Inc. <support@digi.com>

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
