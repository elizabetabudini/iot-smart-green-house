#ifndef __SERIAL_RADAR__
#define __SERIAL_RADAR__

#include "Task.h"
#include "Arduino.h"

extern String Buffer;

class SerialRadar: public Task {
    char  c;
    String s;
    
  public:
    void init(int period);
    void tick();
};
#endif

