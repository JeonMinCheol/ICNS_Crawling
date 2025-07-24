# 소송 사례 데이터와 관련된 정보를 구성하고, DB 쿼리를 동적으로 생성·실행
class CaseInfoBuilder:
    def __init__(self):
        self.CASE_NAME = "Unknown"
        self.COURT_NAME = "Unknown"
        self.JUDGE_NAME = "Unknown"
        self.INDEX_NO = "Unknown"
        self.SLIPOP_NO = "Unknown"
        self.PLAINTIFF_NAME = "Unknown"
        self.DEFENDANT_NAME = "Unknown"
        self.PLAINTIFF_LAWYER_NO = 0
        self.DEFENDANT_LAWYER_NO = 0
        self.DECISION_DATE = "1900-01-01"
        self.LAYWYER_INFO = {} # LAWYER_NAME: [ [의뢰인 이름, 의뢰인과의 관계], .. ]
        self.LAWYER_LAWFIRM = {} # LAWYER_NAME: [LAWFIRM, LOCATION]
        self.KEYWORDS = {} # KEYWORD: [PARAGRAPHS, ..]
        self.LAWYER_PARAGRAPH = ""
        self.KEYWORD_PARAGRAPH = ""
    
    # 객체를 문자열로 출력할 때의 형식
    def __str__(self):
        info = f"""
            case name : {self.CASE_NAME},
            court name : {self.COURT_NAME},
            judge name : {self.JUDGE_NAME}
            index no : {self.INDEX_NO}
            slip op : {self.SLIPOP_NO}
            plaintiff : {self.PLAINTIFF_NAME}
            defendant : {self.DEFENDANT_NAME}
            plaintiff_lawyer_no : {self.PLAINTIFF_LAWYER_NO}
            defendant_lawyer_no : {self.DEFENDANT_LAWYER_NO}
            date : {self.DECISION_DATE}
            lawyer: {self.LAYWYER_INFO.items()}
            lawfirm: {self.LAWYER_LAWFIRM.items()}
            keywords: {self.KEYWORDS.items()}
            raw_lawyer_paragraph: {self.LAWYER_PARAGRAPH}
        """

        return info

    # 모든 정보 초기화
    def clear(self):
        self.CASE_NAME = "Unknown"
        self.COURT_NAME = "Unknown"
        self.JUDGE_NAME = "Unknown"
        self.INDEX_NO = "Unknown"
        self.SLIPOP_NO = "Unknown"
        self.PLAINTIFF_NAME = "Unknown"
        self.DEFENDANT_NAME = "Unknown"
        self.PLAINTIFF_LAWYER_NO = 0
        self.DEFENDANT_LAWYER_NO = 0
        self.DECISION_DATE = "1900-01-01"
        self.URL = ""
        self.LAYWYER_INFO = {} # LAWYER_NAME: [ [의뢰인 이름, 의뢰인과의 관계], .. ]
        self.LAWYER_LAWFIRM = {} # LAWYER_NAME: [LAWFIRM, LOCATION]
        self.KEYWORDS = {} # KEYWORD: [PARAGRAPHS, ..]

    # 변호사 수 설정
    def setLawyerNo(self, p_no, d_no):
        self.PLAINTIFF_LAWYER_NO = p_no
        self.DEFENDANT_LAWYER_NO = d_no

    # 키워드에 해당 문단 추가
    def addKeyword(self, keyword, para):
        keyword_paragraphs = self.KEYWORDS.get(keyword)
        if keyword_paragraphs is None:
            self.KEYWORDS[keyword] = list(para)
        else:
            keyword_paragraphs.append(para)
            self.KEYWORDS[keyword] = keyword_paragraphs
    # 변호사 관련 정보 추가
    def addLawyer(self, lawyer, customer, relationship, lawfirm, loc):
        customer_relationship = self.LAWYER_INFO.get(lawyer)
        if customer_relationship is None:
            self.LAWYER_INFO[lawyer] = [[customer, relationship]]
            self.LAWYER_LAWFIRM[lawyer] = [lawfirm, loc]
        else:
            customer_relationship.append([customer, relationship])
            self.LAWYER_INFO[lawyer] = customer_relationship
            x = self.LAWYER_LAWFIRM[lawyer]
            x.append([lawfirm, loc])
            self.LAWYER_LAWFIRM[lawyer] = x
        
