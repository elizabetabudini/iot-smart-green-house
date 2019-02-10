#include "Radar.h"
#include "Scheduler.h"
#include "SerialRadarComm.h"

#define LED_PIN 8
#define ECHO 13
#define TRIGGER 12
#define SERVO_PIN 5
#define WAITSCHED 20
#define WAITRADAR 200
#define WAITSERIAL 20

//Variabili globali
Scheduler sched;

//Metodo setup
void setup() {
  pinMode(LED_PIN, OUTPUT);
  pinMode(ECHO, INPUT);
  pinMode(TRIGGER, OUTPUT);
  pinMode(SERVO_PIN, OUTPUT);
  //Facciamo partire la seriale
  Serial.begin(9600);

  //Facciamo partire lo scheduler
  sched.init(WAITSCHED);

  //Creiamo task Radar, lo facciamo partire e lo aggiungiamo allo scheduler
  Task* radar = new Radar(ECHO, TRIGGER, LED_PIN, SERVO_PIN);
  radar->init(WAITRADAR);
  sched.addTask(radar);

  //Creiamo task SerialRadarComm, lo facciamo partire e lo aggiungiamo allo scheduler
  Task* serial =  new SerialRadarComm();
  serial->init(WAITSERIAL);
  sched.addTask(serial);
}

//Metodo loop
void loop() {
  sched.schedule();
}

