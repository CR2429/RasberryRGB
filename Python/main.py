import RPi.GPIO as GPIO
from gpiozero import Button
import time
from ADCDevice import *
import threading

bouton = Button(4) #le bouton du joystick qui es sur la pin 7 = GPIO4
adc = ADS7830() #Ceci est la class qui controle ma puce ADC


def loopX():
    """
    thread qui gere l'axe X du joystick (couleur)
    """
    while True:
        print(f'valeur x : {adc.analogRead(0)}')
        time.sleep(1)

def loopY():
    """
    thread qui gere l'axe Y du joystick (mode)
    """
    while True:
        print(f'valeur y : {adc.analogRead(1)}')
        time.sleep(1)

def loopZ():
    """
    thread qui gere le bouton du joystick (on/off)
    """
    while True:
        print(f'valeur z : {not bouton.value}')
        time.sleep(1)

def destroy():
    """
    Gere l'arret des processus de la puce et du joystick
    """
    adc.close()
    bouton.close()
    
    
##demarrage des threads
if __name__ == '__main__':
    print('Demarrage du serveur en cours')
    try:
        threadX = threading.Thread(target=loopX)
        threadY = threading.Thread(target=loopY)
        threadZ = threading.Thread(target=loopZ)
        
        threadX.start()
        threadY.start()
        threadZ.start()
        
    except KeyboardInterrupt:
        destroy()
        print('Fermeture du serveur')