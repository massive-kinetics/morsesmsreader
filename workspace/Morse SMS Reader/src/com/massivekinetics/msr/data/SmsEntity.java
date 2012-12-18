package com.massivekinetics.msr.data;

public class SmsEntity {
	public final long timeStamp;
	public final String body;
	public final String from;

	public SmsEntity(long timeStamp, String body, String from) {
		this.timeStamp = timeStamp;
		this.body = body;
		this.from = from;
	}
}
