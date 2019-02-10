#include "ParkingTask.h"
#include "Scheduler.h"
#include "Pir.h"
#include "Sonar.h"
#include "ButtonImpl.h"
#include "Arduino.h"
#include <Arduino.h>

#define FADEAMOUNT 15
#define DISTMIN 10
#define DISTMAX 100
#define DISTCLOSE 50
#define INTESITYMAX 255
#define INTESITYMIN 0
#define TWOINTENSITYMAX 510

//Extern variable for change tasks
extern bool PROX;

//Constructor,
ParkingTask::ParkingTask(int pin, int pin1, int pin2, int echo, int trigger, int b1, int b2) {
  this->pin = pin;
  this->pin1 = pin1;
  this->pin2 = pin2;
  this->echo = echo;
  this->b1 = b1;
  this->b2 = b2;
  this->trigger = trigger;
  currFade = 255;
}

//Initialize Garage, set led,button and sonar with their pin
void ParkingTask::init(int period) {
  Task::init(period);
  sonar = new Sonar(echo, trigger);
  ledr = new Led(pin);
  led1 = new Led(pin1);
  led2 = new Led(pin2);
  button1 = new ButtonImpl(b1);
  button2 = new ButtonImpl(b2);
  timer = 0;
  state = READY;
}

//Method that explain what the Parking need to do each tick.
void ParkingTask::tick() {
  switch (state) {
    //When the Parking is ready for the car
    case READY:
      if (PROX) {
        //Use the sonar for see how much the car is far to wall
        a = sonar->getDistance();
        if (a < 5) {
          //Print this information each 5 ticks of ParkingTasks
          Serial.print("Distance: ");
          Serial.print(a);
          Serial.println(" m");
        }
        //Trasform a from m to cm
        a = a * 100;
        //If the forceClose is pressed and the car is in the garage
        if (button2->isPressed() and a < DISTCLOSE) {
          Serial.println("Closing");
          //Change state to STOPPED
          state = STOPPED;
        } else if (button2->isPressed() and a >= DISTCLOSE) {
          Serial.println("You are too far");
        }
        //If the machine is perfectly near the wall
        if (a <= DISTMIN and a > 0) {
          led1->setIntensity(INTESITYMAX);
          led2->setIntensity(INTESITYMAX);
          //If the button is pressed, simulate a touching
          if (button1->isPressed()) {
            Serial.println("TOUCHING");
          }
          Serial.println("OK CAN STOP!");
          if (Serial.available()) {
            c = Serial.read();
            s += c;
          } else {
            //If the user from Serial write "STOP" or "stop"
            if (s == "stop" or s == "STOP") {
              Serial.println("OK");
              //Change state to STOPPED
              state = STOPPED;
            }
            s = "";
          }
          out = false;
        } else {
          //If the car want to stop but is too far
          if (Serial.available()) {
            incomingByte = Serial.read();
            if (timer == 3) {
              Serial.println("TOO FAR!");
              timer = 0;
            } else {
              timer ++;
            }
          }
          //If the car is between DISTMIN and DISTMAX
          if (a > DISTMIN and a <= DISTMAX) {
            //Do map for the intensity of led
            n = map(a, DISTMIN, DISTMAX, TWOINTENSITYMAX, INTESITYMIN);
            out = false;
            if (n >= INTESITYMAX) {
              led1->setIntensity(INTESITYMAX);
              led2->setIntensity(n - INTESITYMAX);
            }
            else if (n < INTESITYMAX) {
              led1->setIntensity(n);
              led2->setIntensity(INTESITYMIN);
            }
          } else {
            if (out) {
              led1->setIntensity(INTESITYMIN);
              led2->setIntensity(INTESITYMIN);
            }
            out = true;
          }
        }
        break;
      //When the car is stopped
      case STOPPED:
        //Change external variable for return to GarageTask
        PROX = false;
        //Change the state to READY
        state = READY;
        led1->setIntensity(INTESITYMIN);
        led2->setIntensity(INTESITYMIN);
        break;
      }
  }
}

