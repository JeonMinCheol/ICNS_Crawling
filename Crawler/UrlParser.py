import pymysql
from bypass import *
import keywords
from Builder import URLBuilder, QueryBuilder
import math
import model
import traceback
from formattor import DateFormattor, Tokenizer
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium_stealth import stealth
import subprocess
import time

END_POINT = "05/21/1876"
CONN = pymysql.connect(host="163.180.117.35", user="user", password="my0504", port= 3306, database="mysql_db", charset="utf8")                        
CURSOR = CONN.cursor()
request = Request()
nle_model = model.NLE()
date_formattor = DateFormattor()
query_builder = QueryBuilder(CURSOR)

links = []

# keyword 검색
subprocess.Popen(r'C:/Program Files/Google/Chrome/Application/chrome.exe --remote-debugging-port=9222 --user-data-dir="C:/chromeCookie"')
chrome_options = Options()
chrome_options.add_experimental_option("debuggerAddress", "127.0.0.1:9222")
chrome_options.add_argument('C:/Users/User/AppData/Local/Naver/Naver Whale/User Data/Profile 1')
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--disable-dev-shm-usage')
chrome_options.add_argument("start-maximized")
chrome_options.add_argument("--headless")
chrome_options.add_argument('--ignore-certificate-errors')
chrome_options.add_argument("--single-process")
chrome_options.binary_location = "C:/Program Files/Google/Chrome/Application/"

service = Service(executable_path=ChromeDriverManager().install())
driver = webdriver.Chrome(service=service, options=chrome_options)

# selenium stealth 옵션추가 ( cloudflare 우회용 )
stealth(
    driver,
    languages=["en-US", "en"],
    vendor="Google Inc.",
    platform="Win32",
    webgl_vendor="Intel Inc.",
    renderer="Intel Iris OpenGL Engine",
    fix_hairline=True,
)

for year in range(2024, 1900, -1):
    for month in range(31, 0, -1):
        if year % 2 == 1 and month == 31:
            month = 30
        elif month == 2:
            
            driver.get("https://iapps.courts.state.ny.us/lawReporting/Search") 

        # Decision Date 입력란 선택 후 날짜 입력
        decision_date_field = driver.find_element(By.ID, "dtStartDate")  # 실제 name 속성 사용
        decision_date_field.send_keys("12/01/2023")  # 원하는 날짜 입력

        # End Date 입력란 선택 후 날짜 입력
        end_date_field = driver.find_element(By.ID, "dtEndDate")  # 실제 name 속성 사용
        end_date_field.send_keys("12/31/2023")  # 원하는 날짜 입력

        # 'Search by Court' 드롭다운 메뉴 선택
        court_dropdown = Select(driver.find_element(By.NAME, "court"))  # 실제 name 속성 사용
        court_dropdown.select_by_visible_text("Other Courts")  # 실제로 목록에 있는 항목을 선택

        # 'Find' 버튼 클릭
        find_button = driver.find_element(By.NAME, "Submit")  # 실제 name 속성 사용
        find_button.click()

        # 페이지 로드 대기
        time.sleep(15)

        links = driver.find_elements(By.CSS_SELECTOR, "font > a")
        f = open(f"/link_{year}_{month}.txt", 'w')
        for x in links:
            link = str(x.get_attribute("href"))
            if link.find("pdf") != -1:
                f.write(link)

        # 작업 완료 후 브라우저 종료
        driver.quit()

