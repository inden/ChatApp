# (Demo) ChatApp - Firebase Backend
## an Android Chat app ##
### This is a demo app developed to test Firebase. 
### I tested its Realtime Database and its File storage. 
### I found Firebase really first and easy to use except the database query.

## How to use ##
### Create your accout for the first time. Your account will be saved on Firebase. ###


<img src="https://cloud.githubusercontent.com/assets/21304543/21225946/a2f193ea-c2d3-11e6-95d3-69c359a918b6.png" width="300"/>



### There are three tabs on screen ###
 * *TOPICS* for the list of topics in progress,
 * *NEW TOPIC* to create a new topic,
 * *USERS* for the members list. 


### On the tab *USERS*,  there is the members list. ###
you can find your account that you've just created at the bottom in the picture.

<img src="https://cloud.githubusercontent.com/assets/21304543/21225948/a2fcda52-c2d3-11e6-9f6c-585b5c237abc.png" width="300"/>


### On the tab *New Topic*, you will create a new topic by filling the form. ###
Fill the form for a new topic with title, description, end date and invite some members to chat with

<img src="https://cloud.githubusercontent.com/assets/21304543/21225943/a2e28e40-c2d3-11e6-9344-f4eab101bc16.png" width="300"/>


For the end date, you can leave it with the default value or select one on the calendar. 

<img src="https://cloud.githubusercontent.com/assets/21304543/21225951/a3120530-c2d3-11e6-9c72-35129f1c6024.png" width="300"/>


Select members to chat with from the members' list.

<img src="https://cloud.githubusercontent.com/assets/21304543/21225944/a2e8ce9a-c2d3-11e6-86b6-a8f95b4b2f2e.png" width="300"/>


Click "Ok" button to save the topic (or "Refresh" button to empty the form). Your topic is saved on Firebase.

<img src="https://cloud.githubusercontent.com/assets/21304543/21225945/a2eb2988-c2d3-11e6-9af6-4a0760282bae.png" width="300"/>


### On the tab *TOPICS*, there is the list of 'in progress' topics that you are invited or you created. ###
you can find the topic which you have just created (at the top in the picture) 
Click a topic item and start to chat.

<img src="https://cloud.githubusercontent.com/assets/21304543/21225947/a2f7a8f2-c2d3-11e6-9374-f829bd499898.png" width="300"/>


### Now the chat is going on. ###
Your messages are on the left side and other members' messages on the right side with their name and avatar  

<img src="https://cloud.githubusercontent.com/assets/21304543/21225949/a302e0aa-c2d3-11e6-9d65-181a36cdc654.png" width="300"/>


### SDK API 19 or later required ###
##
## Technology 
- Splash activity
- Material Design pour tab
- Firebase (realtime database, storage)
- Broadcast Receiver pour la communication entre Tab (activity) et Pages (Fragment)
- Library Picasso 


