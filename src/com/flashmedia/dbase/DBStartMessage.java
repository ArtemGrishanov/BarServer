package com.flashmedia.dbase;

import java.io.Serializable;

import com.sun.sgs.app.ManagedObject;

public class DBStartMessage implements Serializable, ManagedObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int message_id;
	public String caption;
	public String message;
	public String buttons;
	public long expiresDate;
}
