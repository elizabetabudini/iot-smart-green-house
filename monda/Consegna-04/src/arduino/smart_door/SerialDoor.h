#ifndef __SERIAL_DOOR__
#define __SERIAL_DOOR__

#include "Task.h"
#include "Arduino.h"

extern int incomingByte;

class SerialDoor: public Task {

  public:
    void init(int period);
    void tick();
};
#endif

