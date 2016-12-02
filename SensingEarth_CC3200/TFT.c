// Standard includes
#include <string.h>
#include <math.h>

// Driverlib includes
#include "hw_types.h"
#include "hw_memmap.h"
#include "hw_common_reg.h"
#include "hw_gpio.h"
#include "hw_ints.h"
#include "spi.h"
#include "rom.h"
#include "gpio.h"
#include "pin.h"
#include "rom_map.h"
#include "utils.h"
#include "prcm.h"
#include "uart.h"
#include "interrupt.h"

// Common interface includes
#include "uart_if.h"
#include "pinmux.h"

#include "TFT.h"
#include "font.h"

#define FONT_SPACE 6
#define FONT_X 8
#define FONT_Y 8

#define  TFT_DC_LOW   tft_dc_low()
#define  TFT_DC_HIGH  tft_dc_high()
#define  TFT_CS_LOW   tft_cs_low()
#define  TFT_CS_HIGH  tft_cs_high()
const unsigned int i;

unsigned int constrain(unsigned int value, unsigned int min, unsigned int max)
{
	if(value > max)
		return max;
	if(value < min)
		return min;
	return value;
}

void  tft_dc_low(void)
{
 GPIOPinWrite(GPIOA0_BASE, GPIO_PIN_6, ~GPIO_PIN_6);
}
void  tft_dc_high(void)
{
 GPIOPinWrite(GPIOA0_BASE, GPIO_PIN_6, GPIO_PIN_6);
}
void  tft_cs_low(void)
{
 GPIOPinWrite(GPIOA0_BASE, GPIO_PIN_7, ~GPIO_PIN_7);
}
void  tft_cs_high(void)
{
 GPIOPinWrite(GPIOA0_BASE, GPIO_PIN_7, GPIO_PIN_7);
}
void  SPI_begin(void)
{
  //
  // Reset the peripheral
  //
  MAP_PRCMPeripheralReset(PRCM_GSPI);
  
  SPIReset(GSPI_BASE);
  SPIConfigSetExpClk(GSPI_BASE,PRCMPeripheralClockGet(PRCM_GSPI),20000000, SPI_MODE_MASTER,SPI_SUB_MODE_0, 
  (SPI_HW_CTRL_CS | SPI_3PIN_MODE | SPI_TURBO_OFF | SPI_WL_8));
  SPIEnable(GSPI_BASE);
}

unsigned char SPI_transfer(unsigned char data)
{
    unsigned long ulDummy;
//    SPICSEnable(GSPI_BASE);
    SPIDataPut(GSPI_BASE,data);
    SPIDataGet(GSPI_BASE,&ulDummy);    
//    SPICSDisable(GSPI_BASE);
    return (unsigned char) ulDummy;
}

void sendCMD(unsigned char index)
{
    TFT_CS_LOW;
    TFT_DC_LOW;
    SPI_transfer(index);
    TFT_CS_HIGH;
}

void WRITE_DATA(unsigned char data)
{
    TFT_CS_LOW;
    TFT_DC_HIGH;
    SPI_transfer(data);
    TFT_CS_HIGH;
}

void sendData(unsigned int data)
{
    unsigned char data1 = data>>8;
    unsigned char data2 = data&0xff;
    TFT_CS_LOW;    
    TFT_DC_HIGH;
    SPI_transfer(data1);
    SPI_transfer(data2);
    TFT_CS_HIGH;
}

void WRITE_Package(unsigned int *data, unsigned char howmany)
{
    unsigned int    data1 = 0;
    unsigned char   data2 = 0;

    TFT_CS_LOW;
    TFT_DC_HIGH;   
    unsigned char count=0;
    for(count=0;count<howmany;count++)
    {
        data1 = data[count]>>8;
        data2 = data[count]&0xff;
        SPI_transfer(data1);
        SPI_transfer(data2);
    }
    TFT_CS_HIGH;
}

unsigned char Read_Register(unsigned char Addr,unsigned char xParameter)
{
    unsigned char data=0;
    sendCMD(0xd9);                                                      /* ext command                  */
    WRITE_DATA(0x10+xParameter);                                        /* 0x11 is the first Parameter  */
    TFT_DC_LOW;
    TFT_CS_LOW;
    SPI_transfer(Addr);
    TFT_DC_HIGH;
    data = SPI_transfer(0);
    TFT_CS_HIGH;
    return data;
}

