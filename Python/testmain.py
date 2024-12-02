import unittest
from unittest.mock import MagicMock, patch
import time

class TestGPIOFunctions(unittest.TestCase):

    @patch('config.config_data')
    def test_button_press(self, mock_config_data):
        # Simuler l'appui sur le bouton
        mock_config_data["bouton-alim"].wait_for_press = MagicMock(return_value=True)

        from NosThread.loop import loopZ

        # Simuler le premier état (allumé)
        mock_config_data["led"].on.assert_called_once()
        mock_config_data["led-alim"].on.assert_called_once()

        # Simuler l'état éteint (imitation d'un autre appui)
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

    @patch('NosThread.led.changeMode')
    @patch('config.config_data')
    def test_move_joystick_down(self, mock_config_data, mock_changeMode):
        # Simuler un mouvement du joystick vers le bas
        mock_config_data["adc"].analogRead.side_effect = [300, 200]  # Valeur qui déclenche le mouvement vers le bas
        from NosThread.loop import loopY

        loopY()  # Exécution du thread

        # Vérifier que le mode a changé (appelle la fonction changeMode)
        mock_changeMode.assert_called_with("down")

    @patch('NosThread.led.changeColor')
    @patch('config.config_data')
    def test_move_joystick_left(self, mock_config_data, mock_changeColor):
        # Simuler un mouvement du joystick vers la gauche
        mock_config_data["adc"].analogRead.side_effect = [300, 200]  # Valeur qui déclenche le mouvement vers la gauche
        from NosThread.loop import loopX

        loopX()  # Exécution du thread

        # Vérifier que la couleur a changé (appelle la fonction changeColor)
        mock_changeColor.assert_called_with("left")

    @patch('NosThread.led.changeColor')
    @patch('config.config_data')
    def test_move_joystick_right(self, mock_config_data, mock_changeColor):
        # Simuler un mouvement du joystick vers la droite
        mock_config_data["adc"].analogRead.side_effect = [200, 300]  # Valeur qui déclenche le mouvement vers la droite
        from NosThread.loop import loopX

        loopX()  # Exécution du thread

        # Vérifier que la couleur a changé (appelle la fonction changeColor)
        mock_changeColor.assert_called_with("right")

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

if __name__ == '__main__':
    unittest.main()
