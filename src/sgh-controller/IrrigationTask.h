#ifndef __IRRIGATIONTASK__
#define __IRRIGATIONTASK__

#include "Task.h"
#include "globalVars.h"
#include "MsgServiceBT.h"
#include "SoftwareSerial.h"
#include "LedExt.h"
#include "Led.h"
#include "ServoTimer2.h"
#include "MsgService.h"

/**
 * Class used to manage the detecting distance task
 */
class IrrigationTask: public Task {

  int portataAutomatica;
  String umiditaAttuale;
  int portataManuale;
  MsgServiceBT * msgService;
  int pin[3];
  int tx;
  int rx;
  int servoPin;
  Light* led[3];
  LightExt* ledMid;
  ServoTimer2 servo;
  int lastTime;
  int lastTimeMsgBluetooth;
  enum localState{WAITING, AUTOMATICO, MANUALE, IRRIGATION};
  localState localState1;
  localState lastState;

public:

  IrrigationTask(int pin0, int pin1, int pin2, int pinServo, int tx, int rx);  
  void init(int period);  
  void tick();
};

#endif
