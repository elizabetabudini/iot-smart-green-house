#include "SerialBluetoothDoor.h"

MsgService msgServiceB(TXB,RXB);
String BufferBluetooth;

//Metodo che inizializza SerialDoor
void SerialBluetoothDoor::init(int period) {
  Task::init(period);
  msgServiceB.init();
}

//Metodo che viene eseguito ad ogni tick del task
void SerialBluetoothDoor::tick() {
  //Se legge un messaggio lo mette in content
  if (msgServiceB.isMsgAvailable()) {
    Msg* msg = msgServiceB.receiveMsg();
    const String& content = msg->getContent();
    BufferBluetooth=content;
    //Eliminazione messaggio
    content = "";
    delete msg;
  }
}

