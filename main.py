import pymysql
from bypass import *
import keywords
from Builder import URLBuilder, QueryBuilder
import math
import model
import traceback
from formattor import DateFormattor, Tokenizer
#from transformers import pipeline

END_POINT = "05/21/1876"
CONN = pymysql.connect(host="163.180.117.35", user="user", password="my0504", port= 3306, database="mysql_db", charset="utf8")                        
CURSOR = CONN.cursor()
START_PAGE = 113
total_time = 0
t_date = "09/05/2024"
search_builder = URLBuilder()
request = Request()
nle_model = model.NLE()
date_formattor = DateFormattor()
query_builder = QueryBuilder(CURSOR)
#question_answerer = pipeline('question-answering', device='cuda')

def postProcess(arr):
    for i in range(len(arr)):
        for k in keywords.REMOVE_KEYWORD + keywords.LAW_FIRMS + keywords.LOC:
            arr[i] = arr[i].replace(k, "")
            
            if arr[i] == "":
                arr.remove("")
                
    arr = list(set(arr))
    return arr
    
def classify(x, person, profession):
    pa , da = [], []
    
    for i in range(len(x) - 1):
        for p in person:
            if profession[x[i]: x[i+1]].find(p) != -1:
                if profession[x[i]: x[i+1]].lower().find(keywords.ATTORNEY_KEYWORD[0]) != -1 or profession[x[i]: x[i+1]].lower().find(keywords.ATTORNEY_KEYWORD[1]) != -1:
                    pa.append(p)
                    print(profession[x[i]: x[i+1]], p)
                elif profession[x[i]: x[i+1]].lower().find(keywords.ATTORNEY_KEYWORD[2]) != -1 or profession[x[i]: x[i+1]].lower().find(keywords.ATTORNEY_KEYWORD[3]) != -1:
                    da.append(p)
                    print(profession[x[i]: x[i+1]], p)
    return list(set(pa)), list(set(da))

def print_case_information(page, i, index, link, plaintiff, defendant, p_attorney, d_attorney, case_name, court, docket_no, date, profession, lawyer, x, winner, loser):
    print("============================================")
    print(f"current page > {i}/{page}, index > {20 * (int(i) - 1) + index}")
    print(f"Case Name> {case_name}")
    print(f"Court Name > {court}")
    print(f"Index no > {docket_no}")
    print(f"plaintiff > {plaintiff}")
    print(f"Defendant > {defendant}")
    print(f"profession > {profession}")
    print(f"plaintiff's Counsel > {p_attorney}")
    print(f"Defendant's Counsel > {d_attorney}")
    print(f"Decision Date > {date}")
    print(f"Decision Key words > {x[2]}")
    print(f"Win > {winner}")
    print(f"Lose > {loser}")
    print(f"URL > {link}")
    print("============================================")

def returnCount(h1):
    before = h1.find(" Search")
    return int(h1[:before])

def keywordIndexing(keyword_type, keyword,  sentence, index, line_index):
    for x in keyword:
        for k in x.finditer(sentence):
            for l in line_index:
                if l[0] <= k.start() and k.end() <= l[1]:
                    inp = index.get(str(l), [0, 0, ""])
                    if inp[2].find(x.pattern) == -1:
                        index[str(l)] = [
                            inp[0] + 1 * int(keyword_type == "plaintiff"), 
                            inp[1] + 1 * int(keyword_type == "defendant"),
                            inp[2] + x.pattern + ", "
                        ]

def findPlaintiffAndDefendant(response):
    plaintiff = response.select("div.co_title > div")[0].getText()
    defendant = "Null"
    
    if len(response.select("div.co_title > div")) > 2:
        defendant = response.select("div.co_title > div")[2].getText()
        
    if plaintiff.lower().find("plaintiff") != -1:
        plaintiff = plaintiff[:plaintiff.lower().find("plaintiff") - 1].replace(",", "").replace("*", "").replace("1 ", "")
    elif plaintiff.lower().find("petitioner") != -1:
        plaintiff = plaintiff[:plaintiff.lower().find("petitioner") - 1].replace(",", "").replace("*", "").replace("1 ", "")
    else:
        plaintiff = plaintiff.replace(",", "").replace("*", "").replace("1 ", "")
    
    if defendant.lower().find("defendant") != -1:
        defendant = defendant[:defendant.lower().find("defendant") - 1].replace(",", "").replace("*", "").replace("1 ", "")
    elif defendant.lower().find("respondent") != -1:
        defendant = defendant[:defendant.lower().find("respondent") - 1].replace(",", "").replace("*", "").replace("1 ", "")
    else:
        defendant = defendant.replace(",", "").replace("*", "").replace("1 ", "")
        
    return plaintiff, defendant    