unsigned char readID(void)
{
    unsigned char i=0;
    unsigned char data[3] ;
    unsigned char ID[3] = {0x00, 0x93, 0x41};
    unsigned char ToF=1;
    for(i=0;i<3;i++)
    {
        data[i]=Read_Register(0xd3,i+1);
        if(data[i] != ID[i])
        {
            ToF=0;
        }
    }
    if(!ToF)                                                            /* data!=ID                     */
    {
        Report("Read TFT ID failed, ID should be 0,147,65, but read ID = 0x");
        for(i=0;i<3;i++)
        {
            Report("%d\r\n",data[i]);
        }
    }
    return ToF;
}

void TFTinit (void)
{
	sendCMD(0xCB);  
	WRITE_DATA(0x39); 
	WRITE_DATA(0x2C); 
	WRITE_DATA(0x00); 
	WRITE_DATA(0x34); 
	WRITE_DATA(0x02); 

	sendCMD(0xCF);  
	WRITE_DATA(0x00); 
	WRITE_DATA(0XC1); 
	WRITE_DATA(0X30); 

	sendCMD(0xE8);  
	WRITE_DATA(0x85); 
	WRITE_DATA(0x00); 
	WRITE_DATA(0x78); 

	sendCMD(0xEA);  
	WRITE_DATA(0x00); 
	WRITE_DATA(0x00); 

	sendCMD(0xED);  
	WRITE_DATA(0x64); 
	WRITE_DATA(0x03); 
	WRITE_DATA(0X12); 
	WRITE_DATA(0X81); 

	sendCMD(0xF7);  
	WRITE_DATA(0x20); 

	sendCMD(0xC0);    	//Power control 
	WRITE_DATA(0x23);   	//VRH[5:0] 

	sendCMD(0xC1);    	//Power control 
	WRITE_DATA(0x10);   	//SAP[2:0];BT[3:0] 

	sendCMD(0xC5);    	//VCM control 
	WRITE_DATA(0x3e);   	//Contrast
	WRITE_DATA(0x28); 

	sendCMD(0xC7);    	//VCM control2 
	WRITE_DATA(0x86);  	 //--

	sendCMD(0x36);    	// Memory Access Control 
	WRITE_DATA(0x48);  	//C8	   //48 68ÊúÆÁ//28 E8 ºáÆÁ

	sendCMD(0x3A);    
	WRITE_DATA(0x55); 

	sendCMD(0xB1);    
	WRITE_DATA(0x00);  
	WRITE_DATA(0x18); 

	sendCMD(0xB6);    	// Display Function Control 
	WRITE_DATA(0x08); 
	WRITE_DATA(0x82);
	WRITE_DATA(0x27);  
 
	sendCMD(0xF2);    	// 3Gamma Function Disable 
	WRITE_DATA(0x00); 

	sendCMD(0x26);    	//Gamma curve selected 
	WRITE_DATA(0x01); 

	sendCMD(0xE0);    	//Set Gamma 
	WRITE_DATA(0x0F); 
	WRITE_DATA(0x31); 
	WRITE_DATA(0x2B); 
	WRITE_DATA(0x0C); 
	WRITE_DATA(0x0E); 
	WRITE_DATA(0x08); 
	WRITE_DATA(0x4E); 
	WRITE_DATA(0xF1); 
	WRITE_DATA(0x37); 
	WRITE_DATA(0x07); 
	WRITE_DATA(0x10); 
	WRITE_DATA(0x03); 
	WRITE_DATA(0x0E); 
	WRITE_DATA(0x09); 
	WRITE_DATA(0x00); 

	sendCMD(0XE1);    	//Set Gamma 
	WRITE_DATA(0x00); 
	WRITE_DATA(0x0E); 
	WRITE_DATA(0x14); 
	WRITE_DATA(0x03); 
	WRITE_DATA(0x11); 
	WRITE_DATA(0x07); 
	WRITE_DATA(0x31); 
	WRITE_DATA(0xC1); 
	WRITE_DATA(0x48); 
	WRITE_DATA(0x08); 
	WRITE_DATA(0x0F); 
	WRITE_DATA(0x0C); 
	WRITE_DATA(0x31); 
	WRITE_DATA(0x36); 
	WRITE_DATA(0x0F); 

	sendCMD(0x11);    	//Exit Sleep 
	MAP_UtilsDelay(3200000);//delay(120); 120ms

	sendCMD(0x29);    //Display on 
	sendCMD(0x2c);   
	fillScreen_a();
}

void setCol(unsigned int StartCol,unsigned int EndCol)
{
    sendCMD(0x2A);                                                      /* Column Command address       */
    sendData(StartCol);
    sendData(EndCol);
}

void setPage(unsigned int StartPage,unsigned int EndPage)
{
    sendCMD(0x2B);                                                      /* Column Command address       */
    sendData(StartPage);
    sendData(EndPage);
}

