package org.openjfx;

public class User {
	private String name;
	private String surname;
	private String bankNo;
	private String password;
	private double balance;

	public User(String name, String surname, String bankNo, String password, double balance) {
		this.name=name;
		this.surname=surname;
		this.bankNo=bankNo;
		this.password=password;
		this.balance=balance;
	}
	
	public String getBalance() {
		return this.balance+"";
	}
	
	public void depositMoney(double amount) {
		this.balance+=amount;
	}
	
	public boolean withdrawMoney(double amount) {
		if(this.balance<amount) {
			return false;
		}
		this.balance-=amount;
		return true;
	}
	
	public boolean checkPassword(String password) {
		if(password.equals(this.password)) return true;
		return false;
	}
	public String getBankNo() {
			return this.bankNo;
	}
	
}
