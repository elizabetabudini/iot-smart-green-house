//Librerie incluse
#include "Door.h"
#include "led.h"
#include "SoftwareSerial.h"
#include <ctype.h>
#include <Wire.h>

//Define
#define MIN_DIST 0.30
#define MIN_SEC 50
#define MAX_DELAY 150
#define START_POS 20
#define END_POS 200
#define START_PERC 0
#define START_INT 0
#define END_PERC 100
#define END_INT 255
#define WAIT_DOOR 60

//Variabili esterne
extern String Buffer;
extern String BufferBluetooth;
extern MsgService msgServiceB;

//Costruttore di Door
Door::Door(int echo, int trigger, int ledpinr, int servopin, int buttonpin, int pirpin) {
  //Imposto valori iniziali delle variabili
  this->echo = echo;
  this->trigger = trigger;
  this->ledpinr = ledpinr;
  this->servopin = servopin;
  this->buttonpin = buttonpin;
  this->pirpin = pirpin;
  trovato = 0;
  timer = 0;
  istime = 0;
}

//Metodo che inizializza Door
void Door::init(int period) {
  //Inizializzo gli oggetti
  Task::init(period);
  temp = new TempSensor();
  ledr = new Led(ledpinr);
  servo.attach(servopin);
  //Impostiamo il servomotore a 90 gradi
  servo.write(START_POS);
  msgServiceB.init();
  sonar = new Sonar(echo, trigger);
  button = new ButtonImpl(buttonpin);
  pir = new Pir(pirpin);
  state = IDLEE;
  prevalue = 0;
}

//Metodo che viene richiamato ad ogni tick del task
void Door::tick() {
  switch (state) {

    //Se si trova nello stato iniziale aspetta la stringa "connesso" dall'app bluetooth
    case IDLEE:
      if (BufferBluetooth == "connesso") {
        state = WAIT;
        BufferBluetooth = "";
        msgServiceB.sendMsg(Msg("@ack@"));
      }
      break;

    //In questo stato il sonar legge la distanza
    case WAIT:
      //Leggo la distanza
      a = sonar->getDistance();
      //Se è minore di MIN_DIST
      if (a < MIN_DIST) {
        trovato = 1;
        timer++;
        //Se rimane per 3 secondi ad una distanza minore di MIN_DIST
        if (timer == MIN_SEC) {
          //Mando un messaggio all'app e a raspberry
          Serial.print("present@Qualcuno è rimasto davanti alla porta per ");
          Serial.print(MIN_SEC * WAIT_DOOR / 1000);
          Serial.println(" secondi");
          msgServiceB.sendMsg(Msg("@pres@"));
          //Cambio stato
          state = PRESENT;
        }

      } else {
        //Se non rimane per 3 secondi ad una distanza minore di MIN_DIST il timer si azzera.
        trovato = 0;
        timer = 0;
      }
      BufferBluetooth = "";
      break;

    //Stato di login e password
    case PRESENT:
      //Se il bottone non è premuto o l'utente non è uscito dall'app
      if (BufferBluetooth != "" and istime == 0) {
        Serial.print("account@");
        Serial.println(BufferBluetooth);
        BufferBluetooth = "";
        istime++;
      }
      //Se riceviamo un messaggio da raspberry
      if (Buffer != "" and BufferBluetooth != "exit" and istime == 1) {
        //Se raspberry ha inviato il numero "1"
        msgServiceB.sendMsg(Msg("@" + Buffer + "@"));
        if (Buffer == "logok") {
          istime++;
        } else {
          //Se ha inviato "0"
          istime = 0;
        }
        Buffer = "";
      }
      //Se l'utente ha fatto l'accesso correttamente
      if (istime == 2) {
        //Apre la porta
        servo.write(END_POS);
        trovato = 0;
        timer = 0;
        //Comunico all'app il cambio di stato
        state = PASSATO;
      }

      break;


    //Stato in cui entra in gioco il PIR
    case PASSATO:
      //Se non rileva alcuna presenza aumenta il timer
      if (!pir->movement()) {
        timer++;
      }
      //Se il timer scade o il bottone è premuto o l'utente esce dall'app
      if (timer == MAX_DELAY or button->isPressed() or BufferBluetooth == "exit") {
        exitDoor();
        Serial.println("exitpir@Il PIR non ha rilevato il movimento di qualcuno nella stanza");
      }
      //Se rileva movimento e il timer non è scaduto
      if (pir->movement() and timer < MAX_DELAY) {
        //Passaggio di stato e comunicazione all'app e raspberry
        state = VISTO;
        Serial.println("visto@Il PIR ha rilevato il movimento di qualcuno nella stanza.");
        msgServiceB.sendMsg(Msg("@visto@"));
      }
      break;

    //Stato in cui si può cambiare intensità e aggiornare temperatura
    case VISTO:
      //Legge la temperatura
      value = temp->readTemperature();
      if (prevalue != value) {
        prevalue = value;
        //La scrive in seriale per raspberry
        Serial.print("temp@");
        Serial.println(prevalue);
        //Mandiamo all'app il valore della temperatura
        msgServiceB.sendMsg(Msg("@" + String(value) + "@"));
      }
      value = "";
      //Se il bottone non è premuto o l'utente esce dall'app
      if (!button->isPressed() and BufferBluetooth != "exit" and BufferBluetooth != "") {
        //Nel caso in cui abbiamo mandato un valore numerico, facciamo la map
        n = map(BufferBluetooth.toInt(), START_PERC, END_PERC, START_INT, END_INT);
        //Settiamo intensità
        ledr->setIntensity(n);
        //Scriviamo in seriale per far sì che raspberry faccia il log
        Serial.print("int@");
        Serial.println(BufferBluetooth.toInt());
        BufferBluetooth = "";
      } else if (button->isPressed() or BufferBluetooth == "exit") {
        exitDoor();
        Serial.println("exit@L'utente ha effettuato l'uscita dall'applicazione, la porta verrà chiusa.");
      }
      break;
  }
}


void Door::exitDoor() {
  //Nel caso in cui il bottone sia premuto o l'utente esca dall'app, chiusura porta e comunicazione a raspberry e app
  BufferBluetooth = "";
  servo.write(START_POS);
  timer = 0;
  trovato = 0;
  istime = 0;
  state = IDLEE;
  ledr->switchOff();
  msgServiceB.sendMsg(Msg("@exit@"));
}

