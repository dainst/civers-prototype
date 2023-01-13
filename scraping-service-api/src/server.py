from selenium import webdriver
# import sys
import time

chrome_options = webdriver.ChromeOptions()
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--window-size=1920,1080')
chrome_options.add_argument('--headless')
chrome_options.add_argument('--disable-gpu')
driver = webdriver.Chrome(chrome_options=chrome_options)

from flask import Flask, request
app = Flask(__name__)

# sys.exit()

# url = 'https://arachne.dainst.org/entity/2003162?fl=20&q=*&resultIndex=1'

@app.route('/api/take-screenshot', methods=['POST'])
def hello_world():
    url = request.json['url']
    driver.get(url)
    time.sleep(3)
    driver.save_screenshot('storage/test.png')
    print(driver.title)
    print(driver.current_url)
    driver.quit()
    return 'Hello World!'

if __name__ == '__main__':
    app.run(
        # Necessary for it to work inside a Docker container
        host="0.0.0.0"
    )
