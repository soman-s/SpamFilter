# CSCI 2020 Winter 2021, Assignment 1 (Spam Detection)

## Project Information 
By: Soman Sheikh and Alex Bianchi and<br />
Written in javafx 11

The function of this program is to filter out spam emails. The program consists of two phases: training and testing. A dataset divided into two folders (train and test) is provided. Both the train and test folders include ham (wanted) and spam (unwanted) folders. In the training phase, each email in train/ham and train/spam is parsed word by word to create two frequency maps (trainHamFreq and trainSpamFreq). These maps will be used to determine the probability that a file is spam, given that it contains a specific word. 
In the testing phase, the test/ham and test/spam folders are parsed word by word to determine whether the file is a spam or not. The name of the file, which folder it belongs in, and the probability that the file is spam will be displayed in a table using JavaFX.
![resultsScreen](https://user-images.githubusercontent.com/55216478/110399713-45ac8e00-8044-11eb-8e8c-b615736cd042.PNG)

## Improvements
Visual improvements:
-	Created a logo
-	Changed the colouring of the buttons and text
-	Changed background colours

## How to Run
Clone the repository to your local machine<br />
Open Intellij<br />
Select the ‘open or import’ option from the main menu<br />
Select the folder that was cloned<br />
In the project directory right click on the folder titled “resources”<br /> 
Select ‘mark directory as’ from the drop-down menu, and then select ‘mark as resources root’<br />
(Depending on your Intellij setup, you may need to go into project module settings and add javafx from your global libraries)<br />
Run the main file and the program will start<br />

When the program starts and it prompts to select a directory, select the directory that contains your train and test folders.<br />
all forders should be in lowercase and train and test should both contain a folder called spam and ham 
![startScreen](https://user-images.githubusercontent.com/55216478/110399757-5f4dd580-8044-11eb-9064-f7ad90665327.PNG)

## Other resources
WordCounter file was based off of Professor Mariana Shimabukuro's WordCounter class provided in the class example
