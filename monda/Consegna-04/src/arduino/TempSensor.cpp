#include "TempSensor.h"
#include <Wire.h>

TempSensor::TempSensor() {
  Wire.begin(); /* lib init */
}

int TempSensor::readTemperature() {
  //start the communication with IC with the address xx
  Wire.beginTransmission(temp_address); 
  //send a bit and ask for register zero
  Wire.write(0);
  //end transmission
  Wire.endTransmission();
  //request 1 byte from address xx
  Wire.requestFrom(temp_address, 1);
  //wait for response
  while(Wire.available() == 0);
  //put the temperature in variable c
  int c = Wire.read();   
  return c;
}
