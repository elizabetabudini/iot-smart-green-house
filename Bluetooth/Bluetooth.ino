// HC-06 with Arduino
// Giuseppe Caccavale
// www.giuseppecaccavale.it
#include <SoftwareSerial.h>

//rx e tx sono invertiti nella funzione
int rxPin = 3;
int txPin = 2;
SoftwareSerial bluetooth(txPin, rxPin);

String message; //string that stores the incoming message

void setup()
{
  Serial.begin(9600); //set baud rate
  bluetooth.begin(9600); //set baud rate
}

void loop()
{
  while(bluetooth.available()){
    message+=char(bluetooth.read());
  }
  if(!bluetooth.available())
  {
    if(message!="")
    {//if data is available
      Serial.println(message); //show the data
      message=""; //clear the data
    }
  }
  delay(5000); //delay
}
