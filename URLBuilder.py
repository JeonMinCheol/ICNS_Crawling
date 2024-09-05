from urllib.parse import urlencode
# https://govt.westlaw.com/nyofficial/Search/Results?transitionType=Default&contextData=%28sc.Default%29&t_date=09%2F05%2F2024&t_p=LE&t_querytext=N.Y.Sup&Page=1&query=advanced%3A%20N.Y.Sup%20&Template=Decision


class URLBuilder:
    def __init__(self):
        self.scheme = "https"
        self.domain = "govt.westlaw.com"
        self.path = "nyofficial/Search/Results"
        self.params = {"transitionType" : "Default", "contextData" : "(sc.Default)", "query": "advanced: N.Y.Sup ", "Template" : "Decision", "t_querytext" : "N.Y.Sup", "Page" : 1, "t_p" : "LE", "t_date" : "09/05/2024"}
        self.port = None

    def set_scheme(self, scheme):
        self.scheme = scheme
        return self

    def set_domain(self, domain):
        self.domain = domain
        return self

    def set_port(self, port):
        self.port = port
        return self

    def set_path(self, path):
        self.path = path
        return self
    
    def add_param(self, key, value):
        self.params[key] = value
        return self

    def build(self):
        url = f"{self.scheme}://{self.domain}"
        if self.port:
            url += f":{self.port}"
        if self.path:
            url += f"/{self.path}"
        if self.params:
            query_string = urlencode(self.params)
            url += f"?{query_string}"
        return url