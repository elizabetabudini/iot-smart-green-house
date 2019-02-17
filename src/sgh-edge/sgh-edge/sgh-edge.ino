#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#define POTENTIOMETER_PIN A0

// Nome rete wifi
char* ssidName = "";
// Password rete wifi 
char* pwd = "";
/* Indirizzo IP Ngrok da contattare */ 
char* address = "http://496ccad1.ngrok.io";

void setup() { 
  Serial.begin(115200);                                
  WiFi.begin(ssidName, pwd);
  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED) {  
    delay(500);
    Serial.print(".");
  } 
  Serial.println("Connected: \n local IP: "+WiFi.localIP());
}

int sendData(String address, float value, String place){  
   HTTPClient http;    
   http.begin(address + "/api/data");      
   http.addHeader("Content-Type", "application/json");     
   String msg = 
    String("{ \"value\": ") + String(value) + 
    ", \"place\": \"" + place +"\" }";
   int retCode = http.POST(msg);   
   http.end();  
      
   // String payload = http.getString();  
   // Serial.println(payload);      
   return retCode;
}
   
void loop() { 
 if (WiFi.status()== WL_CONNECTED){   

   /* Legge il potenziomentro. Verrà letto un valore da 0 a 1023 */
   float value = (float) analogRead(POTENTIOMETER_PIN);
   
   /* send data */
   Serial.print("sending "+String(value)+"...");    
   int code = sendData(address, value, "home");

   /* log result */
   if (code == 200){
     Serial.println("ok");   
   } else {
     Serial.println("error");
   }
 } else { 
   Serial.println("Error in WiFi connection");   
 }
 
 delay(5000);  
 
}
