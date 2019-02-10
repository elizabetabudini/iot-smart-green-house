#ifndef __DOOR__
#define __DOOR__
#include "Arduino.h"
#include "Sonar.h"
#include "Light.h"
#include "Task.h"
#include <Servo.h>
#include "Pir.h"
#include "ButtonImpl.h"
#include "TempSensor.h"
#include "MsgService.h"

class Door: public Task {
    Servo servo;
    Sonar* sonar;
    Light* ledr;
    Button* button;
    Pir* pir;
    TempSensor* temp;
    float a;
    int trovato, timer, n, value, prevalue,istime;
    int echo, trigger, ledpinr, servopin, buttonpin, pirpin;
    enum {IDLEE, WAIT, PRESENT, PASSATO, VISTO} state;

  public:
    Door(int echo, int trigger, int ledpin, int servopin, int buttonpin, int pirpin);
    void init(int period);
    void tick();
    void exitDoor();
};

#endif
