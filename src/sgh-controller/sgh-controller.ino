/*Secondo progetto: Smart coffee machine
*Boschi Francesco matricola 0000801376
*Budini Elizabeta matrciola 0000801989
*Gomulka Konrad matricola 0000792661
*/
#include "MakingCoffeeTask.h"
#include "SugarLevelTask.h"
#include "MaintenanceTask.h"
#include "DetectDistanceTask.h"
#include "SleepTask.h"
#include "Scheduler.h"
#include "globalVars.h"


//define the pin of each sensor
#define LED_RED_PIN 13
#define LED_MID_PIN 11
#define LED_LEFT_PIN 12
#define TX_PIN 2
#define RX_PIN 3
#define ECHO_SONAR_PIN 7
#define TRIGGER_SONAR_PIN 8
//DEFINE TIME OF EACH TASK
#define CLOCK_TIME 50

//Scheduler
Scheduler sched;

//These are two external variables, and this is the only file whee they are declared.
statiGlobali globalState;
int portata;

//Program setup
void setup() {
  //set PinMode and Serial
  pinMode(LED_RED_PIN, OUTPUT);
  pinMode(LED_MID_PIN, OUTPUT);
  pinMode(LED_LEFT_PIN, OUTPUT);
  pinMode(ECHO_SONAR_PIN, INPUT);
  pinMode(TRIGGER_SONAR_PIN, OUTPUT);
  Serial.begin(9600);

  //Set Scheduler tick each 20ms
  sched.init(WAITSCHED);

  //Create the tasks
  Task* makingCoffee = new MakingCoffeeTask(LED_1_PIN, LED_2_PIN, LED_3_PIN);
  Task* sugarLevel = new SugarLevelTask("A0");
  
  //Initialize Tasks
  makingCoffee->init(WAITMAKING);
  sugarLevel->init(SUGARTIME);

  //Add Tasks to Scheduler
  sched.addTask(makingCoffee);
  sched.addTask(sugarLevel);


  portata = 0;
  globalState = AUTOMATIC;
}

//program loop
void loop() {
  //Start Scheduler
  sched.schedule();
}
