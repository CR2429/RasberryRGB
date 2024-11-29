import threading
import time
from main import led,color_list,mode_thread,mode_active,current_color,current_index

def setColor(r_val,g_val,b_val):
    led.red=r_val/255 
    led.green = g_val/255
    led.blue = b_val/255 

def changeColor(direction):
    global current_index, current_color
    # Cycle through the color list
    if direction == "right":
        current_index += 1
    elif direction == "left":
        current_index -= 1

    # Wrap around the index if it goes out of bounds
    if current_index >= len(color_list):
        current_index = 0
    elif current_index < 0:
        current_index = len(color_list) - 1

    # Set the new color
    current_color = color_list[current_index]  # Save the current color
    setColor(*current_color)  # Set the LED to the new color
    print(f"Changed to color {current_index}: r={current_color[0]}, g={current_color[1]}, b={current_color[2]}")

def changeMode(direction):
    global current_index, mode_active, mode_thread
    modes = ["vague", "flash", "full"]  # Example modes
    if direction == 'up':
        current_index = (current_index + 1) % len(modes)  # Move to the next mode
    elif direction == 'down':
        current_index = (current_index - 1) % len(modes)  # Move to the previous mode
    
    print(f"Current mode: {modes[current_index]}")
     
     
    if mode_thread is not None and mode_thread.is_alive():
        mode_active = False  # Stop the active mode
        mode_thread.join()  

    
    # Start the new mode
    mode_active = True
    if current_index == 0:
        mode_thread = threading.Thread(target=vague)
        mode_thread.start()  # Run vague in a separate thread
    elif current_index == 1:
        mode_thread = threading.Thread(target=flash)
        mode_thread.start()  # Run flash in a separate thread
    elif current_index == 2:
        r, g, b = current_color  # Just for example, use current color for full mode
        full(r, g, b)

def vague():
    """
    Crée un effet de vague, où la LED change de couleur progressivement.
    """
    while mode_active:  # Run while mode is active
        r, g, b = current_color  # Keep the current color
        for intensity in range(0, 256):  # On fait varier l'intensité de 0 à 255
            setColor(intensity * r // 255, intensity * g // 255, intensity * b // 255)  # Fade the current color
            time.sleep(0.01)  # Vitesse d'augmentation de l'intensité

        for intensity in range(255, -1, -1):  # On diminue l'intensité de 255 à 0
            setColor(intensity * r // 255, intensity * g // 255, intensity * b // 255)  # Fade the current color
            time.sleep(0.01)  

def flash(interval=1):
    """
    Crée un effet de clignotement où la LED s'allume et s'éteint à un intervalle donné.
    interval : le temps en secondes entre chaque clignotement.
    """
    while mode_active:
        r, g, b = current_color  # Always use the current color
        setColor(r, g, b)
        led.on()  # Allume la LED
        setColor(r, g, b)
        time.sleep(interval)
        # Maintient la LED allumée pour l'intervalle spécifié
        led.off()  # Éteint la LED
        time.sleep(interval)  # Maintient la LED éteinte pour l'intervalle spécifié

def full(r, g, b):
    """
    Affiche une couleur pleine sur la LED.
    """
    setColor(r, g, b)
    print(f"Full color: Red={r}, Green={g}, Blue={b}")
