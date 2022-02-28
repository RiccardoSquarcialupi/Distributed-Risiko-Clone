from flask import Flask

app = Flask(__name__)


@app.route('/')
def hello_world():  # put application's code here
    return 'Hello World!'


@app.route('/home', methods=['GET'])
def home():
    return "<h1>Distant Reading Archive</h1><p>This site is a prototype API.</p>"


@app.route('/product/<name>')
def get_product(name):
    return "The product is " + str(name)


if __name__ == '__main__':
    app.run()
