#ifndef __VOICE_H__
#define __VOICE_H__

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
//*****************************************************************************

#define NO_OF_SAMPLES_VOICE 		128

//*****************************************************************************
//
// API Function prototypes
//
//*****************************************************************************
void Voiceinit(void);
float CollectVoice(void);


//*****************************************************************************
//
// Mark the end of the C bindings section for C++ compilers.
//
//*****************************************************************************
#ifdef __cplusplus
}
#endif

#endif //  __TMP006DRV_H__