class QueryBuilder:
    def __init__(self, CURSOR):
        self.CURSOR = CURSOR

    # 사건명과 사건번호(INDEX_NO)로 사건 ID 조회    
    def findCaseIdQuery(self, table, case_name, docket_no):
        CASE_ID_QUERY = f"SELECT _id FROM {table} WHERE CASE_NAME = '{case_name}' AND INDEX_NO = '{docket_no}'"
        self.CURSOR.execute(CASE_ID_QUERY)
        CASE_ID = self.CURSOR.fetchone()[0]
        return CASE_ID

    # 변호사 정보 존재 여부 확인 후, 없다면 INSERT
    def insertQuery(self, table, name):
        LAWYER_INFORMATION_SELECT_QUERY = f"SELECT WIN, LOSE FROM LAWYER_INFORMATION WHERE NAME = '{name}'"
        LAWYER_INFORMATION_INSERT_QUERY = f'INSERT INTO {table}(NAME, WIN, LOSE, DRAW, COUNT, CASE_WIN, CASE_LOSE, CASE_DRAW) VALUES("{name}", {0}, {0}, {0}, {0}, {0}, {0}, {0})'
        
        # LAWYER INFORMATION 추가 부분
        if self.CURSOR.execute(LAWYER_INFORMATION_SELECT_QUERY) == 0:
            self.CURSOR.execute(LAWYER_INFORMATION_INSERT_QUERY)
            print(LAWYER_INFORMATION_INSERT_QUERY)

    # 변호사 번호 조회 후, 사건 변호사 관계 테이블에 INSERT
    def insertQuery2(self, CASE_ID, table, name, win, lose, draw):
        LAWYER_NO_QUERY = f"SELECT _id FROM LAWYER_INFORMATION WHERE NAME = '{name}'"
        self.CURSOR.execute(LAWYER_NO_QUERY)
        LAWYER_NO = self.CURSOR.fetchone()[0]
        print(f"LAYWER_NO : {LAWYER_NO}")
        
        LAWYER_INSERT_QUERY = f'INSERT INTO {table}(CASE_ID, LAWYER_NO, NAME, WIN, LOSE, DRAW) VALUES("{CASE_ID}", "{LAWYER_NO}", "{name}", "{win}", "{lose}", "{draw}")'
        self.CURSOR.execute(LAWYER_INSERT_QUERY)

    # 변호사의 소송 결과 반영하여 정보 업데이트
    def updateQuery(self, lawyer_type, table, name, plaintiff_win, plaintiff_lose, defendant_win, defendant_lose, draw):
        if lawyer_type == "plaintiff":
            # CASE: WIN
            if plaintiff_win > plaintiff_lose:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {plaintiff_win}, LOSE = LOSE + {plaintiff_lose}, DRAW = DRAW + {draw}, CASE_WIN = CASE_WIN + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
            
            # CASE: LOSE
            elif plaintiff_win < plaintiff_lose:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {plaintiff_win}, LOSE = LOSE + {plaintiff_lose}, DRAW = DRAW + {draw}, CASE_LOSE = CASE_LOSE + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
            
            # CASE: DRAW
            else:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {plaintiff_win}, LOSE = LOSE + {plaintiff_lose}, DRAW = DRAW + {draw}, CASE_DRAW = CASE_DRAW + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
                
        elif lawyer_type == "defendant":
            if defendant_win > defendant_lose:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {defendant_win}, LOSE = LOSE + {defendant_lose}, DRAW = DRAW + {draw}, CASE_WIN = CASE_WIN + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
            
            # CASE: LOSE
            elif defendant_win < defendant_lose:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {defendant_win}, LOSE = LOSE + {defendant_lose}, DRAW = DRAW + {draw}, CASE_LOSE = CASE_LOSE + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)
            
            # CASE: DRAW
            else:
                LAWYER_INFORMATION_UPDATE_QUERY = f"UPDATE {table} SET COUNT = COUNT + 1, WIN = WIN + {defendant_win}, LOSE = LOSE + {defendant_lose}, DRAW = DRAW + {draw}, CASE_DRAW = CASE_DRAW + 1 WHERE NAME = '{name}'"
                self.CURSOR.execute(LAWYER_INFORMATION_UPDATE_QUERY)
                print(LAWYER_INFORMATION_UPDATE_QUERY)

    # 사건 ID와 키워드를 DECISION_KEYWORD 테이블에 삽입
    def insertDecisonKeyword(self, CASE_ID, keyword):
        DECISION_KEYWORD_INSERT_QUERY = f'INSERT INTO DECISION_KEYWORD(CASE_ID, KEYWORD, PARAGRAPH) VALUES("{CASE_ID}", "{keyword[1]}", "{keyword[0]}")'
        self.CURSOR.execute(DECISION_KEYWORD_INSERT_QUERY)