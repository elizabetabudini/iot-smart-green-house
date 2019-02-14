#include "Pir.h"
#include <Arduino.h>

Pir::Pir(int pin) {
  this->pin = pin;
}

bool Pir::movement() {
  if (digitalRead(pin) == HIGH) {
    return true;
  } else {
    return false;
  }
}


