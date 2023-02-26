import time
import os
from bs4 import BeautifulSoup

from src import css
from src import imgs

archive_path = 'archive/'

def archive(driver, soup, resource_version_id, url):
    driver.save_screenshot(archive_path + resource_version_id + '.png')

    os.mkdir(archive_path + resource_version_id)
    soup = css.download_css_files(soup, url, archive_path + resource_version_id)
    soup = imgs.download_imgs(driver, soup, url, archive_path + resource_version_id)

    with open(archive_path + resource_version_id + '/index.html', 'w') as f:
        f.write(str(soup))

def scrape(driver, url, resource_version_id, existing_last_updated):
    driver.get(url)
    time.sleep(3)

    content = driver.page_source
    soup = BeautifulSoup(content, 'html.parser')

    last_updated = "" 
    civers_last_updated = soup.find(id="civers-last-updated")
    
    if civers_last_updated:
        last_updated = civers_last_updated.get_text().strip()

    if existing_last_updated == last_updated and last_updated != "" and existing_last_updated != "":
        return last_updated

    archive(driver, soup, resource_version_id, url)

    return last_updated
