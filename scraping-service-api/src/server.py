from flask import Flask
app = Flask(__name__)

@app.route('/api')
def hello_world():
    return 'Hello World!'

if __name__ == '__main__':
    app.run(
        # Necessary for it to work inside a Docker container
        host="0.0.0.0"
    )
