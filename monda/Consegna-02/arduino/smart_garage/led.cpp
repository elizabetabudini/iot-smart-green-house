/*
  Implementazione concreta della classe Led.
*/
#define FADEAMOUNT 5
#include "Led.h"
#include "Arduino.h"

int currFade = 0;

Led::Led(int pin) {
  this->pin = pin;
  pinMode(pin, OUTPUT);
}

void Led::switchOn() {
  digitalWrite(pin, HIGH);
}

void Led::switchOff() {
  digitalWrite(pin, LOW);
}

void Led::setIntensity(int value) {
  analogWrite(pin, value);
}
