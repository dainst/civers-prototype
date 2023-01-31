from src import scraping

from flask import Flask, request, jsonify
app = Flask(__name__)

driver = scraping.get_chrome_driver()

@app.route('/api/archive', methods=['POST'])
def take_screenshot():
    url = request.json['url']
    target = request.json['target']
    scraping.scrape(driver, url, target)
    return jsonify(status="ok")

if __name__ == '__main__':
    app.run(
        # Necessary for it to work inside a Docker container
        host="0.0.0.0"
    )

# driver.quit()