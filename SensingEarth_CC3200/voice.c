#include "voice.h"

#include "rom_map.h"
#include "pin.h"
#include "hw_memmap.h"
#include "adc.h"


unsigned long pulAdcSamples[4096];
unsigned int  uiIndex=0;


void Voiceinit(void)
{

    //
    // Pinmux for the selected ADC input pin
    //
    MAP_PinTypeADC(PIN_60,PIN_MODE_255);

    //
    // Configure ADC timer which is used to timestamp the ADC data samples
    //
    MAP_ADCTimerConfig(ADC_BASE,2^17);

    //
    // Enable ADC timer which is used to timestamp the ADC data samples
    //
    MAP_ADCTimerEnable(ADC_BASE);

    //
    // Enable ADC module
    //
    MAP_ADCEnable(ADC_BASE);

}

float CollectVoice(void)
{
    unsigned long ulSample;
    float Vol_value;

    //
    // Initialize Array index for multiple execution
    //
    uiIndex=0;

    //
    // Enable ADC channel
    //

    MAP_ADCChannelEnable(ADC_BASE, ADC_CH_3);

    while(uiIndex < NO_OF_SAMPLES_VOICE + 4)
    {
        if(MAP_ADCFIFOLvlGet(ADC_BASE, ADC_CH_3))
        {
            ulSample = MAP_ADCFIFORead(ADC_BASE, ADC_CH_3);
            pulAdcSamples[uiIndex++] = ulSample;
        }
    }

    MAP_ADCChannelDisable(ADC_BASE, ADC_CH_3);

    uiIndex = 0;

    //
    // Convert ADC samples
    //
    while(uiIndex < NO_OF_SAMPLES_VOICE)
    {
     Vol_value=(((float)((pulAdcSamples[4+uiIndex] >> 2 ) & 0x0FFF))*1.4)/4096;
     uiIndex++;
    }

    return 20*Vol_value;
}
