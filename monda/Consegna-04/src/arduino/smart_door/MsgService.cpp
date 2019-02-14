#include "Arduino.h"
#include "MsgService.h"


MsgService::MsgService(int rxPin, int txPin){
  channel = new SoftwareSerial(rxPin, txPin);
}

void MsgService::init(){
  content.reserve(256);
  channel->begin(9600);
}

bool MsgService::sendMsg(Msg msg){
  channel->println(msg.getContent());  
}

bool MsgService::isMsgAvailable(){
  return channel->available();
}

Msg* MsgService::receiveMsg(){
  if (channel->available()){    
    content="";
    while (channel->available()) {
      content += (char)channel->read();      
    }
    return new Msg(content);
  } else {
    return NULL;  
  }
}




