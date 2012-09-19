/*
 * Created on 15.06.2010
 */
package com.flashmedia;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.flashmedia.dbase.DAOException;
import com.flashmedia.dbase.DBClient;
import com.flashmedia.dbase.DBDecor;
import com.flashmedia.dbase.DBProduction;
import com.flashmedia.dbase.DBStartMessage;
import com.flashmedia.dbase.DBUser;
import com.flashmedia.dbase.UserDAO;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

/**
 * @author Flysoft Development Team.
 */
public class BarPlace implements ManagedObject, ClientSessionListener, Serializable
{
	public static final String url = "http://api.vkontakte.ru/api.php";
	public static final String APP_ID = "1955775";
	public static final String SECURE_API_KEY = "Sf3jPCp8bdmwlsOtGlqL";

	private ManagedReference<ClientSession> currentSessionRef = null;

	public boolean firstLaunch = false;
	public int level = 0;
	public int experience = 0;
	public int love = 0;
	public int invites = 0;
	public int moneyCent = 0;
	public int moneyEuro = 0;

	public String id_user = "";
	public String fullName = "";
	public String photoPath = "";
	public int id_user_in_db = 0;
	public int last_msg_id = 0;

	public Vector<String> licensedProdTypes = null;
	//public Vector<String> invitedIds = null;

	public final int MAX_CLIENT_COUNT = 4;
	public final int NO_ID = -1;
	public final String NO_STR = "";
	public int[] clientIds = null;
	public String[] clientTypes = null;
	public String[] clientUserIds = null;
	public String[] clientNames = null;
	public String[] clientGoodsType = null;

	public final int MAX_PRODUCTION_COUNT = 50;
	public int[] prodIds = null;
	public String[] prodTypes = null;
	public int[] prodPartsCount = null;
	public int[] prodCell = null;
	public int[] prodRow = null;

	public final int MAX_DECOR_COUNT = 100;
	public int[] decorIds = null;
	public String[] decorTypes = null;
	
	public long timeRequestStartMessages;
	public LinkedList<ManagedReference<DBStartMessage>> lastStartMessages = null;
	/**
	 * Какое максимальное количество сообщений разрешается выслать.
	 */
	public final int MAX_LAST_MESSAGES_COUNT = 3;
	/**
	 * Время, в течении которого сообщения запрошенные из БД считаются актуальными
	 * Секунды
	 */
	public final int ACTUAL_START_MESSAGES_TIME = 60;
	/**
	 *
	 */
	private static final long	serialVersionUID	= 2289627692265491407L;
	

	BarPlace () {
	}

	public void init(ClientSession session) {
		setSession(session);
		id_user = session.getName();
		initWithDB();
	}

	/**
	 * Загрузка из базы данных MySQL
	 *
	 */
	public void initWithDB() {
		BarServer.debug(id_user, "InitWithDB");
		try {
			DBUser u = UserDAO.getInstance().getUserByVK(Integer.parseInt(id_user));
			level = u.level;
			love = u.love;
			experience = u.exp;
			moneyCent = u.cents;
			moneyEuro = u.euro;
			id_user_in_db = u.id;
			fullName = u.fullName;
			photoPath = u.photoPath;
			firstLaunch = (u.initialized == 0);
			last_msg_id = u.last_msg_id;
		}
		catch (Exception e) {
			BarServer.debug(id_user, "[CATCHED EXCEPTION]");
			//e.printStackTrace();
			try {
				UserDAO.getInstance().insertUser(Integer.parseInt(id_user), fullName, level, experience, love, invites, moneyCent, moneyEuro, photoPath, 0);
				DBUser u = UserDAO.getInstance().getUserByVK(Integer.parseInt(id_user));
				id_user_in_db = u.id;
				firstLaunch = true;
			}
			catch (DAOException e1) {
				BarServer.debug(id_user, "[CATCHED EXCEPTION]");
				e1.printStackTrace();
			}
		}
		// Начало сессии пользователя - вносим отметку в таблицу сессий
		try {
			UserDAO.getInstance().startUserSession(id_user_in_db);
		}
		catch (DAOException e) {
			BarServer.debug(id_user, "[CATCHED EXCEPTIONS]");
			e.printStackTrace();
		}
		if (firstLaunch) {
			// очистка информации пользователя, которая есть в базе.
			try {
				UserDAO.getInstance().clearUserInfo(id_user_in_db);
			}
			catch(DAOException e) {
				BarServer.debug(id_user, "[CATCHED EXCEPTION]");
				e.printStackTrace();
			}
		}
		try {
			// init clients
			BarServer.debug(id_user, "Init Clients");
			initClients();
			Vector<DBClient> dbClients = UserDAO.getInstance().getClients(id_user_in_db);
			for (DBClient client : dbClients)
			{
				clientIds[client.position] = client.game_id;
				clientTypes[client.position] = client.type;
				//TODO пока не используется
				clientUserIds[client.position] = NO_STR;
				clientNames[client.position] = client.name;
				clientGoodsType[client.position] = client.goods_type;
			}
		}
		catch (Exception e) {
			BarServer.debug(id_user, "[CATCHED EXCEPTION]");
			e.printStackTrace();
		}
		try {
			// init productions
			BarServer.debug(id_user, "Init productions");
			initProduction();
			Vector<DBProduction> dbProduction = UserDAO.getInstance().getProduction(id_user_in_db);
			int i = 0;
			for (DBProduction prod : dbProduction)
			{
				prodIds[i] = prod.game_id;
				prodTypes[i] = prod.type;
				prodPartsCount[i] = prod.parts_count;
				prodCell[i] = prod.cell;
				prodRow[i] = prod.row;
				i++;
			}
		}
		catch (Exception e) {
			BarServer.debug(id_user, "[CATCHED EXCEPTIONS]");
			e.printStackTrace();
		}
		try {
			// init decor
			BarServer.debug(id_user, "[Init decor]");
			initDecor();
			Vector<DBDecor> dbDecor = UserDAO.getInstance().getDecor(id_user_in_db);
			int i = 0;
			for (DBDecor decor : dbDecor)
			{
				decorIds[i] = decor.game_id;
				decorTypes[i] = decor.type;
				i++;
			}
		}
		catch (Exception e) {
			BarServer.debug(id_user, "[CATCHED EXCEPTIONS]");
			e.printStackTrace();
		}
		try {
			BarServer.debug(id_user, "Init license types");
			licensedProdTypes = UserDAO.getInstance().getLicensedProdTypes(id_user_in_db);
		}
		catch (Exception e) {
			BarServer.debug(id_user, "[CATCHED EXCEPTIONS]");
			e.printStackTrace();
		}
	}

