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

def scrape(driver, url, resource_version_id, existing_description):
    driver.get(url)
    time.sleep(3)

    content = driver.page_source
    soup = BeautifulSoup(content, 'html.parser')

    description = "" 
    civers_description = soup.find(id="civers-description")
    
    if civers_description:
        description = civers_description.get_text().strip()

    if existing_description == description and description != "" and existing_description != "":
        return description

    archive(driver, soup, resource_version_id, url)

    return description
