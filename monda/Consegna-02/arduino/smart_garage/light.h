/*
  Interfaccia Light che verrà poi modellata in Led.
*/

#ifndef __LIGHT__
#define __LIGHT__

class Light {
  public:
    virtual void switchOn() = 0;
    virtual void switchOff() = 0;
    virtual void setIntensity(int) = 0;
};

#endif
