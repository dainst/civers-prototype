import urllib.parse

def url_without_path(url):
    url = url.split('?')[0]
    path = urllib.parse.urlparse(url).path
    return url.replace(path, '')

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
