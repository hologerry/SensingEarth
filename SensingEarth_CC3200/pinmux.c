//*****************************************************************************
//
// Copyright (C) 2014 Texas Instruments Incorporated - http://www.ti.com/ 
// 
// 
//  Redistribution and use in source and binary forms, with or without 
//  modification, are permitted provided that the following conditions 
//  are met:
//
//    Redistributions of source code must retain the above copyright 
//    notice, this list of conditions and the following disclaimer.
//
//    Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the 
//    documentation and/or other materials provided with the   
//    distribution.
//
//    Neither the name of Texas Instruments Incorporated nor the names of
//    its contributors may be used to endorse or promote products derived
//    from this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
//  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
//  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
//  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
//  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
//  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
//  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
//  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
//  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
//  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//*****************************************************************************

// This file was automatically generated on 7/21/2014 at 3:06:20 PM
// by TI PinMux version 3.0.334
//
//*****************************************************************************

#include "pinmux.h"
#include "hw_types.h"
#include "hw_memmap.h"
#include "hw_gpio.h"
#include "pin.h"
#include "rom.h"
#include "rom_map.h"
#include "gpio.h"
#include "prcm.h"

//*****************************************************************************
void
PinMuxConfig(void)
{
    //UART
    MAP_PRCMPeripheralClkEnable(PRCM_UARTA0, PRCM_RUN_MODE_CLK);

    // Configure PIN_55 for UART0 UART0_TX
    MAP_PinTypeUART(PIN_55, PIN_MODE_3);

    //
    // Configure PIN_57 for UART0 UART0_RX
    //
    MAP_PinTypeUART(PIN_57, PIN_MODE_3);

    //temperature

    MAP_PRCMPeripheralClkEnable(PRCM_GPIOA0, PRCM_RUN_MODE_CLK);
    MAP_PRCMPeripheralClkEnable(PRCM_GPIOA1, PRCM_RUN_MODE_CLK);
    // Configure PIN_58 for GPIOInput
    //
    MAP_PinTypeGPIO(PIN_58, PIN_MODE_0, false);
    MAP_GPIODirModeSet(GPIOA0_BASE, 0x8, GPIO_DIR_MODE_IN);

    //
    // Configure PIN_64 for GPIOOutput
    //
    MAP_PinTypeGPIO(PIN_64, PIN_MODE_0, false);
    MAP_GPIODirModeSet(GPIOA1_BASE, 0x2, GPIO_DIR_MODE_OUT);



    //light

    MAP_PRCMPeripheralClkEnable(PRCM_I2CA0, PRCM_RUN_MODE_CLK);
    // Configure PIN_01 for I2C0 I2C_SCL
    //
    MAP_PinTypeI2C(PIN_01, PIN_MODE_1);

    // Configure PIN_02 for I2C0 I2C_SDA
    //
    MAP_PinTypeI2C(PIN_02, PIN_MODE_1);


//voice

    MAP_PRCMPeripheralClkEnable(PRCM_GPIOA3, PRCM_RUN_MODE_CLK);

    //
    // Configure PIN_45 for GPIO Output====FONT_CS
    //
    PinTypeGPIO(PIN_45, PIN_MODE_0, false);
    GPIODirModeSet(GPIOA3_BASE, 0x80, GPIO_DIR_MODE_OUT);




//LCD

    MAP_PRCMPeripheralClkEnable(PRCM_GSPI, PRCM_RUN_MODE_CLK);

        // Configure PIN_05 for SPI0 GSPI_CLK
        //
        MAP_PinTypeSPI(PIN_05, PIN_MODE_7);

        //
        // Configure PIN_06 for SPI0 GSPI_MISO
        //
        MAP_PinTypeSPI(PIN_06, PIN_MODE_7);

        //
        // Configure PIN_07 for SPI0 GSPI_MOSI
        //
        MAP_PinTypeSPI(PIN_07, PIN_MODE_7);

        //
        // Enable Peripheral Clocks
        //
        PRCMPeripheralClkEnable(PRCM_GPIOA0, PRCM_RUN_MODE_CLK);

        //
        // Configure PIN_61 for GPIO Output====CD
        //
        PinTypeGPIO(PIN_61, PIN_MODE_0, false);
        GPIODirModeSet(GPIOA0_BASE, 0x40, GPIO_DIR_MODE_OUT);

        //
        // Configure PIN_62 for GPIO Output====CS
        //
        PinTypeGPIO(PIN_62, PIN_MODE_0, false);
        GPIODirModeSet(GPIOA0_BASE, 0x80, GPIO_DIR_MODE_OUT);


//LED

        PRCMPeripheralClkEnable(PRCM_GPIOA2, PRCM_RUN_MODE_CLK);

        PinTypeGPIO(PIN_18, PIN_MODE_0, false);
        GPIODirModeSet(GPIOA3_BASE, 0x10, GPIO_DIR_MODE_OUT);

        //P29--GPIO26引脚的初始化要操作对应的寄存器GPIO_PAD_CONFIG_26(0x4402E108)
        GPIODirModeSet(GPIOA3_BASE,GPIO_PIN_2,GPIO_DIR_MODE_OUT);
        //PinModeSet
        HWREG(0x4402E108) = (((HWREG(0x4402E108) & ~0x0000000F) |
                                                            0x00000000) & ~(3<<10));
        //PinConfigSet
        HWREG(0x4402E108) = ((HWREG(0x4402E108) & ~0xC00) | 0x00000800);

        //P30--GPIO27引脚的初始化要操作对应的寄存器GPIO_PAD_CONFIG_27(0x4402E10C)
        GPIODirModeSet(GPIOA3_BASE,GPIO_PIN_3,GPIO_DIR_MODE_OUT);
        //PinModeSet
        HWREG(0x4402E10C) = (((HWREG(0x4402E10C) & ~0x0000000F) |
                                                            0x00000000) & ~(3<<10));
        //PinConfigSet
        HWREG(0x4402E10C) = ((HWREG(0x4402E10C) & ~0xC00) | 0x00000800);
}
