from gpiozero import RGBLED
import RPi.GPIO as GPIO
import time
import random
import threading

GPIO.setmode(GPIO.BOARD)



led = RGBLED(red=17, green=18, blue=27, active_high=True) # define the pins for R:GPIO17,G:GPIO18,B:GPIO27

color_list = [
    (255, 0, 0),  # Red 0
    (0, 255, 0),  # Green 1
    (0, 0, 255),  # Blue 2
    (255, 255, 0),  # Yellow 3
    (0, 255, 255),  # Cyan 4
    (255, 0, 255),  # Magenta 5
    (255, 165, 0),  # Orange 6
    (128, 0, 128),  # Purple 7
    (255, 255, 255),  # White 8
    (0, 0, 0),  # Off (black) 9
    (255, 105, 180),  # Hot Pink 10
    (0, 128, 128),  # Teal 11 
    (34, 139, 34),  # Forest Green 12
    (255, 99, 71),  # Tomato Red 13
    (148, 0, 211),  # Dark Violet 14 
    (255, 222, 173),  # Navajo White 15
]

mode_active = False
mode_thread = None
# Current color index
current_index = 0
current_color = color_list[0]

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


 
def loop():
    global current_index
    print("Available commands: 'left' to change color left, 'right' to go right, 'up' to change color right, 'down' to change mode down, 'exit' to quit.")
    
    while True:
        # Get the user's input command
        direction = input("Enter command: ").strip().lower()
        if direction == 'exit':
            # Stop the active mode before quitting
            mode_active = False
            if mode_thread is not None:
                mode_thread.join()  # Wait for the mode thread to finish
            break
        
        if direction in ['left', 'right']:
            changeColor(direction)
        elif direction == 'up':
            changeMode('up')  # Change mode up
        elif direction == 'down':
            changeMode('down')
        else:
            print("Invalid input! Please type 'left', 'right', or 'exit'.")

        time.sleep(1)


def destroy():
 led.close()
if __name__ == '__main__': # Program entrance
 print ('Program is starting ... ')
 try:
     loop()
 except KeyboardInterrupt: # Press ctrl-c to end the program.
     destroy()
 print("Ending program")