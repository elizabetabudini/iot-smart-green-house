#ifndef __SERIAL_RADAR_COMM__
#define __SERIAL_RADAR_COMM__

#include "Task.h"
#include "Arduino.h"

extern String Buffer;

class SerialRadarComm: public Task {

  public:
    void init(int period);
    void tick();
};
#endif

