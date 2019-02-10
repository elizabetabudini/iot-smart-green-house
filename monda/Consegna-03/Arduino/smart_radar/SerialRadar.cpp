#include "SerialRadar.h"
#include "Arduino.h"
#include "Radar.h"

extern String Buffer;

void SerialRadar::init(int period) {
  Task::init(period);
  Buffer="";
}

void SerialRadar::tick() {
  if (Serial.available()) {
    c = Serial.read();
    Buffer += c;
  }
}
