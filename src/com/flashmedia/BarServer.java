/*
 * Created on 15.06.2010
 */
package com.flashmedia;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.NameNotBoundException;

/**
 * @author Flysoft Development Team.
 */
public class BarServer implements AppListener, Serializable, ManagedObject
{
	public static Boolean DEBUG = true;
	/**
	 *
	 */
	private static final long	serialVersionUID	= -6732790465072698883L;
	//private ManagedReference<Channel> channel = null;
	//private HashSet<ManagedReference<BarPlace>> barPlaces = new HashSet<ManagedReference<BarPlace>>();

	@Override
	public void initialize(Properties arg0)
	{
		System.out.println("Initialize: " + arg0.size());

//		DataManager dataMgr = AppContext.getDataManager();
//		dataMgr.markForUpdate(this);
//		Channel c;
//		try {
//			c = AppContext.getChannelManager().getChannel("SimpleChatChannel");
//		} catch (NameNotBoundException e) {
//			c = AppContext.getChannelManager().createChannel("SimpleChatChannel", null, Delivery.UNRELIABLE);
//		}
//		channel = dataMgr.createReference(c);
	}

	@Override
	public ClientSessionListener loggedIn(ClientSession session)
	{
		BarServer.debug(session.getName(), "Logged In");

		DataManager dataMgr = AppContext.getDataManager();
//		dataMgr.markForUpdate(this);
//		channel.getForUpdate().join(session);
//		BarPlace bp = new BarPlace(session);
		BarPlace bp;
		try {
			bp = (BarPlace) dataMgr.getBinding(session.getName());
			BarServer.debug(session.getName(), "Existing BarPlace." + bp.id_user);
		} catch (NameNotBoundException e) {
			BarServer.debug(session.getName(), "Creating barPlace...");
			bp = new BarPlace();
			dataMgr.setBinding(session.getName(), bp);
			BarServer.debug(session.getName(), "New BarPlace." + bp.id_user);
		}
		bp.init(session);
		BarServer.debug(session.getName(), "BarPlace inited");
		//barPlaces.add(dataMgr.createReference(bp));
		//System.out.println("Bar Places Count: " + barPlaces.size());
		return bp;
	}

	public static void debug(String id_user, String msg) {
		if (BarServer.DEBUG) {
			Date date = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("yyyy.MMMMM.dd 'at' hh:mm:ss aaa");
			String today = formatter.format(date);
			System.out.println(id_user + "[" + today + "]: " + msg);
		}
	}

}