void fillScreen_b(unsigned int XL, unsigned int XR, unsigned int YU, unsigned int YD, unsigned int color)
{
    unsigned long  XY=0;
    unsigned long i=0;

    if(XL > XR)
    {
        XL = XL^XR;
        XR = XL^XR;
        XL = XL^XR;
    }
    if(YU > YD)
    {
        YU = YU^YD;
        YD = YU^YD;
        YU = YU^YD;
    }
    XL = constrain(XL, MIN_X,MAX_X);
    XR = constrain(XR, MIN_X,MAX_X);
    YU = constrain(YU, MIN_Y,MAX_Y);
    YD = constrain(YD, MIN_Y,MAX_Y);

    XY = (XR-XL+1);
    XY = XY*(YD-YU+1);

    setCol(XL,XR);
    setPage(YU, YD);
    sendCMD(0x2c);                                                  /* start to write to display ra */
                                                                        /* m                            */

    TFT_DC_HIGH;
    TFT_CS_LOW;

    unsigned char Hcolor = color>>8;
    unsigned char Lcolor = color&0xff;
    for(i=0; i < XY; i++)
    {
        SPI_transfer(Hcolor);
        SPI_transfer(Lcolor);
    }

    TFT_CS_HIGH;
}

void fillScreen_a(void)
{
    setCol(0, 239);
    setPage(0, 319);
    sendCMD(0x2c);                                                  /* start to write to display ra */
                                                                        /* m                            */

    TFT_DC_HIGH;
    TFT_CS_LOW;
    int i____;
    for(i____=0; i____<38400; i____++)
    {
        SPI_transfer(0);
        SPI_transfer(0);
        SPI_transfer(0);
        SPI_transfer(0);
    }
    TFT_CS_HIGH;
}


void setXY(unsigned int poX, unsigned int poY)
{
    setCol(poX, poX);
    setPage(poY, poY);
    sendCMD(0x2c);
}

void setPixel(unsigned int poX, unsigned int poY,unsigned int color)
{
    setXY(poX, poY);
    sendData(color);
}

void drawChar( unsigned char ascii, unsigned int poX, unsigned int poY,unsigned int size, unsigned int fgcolor)
{
    if((ascii>=32)&&(ascii<=127))
    {
        ;
    }
    else
    {
        ascii = '?'-32;
    }
    int ii;
    for (ii =0; ii<FONT_X; ii++ ) {
        unsigned char temp = (simpleFont[ascii-0x20][ii]);
        int ff;
        for(ff=0;ff<8;ff++)
        {
            if((temp>>ff)&0x01)
            {
                fillRectangle(poX+ii*size, poY+ff*size, size, size, fgcolor);
            }

        }

    }
}

void drawString(char *string,unsigned int poX, unsigned int poY, unsigned int size,unsigned int fgcolor)
{
    while(*string)
    {
        drawChar(*string, poX, poY, size, fgcolor);
        *string++;

        if(poX < MAX_X)
        {
            poX += FONT_SPACE*size;                                     /* Move cursor right            */
        }
    }
}

//fillRectangle(poX+i*size, poY+f*size, size, size, fgcolor);
void fillRectangle(unsigned int poX, unsigned int poY, unsigned int length, unsigned int width, unsigned int color)
{
    fillScreen_b(poX, poX+length, poY, poY+width, color);
}

void  drawHorizontalLine( unsigned int poX, unsigned int poY,
unsigned int length,unsigned int color)
{
    setCol(poX,poX + length);
    setPage(poY,poY);
    sendCMD(0x2c);
    int iii;
    for(iii=0; iii<length; iii++)
    sendData(color);
}

void drawLine( unsigned int x0,unsigned int y0,unsigned int x1, unsigned int y1,unsigned int color)
{

    int x = x1-x0;
    int y = y1-y0;
    int dx = abs(x), sx = x0<x1 ? 1 : -1;
    int dy = -abs(y), sy = y0<y1 ? 1 : -1;
    int err = dx+dy, e2;                                                /* error value e_xy             */
    for (;;){                                                           /* loop                         */
        setPixel(x0,y0,color);
        e2 = 2*err;
        if (e2 >= dy) {                                                 /* e_xy+e_x > 0                 */
            if (x0 == x1) break;
            err += dy; x0 += sx;
        }
        if (e2 <= dx) {                                                 /* e_xy+e_y < 0                 */
            if (y0 == y1) break;
            err += dx; y0 += sy;
        }
    }

}

void drawVerticalLine( unsigned int poX, unsigned int poY, unsigned int length,unsigned int color)
{
    setCol(poX,poX);
    setPage(poY,poY+length);
    sendCMD(0x2c);
    int i_;
    for(i_=0; i_<length; i_++)
    sendData(color);
}

