#include "IrrigationTask.h"
#include "Arduino.h"
#include "globalVars.h"

//define the time between each led switch on. It 's necessary because this task has two different functions inside and the period is the MCD between the two, so we need to set another time.
#define IRRIGATIONTIME 5000
#define BLUETOOTHTIME 2000

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
  for (int i = 0; i < 2; i++){
    led[i] = new Led(pin[i]); 
  }
  ledMid = new LedExt(pin[2],128);  
  msgService = new MsgServiceBT(this->tx, this->rx);
  msgService->init();
  portataManuale = 255;
  localState1 = WAITING;
  MsgService.init();
  lastTimeMsgBluetooth = 0;
}
  

void IrrigationTask::tick(){
  msgService->sendMsg(Msg("@vicino@")); //debug bt

  //setting iniziale dello stato. Solamente se non è già in stato di irrigazione
  if(statoDistanza == VICINO && localState1 != IRRIGATION && msgService->isMsgAvailable()){
    /*
     Msg* msg = msgService->receiveMsg();
     MsgService.sendMsg(msg->getContent());
     delete msg;
     */
     
    //controllare se è connesso o meno
    if(lastState != MANUALE){
      MsgService.sendMsg("ManIn");
    }
    localState1 = MANUALE;
    lastState = MANUALE;
  } else if(statoDistanza == LONTANO && localState1 != IRRIGATION){
      if(lastState == MANUALE){
        MsgService.sendMsg("ManOut");  
      }
      localState1 = AUTOMATICO;
      lastState = AUTOMATICO;
  }

  //Buffer cleaning. If you are in another state, and receive a message via serial or bluetooth, it will delete the message, otherwise you would receive it when they they get connected
  /*
  if (msgService->isMsgAvailable() && (localState1 != MANUALE || localState1 == IRRIGATION && lastState != MANUALE)) {
      Msg* msg = msgService->receiveMsg();
      delete msg;
  }
  */

  if (localState1 != MANUALE && !(localState1 == IRRIGATION && lastState == MANUALE)) {
      if (msgService->isMsgAvailable()){
        MsgService.sendMsg("meesaagio cancellato no problema");
        Msg* msg = msgService->receiveMsg();     
        delete msg;
      }
  }
  
  if (localState1 != AUTOMATICO && !(localState1 == IRRIGATION && lastState == AUTOMATICO)) {
      if (MsgService.isMsgAvailable()){
        Msg* msg = MsgService.receiveMsg();     
        delete msg;
      }
  }
  

  //switch on the task state
  switch(localState1){
  case WAITING:
  lastTimeMsgBluetooth = 0;
  /*
    //debug collegamento bluetooth
    if (msgService->isMsgAvailable()) {
        Msg* msg = msgService->receiveMsg();
        if (msg->getContent() == "connesso"){
            Serial.println("connesso");
        }
    }
    */
    servo.detach();
    break;
   
	case AUTOMATICO:
    servo.detach();
		led[0]->switchOn();
		led[1]->switchOff();
    ledMid->switchOff();
		lastTime = 0;
    if (MsgService.isMsgAvailable()) {
      Msg* msg = MsgService.receiveMsg();    
      if (msg->getContent() == "Start0"){
        MsgService.sendMsg("Start");
        portataAutomatica = 10;
        localState1 = IRRIGATION;
      } else if (msg->getContent() == "Start1"){
        MsgService.sendMsg("Start");
        portataAutomatica = 80;
        localState1 = IRRIGATION;
      } else if (msg->getContent() == "Start2"){
        MsgService.sendMsg("Start");
        portataAutomatica = 255;
        localState1 = IRRIGATION;
      } 
      delete msg;
   }
    break;
   
	case MANUALE:
    MsgService.sendMsg("Semo in manuale ahaha");
    servo.detach();
    msgService->sendMsg(Msg("@vicino@"));
		led[0]->switchOff();
		led[1]->switchOn();
    ledMid->switchOff();
    if (msgService->isMsgAvailable() <= 0){
       MsgService.sendMsg("Non disponibile skere");
        lastTimeMsgBluetooth += myPeriod;
        if(lastTimeMsgBluetooth >= BLUETOOTHTIME){
            localState1 = WAITING;
        }
     }
		if (msgService->isMsgAvailable() > 0) {
    lastTimeMsgBluetooth = 0;
    		Msg* msg = msgService->receiveMsg();
    		if (msg->getContent() == "connesso"){
        MsgService.sendMsg("DISPNIBILISSIMO AHAHA");
        MsgService.sendMsg("Non disponibile skere");
    		if (msg->getContent() == "1"){
            MsgService.sendMsg("Start");
       			localState1 = IRRIGATION;
    		} else if (msg->getContent() == "3"){
            portataManuale = 10;
        } else if (msg->getContent() == "4"){
            portataManuale = 80;
        } else if (msg->getContent() == "5"){
            portataManuale = 255;
        } 	
    		delete msg;
  	}
    break;
     
	case IRRIGATION:   
    servo.attach(servoPin);  		
		if(lastState == AUTOMATICO){
      ledMid->setIntensity(portataAutomatica);
      servo.write(1500);
			lastTime += myPeriod;
			if(lastTime >= IRRIGATIONTIME){
        MsgService.sendMsg("StopT");
				localState1 = WAITING;
			}
      if (MsgService.isMsgAvailable()) {
        Msg* msg = MsgService.receiveMsg();    
        if (msg->getContent() == "Stop"){
          MsgService.sendMsg("Stop");
          localState1 = WAITING;
        }
        delete msg;
      } 
		} else if(lastState == MANUALE){   
      ledMid->setIntensity(portataManuale);
      servo.write(1500);
      //ipotizzando che l'APP invii sempre in loop dei valori.
      if (statoDistanza == LONTANO) {
          MsgService.sendMsg("Stop");
          localState1 = WAITING;
      }
       if (!msgService->isMsgAvailable()){
          lastTimeMsgBluetooth += myPeriod;
          if(lastTimeMsgBluetooth >= BLUETOOTHTIME){
              MsgService.sendMsg("Stop");
              localState1 = WAITING;
          }
       }
			if (msgService->isMsgAvailable()) {
      lastTimeMsgBluetooth = 0;
	    		Msg* msg = msgService->receiveMsg();
	    		if (msg->getContent() == "2"){
              MsgService.sendMsg("Stop");
	       			localState1 = WAITING;
	    		}		
	    		delete msg;
	  	}			
		} 
    ledMid->switchOn(); 
    break;
  }
  
}
