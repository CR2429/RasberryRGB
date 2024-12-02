import unittest
from unittest.mock import patch
import gpiozero
from io import BytesIO
import json



class TestGPIOFunctions(unittest.TestCase):

    def setUp(self):
        # Réinitialiser tous les périphériques GPIO
        gpiozero.Device.pin_factory.clear()
    
    @patch('gpiozero.LED')
    @patch('gpiozero.Button')
    @patch('config.config_data')
    def test_button_press(self, mock_config_data):
        # Simuler un appui sur le bouton
        mock_config_data["bouton-alim"].wait_for_press.return_value = True
        from NosThread.loop import loopZ

        # Exécution du code (en supposant que cela modifie les états de la LED)
        loopZ()  # Exécution du thread

        # Vérification des appels
        mock_config_data["led"].on.assert_called_once()
        mock_config_data["led-alim"].on.assert_called_once()

        # Vérification de l'état après l'appui sur le bouton
        mock_config_data["led"].off.assert_called_once()
        mock_config_data["led-alim"].off.assert_called_once()

    @patch('NosThread.led.changeMode')
    @patch('config.config_data')
    def test_move_joystick_up(self, mock_config_data, mock_changeMode):
        # Simuler un mouvement du joystick vers le haut
        mock_config_data["adc"].side_effect = [200, 300]  # Valeur qui déclenche le mouvement vers le haut
        from NosThread.loop import loopY

        loopY()  # Exécution du thread

        # Vérifier que le mode a changé (appelle la fonction changeMode)
        mock_changeMode.assert_called_with("up")

        # Vérification d'autres comportements si nécessaire
        self.assertTrue(mock_changeMode.called)  # Pour vérifier que la fonction a été appelée.

    @patch('NosThread.led.changeMode')
    @patch('config.config_data')
    def test_move_joystick_down(self, mock_config_data, mock_changeMode):
        # Simuler un mouvement du joystick vers le bas
        mock_config_data["adc"].analogRead.side_effect = [300, 200]  # Valeur qui déclenche le mouvement vers le bas
        from NosThread.loop import loopY

        loopY()  # Exécution du thread

        # Vérifier que le mode a changé (appelle la fonction changeMode)
        mock_changeMode.assert_called_with("down")

        # Exemple d'assertion supplémentaire
        self.assertEqual(mock_config_data["led"].is_on(), False)  # Si on attend que la LED soit éteinte

    @patch('NosThread.led.changeColor')
    @patch('config.config_data')
    def test_move_joystick_left(self, mock_config_data, mock_changeColor):
        # Simuler un mouvement du joystick vers la gauche
        mock_config_data["adc"].analogRead.side_effect = [300, 200]  # Valeur qui déclenche le mouvement vers la gauche
        from NosThread.loop import loopX

        loopX()  # Exécution du thread

        # Vérifier que la couleur a changé (appelle la fonction changeColor)
        mock_changeColor.assert_called_with("left")

        # Exemple d'assertion supplémentaire
        self.assertTrue(mock_changeColor.called)  # Vérifier si la fonction a été appelée

    @patch('NosThread.led.changeColor')
    @patch('config.config_data')
    def test_move_joystick_right(self, mock_config_data, mock_changeColor):
         # Simuler un mouvement du joystick vers la droite
        mock_config_data["adc"].analogRead.side_effect = [200, 300]  # Valeur qui déclenche le mouvement vers la droite
        from NosThread.loop import loopX

        loopX()  # Exécution du thread

        # Vérifier que la couleur a changé (appelle la fonction changeColor)
        mock_changeColor.assert_called_with("right")

        # Exemple d'assertion supplémentaire
        self.assertEqual(mock_config_data["led"].is_on(), True)  # Si on attend que la LED soit allumée

    @patch('my_http_request_handler.MyHttpRequestHandler.send_response')
    @patch('my_http_request_handler.MyHttpRequestHandler.send_header')
    @patch('my_http_request_handler.MyHttpRequestHandler.end_headers')
    @patch('my_http_request_handler.MyHttpRequestHandler.wfile.write')
    def test_get_request(self, mock_write, mock_end_headers, mock_send_header, mock_send_response):
        # Simuler une requête GET
        from my_http_request_handler import MyHttpRequestHandler
        handler = MyHttpRequestHandler

        # Simuler la requête GET
        mock_send_response.assert_called_with(200)
        mock_send_header.assert_called_with("Content-type", "application/json")
        mock_end_headers.assert_called_once()

        # Vérifier que la réponse contient bien les informations de la LED RGB
        mock_write.assert_called_once_with(bytes(
            '{"current_color": [255, 0, 0], "mode_thread": null, "mode_active": false}', 'utf8'))
        
        # Vérification des données retournées
        self.assertEqual(mock_write.call_args[0][0], b'{"current_color": [255, 0, 0], "mode_thread": null, "mode_active": false}')



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

if __name__ == '__main__':
    unittest.main()
