import urllib.parse
import requests

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
