from flask import Flask , render_template, jsonify, request
import json
from bson import json_util
from bson.json_util import dumps
#from flask.ext.pymongo import PyMongo
import pymongo
from pymongo import MongoClient
from flask_pymongo import PyMongo
import pandas as pd

app = Flask(__name__)

MONGODB_HOST = '172.19.20.222'
#MONGODB_HOST = '192.168.20.12'
MONGODB_PORT = 27017
DBS_NAME = 'Twitter'
COLLECTION_NAME = ['Senti', 'WordCount', 'CountryCount', 'HashCount']
FIELDS = {'_id':True}

@app.route('/')
def index():
    return render_template('home.html')

@app.route("/bolt1.html")
def bolt1():
    #print(rows)
    return render_template('bolt1.html')

@app.route("/bolt2.html")
def bolt2():
    return render_template('bolt2.html')

@app.route("/bolt3.html")
def bolt3():
    return render_template('')

@app.route("/bolt4.html")
def bolt4():
    return render_template('bolt4.html')

@app.route("/add1")
def gen_graphs1():
    connection = MongoClient(MONGODB_HOST, MONGODB_PORT)
    collection = connection[DBS_NAME][COLLECTION_NAME[0]]
    rows = collection.find(projection=FIELDS).sort([('_id',-1)]).limit(40)

    #tweets = collection.find({'text':True}).limit(5)
    json_projects = []
    for row in rows:
        json_projects.append(row)
    json_projects = json.dumps(json_projects, default=json_util.default)
    #print(json_projects)
    return json_projects

@app.route("/add2")
def gen_graphs2():
    connection = MongoClient(MONGODB_HOST, MONGODB_PORT)
    collection = connection[DBS_NAME][COLLECTION_NAME[1]]
    rows = collection.find(projection=FIELDS).sort([('count',-1)]).limit(30)
    #tweets = collection.find({'text':True}).limit(5)
    json_projects = []
    for row in rows:
        json_projects.append(row)
    json_projects = json.dumps(json_projects, default=json_util.default)
    return json_projects

@app.route("/add3")
def gen_graphs3():
    connection = MongoClient(MONGODB_HOST, MONGODB_PORT)
    collection = connection[DBS_NAME][COLLECTION_NAME[2]]
    rows = collection.find(projection=FIELDS).limit(10)

    #tweets = collection.find({'text':True}).limit(5)
    json_projects = []
    for row in rows:
        json_projects.append(row)
    json_projects = json.dumps(json_projects, default=json_util.default)
    #print(json_projects)
    return json_projects

@app.route("/add4")
def gen_graphs4():
    connection = MongoClient(MONGODB_HOST, MONGODB_PORT)
    collection = connection[DBS_NAME][COLLECTION_NAME[3]]
    rows = collection.find(projection=FIELDS).sort([('count',-1)]).limit(30)
    json_projects = []
    for row in rows:
        json_projects.append(row)
    json_projects = json.dumps(json_projects, default=json_util.default)
    #print(json_projects)
    return json_projects
if __name__ == '__main__':
    app.run()
