
**Dynamic Redis Config Loader**

This is a small Spring Boot project I built to make working with Redis easier and more flexible.
Instead of hard-coding one single Redis setup, the app can read different configurations from simple .properties files that live outside the project. That way, I can switch environments (dev / test / prod) without rebuilding the app.

**Why I built it**

I wanted a quick way to change Redis host, port, db number, etc. without touching the code or redeploying. Just drop a new file, restart the app, and it connects to the right Redis.

**How it works**
	•	I keep a folder (mine is E:/redis-config) with one file per cache.
Example: session.properties contains things like redis.host=localhost and redis.port=6379.
	•	When the API gets a cacheName in the URL, it loads the matching file, builds a Redis connection, and uses that to store or fetch data.

Endpoints**
**	•	GET  /cache/config/{cacheName} – shows the settings from the file.
	•	POST /cache/{cacheName}/put?key=K&value=V – stores a value in Redis.
	•	GET  /cache/{cacheName}/get?key=K – retrieves the value.

**How to run**
	1.	In src/main/resources/application.properties, set:

app.redis.config.base=E:/redis-config (this is mine but you can put your path)
server.port=8081


	2.	Create the folder (e.g., E:/redis-config) and put a .properties file inside, like:

redis.host=localhost
redis.port=6379
redis.db=0
redis.timeout.ms=2000
redis.ttl.seconds=3600


	3.	Run the app from your IDE (or mvn spring-boot:run).
	4.	Test with Postman or your browser:
	•	GET  http://localhost:8081/cache/config/session
	•	POST http://localhost:8081/cache/session/put?key=name&value=Mariam
	•	GET  http://localhost:8081/cache/session/get?key=name
