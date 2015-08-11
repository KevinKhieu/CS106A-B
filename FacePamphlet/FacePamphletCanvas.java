/*
 * File: FacePamphletCanvas.java
 * -----------------------------
 * This class represents the canvas on which the profiles in the social
 * network are displayed.  NOTE: This class does NOT need to update the
 * display when the window is resized.
 */


import acm.graphics.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class FacePamphletCanvas extends GCanvas implements FacePamphletConstants {
	
	// Private instance variables
	private GLabel text;
	private GLabel name;
	private GLabel status;
	private GImage userImage;
	/** 
	 * Constructor
	 * This method takes care of any initialization needed for 
	 * the display
	 */
	public FacePamphletCanvas() {
	
	}

	
	/** 
	 * This method displays a message string near the bottom of the 
	 * canvas.  Every time this method is called, the previously 
	 * displayed message (if any) is replaced by the new message text 
	 * passed in.
	 */
	public void showMessage(String msg) {
		// removes text if there is still canvas text on the screen.
		if (text != null){
			remove(text);
		}
		
		// recreates our text label with the given message "msg" and places it at bottom of screen.
		text = new GLabel(msg, 0,0);
		text.setFont(MESSAGE_FONT);
		text.setColor(Color.BLACK);
		double x = getWidth()/2 - text.getWidth()/2;
		double y = getHeight() - BOTTOM_MESSAGE_MARGIN;
		add(text, x, y);
	}
	
	
	/** 
	 * This method displays the given profile on the canvas.  The 
	 * canvas is first cleared of all existing items (including 
	 * messages displayed near the bottom of the screen) and then the 
	 * given profile is displayed.  The profile display includes the 
	 * name of the user from the profile, the corresponding image 
	 * (or an indication that an image does not exist), the status of
	 * the user, and a list of the user's friends in the social network.
	 */
	public void displayProfile(FacePamphletProfile profile) {
		// Removes entire canvas then goes about doing the canvas again.
		removeAll();
		addUserName(profile.getName());
		addUserImage(profile.getImage());
		addUserStatus(profile.getStatus());
		addUserFriends(profile.getFriends());
	}
	
	// Adds the label of the profile's Name onto the canvas.
	private void addUserName(String FPname){
		// Check to make sure name isn't still on the canvas.
		if (name != null){
			remove(name);
		}
		// Reset name to be the current Profile's name.
		name = new GLabel(FPname,LEFT_MARGIN,TOP_MARGIN);
		name.setFont(PROFILE_NAME_FONT);
		name.setColor(Color.BLUE);
		name.move(0, name.getHeight());
		add(name);
	}
	
	// Adds the user's profile picture to the canvas.
	private void addUserImage(GImage userImage2){
		userImage = userImage2;
		// If image isn't blank, we put the image into the area we have set for the image to be placed.
		if (userImage != null){
			userImage.setBounds(LEFT_MARGIN, TOP_MARGIN + name.getHeight() + IMAGE_MARGIN, IMAGE_WIDTH, IMAGE_HEIGHT);
			add(userImage); 
		
			// If the image is not available, we display a blank square with text "No Image" in the middle of it.
		} else {
			GRect blankImage = new GRect(LEFT_MARGIN, TOP_MARGIN + name.getHeight() + IMAGE_MARGIN, IMAGE_WIDTH, IMAGE_HEIGHT);
			add(blankImage);
			GLabel blank = new GLabel("No Image", LEFT_MARGIN + (IMAGE_WIDTH/2), TOP_MARGIN + name.getHeight() + IMAGE_MARGIN + IMAGE_HEIGHT/2);
			blank.setFont(PROFILE_IMAGE_FONT);
			blank.move(-blank.getWidth()/2, +blank.getHeight()/2);
			add(blank);
		}
	}
	
	// Adds the User's status to the canvas.
	private void addUserStatus(String fpstatus){
		// Remove status if there is a previous status on the board.
		if (status != null){
			remove(status);
		}
		
		// Set the status label to be the new FacePamphlet status for the current profile.
		status = new GLabel(fpstatus,LEFT_MARGIN,TOP_MARGIN + name.getHeight() + IMAGE_MARGIN + IMAGE_HEIGHT + STATUS_MARGIN);
		status.setFont(PROFILE_STATUS_FONT);
		status.move(0,status.getHeight());
		add(status);
	}
	
	// Adds a list of friends to the right of the canvas.
	private void addUserFriends(Iterator<String> friendList){
		// Add a GLabel to mark the beginning of the list of the current profile's list of friends.
		GLabel friends = new GLabel("Friends: ", getWidth()/2, TOP_MARGIN + name.getHeight() + IMAGE_MARGIN);
		friends.setFont(PROFILE_FRIEND_LABEL_FONT);
		add(friends);
		
		// Using the passed iterator, we display the list of friends for this current profile.
		int friendCount = 1;
		while (friendList.hasNext()){
			String label = friendList.next();
			GLabel friendName = new GLabel(label, getWidth()/2, TOP_MARGIN + name.getHeight() + IMAGE_MARGIN + friends.getHeight()*(friendCount));
			name.setFont(PROFILE_FRIEND_FONT);
			add(friendName);
			
			// Friend count ensures that each friend name appears just below the next one.
			friendCount++;	
		}	
	}	
}
