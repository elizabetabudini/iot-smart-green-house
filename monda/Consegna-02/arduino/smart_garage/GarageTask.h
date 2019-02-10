#ifndef __GARAGETASK__
#define __GARAGETASK__
#include "Task.h"
#include "led.h"
#include "Pir.h"
#include "Arduino.h"
#include "ButtonImpl.h"

extern bool PROX;

class GarageTask: public Task {
    char c;
    int currFade;
    int b2;
    int pin;
    int pinpir;
    int timer;
    String s;
    Button* button2;
    Light* led;
    Pir* pir;
    enum {OPEN, CLOSE, OPENING, CLOSING, WH} state;

  public:
    GarageTask(int pin, int pinpir, int b2);
    void init(int period);
    void fade(char v);
    void tick();
};

#endif
