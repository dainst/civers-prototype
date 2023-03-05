import urllib.parse

# TODO get rid of this, on call-sites, replace with calls to get_new_href_and_download_path
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

def url_without_path(url):
    url = url.split('?')[0]
    path = urllib.parse.urlparse(url).path
    return url.replace(path, '')

def url_with_simple_path(path):
    """
    Given a url, returns True
    if it has 3 occurrences of "/", like
    http(s)://abc.org/abc.png
    This is to check that we have a path (/abc.png),
    but one which is not complex (like /abc/def.png).
    """
    return path.count("/") == 3

def get_new_href_and_download_path(base_url, href, i):

    if not href.startswith("http"):
        if not href.startswith("/"):
            # this seems not to be correct (relative path), but is needed for the styles.css and main.css of the arachne entity pages
            return "/" + href, url_without_path(base_url) + "/" + href
        return href, url_without_path(base_url) + href

    if base_url.startswith(url_without_path(href)):
        return href.replace(url_without_path(href), ""), href
    
    # naive version to prevent us to have to url encode the url to be suitable as folder name
    return "/" + str(i) + href.replace(url_without_path(href), ""), href
