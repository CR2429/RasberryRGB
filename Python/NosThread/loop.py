import time
from NosThread.led import changeColor, changeMode

def loopX(SEUIL_GAUCHE,SEUIL_DROIT,adc,adc_lock):
    """
    thread qui gere l'axe X du joystick (couleur)
    """
    while True:
        with adc_lock:
            valeur_detect = adc.analogRead(2)
        
        #clic gauche
        if valeur_detect < SEUIL_GAUCHE: 
            print("clic gauche")
            changeColor("left")
            
            while valeur_detect < SEUIL_GAUCHE:
                with adc_lock:
                    valeur_detect = adc.analogRead(2)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #clic droit
        if valeur_detect > SEUIL_DROIT: 
            print("clic droit")
            changeColor("right")
            
            while valeur_detect > SEUIL_GAUCHE:
                with adc_lock:
                    valeur_detect = adc.analogRead(2)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #temps de recharge
        time.sleep(0.2)

def loopY(SEUIL_HAUT,SEUIL_BAS,adc,adc_lock):
    """
    thread qui gere l'axe Y du joystick (mode)
    """
    while True:
        with adc_lock:
            valeur_detect = adc.analogRead(7)
        
        #clic bas
        if valeur_detect < SEUIL_BAS: 
            print("clic bas")
            changeMode("down")
            
            while valeur_detect < SEUIL_BAS:
                with adc_lock:
                    valeur_detect = adc.analogRead(7)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #clic haut
        if valeur_detect > SEUIL_HAUT: 
            print("clic haut")
            changeMode("up")
            
            while valeur_detect > SEUIL_HAUT:
                with adc_lock:
                    valeur_detect = adc.analogRead(7)
                time.sleep(0.2)
                
            time.sleep(1.5)
        
        #temps de recharge
        time.sleep(0.2)

def loopZ(bouton):
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
