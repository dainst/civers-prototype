import time
import os
from selenium import webdriver
from bs4 import BeautifulSoup
import urllib.parse
import requests 

archive_path = 'archive/'

def get_chrome_driver():
    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--window-size=1220,960')
    chrome_options.add_argument('--headless')
    chrome_options.add_argument('--disable-gpu')
    return webdriver.Chrome(chrome_options=chrome_options)

def get_artifact_url(url_without_path, href):
    artifact_url = ''
    path = urllib.parse.urlparse(href).path

    if href.startswith('http'):
        artifact_url = href
    else:
        if path.startswith('/'):
            artifact_url = url_without_path + path
        else:
            artifact_url = url_without_path + '/' + path

    if not path.startswith('/'):
        path = '/' + path
    return path, artifact_url
        
def is_eligible_for_download(href):
    return (href.endswith('css') and ((href.startswith('http') and href.count('/') == 3) 
        or (href.startswith('/') and href.count('/') == 1) 
        or href.count('/') == 0))

def download_css_files(soup, url, target):
    url = url.split('?')[0]
    path = urllib.parse.urlparse(url).path
    url_without_path = url.replace(path, '')

    for link in soup.find_all('link'):
        href = link.get('href')
        if not is_eligible_for_download(href):
            continue

        path, download_path = get_artifact_url(url_without_path, href)

        link['href'] = target + path

        c = download_path.count('/')
        if (c != 3):
            continue
        artifact = download_path.split('/')[3]

        r = requests.get(download_path)
        if r.status_code == 200:
            with open(target + '/' + artifact, 'w') as f:
                f.write(r.text)

    return soup

def scrape(driver, url, target):
    driver.get(url)
    time.sleep(3)
    driver.save_screenshot(archive_path + target + '.png')

    content = driver.page_source
    soup = BeautifulSoup(content, 'html.parser')

    os.mkdir(archive_path + target)
    soup = download_css_files(soup, url, archive_path + target)

    with open(archive_path + target + '/index.html', 'w') as f:
        f.write(str(soup))
