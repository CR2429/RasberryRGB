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
        valeur_detect = adc.analogRead(0)
        
        #clic gauche
        if valeur_detect < 40: 
            print("clic gauche")
            
            while valeur_detect < 40:
                valeur_detect = adc.analogRead(0)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #clic droit
        if valeur_detect > 215: 
            print("clic droit")
            
            while valeur_detect > 215:
                valeur_detect = adc.analogRead(0)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #temps de recharge
        time.sleep(0.2)

def loopY():
    """
    thread qui gere l'axe Y du joystick (mode)
    """
    while True:
        valeur_detect = adc.analogRead(1)
        
        #clic bas
        if valeur_detect < 40: 
            print("clic bas")
            
            while valeur_detect < 40:
                valeur_detect = adc.analogRead(1)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #clic haut
        if valeur_detect > 215: 
            print("clic haut")
            
            while valeur_detect > 215:
                valeur_detect = adc.analogRead(1)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #temps de recharge
        time.sleep(0.2)

def loopZ():
    """
    thread qui gere le bouton du joystick (on/off)
    """
    allumer = False
    while True:
        bouton_joystick = bouton.value
        
        if bouton_joystick:
            #changer on off
            if allumer :
                allumer = False
                print("Etaint")
            else :
                allumer = True
                print("Allumer")
                
            #boucle pour eviter les erreurs
            while bouton_joystick:
                bouton_joystick = bouton.value
                time.sleep(0.2)
            
        time.sleep(0.2)

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