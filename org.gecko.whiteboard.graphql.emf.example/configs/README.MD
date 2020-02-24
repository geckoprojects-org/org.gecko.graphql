You can modify the config as you like:

For the EMFMongoRepositoryConfigruator you currently see:

"EMFMongoRepositoryConfigurator": 
  	{
	    "mongo.instances": "yourName",
		"yourName.baseUris" : "mongodb://localhost/",
		"yourName.databases" : "yourDB", 
		"yourName.yourDB.user" : "yourDBUser",// optional  
		"yourName.yourDB.password" : "yourDBPassword",  //optional
		"yourName.yourDB.repoType" : "PROTOTYPE" // prototype scoped service, can also be SINGLE
	}
	
You can give you instance any name you like, e.g. 'petshop' and you want to configure a database called 'catsndogs'.
Than you configuration would look like this:

"EMFMongoRepositoryConfigurator": 
  	{
	    "mongo.instances": "petshop",
		"petshop.baseUris" : "mongodb://localhost/",
		"petshop.databases" : "catsndogs", 
		"petshop.catsndogs.user" : "mietzy",  
		"petshop.catsndogs.user.env" : "USER_ENVIRONMENT_VARIABLE",  
		"petshop.catsndogs.password" : "katze",  
		"petshop.catsndogs.password.env" : "PASSWORD_ENVIRONMENT_VARIABLE",  
		"petshop.catsndogs.repoType" : "PROTOTYPE"
	}