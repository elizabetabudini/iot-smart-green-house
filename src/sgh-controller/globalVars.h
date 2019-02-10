#ifndef __GLOBALVARS__
#define __GLOBALVARS__




//library used to define global variables. Both variables are external and defined in .ino file.

enum distanza{
  VICINO, //Auto mode
  LONTANO //Manual mode
};



  //global bar used to comunicate between tasks
  extern distanza statoDistanza;

#endif
