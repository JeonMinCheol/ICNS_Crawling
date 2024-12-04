import requests
from bs4 import BeautifulSoup
import time
class Request:
  def __init__(self):
    self.url = ""
    
  def get(self, url):
    self.post_body = {
      "cmd": "request.get",
      "url": url,
      "maxTimeout": 60000
    }
    s = time.time()
    response = requests.post('http://localhost:8191/v1', headers={'Content-Type': 'application/json'}, json=self.post_body)
    
    if response.status_code == 200:
      print(f"Bypass complete! : {time.time() - s}")
      print(response.content)
      