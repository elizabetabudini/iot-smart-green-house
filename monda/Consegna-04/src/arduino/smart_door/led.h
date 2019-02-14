/*
  Tipo specifico di Light.
  Rappresenta la dichiarazione della classe concreta Led.
*/

#ifndef __LED__
#define __LED__

#include "Light.h"

class Led: public Light {
  public:
    Led(int pin);
    void switchOn();
    void switchOff();
    void setIntensity(int value);
  protected:
    int pin;
};

#endif
