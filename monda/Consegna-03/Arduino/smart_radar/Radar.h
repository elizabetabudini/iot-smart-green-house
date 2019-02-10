#ifndef __RADAR__
#define __RADAR__
#include "Arduino.h"
#include "Sonar.h"
#include "Light.h"
#include "Task.h"
#include <Servo.h>

extern String Buffer;

class Radar: public Task {
    Servo servo;
    Sonar* sonar;
    Light* led;
    int pos;
    int ok = 0;
    float a;
    int echo, trigger, ledpin, servopin;
    enum {IDLEE, SCANNING, TRACKING} state;

  public:
    Radar(int echo, int trigger, int ledpin, int servopin);
    void init(int period);
    void tick();
    void scanning(char m);
    void off();
};

#endif
