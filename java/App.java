package org.openjfx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class App extends Application {
	
	Stage window;
	Scene logInScreen, optionsScreen;
	
	public static Map<String,Integer> userIndex=new HashMap<String, Integer>();
	public static ArrayList<User> accounts = new ArrayList<User>();
	public static User loggedUser=null;
	
	public static String[] record=new String[5];
	
	
	public static void main(String[] args) throws IOException {
		
		File inputFile=new File("AccountInfo.txt");
		Scanner reader=new Scanner(inputFile);
		int i=0;
		while(reader.hasNextLine()) {
			String line = reader.nextLine();
			record[i]=line;
			Scanner scanner=new Scanner(line);
			accounts.add(new User(scanner.next(),scanner.next(),scanner.next(),scanner.next(),scanner.nextDouble()));
			userIndex.put(accounts.get(i).getBankNo(), i);
			i++;
			scanner.close();
		}
		launch(args);
		reader.close();
	}
	
	public static boolean logIn(String bankNo, String password) {
	
		if(userIndex.containsKey(bankNo)) {
			if(accounts.get(userIndex.get(bankNo)).checkPassword(password)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean transferMoney(String bankNo, double amount) {
			
			if(userIndex.containsKey(bankNo)) {
				if(loggedUser.withdrawMoney(amount))
				{
					User receivingAccount=accounts.get(userIndex.get(bankNo));
					receivingAccount.depositMoney(amount);
					return true;
				}
			}
			return false;
		}
	
	public static void updateRecords(FileOutputStream outputFile,int transferTo) {
		
		if(transferTo!=(-1)) {
	    	String[] toData=new String[5];
	    	toData=record[transferTo].split(" ");
	    	toData[4]=accounts.get(transferTo).getBalance();
	    	record[transferTo]=String.join(" ", toData);
		}
		int index=accounts.indexOf(loggedUser);
    	String[] userData=new String[5];
    	userData=record[index].split(" ");
    	userData[4]=loggedUser.getBalance();
    	record[index]=String.join(" ", userData);
    	
    	try {
    		outputFile.write(String.join("\n", record).getBytes());
    		outputFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	
	  @Override
	    public void start(Stage primaryStage) throws FileNotFoundException {
		  
		  window=primaryStage;
		  
		  Label balanceLabel=new Label("");
		  
		  Text text1=new Text("Bank No");
		  Text text2=new Text("PIN");
		  TextField bankNoField=new TextField();
		  Button[] pinButtons=new Button[10];
		  for(int i=0;i<10;i++) {
			  pinButtons[i]=new Button(""+i);
		  }
		  
		  Text warning1=new Text("");
		  
		  Button logInButton=new Button("Log In");
		  logInButton.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	if(logIn(bankNoField.getText(),PIN.pin)) {
	            		window.setScene(optionsScreen);
	            		loggedUser=accounts.get(userIndex.get(bankNoField.getText()));
	            		balanceLabel.setText("Your balance is: "+ loggedUser.getBalance());
	            	}
	            	else {
	            		warning1.setText("Log in failed. Check your account information.");
	            	}
	            }
	        });
		  
		  Button clearButton= new Button("Clear");
		  clearButton.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            		bankNoField.clear();
	            		warning1.setText("");
	            		PIN.pin="";
	            }
	        });
		  
		  GridPane layout1=new GridPane();
		  layout1.setMinSize(400,400);
		  layout1.setPadding(new Insets(10,10,10,10));
		  layout1.setVgap(5);
		  layout1.setHgap(5);
		  layout1.setAlignment(Pos.CENTER);
		  
		  layout1.add(text1, 0, 0);
		  layout1.add(bankNoField, 0, 1);
		  layout1.add(text2, 1, 0);
		  
		  layout1.add(pinButtons[1], 1,1);
		  layout1.add(pinButtons[2], 2,1);
		  layout1.add(pinButtons[3], 3,1);
		  layout1.add(pinButtons[4], 1,2);
		  layout1.add(pinButtons[5], 2,2);
		  layout1.add(pinButtons[6], 3,2);
		  layout1.add(pinButtons[7], 1,3);
		  layout1.add(pinButtons[8], 2,3);
		  layout1.add(pinButtons[9], 3,3);
		  layout1.add(pinButtons[0], 2,4);
		  
		  layout1.add(logInButton, 0, 2);
		  layout1.add(clearButton, 0,3);
		  layout1.add(warning1, 0, 4);
		  
		  for(int i=0;i<10;i++) {
			  int x=i;
			  pinButtons[i].setOnAction(new EventHandler<ActionEvent>() {
		            @Override
		            public void handle(ActionEvent event) {
		            		PIN.readIntoPin(x);
		            }
		        });
		  }
		  
		  logInScreen=new Scene(layout1);
		  
		  
		  GridPane layout2=new GridPane();
		  layout2.setMinSize(500,500);
		  layout2.setPadding(new Insets(10,10,10,10));
		  layout2.setVgap(5);
		  layout2.setHgap(5);
		  layout2.setAlignment(Pos.CENTER);
		  TextField withdrawAmount=new TextField();
		  Button withdrawButton=new Button("Withdraw");
		  
		  Text warning2=new Text("");
		  
		  withdrawButton.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	if(loggedUser.withdrawMoney(Double.parseDouble(withdrawAmount.getText()))) {
	            		warning2.setText("");
		            	balanceLabel.setText("Your balance is: "+ loggedUser.getBalance());
		            	
		            	try {
							updateRecords(new FileOutputStream("AccountInfo.txt",false),-1);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
	            	}
	            	else warning2.setText("Your balance isn't enough.");
	            	withdrawAmount.clear();
	            }
	        });
		  TextField depositAmount=new TextField();
		  Button depositButton=new Button("Deposit");
		  depositButton.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	warning2.setText("");
	            	loggedUser.depositMoney(Double.parseDouble(depositAmount.getText()));
	            	depositAmount.clear();
	            	balanceLabel.setText("Your balance is: "+ loggedUser.getBalance());
	            	
	            	try {
						updateRecords(new FileOutputStream("AccountInfo.txt",false),-1);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
	            }
	        });
		  
		  TextField transferTo=new TextField();  TextField transferAmount=new TextField();
		  transferTo.setPromptText("Transfer To"); transferAmount.setPromptText("Transfer Amount");
		  Button transferButton=new Button("Transfer");
		  transferButton.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	warning2.setText("");
	            	if(transferMoney(transferTo.getText(),Double.parseDouble(transferAmount.getText()))) {
		            	balanceLabel.setText("Your balance is: "+ loggedUser.getBalance());
		            	
		            	try {
							updateRecords(new FileOutputStream("AccountInfo.txt",false),
									userIndex.get(transferTo.getText()));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
	            	}
	            	else warning2.setText("Your balance isn't enough./No such user.");
	            	transferTo.clear(); transferAmount.clear();
	            }
	        });
		  Button logOutButton=new Button("Log Out");
		  logOutButton.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	warning2.setText("");
	            		bankNoField.clear();
	            		window.setScene(logInScreen);
	            		loggedUser=null;
	            		
	            		PIN.pin="";
	            }
	        });
		  layout2.add(balanceLabel,0,0);
		  layout2.add(withdrawAmount,0,1);
		  layout2.add(withdrawButton,1,1);
		  layout2.add(depositAmount,0,2);
		  layout2.add(depositButton,1,2);
		  layout2.add(transferTo,0,3);
		  layout2.add(transferAmount,1,3);
		  layout2.add(transferButton,2,3);
		  layout2.add(logOutButton,0,4);
		  layout2.add(warning2, 0, 5);
		  
		  optionsScreen=new Scene(layout2,500,500);
		  
		  window.setScene(logInScreen);
		  window.show();
		  
	    }

}


