#include "Potenziometro.h"
#include <Arduino.h>

/**
 * Class used to manage the potentiometer, used to detect the sugar level.
 */
Potenziometro::Potenziometro(String pin) {
  this->pin = pin;
}

int Potenziometro::getValue() {
  int val = analogRead(A0);
  //map the analogic value read from the potentiometer in a range from 0 to 6.
  val = map(val, 0, 1023, 0, 100);
	return val;
}
