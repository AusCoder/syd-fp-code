import requests

res = {"id":"1","results":[0,1,1,2,3,5]}

#r = requests.get("http://localhost:8080/test")
r = requests.post("http://localhost:8080/results", json=res)
print(r.text)