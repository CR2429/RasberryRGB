import threading
import time
from config import config_data as data

# Configure la couleur de la LED avec les valeurs spécifiées
def setColor(r_val,g_val,b_val):
    """
    Méthode qui applique la couleur de la led
    """ 
    data["led"].red=r_val/255 
    data["led"].green = g_val/255
    data["led"].blue = b_val/255 


def on_Off():
    """
    Change l'état on/off et met à jour les la LED RGB et la LED jaune.
    """
    if data["on/off"]:
        data["on/off"] = False
        print("Éteint")
        
        # Éteindre les LED
        data["led-alim"].off()
        data["led"].off()
    else:
        data["on/off"] = True
        print("Allumé")
        
        # Allumer les LED et définir la couleur RGB
        data["led-alim"].on()
        setColor(*data["current_color"])

def changeColor(direction):
    """
    Méthode qui permet de changer la couleur de la del à partir du joystick
    """ 
    color_list = data["color_list"]
    
    if direction == "right":
        data["current_index"] += 1
    elif direction == "left":
        data["current_index"] -= 1

    if data["current_index"] >= len(color_list):
        data["current_index"] = 0
    elif data["current_index"] < 0:
        data["current_index"] = len(color_list) - 1

    # Définit la nouvelle couleur
    data["current_color"] = color_list[data["current_index"]]  
    setColor(*data["current_color"])  
    print(f'Changed to color {data["current_index"]}: r={data["current_color"][0]}, g={data["current_color"][1]}, b={data["current_color"][2]}')

def changeMode(direction):
    """
    Méthode qui permet de changer le mode de la del à partir du joystick
    """ 
    modes = ["vague", "flash", "full"]  
    
    if direction == 'up':
        data["current_index"] = (data["current_index"] + 1) % len(modes)  
    elif direction == 'down':
        data["current_index"] = (data["current_index"] - 1) % len(modes) 
    
    print(f'Current mode: {modes[data["current_index"]]}')
     
     
    if data["mode_thread"] is not None and data["mode_thread"].is_alive():
        data["mode_active"] = False 
        data["mode_thread"].join()   # Attend la fin du thread en cours

    
    data["mode_active"] = True
    if modes[data["current_index"]] == "vague":
        data["mode_thread"] = threading.Thread(target=vague, name="vague")
        data["mode_thread"].start()
    elif modes[data["current_index"]] == "flash":
        data["mode_thread"] = threading.Thread(target=flash, name="flash")
        data["mode_thread"].start()
    elif modes[data["current_index"]] == "full":
        full(*data["current_color"])
    

def changeModeClient(mode):
    """
    Méthode qui permet de changer le mode de la del à partir du client
    """    
    if data["mode_thread"] is not None and data["mode_thread"].is_alive():
        data["mode_active"] = False 
        data["mode_thread"].join()  
    
    data["mode_active"] = True

    current_mode = mode

    if current_mode == "vague":
        data["mode_thread"] = threading.Thread(target=vague, name="vague")
        data["mode_thread"].start()
    elif current_mode == "flash":
        data["mode_thread"] = threading.Thread(target=flash, name="flash")
        data["mode_thread"].start()
    elif current_mode == "full":
        full(*data["current_color"]) 
    
    if current_mode != "full":
        data["mode_thread"].start()


def vague():
    """
    Crée un effet de vague, où la LED change d'intensité progressivement.
    """    
    while data["mode_active"]:  
        if data["on/off"]:
            r, g, b = data['current_color'] 
            for intensity in range(0, 256):  
                setColor(intensity * r // 255, intensity * g // 255, intensity * b // 255) 
                time.sleep(0.01)
            for intensity in range(255, -1, -1): 
                setColor(intensity * r // 255, intensity * g // 255, intensity * b // 255) 
                time.sleep(0.01)  
                


def flash(interval=1):
    """
    Crée un effet de clignotement où la LED s'allume et s'éteint à un intervalle donné.
    interval : le temps en secondes entre chaque clignotement.
    """
    while data["mode_active"]:
        if data["on/off"]:
            r, g, b = data['current_color']  
            setColor(r, g, b)
            data["led"].on()  
            setColor(r, g, b)
            time.sleep(interval)
        
            data["led"].off() 
            time.sleep(interval)  
        

def full(r, g, b):
    """
    Affiche une couleur pleine sur la LED.
    """
    setColor(r, g, b)
    print(f"Full color: Red={r}, Green={g}, Blue={b}")
