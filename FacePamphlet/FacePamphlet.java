/* 
 * File: FacePamphlet.java
 * -----------------------
 * This program implements a basic social network.
 */

import acm.program.*;
import acm.graphics.*;
import acm.util.*;
import java.awt.event.*;
import javax.swing.*;

public class FacePamphlet extends Program implements FacePamphletConstants {
	
	// Instance variables (private)
	private JTextField enterName;
	private JTextField status;
	private JTextField picture;
	private JTextField friend;
	private FacePamphletDatabase faceDatabase;
	private FacePamphletCanvas canvas;
	private FacePamphletProfile currentProfile;
	
	/**
	 * This method has the responsibility for initializing the 
	 * interactors in the application, and taking care of any other 
	 * initialization that needs to be performed.
	 */
	public void init() {
		// Creates our user interface! We add the field for entering names and our three buttons.
	    JLabel label = new JLabel("Name ");
	    enterName = new JTextField(TEXT_FIELD_SIZE);
	    
	    // Add, delete, and look-up buttons created.
	    JButton addProfile = new JButton("Add");
	    JButton deleteProfile = new JButton("Delete");
	    JButton lookupProfile = new JButton("Lookup");
	    
	    // Creates the text fields for entering changes to status, profile pic, and friend requests.
	    status = new JTextField(TEXT_FIELD_SIZE);
	    picture = new JTextField(TEXT_FIELD_SIZE);
	    friend = new JTextField(TEXT_FIELD_SIZE);
	    
	    // Adds buttons to allow for the user to click on change status/pic/friends.
	    JButton changeStatus = new JButton("Change Status");
	    JButton changePicture = new JButton("Change Picture");
	    JButton addFriend = new JButton("Add Friend");
	    
	    // Adds all of the above JObjects to our interface. 
	    add (label, NORTH);
	    add (enterName, NORTH);
	    add (addProfile, NORTH);
	    add (deleteProfile, NORTH);	    
	    add (lookupProfile, NORTH);	 
	    add (status, WEST);
	    add (changeStatus, WEST);
	    add(new JLabel(EMPTY_LABEL_TEXT), WEST);
	    add (picture, WEST);
	    add (changePicture, WEST);
	    add(new JLabel(EMPTY_LABEL_TEXT), WEST);
	    add (friend, WEST);
	    add (addFriend, WEST);
	    
	    // Makes the text field responsive to user actions.
	    addActionListeners();
	    status.addActionListener(this);
	    picture.addActionListener(this);
	    friend.addActionListener(this);
	    
	    // Creates our database of names to refer to for this assignment.
	    faceDatabase = new FacePamphletDatabase();
	    
	    // Creates our graph/canvas.
	    canvas = new FacePamphletCanvas();
	    add(canvas);
	    
    }
    
  
    /**
     * This class is responsible for detecting when the buttons are
     * clicked or interactors are used, so you will have to add code
     * to respond to these actions.
     */
    public void actionPerformed(ActionEvent e) {
    	/* If any of the 6 buttons are pressed and their respective textfields are not blank, we 
    	 * run that button's respective action that it is designed to do. In addition, we allow the 
    	 * user to hit "enter" if he/she is using Change Status/Pic/Add Friend.
    	 */
    	if ((e.getActionCommand().equals("Change Status") || e.getSource() == status) && status.getText().length() != 0 && status.getText().charAt(0)!=32){
    		changeStatusClicked();
        } else if ((e.getActionCommand().equals("Change Picture") || e.getSource() == picture) && picture.getText().length() != 0 && picture.getText().charAt(0)!=32 ) {
        	changePictureClicked();
        } else if ((e.getActionCommand().equals("Add Friend") || e.getSource() == friend) && friend.getText().length() != 0 && friend.getText().charAt(0)!= 32 ){
        	addFriendClicked();
        } else if (e.getActionCommand().equals("Add") && enterName.getText().length() != 0 && enterName.getText().charAt(0)!=32){
        	addClicked();
        } else if (e.getActionCommand().equals("Delete") && enterName.getText().length() != 0 && enterName.getText().charAt(0)!=32 ){
        	deleteClicked();
        } else if (e.getActionCommand().equals("Lookup") && enterName.getText().length() != 0 && enterName.getText().charAt(0)!=32 ){
        	lookUpClicked();
        }
	}
    
    // Changes the status if we have a currentProfile selected. If not, we tell the user to select a status. 
    private void changeStatusClicked(){
    	/* If a profile is selected at the moment, we set its status to equal what's in the text field and 
    	  * update the display to adjust for that change.
    	  */
    	if (currentProfile != null){
    		String entry = status.getText();
    		FacePamphletProfile edit = faceDatabase.getProfile(currentProfile.getName());
    		edit.setStatus(entry);
    		currentProfile.setStatus(edit.getName() + " is " + entry);
    		canvas.displayProfile(edit);
    		canvas.showMessage("Status updated to " + entry);
    		
    		// Otherwise we tell the user to select a profile to change a status.
    	} else {
    		canvas.showMessage("Please select a profile to change status.");
    	}
    }
    
