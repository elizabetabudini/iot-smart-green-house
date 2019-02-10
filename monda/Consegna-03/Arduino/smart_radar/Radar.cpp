#include "Radar.h"
#include "led.h"

#define MED_DIST 70
#define MIN_DIST 10
#define OMEGA 1
#define STARTING_POS 90
#define MAX_SERVO 180
#define MIN_SERVO 0

//Variabile globale esterna
extern String Buffer;

//Costruttore di Radar
Radar::Radar(int echo, int trigger, int ledpin, int servopin) {
  this->echo = echo;
  this->trigger = trigger;
  this->ledpin = ledpin;
  this->servopin = servopin;
  pos = STARTING_POS;
}

//Metodo che inizializza Radar
void Radar::init(int period) {
  Task::init(period);
  led = new Led(ledpin);
  servo.attach(servopin);
  //Impostiamo il servomotore a 90 gradi
  servo.write(pos);
  sonar = new Sonar(echo, trigger);
  state = IDLEE;
}

//Metodo che viene richiamato ad ogni tick del task
void Radar::tick() {
  switch (state) {
    case IDLEE:
      //Se Arduino legge dalla seriale ON
      if (Buffer == "ON") {
        //Passiamo allo stato di SCANNING e accendiamo il led
        state = SCANNING;
        digitalWrite(ledpin, HIGH);
        Buffer = "";
      }
      break;
    case SCANNING:
      //Leggiamo la distanza tramite il sensore di prossimità
      a = sonar->getDistance();
      //Se Arduino non legge dalla seriale OFF
      if (Buffer != "OFF") {
        if (pos == MAX_SERVO) {
          ok = 1;
        }
        //Se il servomotore ha un angolo minore di 190 gradi e sta aumentando
        if (pos <= MAX_SERVO and ok == 0) {
          scanning('+');
        }
        //Se il servomotore ha un angolo maggiore di 10 gradi e sta diminuendo
        else if (pos >= MIN_SERVO and ok == 1) {
          scanning('-');
        }
        if (pos == MIN_SERVO) {
          ok = 0;
        }
      }
      //Se Arduino ha letto OFF
      else {
        off();
      }
      break;
    case TRACKING:
      if (Buffer != "OFF") {
        //Continuiamo a leggere la distanza ma fermiamo il servomotore
        a = sonar->getDistance();
        //Mandiamo in seriale la distanza e la posizione
        Serial.print(pos);
        Serial.print(" ");
        Serial.println(a);
        //Se Arduino legge UNTRACK dalla seriale
        if (Buffer == "UNTRACK") {
          //Ritorniamo nello state di SCANNING
          state = SCANNING;
          Buffer = "";
        }
      } else {
        off();
      }
  }
}

//Metodo che sposta il servomotore e manda in Seriale ciò che ha ottenuto.
void Radar::scanning(char m) {
  servo.write(pos);
  Serial.print(pos);
  Serial.print(" ");
  Serial.println(a);
  if (m == '+') {
    pos += OMEGA;
  } else {
    pos -= OMEGA;
  }
  //Se Arduino legge da seriale TRACK
  if (Buffer == "TRACK") {
    //Passa allo stato di TRACKING
    state = TRACKING;
    Buffer = "";
  }
}

//Metodo che viene eseguito quando Arduino legge OFF dalla seriale, ritornando allo stato di IDLEE.
void Radar::off() {
  Buffer = "";
  digitalWrite(ledpin, LOW);
  pos = STARTING_POS;
  servo.write(pos); 
  state = IDLEE;
}

