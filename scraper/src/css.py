import os
import requests
import urllib.parse
from src import urls

def get_new_href_and_download_path(base_url, href, i):

    if not href.startswith("http"):
        if not href.startswith("/"):
            # this seems not to be correct (relative path), but is needed for the styles.css and main.css of the arachne entity pages
            return "/" + href, urls.url_without_path(base_url) + "/" + href
        return href, urls.url_without_path(base_url) + href

    if base_url.startswith(urls.url_without_path(href)):
        return href.replace(urls.url_without_path(href), ""), href
    
    # naive version to prevent us to have to url encode the url to be suitable as folder name
    return "/" + str(i) + href.replace(urls.url_without_path(href), ""), href

def download_css_files(soup, url, base_path):
    i = 0
    for link in soup.find_all('link'):
        href = link.get('href')
        if (not href.endswith('css') 
                # propylaeum test
                and not href.endswith('?roxvr9')): 
            continue
        
        # propylaeum test
        href = href.replace("?roxvr9", "")

        new_href, download_path = get_new_href_and_download_path(url, href, i)

        relative_path = base_path + new_href

        r = requests.get(download_path)
        if r.status_code == 200:
            folder, file = os.path.split(relative_path)
            os.makedirs(folder, exist_ok=True)
            with open(relative_path, 'w') as f:
                f.write(r.text)

        link['href'] = "/" + relative_path
        i += 1
    return soup