	@Override
	public void disconnected(boolean arg0)
	{
		try {
			UserDAO.getInstance().endUserSession(id_user_in_db);
		}
		catch (DAOException e) {
			BarServer.debug(id_user, "[CATCHED EXCEPTIONS]");
			e.printStackTrace();
		}
		BarServer.debug(id_user, "Disconnected: " + arg0);
	}

	protected void setSession(ClientSession session) {
        DataManager dataMgr = AppContext.getDataManager();
//        dataMgr.markForUpdate(this);
        currentSessionRef = dataMgr.createReference(session);
//        System.out.println("Set session: " + session.getName());
    }

//	public void sendFirstLaunch() {
//		System.out.println("sendFirstLaunch");
//		if (getSession() != null) {
//			System.out.println("getSession() != null");
//			try {
////				DataManager dataMgr = AppContext.getDataManager();
////				dataMgr.markForUpdate(this);
//				ByteOutputStream bos = new ByteOutputStream();
//				DataOutputStream dos = new DataOutputStream(bos);
//				dos.writeInt(ServerProtocol.S_FIRST_LAUNCH);
//				dos.flush();
//				bos.flush();
//				byte[] byteArray = bos.getBytes();
//				getSession().send(ByteBuffer.wrap(byteArray));
//				dos.close();
//				bos.close();
//				System.out.println("[S_FIRST_LAUNCH SENDED]");
//			}
//			catch (Exception e) {
//				BarServer.debug("Cannot send FIRST_LAUNCH!");
//			}
//		}
//	}

	protected ClientSession getSession() {
        if (currentSessionRef == null) {
//        	System.out.println("currentSessionRef == null");
            return null;
        }
//        System.out.println("currentSessionRef.get(): " + currentSessionRef.get());
        return currentSessionRef.get();
    }

	public void initClients() {
		clientIds = new int[MAX_CLIENT_COUNT];
		clientTypes = new String[MAX_CLIENT_COUNT];
		clientUserIds = new String[MAX_CLIENT_COUNT];
		clientNames = new String[MAX_CLIENT_COUNT];
		clientGoodsType = new String[MAX_CLIENT_COUNT];
		for (int i = 0; i < MAX_CLIENT_COUNT; i++) {
			clientIds[i] = NO_ID;
			clientTypes[i] = NO_STR;
			clientUserIds[i] = NO_STR;
			clientNames[i] = NO_STR;
			clientGoodsType[i] = NO_STR;
		}
	}

	public void initProduction() {
		prodIds = new int[MAX_PRODUCTION_COUNT];
		prodTypes = new String[MAX_PRODUCTION_COUNT];
		prodPartsCount = new int[MAX_PRODUCTION_COUNT];
		prodCell = new int[MAX_PRODUCTION_COUNT];
		prodRow = new int[MAX_PRODUCTION_COUNT];
		for (int i = 0; i < MAX_PRODUCTION_COUNT; i++) {
			prodIds[i] = NO_ID;
			prodTypes[i] = NO_STR;
			prodPartsCount[i] = NO_ID;
			prodCell[i] = NO_ID;
			prodRow[i] = NO_ID;
		}
	}

	public void addProduction(int prodId, String type, int partsCount, int cell, int row) {
		for (int i = 0; i < MAX_PRODUCTION_COUNT; i++) {
			if (prodIds[i] == NO_ID) {
				prodIds[i] = prodId;
				prodTypes[i] = type;
				prodPartsCount[i] = partsCount;
				prodCell[i] = cell;
				prodRow[i] = row;
				break;
			}
		}
		try {
			UserDAO.getInstance().insertProduction(id_user_in_db, type, prodId, partsCount, cell, row);
		}
		catch (DAOException e) {

		}
	}

	public void deleteProduction(int prodId) {
		for (int i = 0; i < MAX_PRODUCTION_COUNT; i++) {
			if (prodIds[i] == prodId) {
				prodIds[i] = NO_ID;
				prodTypes[i] = NO_STR;
				prodPartsCount[i] = NO_ID;
				prodCell[i] = NO_ID;
				prodRow[i] = NO_ID;
				break;
			}
		}
		try {
			UserDAO.getInstance().deleteProduction(id_user_in_db, prodId);
		}
		catch (DAOException e) {

		}
	}

	public int productionCount() {
		int count = 0;
		for (int i = 0; i < MAX_PRODUCTION_COUNT; i++) {
			if (prodIds[i] != NO_ID) {
				count++;
			}
		}
		return count;
	}

