/*Terzo progetto: Serra
*Boschi Francesco matricola 0000801376
*Budini Elizabeta matrciola 0000801989
*Gomulka Konrad matricola 0000792661
*/
#include "DetectDistanceTask.h"
#include "IrrigationTask.h"
#include "Scheduler.h"
#include "globalVars.h"

//define the pin of each sensor
#define LED_1_PIN 12
#define LED_2_PIN 11
//Using pin 6 because pin 9 and 10 are used from Timer2, which is used in servo library.
#define LED_3_PIN 6
#define ECHO_SONAR_PIN 7
#define TRIGGER_SONAR_PIN 8
#define TX 2
#define RX 3
#define SERVO_PIN 5

//Define period of each task. in this case all the tasks have the same period
#define WAITSCHED 50

//Scheduler
Scheduler sched;

//These is the external variable, and this is the only file where it's declared.
distanza statoDistanza;


//Program setup
void setup() {
  Serial.begin(9600);
  //set PinMode and Serial
  pinMode(LED_1_PIN, OUTPUT);
  pinMode(LED_2_PIN, OUTPUT);
  pinMode(LED_3_PIN, OUTPUT);
  
  pinMode(ECHO_SONAR_PIN, INPUT);
  pinMode(TRIGGER_SONAR_PIN, OUTPUT);

  //Set Scheduler tick each 50ms
  sched.init(WAITSCHED);

  //Create the tasks
  Task* irrigation = new IrrigationTask(LED_1_PIN, LED_2_PIN, LED_3_PIN, SERVO_PIN, TX, RX);
  Task* detection = new DetectDistanceTask(TRIGGER_SONAR_PIN, ECHO_SONAR_PIN);
  
  //Initialize Tasks
  irrigation->init(WAITSCHED);
  detection->init(WAITSCHED);

  //Add Tasks to Scheduler
  sched.addTask(detection);
  sched.addTask(irrigation);
  
  statoDistanza = LONTANO;
}

//program loop
void loop() {
  //Start Scheduler
  sched.schedule();
}
