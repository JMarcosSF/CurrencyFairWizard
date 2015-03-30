package com.skybabble.wizard.app.model;

import java.util.Date;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

public class Message {
	
	@ObjectId
	@Id
	private String _id;
	
	private String userId;
	
	private String currencyFrom;
	private String currencyTo;
	
	private int ammountSell;
	private int ammountBuy;
	private Double rate;
	private Date timePlaced;
	
	private String originatingCountry;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCurrencyFrom() {
		return currencyFrom;
	}

	public void setCurrencyFrom(String currencyFrom) {
		this.currencyFrom = currencyFrom;
	}

	public String getCurrencyTo() {
		return currencyTo;
	}

	public void setCurrencyTo(String currencyTo) {
		this.currencyTo = currencyTo;
	}

	public int getAmmountSell() {
		return ammountSell;
	}

	public void setAmmountSell(int ammountSell) {
		this.ammountSell = ammountSell;
	}

	public int getAmmountBuy() {
		return ammountBuy;
	}

	public void setAmmountBuy(int ammountBuy) {
		this.ammountBuy = ammountBuy;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Date getTimePlaced() {
		return timePlaced;
	}

	public void setTimePlaced(Date timePlaced) {
		this.timePlaced = timePlaced;
	}

	public String getOriginatingCountry() {
		return originatingCountry;
	}

	public void setOriginatingCountry(String originatingCountry) {
		this.originatingCountry = originatingCountry;
	}
	
    
}
