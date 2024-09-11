import keywords
import formattor
import re

        

#  단위로 나눈 후 쓸데 없는 거 치우고 확인
c = re.compile("Plaintiff(\w?\s?\/?\,?\'?\(?\)?\.?\#?\$?)*\.",re.I)
#c = re.compile("Defendant(\w?\s?\/?\,?\'?\(?\)?\.?\#?\$?)*\.",re.I)
#c = re.compile("Petitioner(\w?\s?\/?\,?\'?\(?\)?\.?\#?\$?)*\.",re.I)
c = re.compile("Respondent(\w?\s?\/?\,?\'?\(?\)?\.?\#?\$?)*\.",re.I)
s = ""
import nltk

from nltk.data import load
tokenizer = load("tokenizers/punkt/english.pickle")
extra_abbreviations = [
    'RE','re','pat', 'no', 'nos','vol','jan','feb','mar','apr','jun',
    'jul','aug','sep','oct','nov','dec','eng','ser','ind','ed','pp',
    'e.g','al','T.E.N.S', 'E.M.S','F.E','U.H.T.S.T','degree',
    '/gm','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O',
    'P','Q','R','S','T','U','V','W','X','Y','Z']
tokenizer._params.abbrev_types.update(extra_abbreviations)


        
            
# for iter in c.finditer(s):
#     print(iter)

# print(c.search(s))
