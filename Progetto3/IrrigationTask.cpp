#include "IrrigationTask.h"
#include "Arduino.h"
#include "globalVars.h"

//define the time between each led switch on. It 's necessary because this task has two different functions inside and the period is the MCD between the two, so we need to set another time.
#define IRRIGATIONTIME 5000

/**
 * Class used to manage the maintenance task, which will allow user to recharge the coffee when it's ended.
 */
IrrigationTask::IrrigationTask(int pin0, int pin1, int pin2, int pinServo, int tx, int rx){
  this->pin[0] = pin0;    
  this->pin[1] = pin1;    
  this->pin[2] = pin2;
  this->servoPin = pinServo;
  this->tx = tx;
  this->rx= rx;
  lastTime = 0;
}

//set the starting state anhd initialize the leds
void IrrigationTask::init(int period){
  Task::init(period);
  for (int i = 0; i < 3; i++){
    led[i] = new Led(pin[i]); 
  }
  msgService = new MsgServiceBT(this->tx, this->rx);
  msgService->init();
  portataManuale = 1;
  //ledMid = new LedExt(10,0);
  //ledMid->switchOn();
  localState1 = WAITING;  
}
  

void IrrigationTask::tick(){

  //setting iniziale dello stato. Solamente se non è già in stato di irrigazione
  if(statoDistanza == VICINO && localState1 != IRRIGATION){
    //controllare se è connesso o meno
    localState1 = MANUALE;
    lastState = MANUALE;
  } else if(statoDistanza == LONTANO && localState1 != IRRIGATION){
      localState1 = AUTOMATICO;
      lastState = AUTOMATICO;
  }

  
  switch(localState1){
  case WAITING:
    servo.detach();
    break;
   
	case AUTOMATICO:
    servo.detach();
		led[0]->switchOn();
		led[1]->switchOff();
    led[2]->switchOff();
    //ledMid->setIntensity(185);
		lastTime = 0;
    break;
   
	case MANUALE:
    servo.detach();
		led[0]->switchOff();
		led[1]->switchOn();
    led[2]->switchOff();
    //ledMid->setIntensity(185);
		if (msgService->isMsgAvailable()) {
    		Msg* msg = msgService->receiveMsg();
    		if (msg->getContent() == "1"){
       			localState1 = IRRIGATION;
    		} else if (msg->getContent() == "P0"){
            portataManuale = 0;
        } else if (msg->getContent() == "P1"){
            portataManuale = 1;
        } else if (msg->getContent() == "P2"){
            portataManuale = 2;
        } 	
    		delete msg;
  	}
     break;
     
	case IRRIGATION:
    //ledMid->switchOn();
   
    servo.attach(servoPin);  
    led[2]->switchOn();
    //ledMid->setIntensity(portata);
		
		if(lastState == AUTOMATICO){
      int portata = 180;
      servo.write(1500);
			lastTime += myPeriod;
			if(lastTime >= IRRIGATIONTIME){
				localState1 = WAITING;
			}	
		} else if(lastState == MANUALE){
      servo.write(1500);
      //ipotizzando che l'APP invii sempre in loop dei valori.
      if (/*!msgService->isMsgAvailable() ||*/ statoDistanza == LONTANO) {
          localState1 = WAITING;
      }
			if (msgService->isMsgAvailable()) {
	    		Msg* msg = msgService->receiveMsg();
	    		if (msg->getContent() == "2"){
	       			localState1 = WAITING;
	    		}		
	    		delete msg;
	  	}			
		}   
    break;
  }
  
}
