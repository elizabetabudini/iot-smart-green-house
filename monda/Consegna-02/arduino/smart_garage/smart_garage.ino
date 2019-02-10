//Seconda consegna Arduino, progetto creato da Luca Mondaini,Daniele Tentoni e Nicolò Pracucci
#include "GarageTask.h"
#include "ParkingTask.h"
#include "Scheduler.h"

#define LED_V1 11
#define LED_V2 6
#define LED_R 5
#define BUTTON1_PIN 2
#define BUTTON2_PIN 3
#define PIR 4
#define ECHO 7
#define TRIGGER 8
#define WAITPARKING 120
#define WAITGARAGE 40
#define WAITSCHED 40

Scheduler sched;
bool PROX;

void setup() {
  //set PinMode and Serial
  pinMode(LED_V1, OUTPUT);
  pinMode(LED_V2, OUTPUT);
  pinMode(LED_R, OUTPUT);
  pinMode(ECHO, INPUT);
  pinMode(TRIGGER, OUTPUT);
  pinMode(PIR, INPUT);
  pinMode(BUTTON1_PIN, INPUT);
  pinMode(BUTTON2_PIN, INPUT);
  digitalWrite(PIR, LOW);
  Serial.begin(9600);

  //Set Scheduler tick each 20ms
  sched.init(WAITSCHED);

  //Create 2 tasks, Garage and Parking
  Task* garage = new GarageTask(LED_R, PIR, BUTTON2_PIN);
  Task* parking = new ParkingTask(LED_R, LED_V1, LED_V2, ECHO, TRIGGER, BUTTON1_PIN, BUTTON2_PIN);

  //Initialize Tasks
  parking->init(WAITPARKING);
  garage->init(WAITGARAGE);

  //Add Tasks to Scheduler
  sched.addTask(garage);
  sched.addTask(parking);
}

void loop() {
  //Start Scheduler
  sched.schedule();
}

