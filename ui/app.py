from flask import Flask , render_template,jsonify
#from flask.ext.pymongo import PyMongo
import pymongo
#from pymongo import MongoClient
#from flask_pymongo import PyMongo
app = Flask(__name__)

#app.config['MONGO_DBNAME'] = 'Twitter'
#app.config['MONGO_DBNAME'] = 'stock'
#app.config['MONGO_URI'] = 'mongodb://Ojasvi Bhalerao:yogi2525@ds125479.mlab.com:25479/animals'
#app.config['MONGO_URI'] = 'mongodb://172.19.20.222/Twitter'
#mongo = PyMongo(app)
@app.route('/')
def index():
    return render_template('/home.html')

@app.route("/bolt1.html")
def bolt1():
    return render_template('bolt1.html')

@app.route("/bolt2.html")
def bolt2():
    return render_template('bolt2.html')

@app.route("/bolt3.html")
def bolt3():
    return render_template('bolt3.html')

@app.route("/add")
def add():
    tweets = mongo.db.tweets
    tweets.insert({'name' : 'Oju'})
    return 'Added'

"""@app.route("/result.html")
def result():
    tweets = mongo.db.tweets

    result =  tweets.find({'id' : '978346625138651137' })
    return jsonify(result)
    #return 'HI
    #return 'you found' + result['id']
@app.route("/add")
def add():
    user = mongo.db.users
    user.insert({'name' : 'ojasvi'})"""


if __name__ == '__main__':
    app.run(debug=True)
