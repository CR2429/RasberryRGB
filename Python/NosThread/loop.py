import time
from NosThread.led import changeColor, changeMode, setColor, on_Off
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
            data["joystick_test"] = "left"
            changeColor("left")
            
            while valeur_detect < SEUIL_GAUCHE:
                with data["adc_lock"]:
                    valeur_detect = adc.analogRead(0)
                time.sleep(0.2)
                
            time.sleep(0.5)
        
        #clic droit
        if valeur_detect > SEUIL_DROIT and data["on/off"]: 
            print("clic droit")
            data["joystick_test"] = "right"
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
            data["joystick_test"] = "down"
            changeMode("down")
            
            while valeur_detect < SEUIL_BAS:
                with data["adc_lock"]:
                    valeur_detect = adc.analogRead(1)
                time.sleep(0.2)
                
            time.sleep(0.5)
        
        #clic haut
        if valeur_detect > SEUIL_HAUT and data["on/off"]: 
            print("clic haut")
            data["joystick_test"] = "up"
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
    data["led"].off()
    
    while True:
        #On attend que le bouton est du joystick est appuyer
        print("...")
        data["bouton-alim"].wait_for_press()
        print("Bouton appuyer")
        
        #On execute le code qu'il faut executer
        on_Off()
        
        #raffraichissemnt
        data["bouton-alim"].wait_for_release() # pour eviter les double click inutile    
        time.sleep(1)
