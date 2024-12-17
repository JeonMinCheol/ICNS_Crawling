from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select

import subprocess
import time
import pyautogui
import pygetwindow as gw

from seleniumbase import Driver

driver = Driver(uc=True)
links = []

def save_pdf(link, name):
    # 링크로 새로운 탭 열기
    current_window = driver.current_window_handle

    driver.execute_script(f"window.open('{link}');")
    
    all_windows = driver.window_handles

    #  새로 열린 탭으로 전환합니다.
    for window in all_windows:
        if window != current_window:
            driver.switch_to.window(window)  # 새 탭으로 전환
            break
        
    time.sleep(4)  # 페이지 로드 대기

    # Ctrl + S 키 입력 시뮬레이션
    pyautogui.click()
    pyautogui.hotkey('ctrl', 's')
    time.sleep(1)  # 대화 상자가 열리는 시간에 따라 조정

    # Enter 키 입력하여 저장
    pyautogui.press('enter')
    pyautogui.press('left')
    pyautogui.press('enter')
    # 현재 탭만 닫습니다.
    driver.close()

    # 원래 탭으로 다시 전환합니다.
    driver.switch_to.window(current_window)

# selenium stealth 옵션추가 ( cloudflare 우회용 )

driver.get("https://iapps.courts.state.ny.us/lawReporting/Search") 

date = [31,28,31,30,31,30,31,31,30,31,30,31]
for year in range(2009, 2020):
    try:
        for month in range(3 if year == 2009 else 0, 12):
            driver.get("https://iapps.courts.state.ny.us/lawReporting/Search") 
            # Decision Date 입력란 선택 후 날짜 입력
            time.sleep(4)
            decision_date_field = driver.find_element(By.ID, "dtStartDate")  # 실제 name 속성 사용
            decision_date_field.send_keys(f"{month+1 if month+1 >= 10 else str(0) + str(month+1)}/{str(0)+str(1)}/{year}")  # 원하는 날짜 입력

            # End Date 입력란 선택 후 날짜 입력
            end_date_field = driver.find_element(By.ID, "dtEndDate")  # 실제 name 속성 사용
            end_date_field.send_keys(f"{month+1 if month+1 >= 10 else str(0) + str(month+1)}/{str(15)}/{year}")  # 원하는 날짜 입력

            # 'Search by Court' 드롭다운 메뉴 선택
            court_dropdown = Select(driver.find_element(By.NAME, "court"))  # 실제 name 속성 사용
            court_dropdown.select_by_visible_text("Other Courts")  # 실제로 목록에 있는 항목을 선택

            # 'Find' 버튼 클릭
            find_button = driver.find_element(By.NAME, "Submit")  # 실제 name 속성 사용
            find_button.click()

            # 페이지 로드 대기
            time.sleep(5)

            links = driver.find_elements(By.CSS_SELECTOR, "font > a")
            f = open(f"./link_{year}_{month+1}.txt", 'w')
            for idx, x in enumerate(links):
                link = str(x.get_attribute("href"))
                if link.find("pdf") != -1:
                    f.write(link+"\n")
                    save_pdf(link, f"{year}_{month+1}_{idx}")
            f.close()
            # 작업 완료 후 브라우저 종료

            print(f"startdate : {month+1 if month+1 >= 10 else str(0) + str(month+1)}/{str(0)+str(1)}/{year}")
            print(f"enddate : {month+1 if month+1 >= 10 else str(0) + str(month+1)}/{str(15)}/{year}")
    except Exception:
        x = open("logs.txt", "a")
        x.write(f"{year}_{month+1}")
        x.close()
    start = 0
    
driver.close()