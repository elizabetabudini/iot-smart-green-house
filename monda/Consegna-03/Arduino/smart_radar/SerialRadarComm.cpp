#include "SerialRadarComm.h"
#include "MsgService.h"

//Variabile globale
String Buffer;

//Metodo che inizializza SerialRadarComm
void SerialRadarComm::init(int period) {
  Task::init(period);
}

//Metodo che viene eseguito ad ogni tick del task
void SerialRadarComm::tick() {
  //Se legge un messaggio lo mette in content
  if (MsgService.isMsgAvailable()) {
    Msg* msg = MsgService.receiveMsg();
    const String& content = msg->getContent();
    Serial.println(content);
    //Se il messaggio è uno di quelli che vogliamo lo inseriamo in Buffer
    if (content == "ON" or content == "OFF" or content == "TRACK" or content == "UNTRACK") {
      Buffer = content;
    }
    //Eliminazione messaggio
    content = "";
    delete msg;
  }
}