void drawRectangle(unsigned int poX, unsigned int poY, unsigned int length, unsigned int width,unsigned int color)
{
    drawHorizontalLine(poX, poY, length, color);
    drawHorizontalLine(poX, poY+width, length, color);
    drawVerticalLine(poX, poY, width,color);
    drawVerticalLine(poX + length, poY, width,color);

}

void drawCircle(int poX, int poY, int r,unsigned int color)
{
    int x = -r, y = 0, err = 2-2*r, e2;
    do {
        setPixel(poX-x, poY+y,color);
        setPixel(poX+x, poY+y,color);
        setPixel(poX+x, poY-y,color);
        setPixel(poX-x, poY-y,color);
        e2 = err;
        if (e2 <= y) {
            err += ++y*2+1;
            if (-x == y && e2 <= x) e2 = 0;
        }
        if (e2 > x) err += ++x*2+1;
    } while (x <= 0);
}

void fillCircle(int poX, int poY, int r,unsigned int color)
{
    int x = -r, y = 0, err = 2-2*r, e2;
    do {

        drawVerticalLine(poX-x, poY-y, 2*y, color);
        drawVerticalLine(poX+x, poY-y, 2*y, color);

        e2 = err;
        if (e2 <= y) {
            err += ++y*2+1;
            if (-x == y && e2 <= x) e2 = 0;
        }
        if (e2 > x) err += ++x*2+1;
    } while (x <= 0);

}

void drawTraingle( int poX1, int poY1, int poX2, int poY2, int poX3, int poY3, unsigned int color)
{
    drawLine(poX1, poY1, poX2, poY2,color);
    drawLine(poX1, poY1, poX3, poY3,color);
    drawLine(poX2, poY2, poX3, poY3,color);
}

unsigned char drawNumber(long long_num,unsigned int poX, unsigned int poY,unsigned int size,unsigned int fgcolor)
{
    unsigned char char_buffer[10] = "";
    unsigned char i = 0;
    unsigned char f = 0;

    if (long_num < 0)
    {
        f=1;
        drawChar('-',poX, poY, size, fgcolor);
        long_num = -long_num;
        if(poX < MAX_X)
        {
            poX += FONT_SPACE*size;                                     /* Move cursor right            */
        }
    }
    else if (long_num == 0)
    {
        f=1;
        drawChar('0',poX, poY, size, fgcolor);
        return f;
        if(poX < MAX_X)
        {
            poX += FONT_SPACE*size;                                     /* Move cursor right            */
        }
    }


    while (long_num > 0)
    {
        char_buffer[i++] = long_num % 10;
        long_num /= 10;
    }

    f = f+i;
    for(; i > 0; i--)
    {
        drawChar('0'+ char_buffer[i - 1],poX, poY, size, fgcolor);
        if(poX < MAX_X)
        {
            poX+=FONT_SPACE*size;                                       /* Move cursor right            */
        }
    }
    return f;
}

unsigned char drawFloat_b(float floatNumber,unsigned char decimal,unsigned int poX, unsigned int poY,unsigned int size,unsigned int fgcolor)
{
    unsigned int temp=0;
    float decy=0.0;
    float rounding = 0.5;
    unsigned char f=0;
    if(floatNumber<0.0)
    {
        drawChar('-',poX, poY, size, fgcolor);
        floatNumber = -floatNumber;
        if(poX < MAX_X)
        {
            poX+=FONT_SPACE*size;                                       /* Move cursor right            */
        }
        f =1;
    }
    int ii_;
    for (ii_=0; ii_<decimal; ++ii_)
    {
        rounding /= 10.0;
    }
    floatNumber += rounding;

    temp = (unsigned int)floatNumber;
    unsigned char howlong=drawNumber(temp,poX, poY, size, fgcolor);
    f += howlong;
    if((poX+8*size*howlong) < MAX_X)
    {
        poX+=FONT_SPACE*size*howlong;                                   /* Move cursor right            */
    }

    if(decimal>0)
    {
        drawChar('.',poX, poY, size, fgcolor);
        if(poX < MAX_X)
        {
            poX+=FONT_SPACE*size;                                       /* Move cursor right            */
        }
        f +=1;
    }
    decy = floatNumber-temp;
    int ii___;
	/* decimal part,  4             */
    for(ii___=0;ii___<decimal;ii___++)
    {
        decy *=10;                                                      /* for the next decimal         */
        temp = decy;                                                    /* get the decimal              */
        drawNumber(temp,poX, poY, size, fgcolor);
        floatNumber = -floatNumber;
        if(poX < MAX_X)
        {
            poX+=FONT_SPACE*size;                                       /* Move cursor right            */
        }
        decy -= temp;
    }
    f +=decimal;
    return f;
}

