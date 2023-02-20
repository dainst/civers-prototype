from src import scraping
from src import webdriver
import requests

from flask import Flask, request, jsonify
app = Flask(__name__)

driver = webdriver.get_chrome_driver()

@app.route('/api/archive', methods=['POST'])
def take_screenshot():
    url = request.json['url']
    target_artifact_identifier = request.json['target']
    existing_description = request.json['existingDescription']
    description = scraping.scrape(driver, url, target_artifact_identifier, existing_description)
    return jsonify(description=description)

if __name__ == '__main__':
    app.run(
        # Necessary for it to work inside a Docker container
        host="0.0.0.0"
    )

# driver.quit()
