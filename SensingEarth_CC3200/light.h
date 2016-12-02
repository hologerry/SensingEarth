//*****************************************************************************

#ifndef __LIGHT_H__
#define __LIGHT_H__

//*****************************************************************************
//
// If building with a C++ compiler, make all of the definitions in this header
// have a C binding.
//
//*****************************************************************************
#ifdef __cplusplus
extern "C"
{
#endif


#define  APDS_Addr  0x39

//*****************************************************************************
//
// API Function prototypes
//
//*****************************************************************************

	void I2C_writedata(unsigned char,unsigned char);
	void I2C_writecom(unsigned char);
	void I2C_writeword(unsigned char,unsigned int);
	unsigned char I2C_readdata(unsigned char);
	unsigned int I2C_readword(unsigned char);
	void apds_init(void);
	unsigned int apds_readProximity(void);
	unsigned int apds_readALS(unsigned char );

	unsigned int CollectLight( void* );


//*****************************************************************************
//
// Mark the end of the C bindings section for C++ compilers.
//
//*****************************************************************************
#ifdef __cplusplus
}
#endif

#endif 
