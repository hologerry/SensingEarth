#include "i2c_if.h"
#include "rom_map.h"
#include "light.h"


void I2C_writedata(unsigned char command,unsigned char data)
{
  unsigned char TxData[2]={0,0};
  TxData[0]=command;
  TxData[1]=data;
  I2C_IF_Write(APDS_Addr,TxData,2,1);
}

//*****************************************************************************
// clear the interrupt
//*****************************************************************************
void I2C_writecom(unsigned char command)
{
  unsigned char *p;
  p=&command;
  I2C_IF_Write(APDS_Addr,p,1,1);
}

//*****************************************************************************
// write data--word
//*****************************************************************************
void I2C_writeword(unsigned char command,unsigned int value)
{
  unsigned char data[3];
  data[0] = command;
  data[2] = value >> 8;
  data[1] = value & 0xFF;
  I2C_IF_Write(APDS_Addr,data,3,1);
}

//*****************************************************************************
// read data--byte
//*****************************************************************************
unsigned char I2C_readdata(unsigned char command)
{
  unsigned char *p1;
  p1=&command;
  I2C_IF_Write(APDS_Addr,p1,1,0);
  unsigned char data;
  I2C_IF_Read(APDS_Addr,&data,1);
  return data;
}

//*****************************************************************************
// read data--word
//*****************************************************************************
unsigned int I2C_readword(unsigned char command)
{
  unsigned char *p2;
  p2=&command;
  I2C_IF_Write(APDS_Addr,p2,1,0);
  unsigned char RxData[2]={0,0};
  I2C_IF_Read(APDS_Addr,RxData,2);
  return RxData[1] << 8 | RxData[0];
}


void apds_init(void)
{
   unsigned char ATIME;
   unsigned char PTIME;
   unsigned char WTIME;
   unsigned char PPULSE;

    ATIME = 0xff; // 2.7 ms - minimum ALS integration time
    WTIME = 0xff; // 2.7 ms - minimum Wait time
    PTIME = 0xff; // 2.7 ms - minimum Prox integration time
    PPULSE = 8; // Minimum prox pulse count

    I2C_writedata(0x80|0x00,0);//Disable and Powerdown
    I2C_writedata(0x80|0x01,ATIME);
    I2C_writedata(0x80|0x02,PTIME);
    I2C_writedata(0x80|0x03,WTIME);
    I2C_writedata(0x80|0x0d,0);
    I2C_writedata(0x80|0x0e,PPULSE);

   unsigned char PDRIVE;
   unsigned char PDIODE;
   unsigned char PGAIN;
   unsigned char AGAIN;

    PDRIVE = 0; //100mA of LED Power
    PDIODE = 0x20; // CH1 Diode
    PGAIN = 0; //1x Prox gain
    AGAIN = 0; //1x ALS gain

    I2C_writedata(0x80|0x0f,PDRIVE | PDIODE | PGAIN | AGAIN);



  unsigned char WEN, PEN, AEN, PON;
  WEN = 8; // Enable Wait
  PEN = 4; // Enable Prox
  AEN = 2; // Enable ALS
  PON = 1; // Enable Power On

  I2C_writedata(0x80|0x00,WEN | PEN | AEN | PON);

  MAP_UtilsDelay(400000); //Wait for 12 ms

}

unsigned int apds_readProximity(void)
{
  unsigned int data;
  data=I2C_readword(0xa0|0x18);
  return data;
}
unsigned int apds_readALS(unsigned char ch)
{
  unsigned int data;
  data=I2C_readword(0xa0|ch);
  return data;
}


unsigned int CollectLight( void *pvParameters )
{
  unsigned int t1,t2,t3;
  //    unsigned int   proximity_data=0;
  t1=I2C_readdata(0x80|0x18);
  t2=I2C_readdata(0x80|0x19);

  t3=t2<<8 | t1;

  I2C_writecom(0xe5);

  return t3;
}
