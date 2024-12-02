import time
from NosThread.led import changeColor, changeMode, setColor
from config import config_data as data
import RPi.GPIO as GPIO

def loopX():
    """
    thread qui gere l'axe X du joystick (couleur)
    """
    #recuperer les valeur
    adc = data['adc']
    SEUIL_GAUCHE = data['SEUIL_GAUCHE']
    SEUIL_DROIT = data['SEUIL_DROIT']
    
    while True:
        with data["adc_lock"]:
            valeur_detect = adc.analogRead(0)
        
        #regarde si la led est allumer ou pas  
        if data["on/off"]: 
            #clic gauche
            if valeur_detect < SEUIL_GAUCHE: 
                print("clic gauche")
                changeColor("left")
                
                while valeur_detect < SEUIL_GAUCHE:
                    with data["adc_lock"]:
                        valeur_detect = adc.analogRead(0)
                    time.sleep(0.2)
                    
                time.sleep(0.5)
            
            #clic droit
            if valeur_detect > SEUIL_DROIT: 
                print("clic droit")
                changeColor("right")
                
                while valeur_detect > SEUIL_GAUCHE:
                    with data["adc_lock"]:
                        valeur_detect = adc.analogRead(0)
                    time.sleep(0.2)
                    
                time.sleep(0.5)
        else: #sa c'est en cas la led est etteinte
            print("La led rgb est eteinte, vous ne pouvez pas gerer ses reglages")
        
        #temps de recharge
        time.sleep(0.2)

def loopY():
    """
    thread qui gere l'axe Y du joystick (mode)
    """
    #recuperer les valeur
    adc = data['adc']
    SEUIL_HAUT = data['SEUIL_HAUT']
    SEUIL_BAS = data['SEUIL_BAS']
    
    while True:
        with data["adc_lock"]:
            valeur_detect = adc.analogRead(1)
        
        #regarde si la led est allumer ou pas  
        if data["on/off"]: 
        
            #clic bas
            if valeur_detect < SEUIL_BAS: 
                print("clic bas")
                changeMode("down")
                
                while valeur_detect < SEUIL_BAS:
                    with data["adc_lock"]:
                        valeur_detect = adc.analogRead(1)
                    time.sleep(0.2)
                    
                time.sleep(0.5)
            
            #clic haut
            if valeur_detect > SEUIL_HAUT: 
                print("clic haut")
                changeMode("up")
                
                while valeur_detect > SEUIL_HAUT:
                    with data["adc_lock"]:
                        valeur_detect = adc.analogRead(1)
                    time.sleep(0.2)
                    
                time.sleep(0.5)
        else: #sa c'est en cas la led est etteinte
            print("La led rgb est eteinte, vous ne pouvez pas gerer ses reglages")
        
        #temps de recharge
        time.sleep(0.2)

def loopZ():
    """
    thread qui gere le bouton du joystick (on/off)
    """
    #valeur
    first_start = True
    
    while True:
        bouton = GPIO.input(22)
        
        if bouton:
            #changer on off
            if data["on/off"] :
                data["on/off"] = False
                print("Eteint")
                
                #bas eteindre la led
                GPIO.output(4, 0)
                setColor(0,0,0)
            else :
                data["on/off"] = True
                print("Allumer")
                
                #je demarre la led RGP aussi
                GPIO.output(4, 1)
                setColor(*data["current_color"])
                
            #boucle pour eviter les erreurs
            while bouton:
                bouton = GPIO.input(22)
                time.sleep(0.2)
            
        time.sleep(0.2)