	public int getProductionIndex(int prodId) {
		for (int i = 0; i < MAX_PRODUCTION_COUNT; i++) {
			if (prodIds[i] == prodId) {
				return i;
			}
		}
		return -1;
	}

	public void initDecor() {
		decorIds = new int[MAX_DECOR_COUNT];
		decorTypes = new String[MAX_DECOR_COUNT];
		for (int i = 0; i < MAX_DECOR_COUNT; i++) {
			decorIds[i] = NO_ID;
			decorTypes[i] = NO_STR;
		}
	}

	public void addDecor(int decorId, String type) {
		for (int i = 0; i < MAX_DECOR_COUNT; i++) {
			if (decorIds[i] == NO_ID) {
				decorIds[i] = decorId;
				decorTypes[i] = type;
				break;
			}
		}
		try {
			UserDAO.getInstance().insertDecor(id_user_in_db, type, decorId);
		}
		catch (DAOException e) {
		}
	}

	public void deleteDecor(int decorId) {
		for (int i = 0; i < MAX_DECOR_COUNT; i++) {
			if (decorIds[i] == decorId) {
				decorIds[i] = NO_ID;
				decorTypes[i] = NO_STR;
				break;
			}
		}
		try {
			UserDAO.getInstance().deleteDecor(id_user_in_db, decorId);
		}
		catch (DAOException e) {
		}
	}

	public int decorCount() {
		int count = 0;
		for (int i = 0; i < MAX_DECOR_COUNT; i++) {
			if (decorIds[i] != NO_ID) {
				count++;
			}
		}
		return count;
	}