    // Changes picture of currently selected profile to the file read in the picture textfield.
    private void changePictureClicked(){
    	// Stores whats in the textfield into a String filename.
    	String filename = picture.getText();
    	
    	// If we have a current Profile selected, we change that profile's image to the file entered.
    	if (currentProfile != null){
    		// Set image to null.
    		GImage image = null;
    		
    		// Get our current FacePamphletProfile. 
    		FacePamphletProfile edit = faceDatabase.getProfile(currentProfile.getName());
    		
    		// test to see if that GImage is an actual file on hand.
    		try {
        		image = new GImage(filename); 
        	} catch (ErrorException ex) {
        		// if we don't have that file name, we set image to be null again.
        		image = null;
        	}
        	
        	// Tell user that the image they entered was not a valid image if image == null.
        	if (image == null){
        		canvas.displayProfile(edit);
        		canvas.showMessage("Unable to open image file: " + filename);
        		
        		// Or else, we update the display and tell the user that their image has been updated.
        	} else {
        		edit.setImage(image);
        		canvas.displayProfile(edit);
        		canvas.showMessage("Picture updated.");
        	}
        // If the user doesn't have a currentProfile selected, we tell them to select a profile.
    	} else{
    		canvas.showMessage("Please select a profile to change picture.");
    	}
    }
    
    // Adds the friend in the friend textfield to the currentProfile's list of friends (and vice versa). 
    private void addFriendClicked(){
    	// Set the text in the friend textfield to be entry.
    	String entry = friend.getText();
    	
    	if (currentProfile != null){
    		// Get our currentProfile we are working with.
    		FacePamphletProfile edit = faceDatabase.getProfile(currentProfile.getName());
    		
    		// Check to make sure you ain't friending yourself.
    		if (edit.getName().equals(entry)){
    			canvas.showMessage("You cannot friend yourself.");
    		
    		// Check to see if the frined actually exists in the database. 
    		} else if (faceDatabase.containsProfile(entry)){
    			// Set a friend FacePamphletProfile to equal the entry we are friending.
    			FacePamphletProfile friend = faceDatabase.getProfile(entry);
    			
    			// Adds the friend to our currentProfile's profile (and vice versa) if they aren't already friends.
    			if (edit.addFriend(entry)){
    				edit.addFriend(entry);
    				friend.addFriend(edit.getName());
    				canvas.displayProfile(edit);
    				canvas.showMessage(entry + " added as a friend.");
    				
    			// if they are already friends remind the user of that. 
    			} else {
    				canvas.showMessage(edit.getName() + " already has " + entry + " as a friend.");
    			}
    			
    		// If the friend doesn't exist, show a message that describes that.
    		} else {
    			canvas.showMessage(entry + " does not exist.");
    		}
    	// If no current Profile selected, tell the user to choose one.
    	} else {
    		canvas.showMessage("Please select a profile to send a friend request from.");
    	}
    }
    
    // Triggers if user clicks the "Add" button with text in the field.
    private void addClicked(){
    	// Store text in the name textfield into our String entry
    	String entry = enterName.getText();
    	if (faceDatabase.containsProfile(entry)){
    		
    		// If the profile already exists, we do not let the user add it again.
    		FacePamphletProfile edit = faceDatabase.getProfile(entry);
    		canvas.displayProfile(edit);
    		canvas.showMessage("A profile with the name " + entry + " already exists!");
    		currentProfile = edit;
    	} else {
    		
    		// Otherwise we create a new FacePamphletProfile with the name given and update the canvas.
    		FacePamphletProfile edit = new FacePamphletProfile(entry);
    		faceDatabase.addProfile(edit);
    		canvas.displayProfile(edit);
    		canvas.showMessage("New profile created.");
    		currentProfile = edit;
    	}
    }
    
    // Triggers if user clicks delete with a name in the name textfield.
    private void deleteClicked(){
    	// Store text in the name textfield into our String entry
    	String entry = enterName.getText();
    	canvas.removeAll();
    	if (faceDatabase.containsProfile(entry)){
    		faceDatabase.deleteProfile(entry);
    		canvas.showMessage("Profile of " + entry + " deleted.");
    	} else {
    		canvas.showMessage("A profile with name " + entry + " does not exist.");
    	}
    	currentProfile = null;
    }
    
	private void lookUpClicked(){
    	canvas.removeAll();
    	// Store text in the name textfield into our String entry
    	String entry = enterName.getText();
    	if(faceDatabase.containsProfile(entry)){
    		// Displays the entry being looked up if found and changes canvas message to reflect that.
    		FacePamphletProfile edit = faceDatabase.getProfile(entry);
    		canvas.displayProfile(edit);
    		canvas.showMessage("Displaying " + entry);
    		currentProfile = edit;
    	} else {
    		// Tells user that the searched entry cannot be found. Current Profile is null.
    		canvas.showMessage("A profile with name " + entry + " does not exist.");
    		currentProfile = null;
    	}
	}

}
