#ifndef __DETECTDISTANCETASK__
#define __DETECTDISTANCETASK__

#include "Task.h"
#include "Sonar.h"
#include "globalVars.h"
#include "MsgServiceBT.h"
#include "SoftwareSerial.h"

/**
 * Class used to manage the detecting distance task
 */
class DetectDistanceTask: public Task {

  int trigPin, echoPin;
  Sonar *sonar;
  int waitingTime;
  enum localState{FAR, NEAR};
  localState localState1;
	

public:

  DetectDistanceTask(int trigPin, int echoPin);  
  void init(int period);  
  void tick();
};

#endif
