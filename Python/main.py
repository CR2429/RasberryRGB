from gpiozero import Button, RGBLED
import time
from ADCDevice import *
import threading
from NosThread.loop import *
import RPi.GPIO as GPIO
from config import config_data

GPIO.cleanup()

def destroy():
    """
    Gere l'arret des processus de la puce et du joystick
    """
    config_data["adc"].close()
    config_data["bouton"].close()
    config_data["led"].close()
    GPIO.cleanup()
    
    
##demarrage des threads
if __name__ == '__main__':
    #calibrage
    print('Demarrage du serveur en cours')
    print('Calibration en cours')
    config_data['VALEUR_CENTRAL_X'] = config_data["adc"].analogRead(2)
    print(f'VALEUR_CENTRAL_X = {config_data["VALEUR_CENTRAL_X"]}')
    config_data['VALEUR_CENTRAL_Y'] = config_data["adc"].analogRead(7)
    print(f'VALEUR_CENTRAL_Y = {config_data["VALEUR_CENTRAL_Y"]}')
    config_data['SEUIL_HAUT'] = config_data['VALEUR_CENTRAL_X'] + 80
    config_data['SEUIL_BAS'] = config_data['VALEUR_CENTRAL_X'] - 80
    config_data['SEUIL_DROIT'] = config_data['VALEUR_CENTRAL_Y'] + 80
    config_data['SEUIL_GAUCHE'] = config_data['VALEUR_CENTRAL_Y'] - 80
    print('Calibrage terminer')
    
    #thread
    try:
        threadX = threading.Thread(target=loopX)
        threadY = threading.Thread(target=loopY)
        threadZ = threading.Thread(target=loopZ)
        
        threadX.start()
        threadY.start()
        threadZ.start()
        print('Serveur ouvert')
        
    except KeyboardInterrupt:
        destroy()
        print('Fermeture du serveur')