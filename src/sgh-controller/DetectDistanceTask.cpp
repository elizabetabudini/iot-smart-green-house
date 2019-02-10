#include "DetectDistanceTask.h"
#include "Arduino.h"
#include "globalVars.h"

//Define constant values
#define DIST 0.5


/**
 * Class used to run the detect distance task and change the global state
 */
DetectDistanceTask::DetectDistanceTask(int trigPin, int echoPin){
	this->trigPin = trigPin;
	this->echoPin = echoPin;
}

void DetectDistanceTask::init(int period){
  Task::init(period);
  this->sonar = new Sonar(this->echoPin, this->trigPin);
  this->localState1 = FAR;
}

void DetectDistanceTask::tick(){

	float distance = sonar->getDistance();
    //Se si � sucfficientemente vicini si entra nello stato vicino, altrimenti lontano
	if(distance <= DIST){
		localState1 = NEAR;
	} else if(distance > DIST){
		localState1 = FAR;
	}

	
	//Depending on the global state and on the task state, we make a different action.
	switch(localState1){
  //msgService->sendMsg(Msg("BELLO"));
    //Il case lontano semplicemente setta la variabile globale a true
		case FAR:
				statoDistanza = LONTANO;
			break;
      
      //stato connesso, setto lo stato globale a manuale
    case NEAR:
      statoDistanza = VICINO;
      break;  		
		default:
      statoDistanza = LONTANO;
			break;
	}	
}
