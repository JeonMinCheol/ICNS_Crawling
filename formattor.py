from datetime import datetime
import nltk
import time
from nltk.data import load
nltk.download('punkt')

class DateFormattor:
    def formatting(self, date_str):
        # 문자열을 datetime 객체로 변환
        date_obj = datetime.strptime(date_str, "%B %d, %Y")

        # datetime 객체를 MySQL 형식인 YYYY-MM-DD 형식의 문자열로 변환
        mysql_date_str = date_obj.strftime("%Y-%m-%d")
        return mysql_date_str
    
class Tokenizer:
    def __init__(self):
        self.tokenizer = load("tokenizers/punkt/english.pickle")
        self.extra_abbreviations = [
            'RE','re','pat', 'no', 'nos','vol','jan','feb','mar','apr','jun',
            'jul','aug','sep','oct','nov','dec','eng','ser','ind','ed','pp',
            'e.g','al','T.E.N.S', 'E.M.S','F.E','U.H.T.S.T','degree',
            '/gm','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O',
            'P','Q','R','S','T','U','V','W','X','Y','Z']
        self.tokenizer._params.abbrev_types.update(self.extra_abbreviations)
        self.no_blank = False

    def get_token(self):
        load_file=open('./input.txt','r')
        
        while True:
            line = load_file.readline()
            if line == "":
                break
            if line.strip() == "":
                if self.no_blank:
                    continue
            else:
                result_ = self.tokenizer.tokenize(line)
                return [ f"{cur_line}\n" for cur_line in result_ ]