from gpiozero import Button, RGBLED
import time
from ADCDevice import *
import threading
from NosThread.loop import *

bouton = Button(4) #le bouton du joystick qui es sur la pin 7 = GPIO4
adc = ADS7830() #Ceci est la class qui controle ma puce ADC
adc_lock = threading.Lock() #ceci est pour eviter les conflit entre les threads dans la lecture de la puce ADC
led = RGBLED(red=17, green=18, blue=27, active_high=True) # define the pins for R:GPIO17,G:GPIO18,B:GPIO27

color_list = [
    (255, 0, 0),  # Red 0
    (0, 255, 0),  # Green 1
    (0, 0, 255),  # Blue 2
    (255, 255, 0),  # Yellow 3
    (0, 255, 255),  # Cyan 4
    (255, 0, 255),  # Magenta 5
    (255, 165, 0),  # Orange 6
    (128, 0, 128),  # Purple 7
    (255, 255, 255),  # White 8
    (0, 0, 0),  # Off (black) 9
    (255, 105, 180),  # Hot Pink 10
    (0, 128, 128),  # Teal 11 
    (34, 139, 34),  # Forest Green 12
    (255, 99, 71),  # Tomato Red 13
    (148, 0, 211),  # Dark Violet 14 
    (255, 222, 173),  # Navajo White 15
]
mode_active = False
mode_thread = None
# Current color index
current_index = 0
current_color = color_list[0]
#Valeur de calibrage
VALEUR_CENTRAL_X = 0
VALEUR_CENTRAL_Y = 0
SEUIL_HAUT = 0
SEUIL_BAS = 0
SEUIL_GAUCHE = 0
SEUIL_DROIT = 0


def destroy():
    """
    Gere l'arret des processus de la puce et du joystick
    """
    adc.close()
    bouton.close()
    led.close()
    
    
##demarrage des threads
if __name__ == '__main__':
    #calibrage
    print('Demarrage du serveur en cours')
    print('Calibration en cours')
    VALEUR_CENTRAL_X = adc.analogRead(2)
    print(f'VALEUR_CENTRAL_X = {VALEUR_CENTRAL_X}')
    VALEUR_CENTRAL_Y = adc.analogRead(7)
    print(f'VALEUR_CENTRAL_Y = {VALEUR_CENTRAL_Y}')
    SEUIL_HAUT = VALEUR_CENTRAL_X + 80
    SEUIL_BAS = VALEUR_CENTRAL_X - 80
    SEUIL_DROIT = VALEUR_CENTRAL_Y + 80
    SEUIL_GAUCHE = VALEUR_CENTRAL_Y - 80
    print('Calibrage terminer')
    
    #thread
    try:
        threadX = threading.Thread(target=loopX(SEUIL_GAUCHE,SEUIL_DROIT,adc,adc_lock))
        threadY = threading.Thread(target=loopY(SEUIL_HAUT,SEUIL_BAS,adc,adc_lock))
        threadZ = threading.Thread(target=loopZ(bouton))
        
        threadX.start()
        threadY.start()
        threadZ.start()
        print('Serveur ouvert')
        
    except KeyboardInterrupt:
        destroy()
        print('Fermeture du serveur')