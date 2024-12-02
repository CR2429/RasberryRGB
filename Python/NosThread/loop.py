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
        
        #clic gauche
        if valeur_detect < SEUIL_GAUCHE and data["on/off"]: 
            print("clic gauche")
            changeColor("left")
            
            while valeur_detect < SEUIL_GAUCHE:
                with data["adc_lock"]:
                    valeur_detect = adc.analogRead(0)
                time.sleep(0.2)
                
            time.sleep(0.5)
        
        #clic droit
        if valeur_detect > SEUIL_DROIT and data["on/off"]: 
            print("clic droit")
            changeColor("right")
            
            while valeur_detect > SEUIL_GAUCHE:
                with data["adc_lock"]:
                    valeur_detect = adc.analogRead(0)
                time.sleep(0.2)
                
            time.sleep(0.5)
        
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
        

        #clic bas
        if valeur_detect < SEUIL_BAS and data["on/off"]: 
            print("clic bas")
            changeMode("down")
            
            while valeur_detect < SEUIL_BAS:
                with data["adc_lock"]:
                    valeur_detect = adc.analogRead(1)
                time.sleep(0.2)
                
            time.sleep(0.5)
        
        #clic haut
        if valeur_detect > SEUIL_HAUT and data["on/off"]: 
            print("clic haut")
            changeMode("up")
            
            while valeur_detect > SEUIL_HAUT:
                with data["adc_lock"]:
                    valeur_detect = adc.analogRead(1)
                time.sleep(0.2)
                
            time.sleep(0.5)
        
        #temps de recharge
        time.sleep(0.2)

def loopZ():
    """
    thread qui gere le bouton du joystick (on/off)
    """
    #valeur
    first_start = True
    GPIO.setmode(GPIO.BCM)
    
    while True:
        #On attend que le bouton est du joystick est appuyer
        data["bouton_alim"].wait_for_press()
        
        #On execute le code qu'il faut executer
        #changer on off
        if data["on/off"] :
            data["on/off"] = False
            print("Eteint")
            
            #bas eteindre la led
            data["led_alim"].off()
            data["led"].off()
        else :
            data["on/off"] = True
            print("Allumer")
            
            #je demarre la led RGP aussi
            data["led_alim"].on()
            setColor(*data["current_color"])
            
        
        #raffraichissemnt    
        time.sleep(0.2)
