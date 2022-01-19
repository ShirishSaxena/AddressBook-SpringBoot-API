import urllib.request, json
import pyperclip

'''
Very basic sample generate that generates multiple samples to use as a test in postman.
The code isn't that efficient here and also uses free API with limited queries???, but it does the work so...


Generates sample and copy to your clipboard.
Needs pyperclip
'''

URL = "https://randomuser.me/api/?inc=name,location,email,phone&noinfo"

count = 20;

def generateRecords():
    finalStr = '''['''
    for i in range(0, count):
      finalStr += getRandomRecord() + ","
    
    finalStr = finalStr[ : -1] + "\n]"

    return finalStr


def listToString(s): 
    str1 = "" 
    for ele in s: 
        str1 += str(ele) + ", "  
    return str1

def getRandomRecord():
    with urllib.request.urlopen(URL) as url:
        data = json.loads(url.read().decode())
        
        FirstName = data["results"][0]['name']['first']
        LastName =  data["results"][0]['name']['last']
        email = data["results"][0]['email']
        PhoneNo = data["results"][0]['phone']

        street = listToString(list(data["results"][0]['location']['street'].values()))
        city = data["results"][0]['location']['city']
        state = data["results"][0]['location']['state']
        country = data["results"][0]['location']['country']
        postcode = data["results"][0]['location']['postcode']
        Address = "%s%s, %s, %s(%s)" %(street, city, state, country, postcode)

    
        Pattern = '''
    {
        "firstName" : "%s",
        "lastName" : "%s",
        "email" : "%s",
        "address" : "%s",
        "phoneNo" : "%s"
    }''' %(FirstName, LastName, email, Address, PhoneNo)

        return Pattern


result = generateRecords()
pyperclip.copy(result)
print(result)
