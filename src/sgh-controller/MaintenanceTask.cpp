#include "MaintenanceTask.h"
#include "Arduino.h"

/**
 * Class used to manage the maintenance task, which will allow user to recharge the coffee when it's ended.
 */
MaintenanceTask::MaintenanceTask(){
  this->c = 'n';
}

void MaintenanceTask::init(int period){
  Task::init(period);
  state = OK;
}

void MaintenanceTask::tick(){
  //Set the local state to WAITREFILL if the system is in maintenance and the local state is not WAITREFILL (this check is necesarry otherwise the program would priunt always "MT STart".
	if(globalState == MAINTENANCE && state == OK){
			state = WAITREFILL;
			Serial.println("MT");
		}
   //depending on the local state, we make a different action
	switch(state){
    //if the local state is ok, we just "RESET" the read character
		case OK:
			c = 'n';
			break;
      //if the local state is WAITREFILL, we try to read from the serial, and if the character read is "K" we change 
      //the globalState and the local state and we increment the number of coffe: the maintenance is ended.
		case WAITREFILL:
	   		 if (Serial.available()) {
        		c = Serial.read();
        	}
        	if(c == 'k'){
            nCoffee = 3;
            Serial.println("NC");
            Serial.println(nCoffee);
        		state = OK;
        		globalState = STANDBY;
            Serial.println("SB");
			}
			break;	
	}	
}
