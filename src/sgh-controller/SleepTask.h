#ifndef __SLEEPTASK__
#define __SLEEPTASK__

#include "Task.h"
#include "Arduino.h"
#include "Pir.h"
#include "globalVars.h"

/**
 * Class used to manage the sleeping task, which will allow the system to use sleep mode.
 */
 
class SleepTask: public Task {
	Pir* pir;
	int pin;
	int interruptN; //interrupt number relative to pin
	int lastTime;
public:
  SleepTask(int Pin);  
  void init(int period);  
  void tick();
private:
  static void wakeup();
};

#endif
