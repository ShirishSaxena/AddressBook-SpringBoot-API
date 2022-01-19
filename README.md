# AddressBook-SpringBoot-API
Simple springboot API for addressBook. Supports all REST controllers and have custom error handling for every specific case.

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
	
	Run `Generate Random POST Requests.py`
	Change these variables as needed
	```
	count = 20 # generate count no of random records
	```
	It'll generate List of POST req and copy to your clipboard. Finally, paste it on POSTMAN...

## Updates
	* To be added
  
## API supports all REST mappings (GET, POST, PUT, DELETE)
### GET requests
