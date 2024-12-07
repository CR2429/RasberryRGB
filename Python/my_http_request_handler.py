from http.server import BaseHTTPRequestHandler
import json
from config import config_data as data

#classe pour les requetes
class MyHttpRequestHandler(BaseHTTPRequestHandler):
    #en cas de requete get
    def do_GET(self):
        print("requete get detecter")
        dataJson = json.dumps({
            'current_color': data['current_color'],
            'mode_thread': data['mode_thread'],
            'mode_active': data['mode_active']
        })
        
        
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()
        self.wfile.write(bytes(dataJson), "utf8")
        
        print('Data envoyer : ' + dataJson)
        return 


    def do_POST(self):
        print("Requête POST détectée")
        
        content_length = int(self.headers['Content-Length'])
        post_data = self.rfile.read(content_length)
        
        try:
            received_data = json.loads(post_data.decode('utf-8'))
            print("Données reçues :", received_data)
            
            if 'current_color' in received_data:
                data['current_color'] = received_data['current_color']
            if 'mode_thread' in received_data:
                data['mode_thread'] = received_data['mode_thread']
            if 'mode_active' in received_data:
                data['mode_active'] = received_data['mode_active']
            
            # Prépare une réponse
            response = {
                'message': 'Données reçues et traitées avec succès',
                'updated_data': data
            }
            
            # Envoie une réponse de succès
            self.send_response(200)
            self.send_header("Content-type", "application/json")
            self.end_headers()
            self.wfile.write(bytes(json.dumps(response), "utf-8"))
        
        except json.JSONDecodeError:
            # Si les données reçues ne sont pas un JSON valide, envoie une réponse d'erreur
            self.send_response(400)
            self.send_header("Content-type", "application/json")
            self.end_headers()
            response = {'erreur': 'JSON invalide'}
            self.wfile.write(bytes(json.dumps(response), "utf-8"))
        
        return