	@Override
	public void receivedMessage(ByteBuffer buf)
	{
//		BarServer.debug(id_user, "receiveMessage: " + buf);
		byte[] bytes = null;
		try {
			bytes = new byte[buf.remaining()];
			buf.get(bytes);
		}
		catch (Exception e) {
			BarServer.debug(id_user, "Cannot parse incoming message!");
			BarServer.debug(id_user, "[CATCHED EXCEPTIONS]");
			e.printStackTrace();
			return;
		}
		//System.out.println("Bytes length: " + bytes.length);
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			DataInputStream dis = new DataInputStream(bis);
			int token = dis.readInt();
			switch (token) {
				case ServerProtocol.C_LOAD_BAR:
					String bar_id_user = dis.readUTF();
					BarServer.debug(id_user, "[C_LOAD_BAR]: " + bar_id_user);
					//System.out.println("bar_id_user: " + bar_id_user);
					//System.out.println("id_user: " + id_user);
					//System.out.println("photoPath: " + photoPath);
					//System.out.println("experience: " + experience);
					if (bar_id_user.compareTo(id_user) == 0) {
						ByteOutputStream bos = new ByteOutputStream();
						DataOutputStream dos = new DataOutputStream(bos);
						dos.writeInt(ServerProtocol.S_BAR_LOADED);
						dos.writeInt((firstLaunch) ? 1 : 0);
						dos.writeUTF(bar_id_user);
						dos.writeUTF(fullName);
						dos.writeUTF(photoPath);
						dos.writeInt(level);
						dos.writeInt(experience);
						dos.writeInt(love);
						dos.writeInt(invites);
						dos.writeInt(moneyCent);
						dos.writeInt(moneyEuro);
						if (clientIds == null) {
							initClients();
						}
						for (int i = 0; i < MAX_CLIENT_COUNT; i++) {
							dos.writeInt(clientIds[i]);
							if (clientIds[i] != NO_ID) {
								dos.writeUTF(clientTypes[i]);
								dos.writeUTF(clientUserIds[i]);
								dos.writeUTF(clientNames[i]);
								dos.writeUTF(clientGoodsType[i]);
							}
						}
						if (licensedProdTypes == null) {
							licensedProdTypes = new Vector<String>();
						}
						//BarServer.debug("Licensed count: " + licensedProdTypes.size());
						dos.writeInt(licensedProdTypes.size());
						for (int i = 0; i < licensedProdTypes.size(); i++) {
							dos.writeUTF(licensedProdTypes.elementAt(i));
						}
						if (prodIds == null) {
							initProduction();
						}
						int pCount = productionCount();
						//BarServer.debug("Production count: " + pCount);
						dos.writeInt(pCount);
						for (int i = 0; i < MAX_PRODUCTION_COUNT; i++) {
							if (prodIds[i] != NO_ID) {
								dos.writeInt(prodIds[i]);
								dos.writeUTF(prodTypes[i]);
								dos.writeInt(prodPartsCount[i]);
								dos.writeInt(prodCell[i]);
								dos.writeInt(prodRow[i]);
							}
						}
						if (decorIds == null) {
							initDecor();
						}
						int dCount = decorCount();
						//BarServer.debug("Decor count: " + dCount);
						dos.writeInt(dCount);
						for (int i = 0; i < MAX_DECOR_COUNT; i++) {
							if (decorIds[i] != NO_ID) {
								dos.writeInt(decorIds[i]);
								dos.writeUTF(decorTypes[i]);
							}
						}
						
						long now = System.currentTimeMillis() / 1000;
						//если уже получены сообщения из БД и они достаточно актуальны по времени, то не делаем запрос
						if ((lastStartMessages == null) || (timeRequestStartMessages == 0) || ((timeRequestStartMessages + ACTUAL_START_MESSAGES_TIME) < now)) {
							DataManager dataMgr = AppContext.getDataManager();
							//получить несколько последних нотификаций из таблицы start_messages
							try {
								timeRequestStartMessages = now;
								DBStartMessage[] msgs = UserDAO.getInstance().getLastMessages(MAX_LAST_MESSAGES_COUNT);
								lastStartMessages = new LinkedList<ManagedReference<DBStartMessage>>();
								for (int i = 0; i < msgs.length; i++) {
									lastStartMessages.add(dataMgr.createReference(msgs[i]));
								}
							}
							catch (DAOException e) {
								e.printStackTrace();
							}
						}
						int maxStartMessageId = 0;
						LinkedList<DBStartMessage> msgToSend = new LinkedList<DBStartMessage>();
						//отбираем только те сообщения, которые last_msg_id
						if (lastStartMessages != null) {
							for (int i = 0; i < lastStartMessages.size(); i++) {
								if (lastStartMessages.get(i).get().message_id > last_msg_id) {
									msgToSend.add(lastStartMessages.get(i).get());
								}
								if (maxStartMessageId < lastStartMessages.get(i).get().message_id) {
									maxStartMessageId = lastStartMessages.get(i).get().message_id;
								}
							}
						}
						last_msg_id = maxStartMessageId;
						//отправляем
						dos.writeInt(msgToSend.size());
						for (int i = 0; i < msgToSend.size(); i++) {
							DBStartMessage m = msgToSend.get(i);
							dos.writeInt(m.message_id);
							dos.writeUTF(m.caption);
							dos.writeUTF(m.message);
							dos.writeUTF(m.buttons);
						}
						//надо записать в таблицу users то, что мы отправили сообщение last_msg_id
						try {
							UserDAO.getInstance().setLastMessages(id_user_in_db, maxStartMessageId);
						}
						catch (DAOException e) {
							e.printStackTrace();
						}
						//флашим и закрываем весь поток
						dos.flush();
						bos.flush();
						byte[] byteArray = bos.getBytes();
						//System.out.println("byteArr len: " + byteArray.length);
						getSession().send(ByteBuffer.wrap(byteArray));
						dos.close();
						bos.close();

						//проверить и отправить бонусы за приглашения
						try {
							Vector<Integer> invitedIds = UserDAO.getInstance().getNotAcceptedInvites(id_user_in_db);
							if (invitedIds.size() > 0) {
								bos = new ByteOutputStream();
								dos = new DataOutputStream(bos);
								dos.writeInt(ServerProtocol.S_BONUS_FROM_INVITE);
								dos.writeInt(invitedIds.size());
								for (int i = 0; i < invitedIds.size(); i++) {
									int invId = invitedIds.elementAt(i).intValue();
									DBUser dbUser = null;
									try {
										dbUser = UserDAO.getInstance().getUser(invId);
									}
									catch (DAOException e1) {
										BarServer.debug(id_user, "[CATCHED EXCEPTION]");
										e1.printStackTrace();
									}
									dos.writeInt((dbUser != null) ? dbUser.id_vk : null);
									dos.writeUTF((dbUser != null) ? dbUser.fullName : null);
									dos.writeUTF((dbUser != null) ? dbUser.photoPath : null);
									try {
										UserDAO.getInstance().acceptInviteBonus(id_user_in_db, invId);
									}
									catch (DAOException e1) {
										BarServer.debug(id_user, "[CATCHED EXCEPTION]");
										e1.printStackTrace();
									}
								}
								dos.flush();
								bos.flush();
								byteArray = bos.getBytes();
								getSession().send(ByteBuffer.wrap(byteArray));
								dos.close();
								bos.close();
							}
						}
						catch (DAOException e) {
							BarServer.debug(id_user, "[CATCHED EXCEPTION]");
							e.printStackTrace();
						}
						if (firstLaunch) {
							firstLaunch = false;
							try {
								UserDAO.getInstance().updateInitialized(id_user_in_db);
							}
							catch (DAOException e) {}
						}
					}
					else {
						// Another user, search
						//TODO выделить функция по записи пользовательских данных в поток. Но старт месаджи не слать
						DBUser dbUser = null;
						try {
							dbUser = UserDAO.getInstance().getUserByVK(Integer.parseInt(bar_id_user));
						}
						catch (DAOException e) {
							BarServer.debug(id_user, "[CATCHED EXCEPTION]");
							e.printStackTrace();
						}
						if (dbUser != null) {
							ByteOutputStream bos = new ByteOutputStream();
							DataOutputStream dos = new DataOutputStream(bos);
							dos.writeInt(ServerProtocol.S_BAR_LOADED);
							dos.writeInt(0); //firstLaunch
							dos.writeUTF(bar_id_user);
							dos.writeUTF(dbUser.fullName);
							dos.writeUTF(dbUser.photoPath);
							dos.writeInt(dbUser.level);
							dos.writeInt(dbUser.exp);
							dos.writeInt(dbUser.love);
							dos.writeInt(dbUser.invites);
							dos.writeInt(dbUser.cents);
							dos.writeInt(dbUser.euro);
							Vector<DBClient> dbUserClients = null;
							try {
								dbUserClients = UserDAO.getInstance().getClients(dbUser.id);
							}
							catch (DAOException e) {
								BarServer.debug(id_user, "[CATCHED EXCEPTION]");
								e.printStackTrace();
							}
							for (int i = 0; i < MAX_CLIENT_COUNT; i++) {
								if ((dbUserClients != null)) {
									boolean existClientOnThisPos = false;
									for (int ci = 0; ci < dbUserClients.size(); ci++) {
										DBClient dbClient = dbUserClients.elementAt(ci);
										if (i == dbClient.position) {
											dos.writeInt(dbClient.game_id);
											if (dbClient.game_id != NO_ID) {
												dos.writeUTF(dbClient.type);
												//TODO пока не используется - ид пользователя
												dos.writeUTF(NO_STR);
												dos.writeUTF(dbClient.name);
												dos.writeUTF(dbClient.goods_type);
											}
											existClientOnThisPos = true;
											break;
										}
									}
									if (!existClientOnThisPos) {
										dos.writeInt(NO_ID);
									}
								}
								else {
									dos.writeInt(NO_ID);
								}
							}
							dos.writeInt(0); //licensed count
							Vector<DBProduction> dbProduction = null;
							try {
								dbProduction = UserDAO.getInstance().getProduction(dbUser.id);
							}
							catch (DAOException e) {
								BarServer.debug(id_user, "[CATCHED EXCEPTION]");
								e.printStackTrace();
							}
							if (dbProduction != null) {
								dos.writeInt(dbProduction.size());
								for (int i = 0; i < dbProduction.size(); i++) {
									DBProduction dbProd = dbProduction.elementAt(i);
									if (dbProd.game_id != NO_ID) {
										dos.writeInt(dbProd.game_id);
										dos.writeUTF(dbProd.type);
										dos.writeInt(dbProd.parts_count);
										dos.writeInt(dbProd.cell);
										dos.writeInt(dbProd.row);
									}
								}
							}
							else {
								dos.writeInt(0);
							}
							Vector<DBDecor> dbDecor = null;
							try {
								dbDecor = UserDAO.getInstance().getDecor(dbUser.id);
							}
							catch (DAOException e) {
								BarServer.debug(id_user, "[CATCHED EXCEPTION]");
								e.printStackTrace();
							}
							if (dbDecor != null) {
								dos.writeInt(dbDecor.size());
								for (int i = 0; i < dbDecor.size(); i++) {
									DBDecor dbDec = dbDecor.elementAt(i);
									if (dbDec.game_id != NO_ID) {
										dos.writeInt(dbDec.game_id);
										dos.writeUTF(dbDec.type);
									}
								}
							}
							else {
								dos.writeInt(0);
							}
							dos.flush();
							bos.flush();
							byte[] byteArray = bos.getBytes();
							getSession().send(ByteBuffer.wrap(byteArray));
							dos.close();
							bos.close();
						}
					}
					break;
				case ServerProtocol.C_MONEY_CENT_CHANGED:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					moneyCent = dis.readInt();
					try {
						UserDAO.getInstance().updateUserCents(id_user_in_db, moneyCent);
					}
					catch (DAOException e) {
					}
					//BarServer.debug("[C_MONEY_CENT_CHANGED]: " + moneyCent);
					break;
				case ServerProtocol.C_MONEY_EURO_CHANGED:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					moneyEuro = dis.readInt();
					try {
						UserDAO.getInstance().updateUserEuro(id_user_in_db, moneyEuro);
					}
					catch (DAOException e) {
					}
					//BarServer.debug("[C_MONEY_EURO_CHANGED]: " + moneyEuro);
					break;
				case ServerProtocol.C_ENTER_BY_INVITE:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					invites++;
//					if (invitedIds == null) {
//						invitedIds = new Vector<String>();
//					}
					String friend_id = dis.readUTF();
//					invitedIds.add(friend_id);
					try {
						DBUser dbUser = UserDAO.getInstance().getUserByVK(Integer.parseInt(friend_id));
						BarServer.debug(id_user, "dbUser: " + dbUser.id);
						if (dbUser != null && !UserDAO.getInstance().existInvite(dbUser.id, id_user_in_db)) {
							BarServer.debug(id_user, "not existInvite");
							UserDAO.getInstance().addInvite(dbUser.id, id_user_in_db);
						}
						else {
							BarServer.debug(id_user, "existInvite");
						}
					}
					catch (DAOException e) {
						BarServer.debug(id_user, "[CATCHED EXCEPTION]");
						e.printStackTrace();
					}
					BarServer.debug(id_user, "[C_ENTER_BY_INVITE]: " + friend_id);
					break;
				case ServerProtocol.C_VK_ATTRS:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					fullName = dis.readUTF();
					photoPath = dis.readUTF();
					try {
						UserDAO.getInstance().updateVKAttrs(id_user_in_db, fullName, photoPath);
					}
					catch (DAOException e) {
					}

					BarServer.debug(id_user, "[C_VK_ATTRS]: " + fullName + ", " + photoPath);
					break;
				case ServerProtocol.C_USER_ATTRS_CHANGED:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					level = dis.readInt();
					experience = dis.readInt();
					love = dis.readInt();
					try {
						UserDAO.getInstance().updateUserAttrs(id_user_in_db, level, experience, love);
					}
					catch (DAOException e) {
					}
					//BarServer.debug("[C_USER_ATTRS_CHANGED]: Lev:" + level + ", Exp:" + experience + ", Love:" + love);
					break;
				case ServerProtocol.C_CLIENT_COME:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					int ccId = dis.readInt();
					String ccType = dis.readUTF();
					String ccId_user = dis.readUTF();
					String ccFullName = dis.readUTF();
					String ccGoodsType = dis.readUTF();
					int ccPosition = dis.readInt();
					if (clientIds == null) {
						initClients();
					}
					if (ccPosition < MAX_CLIENT_COUNT) {
						clientIds[ccPosition] = ccId;
						clientTypes[ccPosition] = ccType;
						clientUserIds[ccPosition] = ccId_user;
						clientNames[ccPosition] = ccFullName;
						clientGoodsType[ccPosition] = ccGoodsType;
					}
					try {
						//TODO ccId_user-ид. пользователя ВКонтакте пока не пишется.
						UserDAO.getInstance().insertClient(id_user_in_db, ccId, ccType, ccGoodsType, ccFullName, ccPosition);
					}
					catch (DAOException e) {

					}
					//BarServer.debug("[C_CLIENT_COME]: id:" + ccId + " type:" + ccType + " idUser:" + ccId_user + " Name:" + ccFullName + " Position:" + ccPosition);
					break;
				case ServerProtocol.C_CLIENT_SERVED:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					int csId = dis.readInt();
					//BarServer.debug("[C_CLIENT_SERVED]: id:" + csId);
					if (clientIds != null) {
						for (int i = 0; i < MAX_CLIENT_COUNT; i++) {
							if (clientIds[i] == csId) {
								clientIds[i] = NO_ID;
								clientTypes[i] = NO_STR;
								clientUserIds[i] = NO_STR;
								clientNames[i] = NO_STR;
								clientGoodsType[i] = NO_STR;
								//BarServer.debug("Client deleted at position: " + i);
								break;
							}
						}
					}
					try {
						UserDAO.getInstance().deleteClient(id_user_in_db, csId);
					}
					catch (DAOException e) {

					}
					break;
				case ServerProtocol.C_CLIENT_DENIED:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					int cdId = dis.readInt();
					//BarServer.debug("[C_CLIENT_DENIED]: id:" + cdId);
					if (clientIds != null) {
						for (int i = 0; i < MAX_CLIENT_COUNT; i++) {
							if (clientIds[i] == cdId) {
								clientIds[i] = NO_ID;
								clientTypes[i] = NO_STR;
								clientUserIds[i] = NO_STR;
								clientNames[i] = NO_STR;
								clientGoodsType[i] = NO_STR;
								//BarServer.debug("Client deleted at position: " + i);
								break;
							}
						}
					}
					try {
						UserDAO.getInstance().deleteClient(id_user_in_db, cdId);
					}
					catch (DAOException e) {

					}
					break;
				case ServerProtocol.C_PRODUCTION_LICENSED:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					String lProdType = dis.readUTF();
					if (licensedProdTypes == null) {
						licensedProdTypes = new Vector<String>();
					}
					licensedProdTypes.add(lProdType);
					try {
						UserDAO.getInstance().insertLicenseProduction(id_user_in_db, lProdType);
					}
					catch (DAOException e) {

					}
					//BarServer.debug("[C_PRODUCTION_LICENSED]: type:" + lProdType);
				break;
				case ServerProtocol.C_PRODUCTION_ADDED_TO_BAR:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					int addIdProd = dis.readInt();
					String addProdType = dis.readUTF();
					int addPartsCount = dis.readInt();
					int addCell = dis.readInt();
					int addRow = dis.readInt();
					if (prodIds == null) {
						initProduction();
					}
					addProduction(addIdProd, addProdType, addPartsCount, addCell, addRow);
					//BarServer.debug("[C_PRODUCTION_ADDED_TO_BAR]: id:" + addIdProd + " type:" + addProdType + " Parts:" + addPartsCount);
				break;
				case ServerProtocol.C_PRODUCTION_CHANGE_PARTS:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					int cpIdProd = dis.readInt();
					int cpPartsProd = dis.readInt();
					if (prodIds != null) {
						int cpIndex = getProductionIndex(cpIdProd);
						if (cpIndex >= 0) {
							prodPartsCount[cpIndex] = cpPartsProd;
							//BarServer.debug("[C_PRODUCTION_CHANGE_PARTS]: id:" + cpIdProd + " Parts:" + cpPartsProd);
						}
					}
					try {
						UserDAO.getInstance().updateProductionParts(id_user_in_db, cpIdProd, cpPartsProd);
					}
					catch (DAOException e) {

					}
				break;
				case ServerProtocol.C_PRODUCTION_CHANGE_PLACE:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					int ppIdProd = dis.readInt();
					int pCell = dis.readInt();
					int pRow = dis.readInt();
					if (prodIds != null) {
						int ppIndex = getProductionIndex(ppIdProd);
						if (ppIndex >= 0) {
							prodCell[ppIndex] = pCell;
							prodRow[ppIndex] = pRow;
							//BarServer.debug("[C_PRODUCTION_CHANGE_PLACE]: id:" + ppIdProd + " Cell:" + pCell + " Row:" + pRow);
						}
					}
					try {
						UserDAO.getInstance().updateProductionPlace(id_user_in_db, ppIdProd, pCell, pRow);
					}
					catch (DAOException e) {

					}
				break;
				case ServerProtocol.C_PRODUCTION_DELETED:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					int delIdProd = dis.readInt();
					if (prodIds != null) {
						deleteProduction(delIdProd);
						//BarServer.debug("[C_PRODUCTION_DELETED]: id:" + delIdProd);
					}
				break;
				case ServerProtocol.C_DECOR_ADDED_TO_BAR:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					int addIdDec = dis.readInt();
					String addTypeDec = dis.readUTF();
					if (decorIds == null) {
						initDecor();
					}
					addDecor(addIdDec, addTypeDec);
					//BarServer.debug("[C_DECOR_ADDED_TO_BAR]: id:" + addIdDec + " type:" + addTypeDec + " count:" + decorCount());
				break;
				case ServerProtocol.C_DECOR_DELETED:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					int delIdDecor = dis.readInt();
					if (decorIds != null) {
						deleteDecor(delIdDecor);
						//BarServer.debug("[C_DECOR_DELETED]: id:" + delIdDecor);
					}
				break;
				case ServerProtocol.C_RESET_GAME:
//					dataMgr = AppContext.getDataManager();
//					dataMgr.markForUpdate(this);
					level = -1;
					experience = -1;
					love = -1;
					invites = -1;
					moneyCent = -1;
					moneyEuro = -1;
//					invitedIds = new Vector<String>();
					licensedProdTypes = new Vector<String>();
					initClients();
					initProduction();
					initDecor();
					//BarServer.debug("[C_RESET_GAME]:");
				break;
				case ServerProtocol.C_LOAD_FRIENDS:
					int friendsCount = dis.readInt();
					//BarServer.debug("[C_LOAD_FRIENDS]: friends count:" + friendsCount);
					Vector<DBUser> friendsInGame = new Vector<DBUser>();
//					Vector<Integer> friendsInGameIds = new Vector<Integer>();
//					Vector<Integer> friendsInGameLevels = new Vector<Integer>();
//					Vector<Integer> friendsInGameExp = new Vector<Integer>();
					//dataMgr = AppContext.getDataManager();
					int[] allFriendsIds = new int[friendsCount];
					for (int i = 0; i < friendsCount; i++) {
						allFriendsIds[i] = dis.readInt();
					}
					try {
						friendsInGame = UserDAO.getInstance().getExistUsers(allFriendsIds);
					}
					catch (DAOException e) {
						BarServer.debug(id_user, "[CATCHED EXCEPTION]");
						e.printStackTrace();
					}
//					for (int i = 0; i < friendsCount; i++) {
//						try {
//							int id = dis.readInt();
//							//BarPlace bp = (BarPlace)dataMgr.getBinding(Integer.toString(id));
//							DBUser u = UserDAO.getInstance().getUser1(id);
//							if (u != null) {
//								friendsInGameIds.add(new Integer(id));
//								if (u.level < 1) {
//									u.level = 1;
//								}
//								friendsInGameLevels.add(new Integer(u.level));
//								friendsInGameExp.add(new Integer(u.exp));
//								System.out.println("Id: " + id);
//							}
//						}
//						catch (Exception e) {
//							e.printStackTrace();
//						}
//					}

					ByteOutputStream bos = new ByteOutputStream();
					DataOutputStream dos = new DataOutputStream(bos);
					dos.writeInt(ServerProtocol.S_FRIENDS_LOADED);
					dos.writeInt(friendsInGame.size());
					BarServer.debug(id_user, "Friends in game count: " + friendsInGame.size());
					for (int i = 0; i < friendsInGame.size(); i++) {
						dos.writeInt(friendsInGame.elementAt(i).id_vk);
						dos.writeInt(friendsInGame.elementAt(i).level);
						dos.writeInt(friendsInGame.elementAt(i).exp);
//						dos.writeInt(friendsInGameIds.elementAt(i).intValue());
//						dos.writeInt(friendsInGameLevels.elementAt(i).intValue());
//						dos.writeInt(friendsInGameExp.elementAt(i).intValue());
					}
					dos.flush();
					bos.flush();
					byte[] byteArray = bos.getBytes();
					getSession().send(ByteBuffer.wrap(byteArray));
					dos.close();
					bos.close();
				break;
				case ServerProtocol.C_WITHDRAW_VOTES:
					final int votes = dis.readInt();
					final String item = dis.readUTF();
					final String authKey = dis.readUTF();
					BarServer.debug(id_user, "[C_WITHDRAW_VOTES]: votes:" + votes + " item:" + item);
					//Thread t = new Thread(new WithdrawVotesRunnable(getSession(), id_user, votes, item, authKey));
					//t.start();
					AppContext.getTaskManager().scheduleTask(new WithdrawVotesRunnable(getSession(), id_user, votes, item, authKey));
				break;
				default:
					BarServer.debug(id_user, "Undefined token: " + token);
			}
			dis.close();
			bis.close();
		}
		catch (IOException e) {
			BarServer.debug(id_user, "Cannot read bytes!");
			return;
		}
	}
}

