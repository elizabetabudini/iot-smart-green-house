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
    //depending on the distance, the task will be in Near o Far state.
	if(distance <= DIST){
		localState1 = NEAR;
	} else if(distance > DIST){
		localState1 = FAR;
	}

	
	//Depending on the global state and on the task state, we make a different action.
	switch(localState1){
    //Far state: setting global distance to LONTANO.
		case FAR:
				statoDistanza = LONTANO;
			break;
      
    //Near state: setting global distance to VICINO.
    case NEAR:
      statoDistanza = VICINO;
      break;  		
		default:
      statoDistanza = LONTANO;
			break;
	}	
}
