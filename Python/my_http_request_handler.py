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