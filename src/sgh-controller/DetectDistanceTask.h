#ifndef __DETECTDISTANCETASK__
#define __DETECTDISTANCETASK__

#include <SoftwareSerial.h>
#include "Task.h"
#include "Sonar.h"
#include "globalVars.h"

/**
 * Class used to manage the detecting distance task
 */
class DetectDistanceTask: public Task {

  int trigPin, echoPin;
  Sonar *sonar;
  int timeElapsed;
  int waitingTime;
  enum localState{LONTANO, VICINO, MOLTOVICINO};
  localState state;
  int lastState;

public:

  DetectDistanceTask(int trigPin, int echoPin);  
  void init(int period);  
  void tick();
};

#endif
