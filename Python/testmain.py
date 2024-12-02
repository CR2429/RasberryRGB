import unittest
from config import config_data as data
from unittest.mock import patch
import gpiozero
from io import BytesIO
import json



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
        
        
        
        



    @patch('my_http_request_handler.MyHttpRequestHandler.send_response')
    @patch('my_http_request_handler.MyHttpRequestHandler.send_header')
    @patch('my_http_request_handler.MyHttpRequestHandler.end_headers')
    @patch('my_http_request_handler.MyHttpRequestHandler.wfile.write')
    def test_post_request_change_color(self, mock_write, mock_end_headers, mock_send_header, mock_send_response):
        """
        Teste si une requête POST change correctement la couleur de la LED RGB.
        """
        from my_http_request_handler import MyHttpRequestHandler
        from config import config_data

        # Simuler une requête POST avec une nouvelle couleur
        mock_handler = MyHttpRequestHandler
        payload = {"current_color": [0, 255, 0]}  # Couleur verte
        mock_handler.rfile = BytesIO(json.dumps(payload).encode("utf-8"))
        mock_handler.headers = {"Content-Length": len(json.dumps(payload))}

        mock_handler.do_POST(mock_handler)

        mock_send_response.assert_called_once_with(200)
        mock_send_header.assert_called_once_with("Content-type", "application/json")
        mock_end_headers.assert_called_once()

        # Vérifier que la couleur est mise à jour dans les données de configuration
        self.assertEqual(config_data["current_color"], [0, 255, 0])

        mock_write.assert_called_once_with(
            bytes(json.dumps({
                "message": "Données reçues et traitées avec succès",
                "updated_data": config_data
            }), "utf-8")
        )

    @patch('my_http_request_handler.MyHttpRequestHandler.send_response')
    @patch('my_http_request_handler.MyHttpRequestHandler.send_header')
    @patch('my_http_request_handler.MyHttpRequestHandler.end_headers')
    @patch('my_http_request_handler.MyHttpRequestHandler.wfile.write')
    def test_post_request_change_mode(self, mock_write, mock_end_headers, mock_send_header, mock_send_response):
        """
        Teste si une requête POST change correctement le mode de la LED RGB.
        """
        from my_http_request_handler import MyHttpRequestHandler
        from config import config_data

        mock_handler = MyHttpRequestHandler
        payload = {"mode_thread": "auto"}  # Mode automatique
        mock_handler.rfile = BytesIO(json.dumps(payload).encode("utf-8"))
        mock_handler.headers = {"Content-Length": len(json.dumps(payload))}

        mock_handler.do_POST(mock_handler)

        mock_send_response.assert_called_once_with(200)
        mock_send_header.assert_called_once_with("Content-type", "application/json")
        mock_end_headers.assert_called_once()

        # Vérifier que le mode est mis à jour 
        self.assertEqual(config_data["mode_thread"], "auto")

        # Vérifier que la console informe du changement
        mock_write.assert_called_once_with(
            bytes(json.dumps({
                "message": "Données reçues et traitées avec succès",
                "updated_data": config_data
            }), "utf-8")
        )

    @patch('my_http_request_handler.MyHttpRequestHandler.send_response')
    @patch('my_http_request_handler.MyHttpRequestHandler.send_header')
    @patch('my_http_request_handler.MyHttpRequestHandler.end_headers')
    @patch('my_http_request_handler.MyHttpRequestHandler.wfile.write')
    def test_post_request_change_state(self, mock_write, mock_end_headers, mock_send_header, mock_send_response):
        """
        Teste si une requête POST change correctement l’état de la LED RGB.
        """
        from my_http_request_handler import MyHttpRequestHandler
        from config import config_data

        mock_handler = MyHttpRequestHandler
        payload = {"mode_active": True}  # Activer la LED
        mock_handler.rfile = BytesIO(json.dumps(payload).encode("utf-8"))
        mock_handler.headers = {"Content-Length": len(json.dumps(payload))}

        mock_handler.do_POST(mock_handler)

        mock_send_response.assert_called_once_with(200)
        mock_send_header.assert_called_once_with("Content-type", "application/json")
        mock_end_headers.assert_called_once()

        self.assertTrue(config_data["mode_active"])

        # Vérifier que la console informe du changement
        mock_write.assert_called_once_with(
            bytes(json.dumps({
                "message": "Données reçues et traitées avec succès",
                "updated_data": config_data
            }), "utf-8")
        )

