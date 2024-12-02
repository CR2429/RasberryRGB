import unittest
from config import config_data as data


class TestGPIOFunctions(unittest.TestCase):

    def test_joystick_left(self):
        if data["joystick_test"] == "left":
            current_color_normalized = (
                data["current_color"][0] / 255.0,
                data["current_color"][1] / 255.0,
                data["current_color"][2] / 255.0
            )
            couleur = (data["led"].red, data["led"].green, data["led"].blue)
            self.assertEqual(couleur, current_color_normalized)
            
            data["joystick_test"] = "none"
    
    def test_joystick_right(self):
        if data["joystick_test"] == "right":
            current_color_normalized = (
                data["current_color"][0] / 255.0,
                data["current_color"][1] / 255.0,
                data["current_color"][2] / 255.0
            )
            couleur = (data["led"].red, data["led"].green, data["led"].blue)
            self.assertEqual(couleur, current_color_normalized)
            
            data["joystick_test"] = "none"
                
    def test_joystick_up(self):
        modes_list = ["vague", "flash", "full"]
        modes_detect = "none"
        
        if data["joystick_test"] == "up":
            #regarder le mode qui est detecter
            if data["mode_active"] == False:
                modes_detect = "full"
            else :
                modes_detect = data["mode_thread"].name
            
            #assert
            self.assertEqual(modes_list[data["current_index"]], modes_detect)
            
            data["joystick_test"] = "none"
                
    def test_joystick_down(self):
        modes_list = ["vague", "flash", "full"]
        modes_detect = "none"
        
        if data["joystick_test"] == "down":
            #regarder le mode qui est detecter
            if data["mode_active"] == False:
                modes_detect = "full"
            else :
                modes_detect = data["mode_thread"].name
            
            #assert
            self.assertEqual(modes_list[data["current_index"]], modes_detect)
            
            data["joystick_test"] = "none"
        
        
        
        