unsigned char drawFloat_a(float floatNumber,unsigned int poX, unsigned int poY,unsigned int size,unsigned int fgcolor)
{
    unsigned char decimal=2;
    unsigned int temp=0;
    float decy=0.0;
    float rounding = 0.5;
    unsigned char f=0;
    if(floatNumber<0.0)                                                 /* floatNumber < 0              */
    {
        drawChar('-',poX, poY, size, fgcolor);                          /* add a '-'                    */
        floatNumber = -floatNumber;
        if(poX < MAX_X)
        {
            poX+=FONT_SPACE*size;                                       /* Move cursor right            */
        }
        f =1;
    }
    int i___;
	for(i___=0; i___<decimal; ++i___)
    {
        rounding /= 10.0;
    }
    floatNumber += rounding;

    temp = (unsigned int)floatNumber;
    unsigned char howlong=drawNumber(temp,poX, poY, size, fgcolor);
    f += howlong;
    if((poX+8*size*howlong) < MAX_X)
    {
        poX+=FONT_SPACE*size*howlong;                                   /* Move cursor right            */
    }


    if(decimal>0)
    {
        drawChar('.',poX, poY, size, fgcolor);
        if(poX < MAX_X)
        {
            poX += FONT_SPACE*size;                                     /* Move cursor right            */
        }
        f +=1;
    }
    decy = floatNumber-temp;
    int i_i;
    /* decimal part,                */
    for(i_i=0;i_i<decimal;i_i++)
    {
        decy *=10;                                                      /* for the next decimal         */
        temp = decy;                                                    /* get the decimal              */
        drawNumber(temp,poX, poY, size, fgcolor);
        floatNumber = -floatNumber;
        if(poX < MAX_X)
        {
            poX += FONT_SPACE*size;                                     /* Move cursor right            */
        }
        decy -= temp;
    }
    f += decimal;
    return f;
}



//void draw_font(void )
//{
//#ifdef USE_FONT_CHIP
//    uint8_t font_width, font_height;
//    uint8_t font_buffer[MAX_FONT_BUFFER_LEN];
//#endif // USE_FONT_CHIP
//    
//    uint16_t msg_len;
//    uint8_t i_msg;
//
//    va_list vl;
//    va_start(vl, format);
//    msg_len = vsprintf(msg_buffer, format, vl);
//
//    if (msg_len >= MAX_MSG_LEN)
//        while (1)
//            ;
//
//    va_end(vl);
//
//    uint8_t cursor_x = x % 0xFF, cursor_y = y;
//
//    msg_len = strlen(msg_buffer);
//
//    if (x == 0xFF)
//    {
//        UC1701InverseEnable();
//        // draw title background
//        UC1701CharDispaly(cursor_y, cursor_x, "                ");
//    }
//    for (i_msg = 0; i_msg < msg_len; i_msg ++)
//    {
//        // test whether current char is Hanzi
//        if ((msg_buffer[i_msg] & 0x80) == 0x80)
//        {
//#ifdef USE_FONT_CHIP
//            if (is_fontchip_ok)
//            { // fetch from font chip
//                GT20Read(GB2312_15x16, *(uint16_t *)&msg_buffer[i_msg], &font_width, &font_height, (uint16_t *)font_buffer);
//                
//                // paint to screen
//                UC1701FontDisplay(cursor_y, cursor_x, font_width, font_height, font_buffer);
//            }
//
//            cursor_x += 2;
//            i_msg ++;
//        }
//        else
//        {
//            UC1701Display(cursor_y, cursor_x, msg_buffer[i_msg]);
//            cursor_x ++;
//        }
//    }
//#endif // USE_FONT_CHIP       
//    
//
//
//
//
//
//
// 
//}


void drawSlogan(int symbol)
{
	int symbolcolor[3]={GREEN,RED};
	char slogan[3][100]={"QUIET","NOSIY"};
	fillScreen_b(25,200,180,270,BLACK);
	drawString("****************************",35,180,1,symbolcolor[symbol]);
	int w;
    for(w=181;w<=270;w+=8)
	{
	    drawString("*",35,w,1,symbolcolor[symbol]);
	    drawString("*",200,w,1,symbolcolor[symbol]);
	}
	drawString("****************************",35,270,1,symbolcolor[symbol]);
	drawString(slogan[symbol],60,213,4,symbolcolor[symbol]);
}

/*********************************************************************************************************
  END FILE
*********************************************************************************************************/
