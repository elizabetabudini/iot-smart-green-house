#ifndef __GLOBALVARS__
#define __GLOBALVARS__

//library used to define global variables. Both variables are external and defined in .ino file.

//Enum used to represent the globalstate nof the system:
enum statiGlobali{
  MANUAL, //System is sleeping and wait to detecting a person
  AUTOMATIC //Person detected, wait the person to be close enough
  }; //maintenace: the coffe is ended and must be recharged from java inteerface.

  //global state var
  extern statiGlobali globalState;
  //number of coffee left
  extern int portata;

#endif
