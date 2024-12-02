import threading
import time
from config import config_data as data

# Configure la couleur de la LED avec les valeurs spécifiées
def setColor(r_val,g_val,b_val):
    data["led"].red=r_val/255 
    data["led"].green = g_val/255
    data["led"].blue = b_val/255 

def changeColor(direction):
    #recuperer les valeurs
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
    #recuperer les datas
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
    if data["current_index"] == 0:
        data["mode_thread"] = threading.Thread(target=vague, name="vague")
        data["mode_thread"].start()  
    elif data["current_index"] == 1:
        data["mode_thread"] = threading.Thread(target=flash, name="flash")
        data["mode_thread"].start()  
    elif data["current_index"] == 2:
        r, g, b = data["current_color"]  
        full(r, g, b)
    

def vague():
    """
    Crée un effet de vague, où la LED change d'intensité progressivement.
    """
    #recuperer data
    mode_active = data["mode_active"]
    
    while mode_active:  
        r, g, b = data['current_color'] 
        for intensity in range(0, 256):  
            setColor(intensity * r // 255, intensity * g // 255, intensity * b // 255) 
            time.sleep(0.01)
        for intensity in range(255, -1, -1): 
            setColor(intensity * r // 255, intensity * g // 255, intensity * b // 255) 
            time.sleep(0.01)  
            
        #reverification
        mode_active = data["mode_active"]

def flash(interval=1):
    """
    Crée un effet de clignotement où la LED s'allume et s'éteint à un intervalle donné.
    interval : le temps en secondes entre chaque clignotement.
    """
    #recuperer data
    mode_active = data["mode_active"]
    led = data["led"]
    
    while mode_active:
        r, g, b = data['current_color']  
        setColor(r, g, b)
        led.on()  
        setColor(r, g, b)
        time.sleep(interval)
       
        led.off() 
        time.sleep(interval)  
        
        #reverification
        mode_active = data["mode_active"]

def full(r, g, b):
    """
    Affiche une couleur pleine sur la LED.
    """
    setColor(r, g, b)
    print(f"Full color: Red={r}, Green={g}, Blue={b}")
