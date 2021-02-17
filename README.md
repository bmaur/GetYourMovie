# GetYourMovie- movie search service
##### Is a movie search service allows you to get basic information about the selected movie.
##Table of contents
* General info
* Technologies
* Features
* Setup
####Page address and test account to log in
* [GetYourMovie](http://filmweb-env.eba-r2xc5km2.eu-central-1.elasticbeanstalk.com)
* Test account : 
  - Username: `testuser@mail.com`
  - Password: `qwas1234`

##General info
##### The website allows you to create an account, and this in turn allows you to add movies to the favorites section, to the movies to watch section and to add comments. Information about movies is taken from the OMDb API
##### The Open Movie Database (http://www.omdbapi.com) - The OMDb API is a RESTful web service to obtain movie information. To use the above-mentioned website, you must first create an account there. In addition to information on films, OMDb also has a poster database, which we also gain access to after creating an account.
##### The entire project is hosted on AWS servers. 


##Technologies
##### Project is created with :
* Java 11 
* Bootstrap 4
* Thymeleaf 3
* MySql 
* SpringBoot
* SpringDataJPA
* AWS: Elastic Beanstalk + RDS


##Features
* Searching for movies by connecting to the OMDb API.
* Registration:
  To set up an account on the site, you must provide a user name, which should be an email address, nickname and password, which will include min. 8 characters (the password on the website is encoded with BCryptPasswordEncoder). After registration, an e-mail is send (made with JavaMailSender) to the e-mail address entered the form. When the user clicks on the link in the email, the account is created.
* Log in using the username and password entered during registration.
* If the user has forgotten the password, there is an option to generate a new password. In the login panel, select the "Forgot password" option, enter the email address assigned to the account, and the new password will be checked and sent to the previously entered e-mail address.
* Having an account, you can search for movies, add comments for any production and add movies to bookmarks: My Favorites Movies and Movies To Watch
* After logging in, in the "Settings" tab you can check your account information, and you can change your account and nickname. To change the password, enter the old and new password, and then an information confirming the password change will be sent by e-mail. Then log in to the website again, using the new password. To change the nickname, you need to enter a new one, if the nickname is unique, the user receives an email about a successful nickname change.


##Set up project on local machine 
1. Download project 
2. Provide properties :
   * spring.mail.username -> required for sending verification email
   * spring.mail.password
   * spring.datasource.url
   * spring.datasource.username
   * spring.datasource.password
   * domain.url -> address for verification email (on local machine localhost:port)
3. `chmod +x mvnw`
4. `./mvnw spring-boot:run`




