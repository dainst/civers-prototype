import requests
from src import urls
  
def is_eligible_for_download(href):
    return (href.endswith('css') and ((href.startswith('http') and href.count('/') == 3) 
        or (href.startswith('/') and href.count('/') == 1) 
        or href.count('/') == 0))

def download_css_files(soup, url, resource_version_id):
    url_without_path = urls.url_without_path(url)

    for link in soup.find_all('link'):
        href = link.get('href')
        if not is_eligible_for_download(href):
            continue

        url_path, download_path = urls.get_artifact_url(url_without_path, href)

        link['href'] = resource_version_id + url_path

        if not urls.url_with_simple_path(download_path):
            continue;
        
        # TODO isn't this equal to the url_path?
        artifact = download_path.split('/')[3]

        r = requests.get(download_path)
        if r.status_code == 200:
            with open(resource_version_id + '/' + artifact, 'w') as f:
                f.write(r.text)

    return soup
