import time
import os
from selenium import webdriver
from bs4 import BeautifulSoup
import urllib.parse
import requests 
import base64

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

# https://stackoverflow.com/a/47425305
def get_file_content_chrome(driver, uri):
  result = driver.execute_async_script("""
    var uri = arguments[0];
    var callback = arguments[1];
    var toBase64 = function(buffer){for(var r,n=new Uint8Array(buffer),t=n.length,a=new Uint8Array(4*Math.ceil(t/3)),i=new Uint8Array(64),o=0,c=0;64>c;++c)i[c]="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charCodeAt(c);for(c=0;t-t%3>c;c+=3,o+=4)r=n[c]<<16|n[c+1]<<8|n[c+2],a[o]=i[r>>18],a[o+1]=i[r>>12&63],a[o+2]=i[r>>6&63],a[o+3]=i[63&r];return t%3===1?(r=n[t-1],a[o]=i[r>>2],a[o+1]=i[r<<4&63],a[o+2]=61,a[o+3]=61):t%3===2&&(r=(n[t-2]<<8)+n[t-1],a[o]=i[r>>10],a[o+1]=i[r>>4&63],a[o+2]=i[r<<2&63],a[o+3]=61),new TextDecoder("ascii").decode(a)};
    var xhr = new XMLHttpRequest();
    xhr.responseType = 'arraybuffer';
    xhr.onload = function(){ callback(toBase64(xhr.response)) };
    xhr.onerror = function(){ callback(xhr.status) };
    xhr.open('GET', uri);
    xhr.send();
    """, uri)
  if type(result) == int :
    raise Exception("Request failed with status %s" % result)
  return base64.b64decode(result)

def download_img_blobs(driver, soup, url, target):
    i = 0
    for img in soup.find_all('img'):
        i += 1
        src = img.get('src')
        if src.startswith('blob'):
            print('yo', src)
            path = target + '/' + str(i) + '.jpg'
            print('yo', path)
            with open(path, 'wb') as binary_file:
                bytes = get_file_content_chrome(driver, src)
                binary_file.write(bytes)
            img['src'] = path
    return soup

def scrape(driver, url, target):
    driver.get(url)
    time.sleep(3)
    driver.save_screenshot(archive_path + target + '.png')

    content = driver.page_source
    soup = BeautifulSoup(content, 'html.parser')

    os.mkdir(archive_path + target)
    soup = download_css_files(soup, url, archive_path + target)
    soup = download_img_blobs(driver, soup, url, archive_path + target)

    with open(archive_path + target + '/index.html', 'w') as f:
        f.write(str(soup))
