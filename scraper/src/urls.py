import urllib.parse

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
