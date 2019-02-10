#ifndef __PIR__
#define __PIR__

class Pir {
  public:
    Pir(int pin);
    bool movement();
  protected:
    int pin;
};

#endif
