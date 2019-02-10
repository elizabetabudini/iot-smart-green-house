#ifndef __PARKINGTASK__
#define __PARKINGTASK__
#include "Task.h"
#include "led.h"
#include "Sonar.h"
#include "ButtonImpl.h"
#include "Arduino.h"

extern bool PROX;

class ParkingTask: public Task {
    int b1;
    int b2;
    int currFade;
    int pin;
    int pin1;
    int pin2;
    int echo;
    int timer;
    int trigger;
    float a;
    int n;
    int incomingByte;
    char c;
    String s;
    bool out;
    Button* button1;
    Button* button2;
    Sonar* sonar;
    Light* ledr;
    Light* led1;
    Light* led2;
    enum {READY, STOPPED} state;

  public:
    ParkingTask(int pin, int pin1, int pin2, int echo, int trigger, int b1, int b2);
    void init(int period);
    void tick();
};

#endif
