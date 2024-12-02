import threading
from NosThread.loop import *
import RPi.GPIO as GPIO
from config import config_data
from my_http_request_handler import MyHttpRequestHandler
from http.server import HTTPServer
import ssl
from testmain import TestGPIOFunctions as Test 
import unittest

#setup pin GPIO
GPIO.cleanup()


def calibrage_adc():
    """
    Calibre les valeurs centrales de l'ADC en prenant plusieurs lectures moyennes.
    """
    print("Début du calibrage...")
    num_samples = 10
    valeurs_x = []
    valeurs_y = []

    for _ in range(num_samples):
        valeurs_x.append(config_data["adc"].analogRead(0))
        valeurs_y.append(config_data["adc"].analogRead(1))
        time.sleep(0.1)  # Délai pour éviter des lectures instantanées

    config_data['VALEUR_CENTRAL_X'] = sum(valeurs_x) // len(valeurs_x)
    config_data['VALEUR_CENTRAL_Y'] = sum(valeurs_y) // len(valeurs_y)

    seuil_offset = 80
    config_data['SEUIL_HAUT'] = config_data['VALEUR_CENTRAL_Y'] + seuil_offset
    config_data['SEUIL_BAS'] = config_data['VALEUR_CENTRAL_Y'] - seuil_offset
    config_data['SEUIL_DROIT'] = config_data['VALEUR_CENTRAL_X'] + seuil_offset
    config_data['SEUIL_GAUCHE'] = config_data['VALEUR_CENTRAL_X'] - seuil_offset

    print(f"Calibrage terminé : VALEUR_CENTRAL_X = {config_data['VALEUR_CENTRAL_X']}, "
          f"VALEUR_CENTRAL_Y = {config_data['VALEUR_CENTRAL_Y']}")

def runTests():
    while True:
        if data["joystick_test"] != "none":
            #run les tests
            test = unittest.TestLoader().loadTestsFromTestCase(Test)
            result = unittest.TextTestRunner().run(test)
            
            #log
            print(result)
            #with open("test_output.log", "w", encoding="utf-8") as file:
            #    runner = unittest.TextTestRunner(stream=file, verbosity=2)
            #    runner.run(test)
 
        time.sleep(1)

def destroy():
    """
    Gere l'arret des processus de la puce et du joystick
    """
    config_data["adc"].close()
    config_data["led"].close()
    config_data["led-alim"].close()
    config_data["bouton-alim"].close()
    GPIO.cleanup()
    
    
##demarrage des threads
if __name__ == '__main__':
    #calibrage
    print('Demarrage du serveur en cours')
    calibrage_adc()
    
    #thread
    try:
        threadX = threading.Thread(target=loopX)
        threadY = threading.Thread(target=loopY)
        threadZ = threading.Thread(target=loopZ)
        testThread = threading.Thread(target=runTests, daemon=True)
        
        threadX.start()
        threadY.start()
        threadZ.start()
        
        time.sleep(0.01)
        testThread.start()
        
        #serveur get/post
        handler = MyHttpRequestHandler
        serveur = HTTPServer(('localhost', 4443), handler)
        serveur.socket = ssl.wrap_socket(serveur.socket, keyfile='cle.pem', certfile='certificat.pem', server_side=True)
        serveur.serve_forever()
        
        print('Serveur ouvert')
        
    except KeyboardInterrupt:
        destroy()
        print('Fermeture du serveur')