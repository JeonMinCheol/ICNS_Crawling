from bypass import *
from selenium.webdriver import Chrome, ChromeOptions
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium_stealth import stealth
import subprocess
import time
from selenium.webdriver.common.action_chains import ActionChains
import pyautogui
import threading
import uuid
import pygetwindow as gw

links = []
subprocess.Popen(r'C:/Program Files/Google/Chrome/Application/chrome.exe --remote-debugging-port=9222 --user-data-dir="C:/chromeCookie"')
# keyword 검색
options = Options()
options.add_argument("--disable-extensions")
options.add_experimental_option("debuggerAddress", "127.0.0.1:9222")
options.add_argument('C:/Users/User/AppData/Local/Naver/Naver Whale/User Data/Profile 1')
options.add_argument('--no-sandbox')
options.add_argument('--disable-dev-shm-usage')
options.add_argument("--start-maximized")
options.add_argument("--headless")
options.add_argument('--ignore-certificate-errors')
options.add_argument("--single-process")
options.binary_location = "C:/Program Files/Google/Chrome/Application/"

service = Service(executable_path=ChromeDriverManager().install())
driver = Chrome(service=service, options=options)

def save_pdf(link, name):
    # 링크로 새로운 탭 열기
    current_window = driver.current_window_handle

    driver.execute_script(f"window.open('{link}');")
    time.sleep(2)  # 페이지 로드 대기

    all_windows = driver.window_handles

    #  새로 열린 탭으로 전환합니다.
    for window in all_windows:
        if window != current_window:
            driver.switch_to.window(window)  # 새 탭으로 전환
            break

    # Ctrl + S 키 입력 시뮬레이션
    pyautogui.hotkey('ctrl', 's')
    time.sleep(1)  # 대화 상자가 열리는 시간에 따라 조정

    # Enter 키 입력하여 저장
    pyautogui.typewrite(name)
    pyautogui.press('enter')
    pyautogui.press('left')
    pyautogui.press('enter')

    # 현재 탭만 닫습니다.
    driver.close()

    # 원래 탭으로 다시 전환합니다.
    driver.switch_to.window(current_window)

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
driver.get("https://iapps.courts.state.ny.us/lawReporting/Search") 
cookie = driver.get_cookie("__cf_bm")
driver.add_cookie(cookie)

date = [31,29,31,30,31,30,31,31,30,31,30,31]
soup = BeautifulSoup()
for year in range(2024, 1998, -1):
    for month in range(8 if year == 2024 else 12, 0, -1):
        time.sleep(5)
        driver.get("https://iapps.courts.state.ny.us/lawReporting/Search") 
        time.sleep(5)
        # Decision Date 입력란 선택 후 날짜 입력
        decision_date_field = driver.find_element(By.ID, "dtStartDate")  # 실제 name 속성 사용
        decision_date_field.send_keys(f"{month if month >= 10 else str(0) + str(month)}/01/{year}")  # 원하는 날짜 입력

        # End Date 입력란 선택 후 날짜 입력
        end_date_field = driver.find_element(By.ID, "dtEndDate")  # 실제 name 속성 사용
        end_date_field.send_keys(f"{month if month >= 10 else str(0) + str(month)}/{date[month-1]}/{year}")  # 원하는 날짜 입력

        # 'Search by Court' 드롭다운 메뉴 선택
        court_dropdown = Select(driver.find_element(By.NAME, "court"))  # 실제 name 속성 사용
        court_dropdown.select_by_visible_text("Other Courts")  # 실제로 목록에 있는 항목을 선택

        # 'Find' 버튼 클릭
        find_button = driver.find_element(By.NAME, "Submit")  # 실제 name 속성 사용
        find_button.click()

        # 페이지 로드 대기
        time.sleep(5)

        links = driver.find_elements(By.CSS_SELECTOR, "font > a")
        f = open(f"./link_{year}_{month}.txt", 'w')
        for idx, x in enumerate(links):
            link = str(x.get_attribute("href"))
            if link.find("pdf") != -1:
                f.write(link+"\n")
                save_pdf(link, f"{year}_{month}_{idx}")
        f.close()
        # 작업 완료 후 브라우저 종료
driver.quit()
