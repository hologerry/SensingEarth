/*
 2012 Copyright (c) Seeed Technology Inc.
 Authors: Albert.Miao & Loovee, 
 Visweswara R (with initializtion code from TFT vendor)
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
#ifndef TFT_h
#define TFT_h

// Standard includes
#include <string.h>

// Driverlib includes
#include "hw_types.h"
#include "hw_memmap.h"
#include "hw_common_reg.h"
#include "hw_ints.h"
#include "spi.h"
#include "rom.h"
#include "rom_map.h"
#include "utils.h"
#include "prcm.h"
#include "uart.h"
#include "interrupt.h"

// Common interface includes
#include "uart_if.h"
#include "pinmux.h"



//Basic Colors
#define RED		0xf800
#define GREEN	0x07e0
#define BLUE	0x001f
#define BLACK	0x0000
#define YELLOW	0xffe0
#define WHITE	0xffff

//Other Colors
#define CYAN		0x07ff	
#define BRIGHT_RED	0xf810	
#define GRAY1		0x8410  
#define GRAY2		0x4208  

//TFT resolution 240*320
#define MIN_X	0
#define MIN_Y	0
#define MAX_X	239
#define MAX_Y	319


#define TS_MINX 116*2
#define TS_MAXX 890*2
#define TS_MINY 83*2
#define TS_MAXY 913*2


//extern unsigned char simpleFont[][8];


        void  tft_dc_low(void);
        void  tft_dc_high(void);
        void  tft_cs_low(void);
        void  tft_cs_high(void);
        void  SPI_begin(void);
        unsigned char SPI_transfer(unsigned char data);
        
	void TFTinit (void);
	void setCol(unsigned int StartCol,unsigned int EndCol);
	void setPage(unsigned int StartPage,unsigned int EndPage);
	void setXY(unsigned int poX, unsigned int poY);
	void setPixel(unsigned int poX, unsigned int poY,unsigned int color);
	void sendCMD(unsigned char index);
	void WRITE_Package(unsigned int *data,unsigned char howmany);
	void WRITE_DATA(unsigned char data);
	void sendData(unsigned int data);
	unsigned char Read_Register(unsigned char Addr,unsigned char xParameter);
	void fillScreen_b(unsigned int XL,unsigned int XR,unsigned int YU,unsigned int YD,unsigned int color);
	void fillScreen_a(void);
	unsigned char readID(void);

	void drawChar(unsigned char ascii,unsigned int poX, unsigned int poY,unsigned int size, unsigned int fgcolor);
	void drawString(char *string,unsigned int poX, unsigned int poY,unsigned int size,unsigned int fgcolor);
	void fillRectangle(unsigned int poX, unsigned int poY, unsigned int length, unsigned int width, unsigned int color);

	void drawLine(unsigned int x0,unsigned int y0,unsigned int x1,unsigned int y1,unsigned int color);
	void drawVerticalLine(unsigned int poX, unsigned int poY,unsigned int length,unsigned int color);
	void drawHorizontalLine(unsigned int poX, unsigned int poY,unsigned int length,unsigned int color);
	void drawRectangle(unsigned int poX, unsigned int poY, unsigned int length,unsigned int width,unsigned int color);

	void drawCircle(int poX, int poY, int r,unsigned int color);
	void fillCircle(int poX, int poY, int r,unsigned int color);

	void drawTraingle(int poX1, int poY1, int poX2, int poY2, int poX3, int poY3, unsigned int color);
	unsigned char drawNumber(long long_num,unsigned int poX, unsigned int poY,unsigned int size,unsigned int fgcolor);
	unsigned char drawFloat_b(float floatNumber,unsigned char decimal,unsigned int poX, unsigned int poY,unsigned int size,unsigned int fgcolor);
	unsigned char drawFloat_a(float floatNumber,unsigned int poX, unsigned int poY,unsigned int size,unsigned int fgcolor);


#endif

/*********************************************************************************************************
  END FILE
*********************************************************************************************************/
