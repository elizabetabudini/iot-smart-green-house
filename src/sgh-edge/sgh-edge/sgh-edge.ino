#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include "Potenziometro.h"
#define POTENTIOMETER_PIN A0

// Nome rete wifi
char* ssidName = "FLAVIETTOCAPO1";
// Password rete wifi 
char* pwd = "soldati98";
/* Indirizzo IP Ngrok da contattare */ 
char* address = "http://99a36b1a.ngrok.io/";
Potenziometro *potenziometro;

void setup() { 
  Serial.begin(115200);                                
  WiFi.begin(ssidName, pwd);
  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED) {  
    delay(500);
    Serial.print(".");
  } 
  Serial.print("Connected: \n local IP: "+WiFi.localIP());
  potenziometro = new Potenziometro("A0");
}

int sendData(String address, float value){  
   HTTPClient http;    
   http.begin(address + "/api/data");      
   http.addHeader("Content-Type", "application/json");     
   String msg = 
   String("{\"umidita\":") + String(value) + "}";
   int retCode = http.POST(msg);   
   http.end();  
   Serial.print(msg);
   // String payload = http.getString();  
   // Serial.println(payload);      
   return retCode;
}
   
void loop() { 
 if (WiFi.status()== WL_CONNECTED){   

   /* Legge il potenziomentro. Verrà letto un valore da 0 a 1023 */
   int value =  potenziometro->getValue();
   
   /* send data */
   Serial.print("sending "+String(value)+"...");    
   int code = sendData(address, value);

   /* log result */
   if (code == 200){
     Serial.println("ok");   
   } else {
     Serial.println("error");
   }
 } else { 
   Serial.println("Error in WiFi connection");   
 }
 
 delay(1000);  
 
}
