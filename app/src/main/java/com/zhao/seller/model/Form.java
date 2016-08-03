package com.zhao.seller.model;

/**
 * Form entity. @author MyEclipse Persistence Tools
 */

public class Form {

	// Fields

	private Long id;
	private Double payPrice;
	private Integer shopId;
	private String buyerAccount;
	private String shopName;
	private String formFood;
	private String formState;
	private String contact;
	private String telephone;
	private String formAddress;
	private String addressLocation;
	private String payMethod;
	private String submitTime;
	private String sendTime;
	private String note;
	private String sendState;
	private String senderAccount;

	// Constructors

	/** default constructor */
	public Form() {
	}

	/** minimal constructor */
	public Form(Long id, Double payPrice, Integer shopId, String buyerAccount,
				String shopName, String formFood, String formState,
				String telephone, String formAddress, String addressLocation,
				String payMethod, String submitTime, String sendTime) {
		this.id = id;
		this.payPrice = payPrice;
		this.shopId = shopId;
		this.buyerAccount = buyerAccount;
		this.shopName = shopName;
		this.formFood = formFood;
		this.formState = formState;
		this.telephone = telephone;
		this.formAddress = formAddress;
		this.addressLocation = addressLocation;
		this.payMethod = payMethod;
		this.submitTime = submitTime;
		this.sendTime = sendTime;
	}

	/** full constructor */
	public Form(Long id, Double payPrice, Integer shopId, String buyerAccount,
				String shopName, String formFood, String formState, String contact,
				String telephone, String formAddress, String addressLocation,
				String payMethod, String submitTime, String sendTime, String note,
				String sendState, String senderAccount) {
		this.id = id;
		this.payPrice = payPrice;
		this.shopId = shopId;
		this.buyerAccount = buyerAccount;
		this.shopName = shopName;
		this.formFood = formFood;
		this.formState = formState;
		this.contact = contact;
		this.telephone = telephone;
		this.formAddress = formAddress;
		this.addressLocation = addressLocation;
		this.payMethod = payMethod;
		this.submitTime = submitTime;
		this.sendTime = sendTime;
		this.note = note;
		this.sendState = sendState;
		this.senderAccount = senderAccount;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getPayPrice() {
		return this.payPrice;
	}

	public void setPayPrice(Double payPrice) {
		this.payPrice = payPrice;
	}

	public Integer getShopId() {
		return this.shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getBuyerAccount() {
		return this.buyerAccount;
	}

	public void setBuyerAccount(String buyerAccount) {
		this.buyerAccount = buyerAccount;
	}

	public String getShopName() {
		return this.shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getFormFood() {
		return this.formFood;
	}

	public void setFormFood(String formFood) {
		this.formFood = formFood;
	}

	public String getFormState() {
		return this.formState;
	}

	public void setFormState(String formState) {
		this.formState = formState;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFormAddress() {
		return this.formAddress;
	}

	public void setFormAddress(String formAddress) {
		this.formAddress = formAddress;
	}

	public String getAddressLocation() {
		return this.addressLocation;
	}

	public void setAddressLocation(String addressLocation) {
		this.addressLocation = addressLocation;
	}

	public String getPayMethod() {
		return this.payMethod;
	}

	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}

	public String getSubmitTime() {
		return this.submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public String getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getSendState() {
		return this.sendState;
	}

	public void setSendState(String sendState) {
		this.sendState = sendState;
	}

	public String getSenderAccount() {
		return this.senderAccount;
	}

	public void setSenderAccount(String senderAccount) {
		this.senderAccount = senderAccount;
	}

}