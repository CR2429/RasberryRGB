import threading
import time
from config import config_data

# Configure la couleur de la LED avec les valeurs spécifiées
def setColor(r_val,g_val,b_val):
    led = config_data["led"]
    led.red=r_val/255 
    led.green = g_val/255
    led.blue = b_val/255 
    config_data["led"] = led

def changeColor(direction):
    #recuperer les valeurs
    current_color = config_data["current_color"]
    current_index = config_data["current_index"]
    color_list = config_data["color_list"]
    
    if direction == "right":
        current_index += 1
    elif direction == "left":
        current_index -= 1

    if current_index >= len(color_list):
        current_index = 0
    elif current_index < 0:
        current_index = len(color_list) - 1

    # Définit la nouvelle couleur
    config_data["current_color"] = color_list[current_index]  
    setColor(*current_color)  
    print(f"Changed to color {current_index}: r={current_color[0]}, g={current_color[1]}, b={current_color[2]}")

def changeMode(direction):
    #recuperer les datas
    current_index = config_data["current_index"]
    mode_active = config_data["mode_active"]
    mode_thread = config_data["mode_thread"]
    modes = ["vague", "flash", "full"]  
    
    if direction == 'up':
        current_index = (current_index + 1) % len(modes)  
    elif direction == 'down':
        current_index = (current_index - 1) % len(modes) 
    
    print(f"Current mode: {modes[current_index]}")
     
     
    if mode_thread is not None and mode_thread.is_alive():
        mode_active = False 
        config_data["mode_active"] = mode_active
        mode_thread.join()   # Attend la fin du thread en cours

    
    mode_active = True
    config_data["mode_active"] = mode_active
    if current_index == 0:
        mode_thread = threading.Thread(target=vague)
        mode_thread.start()  
        config_data["mode_thread"] = mode_thread
    elif current_index == 1:
        mode_thread = threading.Thread(target=flash)
        mode_thread.start()  
        config_data["mode_thread"] = mode_thread
    elif current_index == 2:
        r, g, b = config_data["current_color"]  
        full(r, g, b)
    

def vague():
    """
    Crée un effet de vague, où la LED change d'intensité progressivement.
    """
    #recuperer data
    mode_active = config_data["mode_active"]
    
    while mode_active:  
        r, g, b = config_data['current_color'] 
        for intensity in range(0, 256):  
            setColor(intensity * r // 255, intensity * g // 255, intensity * b // 255) 
            time.sleep(0.01)
        for intensity in range(255, -1, -1): 
            setColor(intensity * r // 255, intensity * g // 255, intensity * b // 255) 
            time.sleep(0.01)  
            
        #reverification
        mode_active = config_data["mode_active"]

def flash(interval=1):
    """
    Crée un effet de clignotement où la LED s'allume et s'éteint à un intervalle donné.
    interval : le temps en secondes entre chaque clignotement.
    """
    #recuperer data
    mode_active = config_data["mode_active"]
    led = config_data["led"]
    
    while mode_active:
        r, g, b = config_data['current_color']  
        setColor(r, g, b)
        led.on()  
        setColor(r, g, b)
        time.sleep(interval)
       
        led.off() 
        time.sleep(interval)  
        
        #reverification
        mode_active = config_data["mode_active"]

def full(r, g, b):
    """
    Affiche une couleur pleine sur la LED.
    """
    setColor(r, g, b)
    print(f"Full color: Red={r}, Green={g}, Blue={b}")
