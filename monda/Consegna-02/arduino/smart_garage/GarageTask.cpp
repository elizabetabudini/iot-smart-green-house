#include "GarageTask.h"
#include "Scheduler.h"
#include "Pir.h"
#include "ButtonImpl.h"
#include <Arduino.h>

#define FADEAMOUNT 5
#define INTESITYMAX 255
#define INTESITYMIN 0
#define WAITSIXSEC 150

//Extern variable for change tasks
extern bool PROX;

//Constructor
GarageTask::GarageTask(int pin, int pinpir, int b2) {
  this->pin = pin;
  this->pinpir = pinpir;
  this->b2 = b2;
}

//Initialize Garage, set led,button and pir with their pin
void GarageTask::init(int period) {
  Task::init(period);
  button2 = new ButtonImpl(b2);
  currFade = 0;
  led = new Led(pin);
  pir = new Pir(pinpir);
  PROX = false;
  state = CLOSE;
  Serial.println("I'm close");
}

//Method that let the led fades from 0 to 255 or from 255 to 0
void GarageTask::fade(char v) {
  if (v == '+') {
    //From 0 to 255
    currFade += FADEAMOUNT;
    led->setIntensity(currFade);
  } else {
    //From 255 to 0
    currFade -= FADEAMOUNT;
    led->setIntensity(currFade);
  }
}

//Method that explain what the Garage need to do each tick.
void GarageTask::tick() {
  switch (state) {
    //When the Garage is CLOSE
    case CLOSE:
      //Read from Serial
      if (Serial.available()) {
        c = Serial.read();
        s += c;
      } else {
        //If the word written in the Serial is "STOP" or "stop"
        if (s == "open" or s == "OPEN") {
          Serial.println("I'm opening");
          //Open the Garage, change state
          state = OPENING;
        }
        s = "";
      }
      break;
    //When the Garage is opening
    case OPENING:
      if (currFade < INTESITYMAX) {
        //Red led is gonna fade to max intensity
        fade('+');
      } else {
        //Change state to OPEN
        state = OPEN;
        Serial.println("I'm open");
      }
      break;
    //When the garage is open
    case OPEN:
      //If forceClose is pressed
      if (button2->isPressed()) {
        Serial.println("Closing");
        //Change state to CLOSING
        state = CLOSING;
      }
      //If Pir pick up movement
      if (pir->movement()) {
        Serial.println("Welcome Home");
        //Change state to WELCOME HOME
        state = WH;
        timer = 0;
      } else {
        timer++;
        //If the pir didn't pick up a movement before 6 seconds
        if (timer == WAITSIXSEC) {
          Serial.println("Closing");
          timer = 0;
          //Change state to CLOSING
          state = CLOSING;
        }
        break;
      //When the garage is closing
      case CLOSING:
        if (!PROX) {
          if (currFade > INTESITYMIN) {
            //Red led is gonna fade to min intensity
            fade('-');
          } else {
            //Change state to CLOSE
            state = CLOSE;
            Serial.println("I'm close");
          }
        }
        break;
      //When the Garage sees the car near it
      case WH:
        //External variabile changing for let ParkingTask start.
        PROX = true;
        //Return to CLOSING
        state = CLOSING;
        break;
      }
  }
}

