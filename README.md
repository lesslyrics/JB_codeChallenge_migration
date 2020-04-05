# JB_codeChallenge_migration
Migrate files from old storage to the new one
## Set up and run
**navigate:**

cd to the project directory

**set a specific path to the folder to load temporary downloaded file and run:**

1 option - via terminal

mvn -q clean compile exec:java -Dexec.mainClass="com.app.rest.client.Main" -Dexec.args="{your path here}/src/main/java/com/mkyong/rest/client/data/"

2 option - manually

Set a **basePath** in the ApacheHttpClient class and run

## General ideal pipeline:
1. Receive list of all files in old storage and save their names to the list
2. Iterate through the list, for each file:
	- get file and save to the temp file in the /data directory
	- post file to the new storage
	- remove file from the old storage
	- remove temp file from the /data directory in order to effiiently use space
3. Check if all the files are migrated

However, I faced several problems during this challenge and had to modify pipeline a bit: 
1. Receive list of all files in old storage and save their names to the list
2. Iterate through the list, for each file:
	- get file and save to the temp file in the /data directory
	- post file to the new storage
	- remove temp file from the /data directory in order to effiiently use space
4. Receive list of all files in new storage and save their names to the list
5. Remove files that are already in the new storage from the old storage
6. Perform 1-5 until there are 0 files in the old storage and amount of files in new storage is equal to the amount of files in the old storage at the very beginning

Test: number of files in old storage is equal to the numer of files in the new storage

## Future improvments:

1. For now there is a redundant step of iterating through old storage several times in order to complete migration of data that was left after the main cycle of migration. It will be removed when all requests will be fixed. By that I mean, that the main issue here is that server is not stable and thus fails quite often, which could possibly lead to the data loss. I added checks for response code 500, and every time it is received the request is send again. The max number of such calls is limited and for now satated as 5. However, even with this check sometimes requests appear to be unsuccessful and I have to loop through old storage several times to transfere all the files. This could be solved by fixing requests response codes checks.  

2. The following tests need to be implemented:
 - test if DELETE returns {}
 - test if only successfully posted filed are deleted
 - test if temp file is removed