def findElement(response):
    court = response.select_one("div.co_contentBlock.co_courtBlock > div").string
    plaintiff, defendant = findPlaintiffAndDefendant(response)
    docket_no = response.select_one("div.co_contentBlock.co_docketBlock > div").string.replace("Index", "").replace(" ", "").replace("No.", "")
    date = date_formattor.formatting(response.select_one("#filedate").string)
    attorney_block = response.select("div.co_contentBlock.co_attorneyBlock > div")
    paragraph_block = response.select("div.co_paragraphText")
    t_date = date.replace("-", "/")
    
    return court, plaintiff, defendant, docket_no, date, attorney_block, paragraph_block, t_date

def predictLawyer(nle_model, attorney_list, lawyer):
    for attorney in attorney_list:
        lawyer.extend(nle_model.predict(attorney, ["lawyer"]))
    
    lawyer = list(set(lawyer))  

def lawyerClassification(profession, lawyer, pa, da):
    p_attorney, d_attorney, x = [], [], []
    
    if profession.find(":") != -1:
        for c in keywords.AFTER_ATTORNEY_COMPILE:
            for iter in c.finditer(profession):
                x.append(iter.start())
    x.append(-1)

    if len(x) > 1:
        p_attorney, d_attorney = classify(x, lawyer, profession)
    else:
        x = [0]
        for c in keywords.BEFORE_ATTORNEY_COMPILE:
            for iter in c.finditer(profession):
                x.append(iter.end())
            
        if len(x) > 1:
            x.append(-1)
            p_attorney, d_attorney = classify(x, lawyer, profession)

    if (pa and len(p_attorney) == 0) or (da and len(d_attorney) == 0):
        if (pa and len(p_attorney) == 0):
            p_attorney.append("Unknown")
        else:
            d_attorney.append("Unknown")
    
    p_attorney = postProcess(p_attorney)
    d_attorney = postProcess(d_attorney)
    
    return p_attorney, d_attorney

