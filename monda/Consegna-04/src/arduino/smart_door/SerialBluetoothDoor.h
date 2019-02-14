#ifndef __SERIAL_BLUETOOTH_DOOR__
#define __SERIAL__BLUETOOTH_DOOR__

#include "Task.h"
#include "Arduino.h"
#include "MsgService.h"

#define TXB 3
#define RXB 2

class SerialBluetoothDoor: public Task {

  public:
    void init(int period);
    void tick();
};
#endif

