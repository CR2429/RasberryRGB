import threading
from NosThread.loop import *
import RPi.GPIO as GPIO
from config import config_data
from my_http_request_handler import MyHttpRequestHandler
from http.server import HTTPServer
import ssl

#setup pin GPIO
GPIO.cleanup()
GPIO.setup(4, GPIO.IN) #config de la pin 7 (GPIO4) pour recevoir un signal d'un bouton
GPIO.setup(22, GPIO.OUT) #config de la pin 15 (GPIO22) pour envoyer un signal on/off

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
        
        #serveur get/post
        handler = MyHttpRequestHandler
        serveur = HTTPServer(('localhost', 4443), handler)
        serveur.socket = ssl.wrap_socket(serveur.socket, keyfile='cle.pem', certfile='certificat.pem', server_side=True)
        serveur.serve_forever()
        
        print('Serveur ouvert')
        
    except KeyboardInterrupt:
        destroy()
        print('Fermeture du serveur')