#include "DetectDistanceTask.h"
#include "Arduino.h"
#include "globalVars.h"

//Define constant values
#define DIST1 0.3
#define DIST2 0.1
#define TIME1 1000
#define TIME2 5000

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
  this->state = LONTANO;
  this->timeElapsed = 0;
}

void DetectDistanceTask::tick(){

	localState tmpState;
  //get the read distance
	float distance = sonar->getDistance();
  //compute the actual state
	if(distance <= DIST2 && globalState == WAITING){

		tmpState = MOLTOVICINO;
	} else if(distance <= DIST1){
		tmpState = VICINO;
	} else {
		tmpState = LONTANO;
	}

  //if the state is the same it was before, increment the time spent in this state, otherwise we change the task state and we reset the time.
	if(tmpState == state){
		timeElapsed += myPeriod;
	} else {
		timeElapsed = 0;
		state = tmpState;
	}
	
	//Depending on the global state and on the task state, we make a different action.
	switch(globalState){
    //switch to ready if is on and there is someone close for enough time
		case ON:
			if(state == VICINO && timeElapsed >= TIME1){
				globalState = READY;
				Serial.println("RD");
			}
			waitingTime = 0;
			break;

      //switch to on if is ready and there is not anyone close for enough time.
		case READY:
			if(state == LONTANO && timeElapsed >= TIME2){
				globalState = ON;
				Serial.println("ON");
			}
			waitingTime = 0;
			break;

      //switch to ready or maintenance depending on how many coffe are left, if the state is waiting and there is someone really close or the time elapsed is > 5s
		case WAITING:
			waitingTime += myPeriod;
			if(state == MOLTOVICINO || waitingTime >= 5000){
				if(nCoffee == 0){
					globalState = MAINTENANCE;
          Serial.println("MT");
				} else {
					globalState = READY;
					Serial.println("RD");
				}
			}
			break;			
		default:
			break;
	}	
}
