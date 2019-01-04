#ifndef __MAKINGCOFFEETASK__
#define __MAKINGCOFFEETASK__

#include "Task.h"
#include "Led.h"
#include "globalVars.h"
#include "Button.h"

/**
 * Class used to manage the maintenance task, which will allow user to recharge the coffee when it's ended.
 */
class MakingCoffeeTask: public Task {

  int pin[3];
  Light* led[3]; 
  enum localState{WAIT, LED1, LED2, LED3};
  localState state;
  Button* button;
  int lastTime;

public:

  MakingCoffeeTask(int pin0, int pin1, int pin2);  
  void init(int period);  
  void tick();
};

#endif
