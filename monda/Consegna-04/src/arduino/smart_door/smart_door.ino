#include "Scheduler.h"
#include "SerialDoor.h"
#include "SerialBluetoothDoor.h"
#include "led.h"
#include "Door.h"

#define SERIAL 9600
#define LEDPINV 12
#define LEDPINR 6
#define ECHO 7
#define TRIGGER 8
#define BUTTONPIN 11
#define SERVOPIN 5
#define PIRPIN 4
#define WAITSCHED 60
#define WAITDOOR 60
#define WAITSERIAL 60
#define WAITSERIALB 60

Scheduler sched;
Light* ledv = new Led(LEDPINV);

void setup() {
  pinMode(LEDPINV, OUTPUT);
  pinMode(LEDPINR, OUTPUT);
  pinMode(ECHO, INPUT);
  pinMode(TRIGGER, OUTPUT);
  pinMode(SERVOPIN, OUTPUT);
  pinMode(PIRPIN, INPUT);
  pinMode(BUTTONPIN, INPUT);
  //servo.attach(SERVOPIN);
  //Facciamo partire la seriale
  Serial.begin(SERIAL);
  ledv->switchOn();
  sched.init(WAITSCHED);

  Task* door = new Door(ECHO, TRIGGER, LEDPINR, SERVOPIN, BUTTONPIN, PIRPIN);
  door->init(WAITDOOR);
  sched.addTask(door);

  Task* serial =  new SerialDoor();
  serial->init(WAITSERIAL);
  sched.addTask(serial);

  Task* serialbluetooth =  new SerialBluetoothDoor();
  serial->init(WAITSERIALB);
  sched.addTask(serialbluetooth);

}

void loop() {
  sched.schedule();
}