# keyword 검색
while(t_date != END_POINT):
    url = search_builder.add_param("t_date", t_date).build()

    search_response = request.get(url)

    count = returnCount(search_response.select_one("#co_twoColumnContent > h1").get_text())
    page = math.floor(count/20)

    for i in range(START_PAGE, page + 1):
        # link 추출
        t1 = time.time()
        link_url = search_builder.add_param("Page", i).build()
        link_response = request.get(link_url)
        links = link_response.select("a.resultLink[href]")
        docket_no = link_response.select("tr > td:nth-child(3)")
        t2 = time.time()
        
        print(f"link count : {len(links)}, Spent time : {t2 - t1}")
        
        total_time += t2 - t1
        
        for index, case in enumerate(links):
            try:
                # 링크 접속
                judges, p_lawyer, d_lawyer, plaintiff, defendant, law_firm = [ None for _ in range(6) ]
                p_attorney, d_attorney, decision_paragraph, line_index, decision_keywords, lawyer, attorney_list = [ [] for _ in range(7) ]
                plaintiff_win, plaintiff_lose, defendant_win, defendant_lose, draw = [ 0 for _ in range(5) ]
                full_decision_sentence, profession = "", ""
                selected_paragraph_index = dict()
                pa, da= False, False 
                labels = ["lawyer"]
                
                case_name, link = case.string.replace("\'", "`"), "https://govt.westlaw.com" + str(case['href'])
                response = request.get(link)
                
                if response == None:
                    print("response == None")
                    continue

                court, plaintiff, defendant, docket_no, date, attorney_block, paragraph_block, t_date = findElement(response)
                
                if court.lower().find("appellate") != -1 or court.lower().find("first") != -1 or court.lower().find("second") != -1 or court.lower().find("third") != -1:
                    print("Skip, 사유 : 재심")
                    continue
                
                for attorney in attorney_block:
                    profession += attorney.getText() + " "
                    for ic, atto in enumerate(keywords.ATTORNEY_KEYWORD):
                        a = attorney.getText().lower().find(atto)
                        
                        if a != -1:
                            if labels.count(atto + " lawyer") == 0:
                                labels.append(atto + " lawyer")
                            
                            attorney_list.append(attorney.getText())
                            
                            if ic == 0 or ic == 1:
                                pa = True
                            else:
                                da = True
                
                if len(labels) == 1:
                    print("skip, labels len is 1")
                    continue
                
                predictLawyer(nle_model, attorney_list, lawyer)
                    
                print(link)
                print(labels)
                
                p_attorney, d_attorney = lawyerClassification(profession, lawyer, pa, da)
                
                for x in paragraph_block:
                    decision_paragraph.append(x.getText())
                
                line_end = 0
                for di, d in enumerate(decision_paragraph):
                    line_index.append([line_end, line_end + len(str(d))])
                    full_decision_sentence += str(d)
                    line_end += len(str(d))
                    
                keywordIndexing("plaintiff", keywords.PLAINTIFF_FAVOR_KEYWORD, full_decision_sentence, selected_paragraph_index, line_index)
                keywordIndexing("defendant", keywords.DEPANDENT_FAVOR_KEYWORD, full_decision_sentence, selected_paragraph_index, line_index)
                
                # keyword 미 발견 시 continue
                if len(selected_paragraph_index.keys()) == 0:
                    print("keyword 미발견")
                    continue
                
                # keyword에 따라 승패 결정
                for key in selected_paragraph_index.keys():
                    x = selected_paragraph_index[key]
                    
                    decision_keywords.append([full_decision_sentence[int(key[1:key.find(",")]):int(key[key.find(",")+2:-1])].replace("\'", "").replace("\"", ""), x[2][:-2]])
                    winner, loser = "", ""

                    if x[0] > x[1]:
                        winner = "Plaintiff"
                        loser = "Defendant"
                        plaintiff_win += 1
                        defendant_lose += 1
                        
                    elif x[0] < x[1]:
                        winner = "Defendant"
                        loser = "Plaintiff"
                        defendant_win += 1
                        plaintiff_lose += 1
                        
                    else:
                        winner = "Draw"
                        loser = "Draw"
                        draw += 1
                
                    print_case_information(page, i, index, link, plaintiff, defendant, p_attorney, d_attorney, case_name, court, docket_no, date, profession, lawyer, x, winner, loser)
                
                # DATABASE SQL
                TEST_CASE_SELECT_QUERY = f"SELECT * FROM TEST_CASE WHERE CASE_NAME = '{case_name}' AND INDEX_NO = '{docket_no}'"
                TEST_CASE_INSERT_QUERY = f'INSERT INTO TEST_CASE(CASE_NAME, COURT_NAME, INDEX_NO, PLAINTIFF, DEFENDANT, DECISION_DATE, URL) VALUES("{case_name}", "{court}", "{docket_no}", "{plaintiff}", "{defendant}", "{date}", "{link}")'
                
                # TEST CASE 추가 부분
                if CURSOR.execute(TEST_CASE_SELECT_QUERY) == 0:
                    CURSOR.execute(TEST_CASE_INSERT_QUERY)
                    CONN.commit()
                else:
                    print("DB에 이미 존재")
                    continue
                
                # CASE_ID 불러오기
                CASE_ID = query_builder.findCaseIdQuery("TEST_CASE", case_name, docket_no)
                print(f"CASE_ID : {CASE_ID}")
                
                for name in p_attorney:
                    table = "LAWYER_INFORMATION"
                    lawyer_type = "plaintiff"
                    
                    query_builder.insertQuery(table, name)
                    query_builder.updateQuery(lawyer_type, table, name, plaintiff_win, plaintiff_lose, defendant_win, defendant_lose, draw)
                    
                    # PLAINTIFF LAWYER 추가 부분
                    query_builder.insertQuery2(CASE_ID, lawyer_type + "_lawyer", name, plaintiff_win, plaintiff_lose, draw)
                    
                    CONN.commit()
                
                for name in d_attorney:
                    table = "LAWYER_INFORMATION"
                    lawyer_type = "defendant"
                    
                    query_builder.insertQuery(table, name)
                    query_builder.updateQuery(lawyer_type, table, name, plaintiff_win, plaintiff_lose, defendant_win, defendant_lose, draw)
                    
                    # DEFENDANT LAWYER 추가 부분
                    query_builder.insertQuery2(CASE_ID, lawyer_type + "_lawyer", name, defendant_win, defendant_lose, draw)
                    
                    CONN.commit()
                
                #  Decision Keyword 추가 부분
                for keyword in decision_keywords:
                    query_builder.insertDecisonKeyword(CASE_ID, keyword)
                    
                CONN.commit()

            except Exception as e:
                traceback.print_exc()

        t3 = time.time()
        total_time += t3 - t2
        
        print("===========================================")
        print(f"page {i} complete, Spent time : {t3 - t2}")
        print(f"현재까지 걸린 시간 : {total_time}")
        print("===========================================")

    print(f"Total Time : {total_time}")
    
    if START_PAGE > 1:
        START_PAGE = 1
        
CONN.close()
    
    