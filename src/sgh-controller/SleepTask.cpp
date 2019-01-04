#include "SleepTask.h"
#include "Arduino.h"
#include <avr/interrupt.h>
#include <avr/sleep.h>
#include <avr/power.h>
#include "globalVars.h"

/**
 * Class used to manage the sleeping task, which will allow the system to use sleep mode.
 */
 
//define the sleep period, which is the time after the coffe system will go sleeping.
#define SLEEPPERIOD 5000

SleepTask::SleepTask(int Pin){
  this->pin = Pin;    

  //setup the interrupt pin depending on the pir pin.
  if (pin == 2){
	  interruptN = 0;
  }
  else if (pin == 3){
	  interruptN = 1;
  }
  //else {
	  //ERROR
   //Serial.println("WRONG INTERRUPTPIN");
  //}
}

//function used to wake up the system. Does not do anything at all.
void SleepTask::wakeup()
{
}

//initialize the task
void SleepTask::init(int period){
  Task::init(period);
  this->myPeriod = period;
  pir = new Pir(pin);
  lastTime = 0;
}

//each tick, check the time elapsed since the pir detected a person.
void SleepTask::tick(){
  //if someone is detected, reset the timer
	if(pir->movement()){
		lastTime = 0;
	} else if(globalState == ON){ //if no one has been detected for enough time and the global sdtate is ON, set the global state to STANDBY
		if(lastTime >= SLEEPPERIOD){
			globalState = STANDBY;
		}
		else {
      //if no one is detected but the time elapsed is not enough,increment the total time.
			lastTime += myPeriod;
		}
	}

  //check the global state, if it's STANDBY go to sleep.
	if(globalState == STANDBY){
  	Serial.println("SB");
  	delay(50);
    //interrupt attached
    attachInterrupt (interruptN, wakeup, RISING);
    //define the sleep mode
    set_sleep_mode(SLEEP_MODE_PWR_DOWN);
    //enable the sleep mode
    sleep_enable();
    //start sleeping
    sleep_mode();  
    /*
    WHEN THE SYSTEM WAPE UP WITH THE INTERRUPT, WILL START EXECUTING FROM HERE
    */
    //disable sleep mode
    sleep_disable();
    //interrupt detached
    detachInterrupt(interruptN);
    //active all the pins     
    power_all_enable();
    //reset the timer
  	lastTime = 0;
   //set global state to ON. System is ready to work.
  	globalState = ON;
    Serial.println("ON");
	}
}
