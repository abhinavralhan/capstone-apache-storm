import pymongo
import pprint

#MONGO_HOST = 'mongodb://172.19.20.222:27017'
#MONGO_DB = "Twitter"


#client = pymongo.MongoClient(MONGO_HOST)
#db = client
#print(db.)

#print(client)
c = pymongo.MongoClient('172.19.20.222',27017)
db = c['Twitter']
l = db['tweets']
#print(db.tweets.find({}))

print(db.stats())
cursor = db.tweets.find({})
#for document in cursor:
#	print(document)
