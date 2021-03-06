#include "IrrigationTask.h"
#include "Arduino.h"
#include "globalVars.h"

//define the max irrigation time in auto mode, and the max time after which the bluetooth will be disconnected
#define IRRIGATIONTIME 5000
#define BLUETOOTHTIME 2000

/**
 * Class used to manage the irrigation task.
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

//set the starting state anhd initialize sensors and actuators
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
  umiditaAttuale = "0";
}
  

void IrrigationTask::tick(){ 

  //Set the task state depending on the distance and bluetooth connection, but only if the task is not in IRRIGATION state (which has its own conditions).
  if(statoDistanza == VICINO && localState1 != IRRIGATION && msgService->isMsgAvailable()){
    //Send message only if the task enter in MANUAL mode.
    if(lastState != MANUALE){
      MsgService.sendMsg("ManIn");    
      msgService->sendMsg(Msg("ManIn\n")); 
    }        
    localState1 = MANUALE;
    lastState = MANUALE;
    lastTimeMsgBluetooth = 0;
  } else if((statoDistanza == LONTANO || lastTimeMsgBluetooth >= BLUETOOTHTIME )&& localState1 != IRRIGATION){
    //Send message only if the task enter in AUTOMATIC mode.
      if(lastState == MANUALE){
        MsgService.sendMsg("ManOut"); 
        msgService->sendMsg(Msg("ManOut\n"));       
      }     
      localState1 = AUTOMATICO;
      lastState = AUTOMATICO;
  }

  //Buffer cleaning. If you are in another state, and receive a message via serial or bluetooth, it will delete the message, otherwise you would receive it when they they get connected
  if (localState1 != MANUALE && !(localState1 == IRRIGATION && lastState == MANUALE)) {
      if (msgService->isMsgAvailable()){
        Msg* msg = msgService->receiveMsg();     
        delete msg;
      }
  }

  //if you are not in AUTO, you delete the message, but if it's humidity update you update the value
  if (localState1 != AUTOMATICO && !(localState1 == IRRIGATION && lastState == AUTOMATICO)) {
      if (MsgService.isMsgAvailable()){
        Msg* msg = MsgService.receiveMsg();
        if (msg->getContent().substring(0,7).equals("Umidita")){
          umiditaAttuale = msg->getContent().substring(8);  
        } 
        delete msg;
      }
  }
  

  //switch on the task state
  switch(localState1){
  case WAITING:
    lastTimeMsgBluetooth = 0;
    ledMid->switchOff();
    servo.detach();
    break;
   
  case AUTOMATICO:
    servo.detach();
    led[0]->switchOn();
    led[1]->switchOff();
    ledMid->switchOff();
    lastTime = 0;
    //depending on the message received, the task update the humidity or start the irrigation.
    if (MsgService.isMsgAvailable()) {
      Msg* msg = MsgService.receiveMsg();    
      if (msg->getContent() == "Start0"){
        MsgService.sendMsg("Start");
        msgService->sendMsg(Msg("Start\n")); 
        portataAutomatica = 10;
        localState1 = IRRIGATION;
      } else if (msg->getContent() == "Start1"){
        MsgService.sendMsg("Start");
        msgService->sendMsg(Msg("Start\n")); 
        portataAutomatica = 80;
        localState1 = IRRIGATION;
      } else if (msg->getContent() == "Start2"){
        MsgService.sendMsg("Start");
        msgService->sendMsg(Msg("Start\n")); 
        portataAutomatica = 255;
        localState1 = IRRIGATION;
      }  else if (msg->getContent().substring(0,7).equals("Umidita")){
        umiditaAttuale = msg->getContent().substring(8);  
      }
      delete msg;
   }
    break;
   
  case MANUALE:
    servo.detach();
    led[0]->switchOff();
    led[1]->switchOn();
    ledMid->switchOff();
    //if no message is available for more than 5s, we suppose bluetooth it's not connected anymore.
    if (msgService->isMsgAvailable() <= 0){
        lastTimeMsgBluetooth += myPeriod;
        if(lastTimeMsgBluetooth >= BLUETOOTHTIME){
            localState1 = WAITING;
        }
     }
    //if a message is available, the task make a different action depending on what message it receive. Also we set to 0 the last message counter.
    if (msgService->isMsgAvailable() > 0) {
        //each time a message is available (bluetooth is connected) the task send the humidity to the mobile APP.
        Msg *mess = new Msg(umiditaAttuale);
        msgService->sendMsg(*mess);      
        lastTimeMsgBluetooth = 0;
        Msg* msg = msgService->receiveMsg();
        if (msg->getContent() == "1"){
            MsgService.sendMsg("Start");
            msgService->sendMsg(Msg("Start\n")); 
            localState1 = IRRIGATION;
        } else if (msg->getContent() == "3"){
            //MsgService.sendMsg("portata 3");
            portataManuale = 10;
        } else if (msg->getContent() == "4"){
            //MsgService.sendMsg("portata 4");
            portataManuale = 80;
        } else if (msg->getContent() == "5"){
            //MsgService.sendMsg("portata 5");
            portataManuale = 255;
        }   
        delete msg;
    }
    break;
     
  case IRRIGATION:   
    servo.attach(servoPin);
    //if it's automatic irrigation     
    if(lastState == AUTOMATICO){
      ledMid->setIntensity(portataAutomatica);
      servo.write(1500);
      lastTime += myPeriod;
      //if t5he max irrigation time ended
      if(lastTime >= IRRIGATIONTIME){
        MsgService.sendMsg("StopT");
        localState1 = WAITING;
      }
      //check if explicit asking for stop
      if (MsgService.isMsgAvailable()) {
        Msg* msg = MsgService.receiveMsg();    
        if (msg->getContent() == "Stop"){
          MsgService.sendMsg("Stop");
          msgService->sendMsg(Msg("Stop\n"));
          localState1 = WAITING;
        }
        delete msg;
      } 
    } else if(lastState == MANUALE){    //if it's manual irrigation   
      ledMid->setIntensity(portataManuale);
      servo.write(1500);
      //if the person is too far while irrigating, goes back to automatic.
      if (statoDistanza == LONTANO) {
          MsgService.sendMsg("Stop"); 
          msgService->sendMsg(Msg("Stop\n"));
          localState1 = WAITING;
      }
       //if no message is available for more than 5s, we suppose bluetooth it's not connected anymore.
       if (msgService->isMsgAvailable() <= 0){
          lastTimeMsgBluetooth += myPeriod;
          if(lastTimeMsgBluetooth >= BLUETOOTHTIME){
              MsgService.sendMsg("Stop");
              msgService->sendMsg(Msg("Stop\n"));
              localState1 = WAITING;
          }
       }
      //if a message is available while irrigating, check if it's Stop.
      if (msgService->isMsgAvailable() > 0) {
      lastTimeMsgBluetooth = 0;
          Msg* msg = msgService->receiveMsg();
          if (msg->getContent() == "2"){
              MsgService.sendMsg("Stop");
              msgService->sendMsg(Msg("Stop\n"));
              localState1 = WAITING;
          }   
          delete msg;
      }     
    } 
    ledMid->switchOn(); 
    break;
  }
  
}
