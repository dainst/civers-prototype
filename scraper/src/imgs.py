import base64
import requests
from src import urls

# https://stackoverflow.com/a/47425305
def get_file_content_chrome(driver, uri):
  result = driver.execute_async_script("""
    var uri = arguments[0];
    var callback = arguments[1];
    var toBase64 = function(buffer){for(var r,n=new Uint8Array(buffer),t=n.length,a=new Uint8Array(4*Math.ceil(t/3)),i=new Uint8Array(64),o=0,c=0;64>c;++c)i[c]="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charCodeAt(c);for(c=0;t-t%3>c;c+=3,o+=4)r=n[c]<<16|n[c+1]<<8|n[c+2],a[o]=i[r>>18],a[o+1]=i[r>>12&63],a[o+2]=i[r>>6&63],a[o+3]=i[63&r];return t%3===1?(r=n[t-1],a[o]=i[r>>2],a[o+1]=i[r<<4&63],a[o+2]=61,a[o+3]=61):t%3===2&&(r=(n[t-2]<<8)+n[t-1],a[o]=i[r>>10],a[o+1]=i[r>>4&63],a[o+2]=i[r<<2&63],a[o+3]=61),new TextDecoder("ascii").decode(a)};
    var xhr = new XMLHttpRequest();
    xhr.responseType = 'arraybuffer';
    xhr.onload = function(){ callback(toBase64(xhr.response)) };
    xhr.onerror = function(){ callback(xhr.status) };
    xhr.open('GET', uri);
    xhr.send();
    """, uri)
  if type(result) == int :
    raise Exception("Request failed with status %s" % result)
  return base64.b64decode(result)

# TODO extract into utils module
def write_binary_file(target_file_relative_storage_path, bytes):
    with open(target_file_relative_storage_path, 'wb') as binary_file:
        binary_file.write(bytes)

def download_img_blob(driver, src, target_file_relative_storage_path):
    bytes = get_file_content_chrome(driver, src)
    write_binary_file(target_file_relative_storage_path, bytes)
    
def download_img_file(download_path, target_file_relative_storage_path):
    r = requests.get(download_path)
    if r.status_code == 200:
        bytes = r.content
        write_binary_file(target_file_relative_storage_path, bytes)

def download_imgs(driver, soup, url, target_artifact_identifier):
    url_without_path = urls.url_without_path(url)
    i = 0
    for img in soup.find_all('img'):
        i += 1
        src = img.get('src')
        target_file_relative_storage_path = target_artifact_identifier + '/' + str(i) + '.jpg'
        
        if src.startswith('blob'):
            download_img_blob(driver, src, target_file_relative_storage_path)
        else:
            _url_path, download_path = urls.get_artifact_url(url_without_path, src)
            if not urls.url_with_simple_path(download_path):
                continue
            download_img_file(download_path, target_file_relative_storage_path)            

        img['src'] = "/" + target_file_relative_storage_path

    return soup