class WithdrawVotesRunnable implements Task, Serializable, ManagedObject {

	private static final long	serialVersionUID	= 6864414276773858678L;
	private ManagedReference<ClientSession> currentSessionRef = null;
	private String id_user;
	private int votes;
	private String item;
	private String authKey;

	public WithdrawVotesRunnable(ClientSession s, String id, int v, String i, String a) {
        DataManager dataMgr = AppContext.getDataManager();
        currentSessionRef = dataMgr.createReference(s);
		id_user = id;
		votes = v;
		item = i;
		authKey = a;
	}

	public void run() {
		withdrawVotes();
	}

	/**
	 *
	 *
	 * @param votes - количество голосов (сотые)
	 * @param authKey
	 */
	public void withdrawVotes() {
		BarServer.debug(id_user, "Withdraw votes:");
		String md5Str = BarPlace.APP_ID + '_' + id_user + '_' + BarPlace.SECURE_API_KEY;
		String authKeyCheck = md5(md5Str);
		BarServer.debug(id_user, "authKeyCheck: " + authKeyCheck);
		BarServer.debug(id_user, "authKey: " + authKey);
		if (authKeyCheck.compareTo(authKey) == 0) {
			Random r = new Random();
			r.nextInt(1000);
			String randStr = Integer.toString(r.nextInt(1000));
			String timeStamp = new Long(System.currentTimeMillis()).toString();
			String params = "api_id=" + BarPlace.APP_ID +
					"format=json" +
					"method=secure.withdrawVotes" +
					"random=" + randStr +
					"timestamp=" + timeStamp +
					"uid=" + id_user +
					"v=2.0" +
					"votes=" + votes;
			String sig = md5(params + BarPlace.SECURE_API_KEY);
			String requestStr = BarPlace.url + "?" +
					"api_id=" + BarPlace.APP_ID +
					"&format=json" +
					"&method=secure.withdrawVotes" +
					"&random=" + randStr +
					"&timestamp=" + timeStamp +
					"&uid=" + id_user +
					"&v=2.0" +
					"&votes=" + votes +
					"&sig=" + sig;
			BarServer.debug(id_user, "Request: " + requestStr);
			try {
				URL vkUrl = new URL(requestStr);
				URLConnection uc = vkUrl.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
				String responseStr = "";
				String inputLine = "";
				while ((inputLine = in.readLine()) != null) {
					responseStr += inputLine;
				}
				BarServer.debug(id_user, "response: " + responseStr);
				in.close();
				ByteOutputStream bos = new ByteOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				JSONObject resp = (JSONObject) JSONSerializer.toJSON(responseStr);
				int transferredVotes = -1;
				try {
					transferredVotes = resp.getInt("response");
				}
				catch (Exception e) {}
				if (transferredVotes == votes) {
					dos.writeInt(ServerProtocol.S_WITHDRAW_VOTES_OK);
					dos.writeInt(votes);
					dos.writeUTF(item);
				}
				else {
					int errorCode = -1;
					try {
						JSONObject error = resp.getJSONObject("error");
						errorCode = error.getInt("error_code");
					}
					catch (Exception e) {}
					if (errorCode == 502) {
						int userBalance = getUserBalance();
						dos.writeInt(ServerProtocol.S_WITHDRAW_VOTES_NOT_ENOUGH);
						dos.writeInt(votes);
						dos.writeUTF(item);
						dos.writeInt(userBalance);
					}
					else {
						dos.writeInt(ServerProtocol.S_WITHDRAW_VOTES_ERROR);
					}
				}
				dos.flush();
				bos.flush();
				byte[] byteArray = bos.getBytes();
				BarServer.debug(id_user, "byteArr len: " + byteArray.length + " item:" + item);
				currentSessionRef.get().send(ByteBuffer.wrap(byteArray));
				dos.close();
				bos.close();
			}
			catch (Exception e) {
				BarServer.debug(id_user, "[CATCHED EXCEPTION]");
				e.printStackTrace();
			}
//			$params = array(
//				'api_id'	=>	constant('APP_ID'),
//				'v'			=>	'2.0',
//				'method'	=>	'secure.withdrawVotes',
//				'timestamp'	=>	time(),
//				'random'	=>	rand(0, 1000),
//				'uid'		=>	$viewer_id,
//				'votes'		=>	($votes * 100),
//				'format'	=>	'json'
//			);
//			$response = Facecontrol_Dispatcher_Api::vk_request($params);
		}
		else {
			BarServer.debug(id_user, "Invalid authKey!");
		}
	}

