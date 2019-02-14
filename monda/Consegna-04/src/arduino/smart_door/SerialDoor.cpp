#include "SerialDoor.h"

//Variabile globale
String Buffer;

//Metodo che inizializza SerialDoor
void SerialDoor::init(int period) {
  Task::init(period);
}

//Metodo che viene eseguito ad ogni tick del task
void SerialDoor::tick() {
  //Se legge un messaggio lo mette in content
  if (Serial.available()>0) {
    Buffer=Serial.readString();
  }
}

