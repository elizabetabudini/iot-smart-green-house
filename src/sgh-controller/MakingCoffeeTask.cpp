#include "MakingCoffeeTask.h"
#include "Arduino.h"
#include "globalVars.h"
#include "ButtonImpl.h"

//define the time between each led switch on. It 's necessary because this task has two different functions inside and the period is the MCD between the two, so we need to set another time.
#define MAKINGPERIOD 1000

/**
 * Class used to manage the maintenance task, which will allow user to recharge the coffee when it's ended.
 */
MakingCoffeeTask::MakingCoffeeTask(int pin0, int pin1, int pin2){
  this->pin[0] = pin0;    
  this->pin[1] = pin1;    
  this->pin[2] = pin2;
  lastTime = 0;
}

//set the starting state anhd initialize the leds
void MakingCoffeeTask::init(int period){
  Task::init(period);
  for (int i = 0; i < 3; i++){
    led[i] = new Led(pin[i]); 
  }
  state = WAIT;  
  this->button= new ButtonImpl(2);
}
  

void MakingCoffeeTask::tick(){
  //we increment each tick a local variable, so we know each time the time elapsed is 1second (to manage the led switch on and off).
  lastTime += myPeriod;
	//If the global state is ready and the local state is WAIT(not working) we wait till the button is pressed.
  if(globalState == READY && state == WAIT){
    //when the button is pressed, the working process starts (LED1 state).
    if(button->isPressed()){
      globalState = WORKING;
      Serial.println("WK");
      state= LED1;
      //reset timer so we wait 1 second from now
      lastTime = 0;
    }

  //This condition allow us to swap the led state once each second.
  }  if(lastTime >= MAKINGPERIOD){
    //each "internal tick", we reset the "internal timer".
    lastTime = 0;
    //when the making nprocess end, we switch off all the leds.
    if(state == WAIT){  
      led[0]->switchOff();
      led[1]->switchOff();
      led[2]->switchOff();
    }
    //if its working, we manage the progressive switch on of the three led, and the switch of the local state
    if(globalState == WORKING){    
      switch(state){
        case LED1:
        Serial.println("L1");
        led[0]->switchOn();
        state = LED2;
        break;
        case LED2:
        led[1]->switchOn();
        Serial.println("L2");
        state = LED3;
        break;
        case LED3:
        led[2]->switchOn();
        Serial.println("L3");
        //when the coffee is ready, we decrement the number of coffee
        nCoffee = nCoffee - 1;
        globalState = WAITING;
        Serial.println("WT");
        state = WAIT;
        break;
      }
    }
      	
  }
}
