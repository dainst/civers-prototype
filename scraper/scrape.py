import sys
from src import scraping

driver = scraping.get_chrome_driver()

if len(sys.argv) != 3:
    print("expected 2 args: url and target")
    sys.exit(1)

url = sys.argv[1]
target = sys.argv[2]

scraping.scrape(driver, url, target, "2023-02-01")
