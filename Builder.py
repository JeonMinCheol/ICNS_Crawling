from urllib.parse import urlencode
# https://govt.westlaw.com/nyofficial/Search/Results?transitionType=Default&contextData=%28sc.Default%29&t_date=09%2F05%2F2024&t_p=LE&t_querytext=N.Y.Sup&Page=1&query=advanced%3A%20N.Y.Sup%20&Template=Decision

# https://govt.westlaw.com/nyofficial/Search/Results?transitionType=Default&contextData=%28sc.Default%29&t_date=05%F05%2F2014&t_p=LE&t_querytext=N.Y.Sup&Page=1&query=advanced%3A%20N.Y.Sup%20&Template=Decision
class URLBuilder:
    def __init__(self):
        self.scheme = "https"
        self.domain = "govt.westlaw.com"
        self.path = "nyofficial/Search/Results"
        self.params = {}
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
    
class QueryBuilder:
    def __init__(self, CURSOR):
        self.CURSOR = CURSOR
        pass
    
    def findCaseIdQuery(self, table, case_name, docket_no):
        CASE_ID_QUERY = f"SELECT _id FROM {table} WHERE CASE_NAME = '{case_name}' AND INDEX_NO = '{docket_no}'"
        self.CURSOR.execute(CASE_ID_QUERY)
        CASE_ID = self.CURSOR.fetchone()[0]
        return CASE_ID

    def insertQuery(self, table, name):
        LAWYER_INFORMATION_SELECT_QUERY = f"SELECT WIN, LOSE FROM LAWYER_INFORMATION WHERE NAME = '{name}'"
        LAWYER_INFORMATION_INSERT_QUERY = f'INSERT INTO {table}(NAME, WIN, LOSE, DRAW, COUNT, CASE_WIN, CASE_LOSE, CASE_DRAW) VALUES("{name}", {0}, {0}, {0}, {0}, {0}, {0}, {0})'
        
        # LAWYER INFORMATION 추가 부분
        if self.CURSOR.execute(LAWYER_INFORMATION_SELECT_QUERY) == 0:
            self.CURSOR.execute(LAWYER_INFORMATION_INSERT_QUERY)
            print(LAWYER_INFORMATION_INSERT_QUERY)

    def insertQuery2(self, CASE_ID, table, name, win, lose, draw):
        LAWYER_NO_QUERY = f"SELECT _id FROM LAWYER_INFORMATION WHERE NAME = '{name}'"
        self.CURSOR.execute(LAWYER_NO_QUERY)
        LAWYER_NO = self.CURSOR.fetchone()[0]
        print(f"LAYWER_NO : {LAWYER_NO}")
        
        LAWYER_INSERT_QUERY = f'INSERT INTO {table}(CASE_ID, LAWYER_NO, NAME, WIN, LOSE, DRAW) VALUES("{CASE_ID}", "{LAWYER_NO}", "{name}", "{win}", "{lose}", "{draw}")'
        self.CURSOR.execute(LAWYER_INSERT_QUERY)

    def updateQuery(self, lawyer_type, table, name, plaintiff_win, plaintiff_lose, defendant_win, defendant_lose, draw):
        if lawyer_type == "plaintiff":
            # CASE: WIN
            if plaintiff_win > defendant_win:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {plaintiff_win}, LOSE = LOSE + {plaintiff_lose}, DRAW = DRAW + {draw}, CASE_WIN = CASE_WIN + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
            
            # CASE: LOSE
            elif plaintiff_win < defendant_win:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {plaintiff_win}, LOSE = LOSE + {plaintiff_lose}, DRAW = DRAW + {draw}, CASE_LOSE = CASE_LOSE + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
            
            # CASE: DRAW
            else:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {plaintiff_win}, LOSE = LOSE + {plaintiff_lose}, DRAW = DRAW + {draw}, CASE_DRAW = CASE_DRAW + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
                
        elif lawyer_type == "defendant":
            if plaintiff_win > defendant_win:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {defendant_win}, LOSE = LOSE + {defendant_lose}, DRAW = DRAW + {draw}, CASE_WIN = CASE_WIN + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
            
            # CASE: LOSE
            elif plaintiff_win < defendant_win:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {defendant_win}, LOSE = LOSE + {defendant_lose}, DRAW = DRAW + {draw}, CASE_LOSE = CASE_LOSE + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
            
            # CASE: DRAW
            else:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {defendant_win}, LOSE = LOSE + {defendant_lose}, DRAW = DRAW + {draw}, CASE_DRAW = CASE_DRAW + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
    
    def insertDecisonKeyword(self, CASE_ID, keyword):
        DECISION_KEYWORD_INSERT_QUERY = f'INSERT INTO DECISION_KEYWORD(CASE_ID, KEYWORD, PARAGRAPH) VALUES("{CASE_ID}", "{keyword[1]}", "{keyword[0]}")'
        self.CURSOR.execute(DECISION_KEYWORD_INSERT_QUERY)