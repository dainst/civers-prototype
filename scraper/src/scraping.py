import time
import os
from selenium import webdriver

archive_path = 'archive/'

def get_chrome_driver():
    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--window-size=1220,960')
    chrome_options.add_argument('--headless')
    chrome_options.add_argument('--disable-gpu')
    return webdriver.Chrome(chrome_options=chrome_options)

def scrape(driver, url, target):
    driver.get(url)
    time.sleep(3)
    driver.save_screenshot(archive_path + target + '.png')

    content = driver.page_source
    os.mkdir(archive_path + target)
    f = open(archive_path + target + '/index.html', 'w')
    f.write(content)
    f.close() 

    print(driver.title)
    print(driver.current_url)
