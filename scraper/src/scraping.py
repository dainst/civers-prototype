import time
import os
from bs4 import BeautifulSoup

from src import css
from src import imgs

archive_path = 'archive/'

def archive(driver, soup, target_artifact_identifier, url):
    driver.save_screenshot(archive_path + target_artifact_identifier + '.png')

    os.mkdir(archive_path + target_artifact_identifier)
    soup = css.download_css_files(soup, url, archive_path + target_artifact_identifier)
    soup = imgs.download_imgs(driver, soup, url, archive_path + target_artifact_identifier)

    with open(archive_path + target_artifact_identifier + '/index.html', 'w') as f:
        f.write(str(soup))

def scrape(driver, url, target_artifact_identifier, existing_description):
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

    archive(driver, soup, target_artifact_identifier, url)

    return description
