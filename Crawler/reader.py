import os
import Builder
import keywords
import numpy as np
from transformers import pipeline

# tokenizer = AutoTokenizer.from_pretrained("nlpaueb/legal-bert-base-uncased")
# model = AutoModel.from_pretrained("nlpaueb/legal-bert-base-uncased")
from transformers import pipeline, AutoModelForQuestionAnswering, AutoTokenizer

model_name = "bert-base-uncased"  # 예시 모델
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForQuestionAnswering.from_pretrained(model_name)

qa_pipeline  = pipeline("question-answering", model=model, tokenizer=tokenizer, device="cuda:0")

# 특정 디렉터리 경로
directory = "../../파싱 자료들/txts/"

# 디렉터리에 있는 파일 목록 가져오기
files = os.listdir(directory)

builder = Builder.CaseInfoBuilder()

month = ["january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"]

# 우선 빌더에 정보 수집하기
# 이후 빌더에 담긴 정보들 모델로 프로세싱하기
# 이후 정제된 데이터들 저장

file_path = os.path.join(directory, files[0])
with open(file_path, 'rb') as file:
    lines = file.readlines()
    textHistory = []
    lawyer_raw_text = []
    keyword_raw_text = []

    builder.clear()
    for idx, line in enumerate(lines):
        text = line.decode('utf-8')
        l_text = text.lower().strip()
        if idx == 0:
            builder.CASE_NAME = text
        
        elif builder.SLIPOP_NO == "Unknown" and l_text.find("slip") != -1:
            builder.SLIPOP_NO = text

        elif builder.DECISION_DATE == "1900-01-01":
            for m in month:
                if l_text.find(m) != -1:
                    builder.DECISION_DATE = text
                    break

        elif builder.COURT_NAME == "Unknown" and l_text.find("court") != -1:
            builder.COURT_NAME = text

        elif builder.INDEX_NO == "Unknown" and (l_text.find("docket") != -1 or l_text.find("index") != -1):
            builder.INDEX_NO = text
                
        elif builder.JUDGE_NAME == "Unknown" and (l_text.find("judge") != -1 or l_text.find("honoroble")):
            builder.JUDGE_NAME = text

        elif builder.PLAINTIFF_NAME == "Unknown" and (l_text.find("v.") != -1 or l_text.find("vs") != -1 or l_text.find("-v-") != -1 or l_text.find("against") != -1):
            builder.PLAINTIFF_NAME = textHistory[-1]

        elif builder.DEFENDANT_NAME == "Unknown" and len(textHistory) > 0 and (textHistory[-1].lower().find("v.") != -1 or textHistory[-1].lower().find("vs") != -1 or textHistory[-1].lower().find("-v-") != -1 or textHistory[-1].lower().find("against") != -1):
            builder.DEFENDANT_NAME = text
            
        elif len(textHistory) > 0 and len(builder.PLAINTIFF_NAME) > 0 and len(builder.DEFENDANT_NAME) > 0 and text[-1] == ".":
            textHistory[-1] = textHistory[-1] + text
        
        textHistory.append(text)

        for attorney_keyword in keywords.ATTORNEY_KEYWORD:
            if l_text.find(attorney_keyword) != -1:
                lawyer_raw_text.append(text)

        for keyword in keywords.KEYWORDS:
            if l_text.find(keyword) != -1:
                keyword_raw_text.append(text)
        
    # print(builder)
    print("-=-=-=--=-=-=-=-=-=--=-=-=--=-=-=-=-=-=--=-=-=--=-=-=-=-=-=--=-=-=--=-=-=-=-=-=--=-=-=--=-=-=-=-=-=-")
    context = "Hugging Face provides state-of-the-art NLP models and tools."
    question = "What does Hugging Face provide?"
    result = qa_pipeline(question=question, context=context)
    print(result)