	/*
	 * Запрос баланса пользователя в приложении - определеить сколько не хватает голосов
	 */
	public int getUserBalance() {
		int userBalance = 0;
		Random r = new Random();
		r.nextInt(1000);
		String randStr = Integer.toString(r.nextInt(1000));
		String timeStamp = new Long(System.currentTimeMillis()).toString();
		String params = "api_id=" + BarPlace.APP_ID +
				"format=json" +
				"method=secure.getBalance" +
				"random=" + randStr +
				"timestamp=" + timeStamp +
				"uid=" + id_user +
				"v=2.0";
		String sig = md5(params + BarPlace.SECURE_API_KEY);
		String requestStr = BarPlace.url + "?" +
				"api_id=" + BarPlace.APP_ID +
				"&format=json" +
				"&method=secure.getBalance" +
				"&random=" + randStr +
				"&timestamp=" + timeStamp +
				"&uid=" + id_user +
				"&v=2.0" +
				"&sig=" + sig;
		BarServer.debug(id_user, "Request User Balance: " + requestStr);
		try {
			URL vkUrlB = new URL(requestStr);
			URLConnection ucB = vkUrlB.openConnection();
			BufferedReader inB = new BufferedReader(new InputStreamReader(ucB.getInputStream()));
			String responseStrB = "";
			String inputLineB = "";
			while ((inputLineB = inB.readLine()) != null) {
				responseStrB += inputLineB;
			}
			BarServer.debug(id_user, "Response: " + responseStrB);
			inB.close();
			ByteOutputStream bosB = new ByteOutputStream();
			DataOutputStream dosB = new DataOutputStream(bosB);
			JSONObject resp = (JSONObject) JSONSerializer.toJSON(responseStrB);
			try {
				userBalance = resp.getInt("balance");
			}
			catch (Exception e) {}
			BarServer.debug(id_user, "UserBalance: " + userBalance);
			dosB.flush();
			bosB.flush();
			dosB.close();
			bosB.close();
		}
		catch (Exception e) {
			BarServer.debug(id_user, "[CATCHED EXCEPTION]");
			e.printStackTrace();
		}
		return userBalance;
	}

	public String md5(String s) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes("UTF8"), 0, s.length());
			BigInteger i = new BigInteger(1,m.digest());
			return String.format("%1$032x", i);
		}
		catch (Exception e) {
			BarServer.debug(id_user, "[CATCHED EXCEPTION]");
			e.printStackTrace();
		}
		return "";
	}
}
