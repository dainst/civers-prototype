import urllib.parse
from src import urls

base_url = "https://arachne.dainst.org/entity/123"
href = "https://arachne.dainst.org/1.css"
assert ("/1.css", href) == urls.get_new_href_and_download_path(base_url, href, 0)
base_url = "https://arachne.dainst.org"
href = "https://arachne.dainst.org/1.css"
assert ("/1.css", href) == urls.get_new_href_and_download_path(base_url, href, 0)
base_url = "https://arachne.dainst.org"
href = "/1.css"
assert (href, base_url + "/1.css") == urls.get_new_href_and_download_path(base_url, href, 0)
base_url = "https://arachne.dainst.org"
href = "/1.css"
assert (href, base_url + href) == urls.get_new_href_and_download_path(base_url, href, 0)
base_url = "https://arachne.dainst.org"
href = "/a/b/c/1.css"
assert (href, base_url + href) == urls.get_new_href_and_download_path(base_url, href, 0)
base_url = "https://abc.de"
href = "https://da.de/a/b/123.css"
assert ("/0/a/b/123.css", href) == urls.get_new_href_and_download_path(base_url, href, 0)
