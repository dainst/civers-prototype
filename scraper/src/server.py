from src import scraping
from src import webdriver
import requests

from flask import Flask, request, jsonify
app = Flask(__name__)

driver = webdriver.get_chrome_driver()

@app.route('/api/archive', methods=['POST'])
def take_screenshot():
    url = request.json['url']
    resource_version_id = request.json['target']
    existing_last_updated = request.json['existingLastUpdated']
    last_updated = scraping.scrape(driver, url, resource_version_id, existing_last_updated)
    return jsonify(lastUpdated=last_updated)

if __name__ == '__main__':
    app.run(
        # Necessary for it to work inside a Docker container
        host="0.0.0.0"
    )

# driver.quit()
