# AddressBook-SpringBoot-API
Simple springboot API for addressBook with redis cache. Supports all REST controllers and have custom error handling for every specific case.

## Updates
	* 20-Jan-2022 (v0.0.2)
	   - Added support to get by phoneNo to method that gets record by either Id or email.
	   - Changed getMapping from /api/get/ to /api/
	   - Improved code to get records by id/email/phoneNo (all 3 are unique)
	   - Added redis cache to more methods with cacheEvict key entries on save/saveAll/Update/delete.
	  
## Assumptions
 - Assumed that in an addressBook, email and PhoneNo will always be unique. And per record can have atmost 1 phoneNo and Email.
 - And that no one is using existing database with duplicate entries.
 - Any record in the database is unique for field email and phoneNo.
 - For now phoneNo field doesn't have any number validation regex, which I do plan on adding it in next update cycle. So with that said, one can input email or any other field as PhoneNo, that might contradict with findBy methods. I hope atleast until v.0.3 no one does :3
 
	
### Setup

1. **Clone the application**

	```bash
	git clone https://github.com/ShirishSaxena/AddressBook-SpringBoot-API.git
	cd AddressBook-SpringBoot-API
	```

2. **Create MySQL database**

	```bash
	create database assign3rd
	```

3. **Change MySQL username and password as per your MySQL installation**

	+ open `src/main/resources/application.properties` file.

	+ change `spring.datasource.username` and `spring.datasource.password` properties as per your mysql installation

4. **Run the app**

	You can run the spring boot app by typing the following command -

	```bash
	mvn spring-boot:run
	```

	The server will start on port 7090. And can be changed from application.properties

	You can also package the application in the form of a `jar` file and then run it like so -

	```bash
	mvn package
	java -jar target/addressBook-0.0.1-SNAPSHOT.jar
	```
4. **Generate Logs (Optional)**
	```pip install pyperclip```
	
	Run `sampleGenerator.py`
	Change these variables as needed
	```
	count = 20 # generate count no of random records
	```
	It'll generate List of POST req and copy to your clipboard. Finally, paste it on POSTMAN...

	
## Further Improvements
 - Implement findByPhoneNo to GET,PUT,DELETE req. (Added 20-Jan-22 update)
 - Improve queries to saveAll

## Error codes
```
   GET Mapping
	G100 : No records by id.
	G101 : No records by Email.
	G102 : No records by PhoneNo.
	G103 : No records by Name(First And/Or Last).

   POST Mapping (RequestBody validation)
	P100 : firstName is required and can not be null or empty. { "firstName" : "yourName"}
	P101 : lastName is required and can not be null or empty. { "lastName" : "yourName"}
	P102 : Email is required and can not be null or empty. { "email" : "valid@email"}
	P103 : phoneNo is required and can not be null or empty.
	P104 : address is required and can not be null or empty.

	P111 : Not a valid email.
	P112 : Duplicate email found.
	P121 : Duplicate PhoneNo found.

   PUT Mapping
	U100 : No record found to update [id/email].

   DELETE Mapping
	D100 : Parameter not in id or email.
```

# API supports all REST mappings (GET, POST, PUT, DELETE)
All of the requests made in these screenshot are from remote database with about 200ms ping, so response is slow.
### GET requests
* Get all records saved in the addressBook.
	```
	localhost:7090/api/getAll/
	```
	
	![image](https://user-images.githubusercontent.com/6762915/150171183-4eeb3bda-9d9a-4910-89c2-7f798faf2280.png)

* Get By ID or EmailAddress or PhoneNo(all are Unique field)
	```
	localhost:7090/api/{parameter}
	```
	
	![gif](http://200.showy.life:6969/rc6hArgBmZ.gif)
	
	
* Get By FirstName or LastName or both
       ```
       localhost:7090/api/getName/{param}
       
       param here can be single word, that is it will find that word in both firstName and LastName.
       or it can have single space {ex. Adam Evee} then it'll find all matching records having firstName as 'Adam' and lastName as 'Evee'.
       
* Get By Address
       ```
       localhost:7090/api/getAddress/{param}
       ```
       Finds all records matching the param in address field of database.
       
       
### POST requests (with error handling)
* Add single record 
	```
	localhost:7090/api/
	
	{
		"firstName" : "firstName",
		"lastName" : "lastName",
		"email" : "valid@email",
		"address" : "Any address???",
		"phoneNo" : "000000000"
	}
	```

	
	![image](http://200.showy.life:6969/ZqSSbctdEV.gif)
	
* Add multiple records
	```
	localhost:7090/api/saveAll
	[
	{
		"firstName" : "firstName",
		"lastName" : "lastName",
		"email" : "valid@email",
		"address" : "Any address???",
		"phoneNo" : "000000000"
	},
	
	{
		"firstName" : "firstName",
		"lastName" : "lastName",
		"email" : "valid@email",
		"address" : "Any address???",
		"phoneNo" : "000000000"
	}
	
	]
	```
	
	![image](http://200.showy.life:6969/IgHAfj31NE.gif)


### DELETE requests (Support delete by either id or email)
	```
	localhost:7090/api/
	```
![image](http://200.showy.life:6969/nIFHIq7bVM.gif)

### PUT requests (need id or email to not be null)
	```
	localhost:7090/api/
	```

![image](http://200.showy.life:6969/0PSIsehkdY.gif)
