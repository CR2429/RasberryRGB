import time
from NosThread.led import changeColor, changeMode
from config import config_data

def loopX():
    """
    thread qui gere l'axe X du joystick (couleur)
    """
    #recuperer les valeur
    adc = config_data['adc']
    SEUIL_GAUCHE = config_data['SEUIL_GAUCHE']
    SEUIL_DROIT = config_data['SEUIL_DROIT']
    
    while True:
        with config_data["adc_lock"]:
            valeur_detect = adc.analogRead(2)
        
        #clic gauche
        if valeur_detect < SEUIL_GAUCHE: 
            print("clic gauche")
            changeColor("left")
            
            while valeur_detect < SEUIL_GAUCHE:
                with config_data["adc_lock"]:
                    valeur_detect = adc.analogRead(2)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #clic droit
        if valeur_detect > SEUIL_DROIT: 
            print("clic droit")
            changeColor("right")
            
            while valeur_detect > SEUIL_GAUCHE:
                with config_data["adc_lock"]:
                    valeur_detect = adc.analogRead(2)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #temps de recharge
        time.sleep(0.2)

def loopY():
    """
    thread qui gere l'axe Y du joystick (mode)
    """
    #recuperer les valeur
    adc = config_data['adc']
    SEUIL_HAUT = config_data['SEUIL_HAUT']
    SEUIL_BAS = config_data['SEUIL_BAS']
    
    while True:
        with config_data["adc_lock"]:
            valeur_detect = adc.analogRead(7)
        
        #clic bas
        if valeur_detect < SEUIL_BAS: 
            print("clic bas")
            changeMode("down")
            
            while valeur_detect < SEUIL_BAS:
                with config_data["adc_lock"]:
                    valeur_detect = adc.analogRead(7)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #clic haut
        if valeur_detect > SEUIL_HAUT: 
            print("clic haut")
            changeMode("up")
            
            while valeur_detect > SEUIL_HAUT:
                with config_data["adc_lock"]:
                    valeur_detect = adc.analogRead(7)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #temps de recharge
        time.sleep(0.2)

def loopZ():
    """
    thread qui gere le bouton du joystick (on/off)
    """
    #valeur
    bouton = config_data['bouton']
    allumer = False
    
    while True:
        bouton_joystick = bouton.value
        
        if bouton_joystick:
            #changer on off
            if allumer : #TODO : ah voir comment on considere la led allumer ou eteinte
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
