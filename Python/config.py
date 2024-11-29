from gpiozero import Button, RGBLED
import threading
from ADCDevice import ADS7830

#Se fichier contient TOUT les valeur mutable qui pourra etre modifier peut importe le fichier
config_data = {
    "bouton": Button(4),  # Le bouton du joystick qui est sur la pin 7 = GPIO4
    "adc": ADS7830(),  # Cette classe contrôle la puce ADC
    "adc_lock": threading.Lock(),  # Verrou pour éviter les conflits entre threads dans la lecture de la puce ADC
    "led": RGBLED(red=17, green=18, blue=27, active_high=True),  # Définition des pins pour RGB LED
    "color_list": [
        (255, 0, 0),  # Red 1
        (0, 255, 0),  # Green 2
        (0, 0, 255),  # Blue 3
        (255, 255, 0),  # Yellow 4
        (0, 255, 255),  # Cyan 5
        (255, 0, 255),  # Magenta 6
        (255, 165, 0),  # Orange 7
        (128, 0, 128),  # Purple 8
        (255, 255, 255),  # White 9
        (0, 0, 0),  # Off (black) 10
        (255, 105, 180),  # Hot Pink 11
        (0, 128, 128),  # Teal 12
        (34, 139, 34),  # Forest Green 13
        (255, 99, 71),  # Tomato Red 14
        (148, 0, 211),  # Dark Violet 15
        (255, 222, 173),  # Navajo White 16
    ],
    "mode_active": False,
    "mode_thread": None,
    "current_index": 0,
    "current_color": (255, 0, 0),  # Initialisation à la première couleur (Rouge)
    "VALEUR_CENTRAL_X": 0,
    "VALEUR_CENTRAL_Y": 0,
    "SEUIL_HAUT": 0,
    "SEUIL_BAS": 0,
    "SEUIL_GAUCHE": 0,
    "SEUIL_DROIT": 0,
}