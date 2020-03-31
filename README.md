# iot-smart-green-house
The aim is to create an integrated embedded system that represents a simplified version of a smart greenhouse. The task of the smart greenhouse is the automated irrigation (of a certain soil or plant) by implementing a strategy that takes into account the perceived humidity, with the ability to control and intervene manually via mobile app.
The system consists of 5 parts (sub-systems):
- GreenHouse Server (PC): it contains the logic that defines and implements the irrigation strategy.
- GreenHouse Controller (Arduino): allows you to control the opening and closing of the sprinklers (water pumps), i.e. the amount of water dispensed per minute.
- GreenHouse Edge (ESP): allows you to feel the moisture in the soil
- GreenHouse Mobile App (Android): allows you to manually control the greenhouse
- GreenHouse Front End (PC): Front-end for data viewing/observation/analysis

More documentation in /doc/
