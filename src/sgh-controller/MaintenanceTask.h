#ifndef __MAINTENANCETASK__
#define __MAINTENANCETASK__

#include "Task.h"
#include "globalVars.h"

/**
 * Class used to manage the maintenance task, which will allow user to recharge the coffee when it's ended.
 */
class MaintenanceTask: public Task {

  char c;
  
public:

  MaintenanceTask();  
  enum localState{OK, WAITREFILL};
  localState state;
  void init(int period);  
  void tick();
};

#endif
