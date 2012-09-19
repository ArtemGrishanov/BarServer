/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.flashmedia.dbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * This class represents a SQL Database Access Object for the {@link User} DTO. This DAO should be
 * used as a central point for the mapping between the User DTO and a SQL database.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public final class UserDAO
{

	// Constants ----------------------------------------------------------------------------------
	private static final String	SQL_GET_USER					= "SELECT * FROM users WHERE id_user = ?";
	private static final String	SQL_GET_VK_USER					= "SELECT * FROM users WHERE id_vk = ?";
	private static final String SQL_UPDATE_USER					= "UPDATE users SET full_name = ?," +
																		"level = ?," +
																		"exp = ?," +
																		"love = ?," +
																		"invites = ?," +
																		"cents = ?," +
																		"euro = ?," +
																		"photo_path = ?" +
																		" WHERE id_user = ?";
	private static final String SQL_UPDATE_INITIALIZED			= "UPDATE users SET initialized = 1" +
																		" WHERE id_user = ?";
	private static final String SQL_INSERT_USER					= "INSERT INTO users (id_vk,full_name,level,exp,love,invites,cents,euro,photo_path,initialized) VALUES (?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE_USER_ATTRS			= "UPDATE users SET level = ?, exp = ?, love = ? WHERE id_user = ?";
	private static final String SQL_UPDATE_USER_CENTS			= "UPDATE users SET cents = ? WHERE id_user = ?";
	private static final String SQL_UPDATE_USER_EURO			= "UPDATE users SET euro = ? WHERE id_user = ?";
	private static final String SQL_UPDATE_VK_ATTRS				= "UPDATE users SET full_name = ?, photo_path = ? WHERE id_user = ?";
	private static final String SQL_INSERT_CLIENT				= "INSERT INTO clients (id_user,game_id,type,goods_type,name,position) VALUES (?,?,?,?,?,?)";
	private static final String SQL_DELETE_CLIENT				= "DELETE FROM clients WHERE id_user = ? AND game_id = ?";
	private static final String SQL_INSERT_LICENCE_PRODUCTION	= "INSERT INTO license (id_user,prod_type) VALUES (?,?)";
	private static final String SQL_INSERT_PRODUCTION			= "INSERT INTO prod (id_user,type,game_id,parts_count,cell,row) VALUES (?,?,?,?,?,?)";
	private static final String SQL_DELETE_PRODUCTION			= "DELETE FROM prod WHERE id_user = ? AND game_id = ?";
	private static final String SQL_UPDATE_PRODUCTION_PARTS		= "UPDATE prod SET parts_count = ? WHERE id_user = ? AND game_id = ?";
	private static final String SQL_UPDATE_PRODUCTION_PLACE		= "UPDATE prod SET cell = ?, row = ? WHERE id_user = ? AND game_id = ?";
	private static final String SQL_INSERT_DECOR				= "INSERT INTO decor (id_user,type,game_id) VALUES (?,?,?)";
	private static final String SQL_DELETE_DECOR				= "DELETE FROM decor WHERE id_user = ? AND game_id = ?";
	private static final String	SQL_GET_CLIENTS					= "SELECT * FROM clients WHERE id_user = ?";
	private static final String	SQL_GET_PRODUCTION				= "SELECT * FROM prod WHERE id_user = ?";
	private static final String	SQL_GET_DECOR					= "SELECT * FROM decor WHERE id_user = ?";
	private static final String	SQL_GET_LICENSED_PROD_TYPES		= "SELECT * FROM license WHERE id_user = ?";
	private static final String SQL_DELETE_USER_CLIENTS			= "DELETE FROM clients WHERE id_user = ?";
	private static final String SQL_DELETE_USER_PRODUCTION		= "DELETE FROM prod WHERE id_user = ?";
	private static final String SQL_DELETE_USER_DECOR			= "DELETE FROM decor WHERE id_user = ?";
	private static final String SQL_DELETE_USER_LICENSE			= "DELETE FROM license WHERE id_user = ?";
	private static final String SQL_INSERT_SESSION				= "INSERT INTO sessions (id_user,start_time) VALUES (?,?)";
	private static final String SQL_GET_MAX_USER_START_SESSION_TIME = "SELECT MAX(start_time) AS MaxStartTime FROM sessions WHERE id_user = ?";
	private static final String SQL_UPDATE_SESSION				= "UPDATE sessions SET end_time = ? WHERE id_user = ? AND start_time = ?";
	private static final String SQL_INSERT_INVITE				= "INSERT INTO invites (id_inviter, id_invited) VALUES (?,?)";
	private static final String SQL_UPDATE_INVITE				= "UPDATE invites SET bonus_accepted = 1 WHERE id_inviter = ? AND id_invited = ?";
	private static final String	SQL_GET_ALL_INVITES				= "SELECT * FROM invites WHERE id_inviter = ? AND id_invited = ?";
	private static final String	SQL_GET_NOTACCEPTED_INVITES		= "SELECT * FROM invites WHERE id_inviter = ? AND bonus_accepted = 0";
	private static final String	SQL_GET_LAST_START_MESSAGES		= "SELECT * FROM `start_messages` ORDER BY id_msg DESC LIMIT ?";
	private static final String SQL_UPDATE_LAST_USER_MESSAGE	= "UPDATE users SET last_msg_id = ? WHERE id_user = ?";
	private static final String SQL_GET_SOME_USERS				= "SELECT * FROM users WHERE id_user > ? LIMIT ?";

//	private static final String	SQL_LAST_MEET_END				= "SELECT last_meet_end FROM vt_meeting_stat WHERE id_user_1 = ? AND id_user_2 = ?";
//	private static final String	SQL_LAST_MEET_END				= "SELECT date_finish FROM vt_meeting WHERE id_user_1 = ? AND id_user_2 = ?";
//	private static final String	SQL_WANT_MEET_STATUS			= "SELECT * FROM vt_wantmeet v WHERE id_user_1 = ? AND id_user_2 = ?";
//	private static final String	SQL_ICON_URL					= "SELECT up.filename, up.server, um.id_user FROM vt_user_meta AS um JOIN vt_user_photo AS up ON um.id_photo = up.id_photo AND up.id_user = ?";
//	private static final String	SQL_PHOTO_COUNT					= "SELECT COUNT(*) AS cnt FROM vt_user_photo v WHERE id_user = ?";
//	private static final String	SQL_ICQ_INTERESTS_ABOUT			= "SELECT icq,interests,about FROM vt_user_meta WHERE id_user = ?";
//	private static final String	SQL_LINKS						= "SELECT name,url FROM vt_user_link WHERE id_user = ?";
//	private static final String	SQL_GIFT_COUNT					= "SELECT COUNT(*) AS cnt FROM vt_user_gift WHERE id_user_to = ?";
//
//	private static final String SQL_USER_GIFT_COUNT				= "SELECT COUNT(*) AS cnt FROM vt_user_gift WHERE id_user_from = ? and id_user_to = ?";
//
//	private static final String	SQL_PLACES						= "SELECT name FROM vt_user_places u JOIN vt_places p ON u.id_place = p.id_place WHERE u.id_user = ?";
//	private static final String	SQL_IS_FAVORITE					= "SELECT * FROM vt_favorites v WHERE id_user_1 = ? AND id_user_2 = ?";
//	private static final String SQL_IS_ONLINE					= "SELECT * FROM vt_user u WHERE id_user = ?";
//	// ----------------------------------------------
//	private static final String	SQL_DELETE_STATUS				= "SELECT delete_status FROM vt_user v WHERE id_user = ?";
//	private static final String	SQL_WANT_MEET_BOTH				= "SELECT * FROM vt_wantmeet WHERE id_user_1 = ? AND status = 1";
//	private static final String	SQL_WANT_MEET					= "SELECT id_user_1, id_user_2 FROM vt_wantmeet WHERE id_user_? = ? AND status = 0";
//	private static final String	SQL_WANT_MEET_COUNTS_ME			= "SELECT COUNT(*) AS cnt FROM `vt_wantmeet` where id_user_2 = ? AND `status` = 0";
//	private static final String	_SQL_WANT_MEET_COUNTS_ME		= "SELECT `want_meet_me` FROM `vt_user_notification`";
//	private static final String	SQL_WANT_MEET_COUNTS_CROSSLY	= "SELECT COUNT(*) AS cnt FROM `vt_wantmeet` where id_user_1 = ? AND `status` = 1";
//	private static final String	_SQL_WANT_MEET_COUNTS_CROSSLY	= "SELECT want_meet_crossly FROM vt_user_notification WHERE id_user = ?";
//	private static final String	_SQL_WANT_MEET					= "SELECT * FROM `vt_wantmeet` WHERE id_user_1 = ? AND id_user_2 = ?";
//	private static final String	SQL_LAST_MEETING				= "SELECT * FROM `vt_meeting` WHERE id_user_1 = ? AND id_user_2 = ? ORDER BY date_start DESC";
//
//	// ------------------------------------------------
//	private static final String	SQL_FAVORITE					= "SELECT id_user_2 FROM vt_favorites WHERE id_user_1 = ?";
//	private static final String	SQL_ENCOUNTERED					= "SELECT id_user_2 FROM vt_meeting_all WHERE id_user_1 = ?";
//	private static final String	SQL_ENCOUNTERED_USUAL			= "SELECT id_user_2 FROM vt_meeting_all WHERE id_user_1 = ? AND date_start >= ? AND date_finish <= ?";
//	private static final String	SQL_ENCOUNTERED_FAVORITE		= "SELECT fav.id_user_2 FROM vt_favorites AS fav JOIN vt_meeting_all AS ma " + "ON fav.id_user_1 = ma.id_user_1 AND fav.id_user_2 = ma.id_user_2 "
//																	+ "AND fav.id_user_1 = ? AND date_start >= ? AND date_finish <= ?";
//
//	private static final String	SQL_ENCOUNTERED_USUAL_SEX		= "SELECT DISTINCT id_user_2 FROM vt_meeting_all AS m JOIN vt_user AS u ON id_user_2 = id_user WHERE id_user_1 = ? AND sex = ? AND age >= ? AND age <= ?";
//	private static final String SQL_ENCOUNTERED_FAVORITE_SEX	= "SELECT id_user_2 FROM vt_favorites AS f JOIN vt_user AS u ON id_user_2 = id_user WHERE id_user_1 = ? AND sex = ? AND age >= ? AND age <= ?";
//	private static final String	SQL_ENCOUNTERED_USUAL_SEX_ALL	= "SELECT DISTINCT id_user_2 FROM vt_meeting_all AS m JOIN vt_user AS u ON id_user_2 = id_user WHERE id_user_1 = ? AND age >= ? AND age <= ?";
//	private static final String SQL_ENCOUNTERED_FAVORITE_SEX_ALL = "SELECT id_user_2 FROM vt_favorites AS f JOIN vt_user AS u ON id_user_2 = id_user WHERE id_user_1 = ? AND age >= ? AND age <= ?";
//
//	//	ssdfsdf
//	private static final String	SQL_ENCOUNTERED_USUAL_NAME 		= "SELECT DISTINCT id_user_2 FROM vt_meeting_all AS m JOIN vt_user AS u ON id_user_2 = id_user WHERE id_user_1 = ? AND nickname LIKE ?";
//	private static final String	SQL_ENCOUNTERED_FAVORITE_NAME 				= "SELECT id_user_2 FROM vt_favorites AS f JOIN vt_user AS u ON id_user_2 = id_user WHERE id_user_1 = ? AND nickname LIKE ?";
//
//	// ------------------------------------------------
//	private static final String	SQL_SETTINGS					= "SELECT q.sex,q.age_from,q.age_to,q.id_city,u.delete_status,v.participate_in_top,v.is_can_rcv_sms,v.sms_valid_begin_hour,v.sms_valid_begin_minute,v.sms_valid_end_hour,v.sms_valid_end_minute "
//																	+ "FROM vt_user_meta v JOIN vt_people_filter q ON v.id_user = q.id_user " + "AND v.id_user = ? JOIN vt_user u ON q.id_user = u.id_user";
//	// ------------------------------------------------
//	private static final String	SQL_PHOTO						= "SELECT * FROM vt_user_photo WHERE id_photo = ?";
//	private static final String	SQL_MAIN_PHOTO_ID				= "SELECT id_photo FROM vt_user_meta WHERE id_user = ?";
//	private static final String	SQL_PHOTOS						= "SELECT * FROM vt_user_photo WHERE id_user = ?";
//	// ------------------------------------------------
//	private static final String	SQL_UNREAD_GIFTS_COUNT			= "SELECT gifts_unread FROM vt_user_notification WHERE id_user = ?";
//	private static final String	SQL_UNREAD_GIFTS				= "SELECT id_user_gift, message FROM vt_user_gift WHERE date_read IS NULL AND id_user_to = ?";
//	private static final String	SQL_GIFT_CATEGORY_CONTENT		= "SELECT id_gift, name FROM `vt_gift` WHERE id_gift_category = ?";
//	private static final String	SQL_GIFT_CATEGORY				= "SELECT id_gift_category, name FROM vt_gift_category";
//	private static final String	SQL_GIFTS_INCOMING				= "SELECT * FROM vt_user_gift WHERE id_user_to = ?";
//	private static final String	SQL_GIFTS_OUTGOING				= "SELECT * FROM `vt_user_gift` WHERE id_user_from = ?";
//	private static final String	SQL_GIFT_LAST					= "SELECT id_user_gift FROM `vt_user_gift` WHERE id_user_from = ? AND id_user_to = ? ORDER BY id_user_gift DESC";
//	private static final String	SQL_GIFT						= "SELECT * FROM vt_user_gift WHERE id_user_gift = ?";
//
//	private static final String SQL_SET_ACTIVITY				= "UPDATE vt_user_meta SET activity = ? WHERE id_user = ?";
//	private static final String SQL_GET_ACTIVITY				= "SELECT activity FROM vt_user_meta WHERE id_user = ?";
//
//	private static final String SQL_TOP_UP						= "SELECT date_top_up FROM vt_user_meta WHERE id_user = ?";
//	//---------------------------------------------------TODO
//	private static final String	SQL_MESSAGE						= "SELECT * FROM `vt_message` WHERE id_message = ?";
//	private static final String	SQL_MESSAGE_COUNT_INCOMING		= "SELECT COUNT(*) AS CNT FROM vt_message WHERE id_user = ? AND status <> ? AND status <> ?";
//	private static final String	SQL_MESSAGE_COUNT_OUTGOING		= "SELECT COUNT(*) AS CNT FROM `vt_message` WHERE id_user_from = ? AND status <> ? AND status <> ?";
//	private static final String	SQL_MESSAGE_LAST				= "SELECT id_message FROM `vt_message` WHERE id_user_from = ? AND id_user = ? ORDER BY id_message DESC";
//	private static final String	SQL_UNREAD_MESSAGE_COUNT		= "SELECT * FROM vt_user_notification WHERE id_user = ?";
//	//----------------------------------------------------TODO
//	private static final String	SQL_USER						= "SELECT * FROM vt_user AS us JOIN vt_user_meta AS um ON us.id_user = um.id_user WHERE us.id_user = ?";
//	private static final String	SQL_USER_PHONE					= "SELECT id_user FROM `vt_user` WHERE phone = ?";
//	private static final String	SQL_USER_MAC					= "SELECT id_user FROM `vt_user` WHERE mac = ?";
//	//----------------------------------------------------
//	private static final String	SQL_COUNTRY						= "SELECT * FROM vt_country WHERE name LIKE ?";
//	private static final String	SQL_CITY						= "SELECT * FROM `vtolpe_dev`.`vt_city` WHERE name LIKE ?";
//	private static final String	SQL_TIMEZONE					= "SELECT timezone FROM vt_city WHERE id_city = ?";
//	private static final String	SQL_REGION						= "SELECT `id_region` FROM `vt_city` WHERE id_city = ?";
//	private static final String	SQL_CAPITAL						= "SELECT `id_city` FROM `vt_region` WHERE id_region = ?";
//	private static final String	SQL_NUMSET						= "SELECT * FROM `vt_region_def` WHERE id_region = ?";
//	private static final String	SQL_PREFIX						= "SELECT prefix FROM vt_country AS co JOIN vt_city AS ci ON co.id_country = ci.id_country AND ci.id_city = ?";
//	private static final String	_SQL_CITY						= "SELECT c.id_city,c.name,r.name AS region FROM vt_city c JOIN vt_region r ON c.id_region = r.id_region AND c.name LIKE ?";
//	//----------------------------------------------------STAT
//	private static final String	SQL_TOP_USER					= "SELECT u.id_user,u.nickname,u.age,u.sex, MAX(rating) FROM vt_user_meta um JOIN vt_user u ON um.id_user = u.id_user WHERE u.id_city = ? GROUP BY rating DESC LIMIT 10";
//
//
//	private static final String	SQL_TOP_PLACES					= "SELECT name, COUNT(up.id_place) AS place_count FROM vt_places AS p JOIN vt_user_places AS up ON p.id_place = up.id_place WHERE p.id_city = ? GROUP BY up.id_place ORDER BY place_count DESC LIMIT 10";
//	private static final String	SQL_ALL							= "SELECT id_user FROM vt_user WHERE id_city = ? AND  age >= ? AND age <= ? AND sex = ? ORDER BY id_user DESC LIMIT ?,? ";
//	//----------------------------------------------------
//	private static final String	SQL_DELETE						= "DELETE FROM `vt_user` WHERE phone = ?";
//	//----------------------------------------------------LOAD TESTING
//	private static final String SQL_CHANGE_PASSWORD_BY_PHONE	= "UPDATE `vtolpe_dev`.`vt_user` SET `password` = ? WHERE phone = ?";
//	private static final String SQL_CHANGE_MAC_BY_PHONE			= "UPDATE `vtolpe_dev`.`vt_user` SET `mac` = ? WHERE phone = ?";
//	private static final String SQL_GET_MACS_BY_PHONE			= "SELECT mac FROM `vtolpe_dev`.`vt_user` WHERE phone >= ? AND phone <= ?";
//	private static final String SQL_GET_IDS_BY_PHONE			= "SELECT id_user FROM `vtolpe_dev`.`vt_user` WHERE phone >= ? AND phone <= ?";

	// Vars ---------------------------------------------------------------------------------------
	private DAOFactory			daoFactory;
	private static UserDAO		instance;

	public static UserDAO getInstance() {
		if (UserDAO.instance == null) {
			UserDAO.instance = DAOFactory.getInstance("javabase").getUserDAO();
		}
		return UserDAO.instance;
	}

	// Constructors -------------------------------------------------------------------------------

	/**
	 * Construct an User DAO for the given DAOFactory. Package private so that it can be constructed
	 * inside the DAO package only.
	 * @param daoFactory The DAOFactory to construct this User DAO for.
	 */
	UserDAO(DAOFactory daoFactory)
	{
		this.daoFactory = daoFactory;
	}

	public DBUser getUser(int id_in_database) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		DBUser user = null;
		Object[] values = {id_in_database};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_USER, false, values);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				user = new DBUser();
				user.id = resultSet.getInt("id_user");
				user.id_vk = resultSet.getInt("id_vk");
				user.fullName = resultSet.getString("full_name");
				user.level = resultSet.getInt("level");
				user.exp = resultSet.getInt("exp");
				user.love = resultSet.getInt("love");
				user.invites = resultSet.getInt("invites");
				user.cents = resultSet.getInt("cents");
				user.euro = resultSet.getInt("euro");
				user.photoPath = resultSet.getString("photo_path");
				user.initialized = resultSet.getInt("initialized");
				user.last_msg_id = resultSet.getInt("last_msg_id");
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		return user;
	}

	public DBUser getUserByVK(int id_vk) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		DBUser user = null;
		Object[] values = {id_vk};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_VK_USER, false, values);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				user = new DBUser();
				user.id = resultSet.getInt("id_user");
				user.id_vk = id_vk;
				user.fullName = resultSet.getString("full_name");
				user.level = resultSet.getInt("level");
				user.exp = resultSet.getInt("exp");
				user.love = resultSet.getInt("love");
				user.invites = resultSet.getInt("invites");
				user.cents = resultSet.getInt("cents");
				user.euro = resultSet.getInt("euro");
				user.photoPath = resultSet.getString("photo_path");
				user.initialized = resultSet.getInt("initialized");
				user.last_msg_id = resultSet.getInt("last_msg_id");
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		return user;
	}

	/**
	 * ���������� �������������� ��� �������������, ������� ���� � ����.
	 * �� ���, ��� ��������.
	 *
	 * @param ids_vk - �������������� ��� ����
	 * @return
	 * @throws DAOException
	 */
	public Vector<DBUser> getExistUsers(int[] ids_vk) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Vector<DBUser> users = new Vector<DBUser>();

		try
		{
			connection = daoFactory.getConnection();
			for (int i = 0; i < ids_vk.length; i++) {
				Object[] values = {ids_vk[i]};
				preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_VK_USER, false, values);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					DBUser user = new DBUser();
					user.id = resultSet.getInt("id_user");
					user.id_vk = ids_vk[i];
					user.fullName = resultSet.getString("full_name");
					user.level = resultSet.getInt("level");
					user.exp = resultSet.getInt("exp");
					user.love = resultSet.getInt("love");
					user.invites = resultSet.getInt("invites");
					user.cents = resultSet.getInt("cents");
					user.euro = resultSet.getInt("euro");
					user.photoPath = resultSet.getString("photo_path");
					user.initialized = resultSet.getInt("initialized");
					user.last_msg_id = resultSet.getInt("last_msg_id");
					users.add(user);
				}
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		return users;
	}
	
	/**
	 * ���������� ��������� �������������, ��������������� �������.
	 * ������������, ��������, � �������� �����������
	 * @param startId
	 * @param limit
	 * @throws DAOException
	 */
	public Vector<DBUser> getSomeUsers(int startId, int limit) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Vector<DBUser> users = new Vector<DBUser>();
		try
		{
			connection = daoFactory.getConnection();
			Object[] values = {startId, limit};
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_SOME_USERS, false, values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				DBUser user = new DBUser();
				user.id = resultSet.getInt("id_user");
				user.id_vk = resultSet.getInt("id_vk");
				user.fullName = resultSet.getString("full_name");
				user.level = resultSet.getInt("level");
				user.exp = resultSet.getInt("exp");
				user.love = resultSet.getInt("love");
				user.invites = resultSet.getInt("invites");
				user.cents = resultSet.getInt("cents");
				user.euro = resultSet.getInt("euro");
				user.photoPath = resultSet.getString("photo_path");
				user.initialized = resultSet.getInt("initialized");
				user.last_msg_id = resultSet.getInt("last_msg_id");
				users.add(user);
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		return users;
	}

	public void clearUserInfo(int id_user) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_DELETE_USER_PRODUCTION, false, values);
			preparedStatement.executeUpdate();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_DELETE_USER_CLIENTS, false, values);
			preparedStatement.executeUpdate();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_DELETE_USER_DECOR, false, values);
			preparedStatement.executeUpdate();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_DELETE_USER_LICENSE, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void updateUser(int id_user_in_db,
								String full_name,
								int level,
								int exp,
								int love,
								int invites,
								int cents,
								int euro,
								String photo_path) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {full_name, level, exp, love, invites, cents, euro, photo_path, id_user_in_db};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_USER, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void updateInitialized(int id_user_in_db) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user_in_db};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_INITIALIZED, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void insertUser(int id_vk,
							String full_name,
							int level,
							int exp,
							int love,
							int invites,
							int cents,
							int euro,
							String photo_path,
							int initialized) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_vk, full_name, level, exp, love, invites, cents, euro, photo_path, initialized};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_INSERT_USER, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void updateUserAttrs(int id_user_in_db,
		int level,
		int exp,
		int love) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {level, exp, love, id_user_in_db};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_USER_ATTRS, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void updateUserCents(int id_user_in_db,
		int cents) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {cents, id_user_in_db};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_USER_CENTS, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void updateUserEuro(int id_user_in_db,
		int euro) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {euro, id_user_in_db};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_USER_EURO, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void updateVKAttrs(int id_user_in_db,
		String fullName,
		String photoPath) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {fullName, photoPath, id_user_in_db};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_VK_ATTRS, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void insertClient(int id_user,
		int game_id,
		String type,
		String goods_type,
		String name,
		int position) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user, game_id, type, goods_type, name, position};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_INSERT_CLIENT, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void deleteClient(int id_user,
		int game_id) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user, game_id};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_DELETE_CLIENT, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void insertLicenseProduction(int id_user,
		String prod_type) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user,prod_type};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_INSERT_LICENCE_PRODUCTION, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void insertProduction(int id_user,
		String type,
		int game_id,
		int parts_count,
		int cell,
		int row) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user, type, game_id, parts_count, cell, row};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_INSERT_PRODUCTION, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void updateProductionParts(int id_user_in_db,
		int game_id,
		int parts_count) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {parts_count, id_user_in_db, game_id};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_PRODUCTION_PARTS, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void updateProductionPlace(int id_user_in_db,
		int game_id,
		int cell,
		int row) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {cell, row, id_user_in_db, game_id};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_PRODUCTION_PLACE, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void deleteProduction(int id_user,
		int game_id) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user, game_id};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_DELETE_PRODUCTION, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void insertDecor(int id_user,
		String type,
		int game_id) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user, type, game_id};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_INSERT_DECOR, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void deleteDecor(int id_user,
		int game_id) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user, game_id};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_DELETE_DECOR, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public Vector<DBClient> getClients(int id_user) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Vector<DBClient> clients = new Vector<DBClient>();
		Object[] values = {id_user};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_CLIENTS, false, values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				DBClient c = new DBClient();
				c.id_client = resultSet.getLong("id_client");
				c.id_user = resultSet.getInt("id_user");
				c.game_id = resultSet.getInt("game_id");
				c.type = resultSet.getString("type");
				c.goods_type = resultSet.getString("goods_type");
				c.name = resultSet.getString("name");
				c.position = resultSet.getInt("position");
				clients.add(c);
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		return clients;
	}

	public Vector<DBProduction> getProduction(int id_user) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Vector<DBProduction> production = new Vector<DBProduction>();
		Object[] values = {id_user};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_PRODUCTION, false, values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				DBProduction p = new DBProduction();
				p.id_prod = resultSet.getLong("idprod");
				p.id_user = resultSet.getInt("id_user");
				p.type = resultSet.getString("type");
				p.game_id = resultSet.getInt("game_id");
				p.parts_count = resultSet.getInt("parts_count");
				p.cell = resultSet.getInt("cell");
				p.row = resultSet.getInt("row");
				production.add(p);
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		return production;
	}

	public Vector<DBDecor> getDecor(int id_user) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Vector<DBDecor> decor = new Vector<DBDecor>();
		Object[] values = {id_user};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_DECOR, false, values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				DBDecor d = new DBDecor();
				d.id_decor = resultSet.getLong("iddecor");
				d.id_user = resultSet.getInt("id_user");
				d.type = resultSet.getString("type");
				d.game_id = resultSet.getInt("game_id");
				decor.add(d);
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		return decor;
	}

	public Vector<String> getLicensedProdTypes(int id_user) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Vector<String> licTypes = new Vector<String>();
		Object[] values = {id_user};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_LICENSED_PROD_TYPES, false, values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				licTypes.add(resultSet.getString("prod_type"));
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		return licTypes;
	}

	public void startUserSession(int id_user) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user, System.currentTimeMillis() / 1000};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_INSERT_SESSION, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	/**
	 * ������� ���������� ��������� �������� ������� ������ ������ ��� ������������.
	 * ����� ��� �� ���������� ����� ���� ������ �������.
	 * @param id_user
	 * @throws DAOException
	 */
	public void endUserSession(int id_user) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_MAX_USER_START_SESSION_TIME, false, values);
			resultSet = preparedStatement.executeQuery();
			long max_start_time = -1;
			while (resultSet.next()) {
				max_start_time = Long.parseLong(resultSet.getString("MaxStartTime"));
			}
			Object[] values1 = {System.currentTimeMillis() / 1000, id_user, max_start_time};
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_SESSION, false, values1);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public void addInvite(int id_user_inviter, int id_user_invited) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user_inviter, id_user_invited};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_INSERT_INVITE, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

	public Boolean existInvite(int id_user_inviter, int id_user_invited) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		int count = 0;
		Object[] values = {id_user_inviter, id_user_invited};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_ALL_INVITES, false, values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				count++;
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		return count > 0;
	}

	/**
	 * ���������� �������� ���, ���� id_user_inviter ���������.
	 * @param id_user_inviter
	 * @return
	 * @throws DAOException
	 */
	public Vector<Integer> getNotAcceptedInvites(int id_user_inviter) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Vector<Integer> ids = new Vector<Integer>();
		Object[] values = {id_user_inviter};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_NOTACCEPTED_INVITES, false, values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Integer id_invited = new Integer(resultSet.getInt("id_invited"));
				ids.add(id_invited);
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		return ids;
	}

	public void acceptInviteBonus(int id_user_inviter, int id_user_invited) throws DAOException
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {id_user_inviter, id_user_invited};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_INVITE, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}
	
	/**
	 * �������� ��������� ��������� ��������� �� start_messages
	 * ������ �������� � ������ - ����� �����
	 * @param count - ���������� ���������
	 * @throws DAOException
	 */
	public DBStartMessage[] getLastMessages(int count) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Vector<DBStartMessage> msgs = new Vector<DBStartMessage>();
		Object[] values = {count};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_LAST_START_MESSAGES, false, values);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				DBStartMessage m = new DBStartMessage();
				m.message_id = resultSet.getInt("id_msg");
				m.caption = resultSet.getString("caption");
				m.message = resultSet.getString("htmlText");
				m.buttons = resultSet.getString("buttons");
				msgs.add(m);
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
		DBStartMessage[] result = new DBStartMessage[msgs.size()];
		for (int i = 0; i < msgs.size(); i++) {
			result[i] = msgs.get(i);
		}
		return result;
	}
	
	/**
	 * ��� ������������ id_user ���������� id ���������� ������������ ���������.
	 * @param count - ���������� ���������
	 * @throws DAOException
	 */
	public void setLastMessages(int id_user, long last_msg_id) throws DAOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		Object[] values = {last_msg_id, id_user};

		try
		{
			connection = daoFactory.getConnection();
			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UPDATE_LAST_USER_MESSAGE, false, values);
			preparedStatement.executeUpdate();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			DAOUtil.close(resultSet);
			DAOUtil.close(preparedStatement);
			DAOUtil.close(connection);
		}
	}

//	public byte getWantMeetStatus(long userId1, long userId2) throws DAOException
//	{
//		byte wantMeet = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId1, userId2};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_WANT_MEET_STATUS, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				switch (resultSet.getByte("status"))
//				{
//					case 0:
//						wantMeet = User.WANT_MEET_I;
//						break;
//					case 1:
//						wantMeet = User.WANT_MEET_ME;
//						break;
//					case 2:
//						wantMeet = User.WANT_MEET_MUTUALLY;
//						break;
//				}
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return wantMeet;
//	}//===============================================
//
//	/*
//	 * Возвращает url иконки 50x60 (главное фото профайла). Если его нет "".
//	 */
//	public String getIconUrl(long userId) throws DAOException
//	{
//		String url = "";
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_ICON_URL, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				url = "http://" + resultSet.getString("server") + "/user_photos/u" + userId + "/" + 1 + "_" + resultSet.getString("filename") + ".jpg";
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return url;
//	}//===============================================
//
//	/*
//	 * Возвращает url иконки в зависимости от разрешения экрана (главное фото профайла). Если его нет "".
//	 */
//	public String _getIconUrl(long userId, short width) throws DAOException
//	{
//		String url = "";
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_ICON_URL, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				byte i = 0;
//
//				if (UserDAO.isIn(100, 149, width))
//				{
//					i = 1;
//				}
//				else if (UserDAO.isIn(150, 199, width))
//				{
//					i = 2;
//				}
//				else
//					i = 3;
//
//				url = "http://" + resultSet.getString("server") + "/user_photos/u" + userId + "/" + i + "_" + resultSet.getString("filename") + ".jpg";
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return url;
//	}//===============================================
//
//	/*
//	 * Возвращает количество фотографий юзера.
//	 */
//	public byte getPhotosCount(long userId) throws DAOException
//	{
//		int cnt = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_PHOTO_COUNT, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				cnt = resultSet.getInt("cnt");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return (byte)cnt;
//	}//===============================================
//
//	/*
//	 * Возвращает 1.icq, 2.interests, 3.about юзера.
//	 */
//	public Vector<String> getIcqInterestsAbout(long userId) throws DAOException
//	{
//		Vector<String> meta = new Vector<String>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_ICQ_INTERESTS_ABOUT, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				meta.add(resultSet.getString("icq"));
//				meta.add(resultSet.getString("interests"));
//				meta.add(resultSet.getString("about"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return meta;
//	}//===============================================
//
//	/*
//	 * Возвращает массив Links[{name,url}] юзера
//	 */
//	public Link[] getLinks(long userId) throws DAOException
//	{
//		Vector<Link> links = new Vector<Link>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_LINKS, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				Link link = new Link();
//
//				link.title = resultSet.getString("name");
//				link.url = resultSet.getString("url");
//
//				links.add(link);
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return links.toArray(new Link[links.size()]);
//	}//===============================================
//
//	/*
//	 * Возвращает число всех подарков, ПОДАРЕННЫХ пользователю
//	 */
//	public short getGiftsCount(long userId) throws DAOException
//	{
//		short cnt = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GIFT_COUNT, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				cnt = resultSet.getShort("cnt");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return cnt;
//	}//===============================================
//
////	SQL_USER_GIFT_COUNT
//	/*
//	 * Количество подарков от доного юзера другому
//	 */
//	public short getUserGiftsCount(long userIdFrom, long userIdTo) throws DAOException
//	{
//		short cnt = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userIdFrom, userIdTo};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_USER_GIFT_COUNT, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				cnt = resultSet.getShort("cnt");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return cnt;
//	}//===============================================
//
//
//	/*
//	 * Возвращает места юзера как строку.
//	 */
//	public String getPlacesAsString(long userId) throws DAOException
//	{
//		String places = "";
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_PLACES, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				if (places.length() == 0)
//				{
//					places = resultSet.getString("name");
//				}
//				else
//					places += "," + " " + resultSet.getString("name");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return places;
//	}//===============================================
//
//	/*
//	 * Возвращает места юзера как вектор.
//	 */
//	public Vector<String> getPlaces(long userId) throws DAOException
//	{
//		Vector<String> places = new Vector<String>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_PLACES, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				places.add(resultSet.getString("name"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return places;
//	}//===============================================
//
//	/*
//	 * Возвращает true если userId1 отметил userId2 как favorite иначе false
//	 */
//	public boolean isFavorite(long userId1, long userId2) throws DAOException
//	{
//		boolean favorite = false;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId1, userId2};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_IS_FAVORITE, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				favorite = true;
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return favorite;
//	}//===============================================
//
//
//	public Integer getActivity(long userId) throws DAOException {
////		SQL_GET_ACTIVITY
//		Integer activity = null;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_ACTIVITY, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				activity = resultSet.getInt("activity");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return activity;
//	}
//
//	public Long getDateTopUp(long userId) throws DAOException {
//		Long dateTopUp = null;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_TOP_UP, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				dateTopUp = resultSet.getLong("date_top_up");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return dateTopUp;
//	}
//
//	/*
//	 * Проверяет находится ли значение в интервале, включая начало и конец
//	 */
//	public static boolean isIn(int from, int to, int value)
//	{
//		return value >= from && value <= to ? true : false;
//	}//===============================================
//
//	//***************************   PROFILE   ************************************
//	//***************************   THE END   ************************************
//
//	/*
//	 * Возвращает структуру User с полями заполненными по аналогии с экшеном ActionUserProfile
//	 */
//	public User getUserProfile(TestClient client) throws DAOException
//	{
//		User user = new User();
//		user.profile = user.new Profile();
//		/*
//		 * server response
//		 */
//		user.status = getOnline(client.user.id);
//		user.meetEndTimestamp = getLastMeetEnd(client.id_user, client.user.id);
//		user.wantToMeet = getWantMeetStatus(client.id_user, client.user.id);
//		user.favorite = isFavorite(client.id_user, client.user.id);
//		/*
//		 * server response
//		 */
//		user.profile.iconUrl = _getIconUrl(client.user.id, client.screenWidth);
//		user.profile.photosCount = getPhotosCount(client.user.id);
//		user.profile.links = getLinks(client.user.id);
//		/*
//		 * server response
//		 */
//		Vector<String> meta = getIcqInterestsAbout(client.user.id);
//		user.profile.icq = meta.get(0);
//		user.profile.interests = meta.get(1);
//		user.profile.about = meta.get(2);
//		/*
//		 * server response.
//		 */
//		user.profile.giftsCount = getGiftsCount(client.user.id);
//		user.profile.favoritePlaces = getPlacesAsString(client.user.id);
//
//		return user;
//	}
//
//	public byte getOnline(long userId) throws DAOException {
//		byte online = -1;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_IS_ONLINE, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				online = resultSet.getByte("is_online");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return online;
//
//	}
//
//	//***************************   WANT MEET   ************************************
//	//***************************     BEGIN     ************************************
//	//======================    helpers    =======================
//
//	/*
//	 * Возвращает статус анкеты пользователя 0 - не удалена, 1 - удалена (скрыта)
//	 */
//	byte deleteStatus(long userId) throws DAOException
//	{
//		byte deleteStatus = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_DELETE_STATUS, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next()) deleteStatus = resultSet.getByte("delete_status");
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return deleteStatus;
//	}//===============================================
//
//	/*
//	 * Возвращает id пользователей, которые взаимно хотят встретиться с userId
//	 */
//	public Vector<Long> wantMeetBoth(long userId) throws DAOException
//	{
//		Vector<Long> users = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_WANT_MEET_BOTH, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				users.add(resultSet.getLong("id_user_2"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return users;
//	}//===============================================
//
//	/*
//	 * Возвращает id пользователей, которых хочет встретить userId если section - User.WANT_MEET_I
//	 * или id пользователей, которые хотят встретить userId если section - User.WANT_MEET_ME
//	 */
//	public Vector<Long> wantMeet(long userId, byte section) throws DAOException
//	{
//		Vector<Long> users = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {section, userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_WANT_MEET, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			switch (section)
//			{
//				case User.WANT_MEET_I:
//					while (resultSet.next())
//					{
//						users.add(resultSet.getLong("id_user_2"));
//					}
//					break;
//				case User.WANT_MEET_ME:
//					while (resultSet.next())
//					{
//						users.add(resultSet.getLong("id_user_1"));
//					}
//					break;
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return users;
//	}//===============================================
//
//	/*/
//	 * Возвращает количество пользователей, которые хотят встретить userId (из таблицы `vt_wantmeet`)
//	 */
//	public long wantMeetCountsMe(long userId) throws DAOException
//	{
//		long count = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_WANT_MEET_COUNTS_ME, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				count = resultSet.getLong("cnt");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return count;
//	}//===============================================
//
//	/*/
//	 * Возвращает количество пользователей, которые хотят встретить userId (из таблицы `vt_user_notification`)
//	 */
//	public long _wantMeetCountsMe(long userId) throws DAOException
//	{
//		long count = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO._SQL_WANT_MEET_COUNTS_ME, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				count = resultSet.getLong("want_meet_me");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return count;
//	}//===============================================
//
//	/*/
//	 * Возвращает количество пользователей, которых взаимно хочет встретить userId (из таблицы `vt_wantmeet`)
//	 */
//	public long wantMeetCountsCrossly(long userId) throws DAOException
//	{
//		long count = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_WANT_MEET_COUNTS_CROSSLY, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				count = resultSet.getLong("cnt");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return count;
//	}//===============================================
//
//	/*/
//	 * Возвращает количество пользователей, которых взаимно хочет встретить userId (из таблицы `vt_user_notification`)
//	 */
//	public long _wantMeetCountsCrossly(long userId) throws DAOException
//	{
//		long count = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO._SQL_WANT_MEET_COUNTS_CROSSLY, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				count = resultSet.getLong("want_meet_me");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return count;
//	}//===============================================
//
//	//***************************   WANT MEET   ************************************
//	//***************************    THE END    ************************************
//
//	/*
//	 * Возвращает true если юзер может участвовать в want meet, иначе - false
//	 */
//	public boolean isServiceAvailable(long userId) throws DAOException
//	{
//		return deleteStatus(userId) != 1 && getPhotosCount(userId) > 0 && getIcqInterestsAbout(userId).get(1) != null && getIcqInterestsAbout(userId).get(2) != null ? true : false;
//	}
//
//	//SFDSDFS
//	//***************************   PEOPLE VTOLPE   ************************************
//	//***************************       BEGIN       ************************************
//
//	public Vector<Long> getFavorite(long userId) throws DAOException
//	{
//		Vector<Long> peopleId = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_FAVORITE, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				peopleId.add(resultSet.getLong("id_user_2"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return peopleId;
//	}//===============================================
//
//	public Vector<Long> getEncountered(long userId) throws DAOException
//	{
//		Vector<Long> peopleId = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_ENCOUNTERED, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				peopleId.add(resultSet.getLong("id_user_2"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return peopleId;
//	}//===============================================
//
//	/*
//	 * Возвращает ВСЕХ пользователей встреченных userId за время от dateFrom, до dateTo (включая концы)
//	 */
//	public Vector<Long> _getEncounteredUsual(long userId, long dateFrom, long dateTo) throws DAOException
//	{
//		Vector<Long> peopleId = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId, dateFrom, dateTo};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_ENCOUNTERED_USUAL, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				peopleId.add(resultSet.getLong("id_user_2"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return peopleId;
//	}//===============================================
//
//	/**
//	 *
//	 */
//	public Vector<Long> _getEncounteredUsual(long userId, String namePart) throws DAOException
//	{
//		Vector<Long> peopleId = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId, new String("%" + namePart + "%")};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_ENCOUNTERED_USUAL_NAME, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				peopleId.add(resultSet.getLong("id_user_2"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return peopleId;
//	}//===============================================
//
//	/**
//	 *
//	 */
//	public Vector<Long> _getEncounteredFavorites(long userId, String namePart) throws DAOException
//	{
//		Vector<Long> peopleId = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		System.out.println("----------------- SQL_ENCOUNTERED_FAVORITE_NAME");
//		//	SQL_ENCOUNTERED_FAVORITE_NAME
//		Object[] values = {userId, new String("%" + namePart + "%")};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_ENCOUNTERED_FAVORITE_NAME, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				peopleId.add(resultSet.getLong("id_user_2"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return peopleId;
//	}//===============================================
//
//
//	/*
//	 * Возвращает �?ЗБРАННЫХ пользователей встреченных userId за время от dateFrom, до dateTo (включая концы)
//	 */
//	public Vector<Long> _getEncounteredFavorites(long userId, long dateFrom, long dateTo) throws DAOException
//	{
//		Vector<Long> peopleId = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId, dateFrom, dateTo};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_ENCOUNTERED_FAVORITE, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				peopleId.add(resultSet.getLong("id_user_2"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return peopleId;
//	}//===============================================
//
//	/**
//	 *
//	 */
//	public Vector<Long> _getEncounteredUsual(long userId, byte sex, byte ageFrom, byte ageTo) throws DAOException
//	{
//		Vector<Long> peopleId = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//		String SQL_EXECUTE;
//		Object[] values;
//
//		if(sex == User.SEX_ALL) {
//			values = new Object[]{userId, ageFrom, ageTo};
//			SQL_EXECUTE = UserDAO.SQL_ENCOUNTERED_USUAL_SEX_ALL;
//		} else {
//			values = new Object[]{userId, sex, ageFrom, ageTo};
//			SQL_EXECUTE = UserDAO.SQL_ENCOUNTERED_USUAL_SEX;
//		}
//
//		//SQL_ENCOUNTERED_USUAL_SEX
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, SQL_EXECUTE, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				peopleId.add(resultSet.getLong("id_user_2"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return peopleId;
//	}//===============================================
//
//
////	SQL_ENCOUNTERED_FAVORITE_SEX
//	public Vector<Long> _getEncounteredFavorites(long userId, byte sex, byte ageFrom, byte ageTo) throws DAOException
//	{
//		Vector<Long> peopleId = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//		//SQL_ENCOUNTERED_USUAL_SEX
//		String SQL_EXECUTE;
//		Object[] values;
//
//		if(sex == User.SEX_ALL) {
//			values = new Object[]{userId, ageFrom, ageTo};
//			SQL_EXECUTE = UserDAO.SQL_ENCOUNTERED_FAVORITE_SEX_ALL;
//			System.out.println("----------------- SQL_ENCOUNTERED_USUAL_SEX_ALL");
//		} else {
//			values = new Object[]{userId, sex, ageFrom, ageTo};
//			SQL_EXECUTE = UserDAO.SQL_ENCOUNTERED_FAVORITE_SEX;
//		}
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, SQL_EXECUTE, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				peopleId.add(resultSet.getLong("id_user_2"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return peopleId;
//	}//===============================================
//	//***************************   PEOPLE VTOLPE   ************************************
//	//***************************      THE END      ************************************
//
//	//***************************  SETTINGS  ************************************
//	//***************************    BEGIN   ************************************
//	//======================    helpers    =======================
//
//	/*
//	 * Возвращает настройки userId
//	 */
//	public TestSettings getSettings(long userId) throws DAOException
//	{
//		TestSettings settings = new TestSettings();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_SETTINGS, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				/*
//				* пол с которым хотят встретиться
//				*/
//				settings.sex = resultSet.getByte("sex");
//				/*
//				* возраст от wantMeetAgeFrom, возраст до wantMeetAgeTo
//				*/
//				settings.wantMeetAgeFrom = resultSet.getByte("age_from");
//				settings.wantMeetAgeTo = resultSet.getByte("age_to");
//				settings.cityId = resultSet.getInt("id_city");
//				/*
//				* статус удаления анкеты. 0 - не удалена, 1 - удалена (скрыта)
//				*/
//				settings.deleteStatus = resultSet.getByte("delete_status") == 0 ? false : true;
//				/*
//				* желание участвовать в топ города 1 - желаю, 0 - не желаю
//				*/
//				settings.participateinTop = resultSet.getByte("participate_in_top") == 0 ? false : true;
//				settings.receiveSms = resultSet.getByte("is_can_rcv_sms") == 0 ? false : true;
//				/*
//				* принимать смс от receiveFromHour часов, receiveFromMin - минут
//				*/
//				settings.receiveFromHour = resultSet.getByte("sms_valid_begin_hour");
//				settings.receiveFromMin = resultSet.getByte("sms_valid_begin_minute");
//				/*
//				* принимать смс до receiveToHour часов, receiveToMin - минут
//				*/
//				settings.receiveToHour = resultSet.getByte("sms_valid_end_hour");
//				settings.receiveToMin = resultSet.getByte("sms_valid_end_minute");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return settings;
//	}//===============================================
//
//	//***************************  SETTINGS  ************************************
//	//***************************   THE END  ************************************
//
//	//***************************  PHOTOS  ************************************
//	//***************************   BEGIN  ************************************
//	/*
//	 * Возвращает photoId, userId, filename, server, comment по photoId
//	 */
//	public TestPhoto getPhotoById(long photoId) throws DAOException
//	{
//		TestPhoto thePhoto = new TestPhoto();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {photoId};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_PHOTO, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				thePhoto.photoId = resultSet.getLong("id_photo");
//				thePhoto.userId = resultSet.getLong("id_user");
//				thePhoto.filename = resultSet.getString("filename");
//				thePhoto.server = resultSet.getString("server");
//				thePhoto.comment = resultSet.getString("comment");
//
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return thePhoto;
//	}//===============================================
//
//	/*
//	 * Возвращает id иконки (главной фото), если ее нет то null
//	 */
//	public Long getMainPhotoId(long userId) throws DAOException
//	{
//		Long photoId = null;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_MAIN_PHOTO_ID, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				photoId = resultSet.getLong("id_photo");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return photoId;
//	}//===============================================
//
//	/*
//	 * возвращает главную фотку
//	 */
//	public TestPhoto userMainPhoto(long userId) throws DAOException
//	{
//		return getPhotoById(getMainPhotoId(userId));
//
//	}//===============================================
//
//	/*
//	 * Возвращает фотографии пользователя
//	 */
//	public Vector<TestPhoto> getUserPhotos(long userId) throws DAOException
//	{
//		Vector<TestPhoto> photos = new Vector<TestPhoto>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_PHOTOS, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				TestPhoto thePhoto = new TestPhoto();
//
//				thePhoto.photoId = resultSet.getLong("id_photo");
//				thePhoto.comment = resultSet.getString("comment");
//				thePhoto.server = resultSet.getString("server");
//				thePhoto.filename = resultSet.getString("filename");
//				thePhoto.userId = resultSet.getLong("id_user");
//
//				photos.add(thePhoto);
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return photos;
//	}//===============================================
//
//	//***************************  PHOTOS  ************************************
//	//***************************  THE END  ************************************
//
//	//***************************  GIFTS  ************************************
//	//***************************  BEGIN  ************************************
//
//	/*
//	 * Возвращает число не просмотренных подарков из таблицы vt_user_notification
//	 */
//	public short getUnreadGiftsCount(long userId) throws DAOException
//	{
//		short count = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UNREAD_GIFTS_COUNT, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next()) count = resultSet.getShort("gifts_unread");
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return count;
//	}//===============================================
//
//	/*
//	 * Возвращает подарки - id_gift, name из категории - categoryId.
//	 */
//	public Map<Long, String> getGiftCategoryContent(long categoryId) throws DAOException
//	{
//		Map<Long, String> gifts = new LinkedHashMap<Long, String>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {categoryId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GIFT_CATEGORY_CONTENT, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				gifts.put(resultSet.getLong("id_gift"), resultSet.getString("name"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return gifts;
//	}//===============================================
//
//	/*
//	 * Возвращает список категорий подарков id_gift_category, name
//	 */
//	public Map<Long, String> getGiftCategoryList() throws DAOException
//	{
//		Map<Long, String> category = new LinkedHashMap<Long, String>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GIFT_CATEGORY, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				category.put(resultSet.getLong("id_gift_category"), resultSet.getString("name"));
//				System.out.println("------------------ +1");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return category;
//	}//===============================================
//
//	/*
//	 * Возвращает ВСЕ подарки юзера (в том числе удаленные и не оплаченные) входящие/исходящие
//	 */
//	public Vector<TestGift> getUserGifts(long userId, byte theType) throws DAOException
//	{
//		Vector<TestGift> gifts = new Vector<TestGift>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			switch (theType)
//			{
//				case Gift.INCOMING:
//					preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GIFTS_INCOMING, false, values);
//					break;
//
//				case Gift.OUTGOING:
//					preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GIFTS_OUTGOING, false, values);
//					break;
//			}
//
//			resultSet = preparedStatement.executeQuery();
//			while (resultSet.next())
//			{
//				TestGift theGift = new TestGift();
//
//				theGift.id_user_gift = resultSet.getLong("id_user_gift");
//				theGift.message = resultSet.getString("message");
//				theGift.date_send = resultSet.getInt("date_send");
//				theGift.date_read = resultSet.getInt("date_read");
//				theGift.id_gift = resultSet.getLong("id_gift");
//				theGift.pay_status = resultSet.getByte("pay_status");
//				theGift.status = resultSet.getByte("status");
//				theGift.id_user_from = resultSet.getLong("id_user_from");
//				theGift.id_user_to = resultSet.getLong("id_user_to");
//
//				gifts.add(theGift);
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return gifts;
//	}//===============================================
//
//	/*
//	 *
//	 */
//	public TestGift getLastGiftByUsers(long sendIdUser, long receiveIdUser) throws DAOException
//	{
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {sendIdUser, receiveIdUser};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GIFT_LAST, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				return getGiftById(resultSet.getLong("id_user_gift"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return null;
//	}//===============================================
//
//	public Vector<String> getUnreadGifts(long userId) throws DAOException
//	{
//		Vector<String> mess = new Vector<String>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UNREAD_GIFTS, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				mess.add(resultSet.getString("message"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return mess;
//	}//===============================================
//
//	/*
//	 * Получает один подарок по его идентификатору
//	 */
//	public TestGift getGiftById(long theGiftID) throws DAOException
//	{
//		TestGift theGift = null;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {theGiftID};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GIFT, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				theGift = new TestGift();
//				theGift.id_user_gift = resultSet.getLong("id_user_gift");
//				theGift.message = resultSet.getString("message");
//				theGift.date_send = resultSet.getInt("date_send");
//				theGift.date_read = resultSet.getInt("date_read");
//				theGift.id_gift = resultSet.getLong("id_gift");
//				theGift.pay_status = resultSet.getByte("pay_status");
//				theGift.status = resultSet.getByte("status");
//				theGift.id_user_from = resultSet.getLong("id_user_from");
//				theGift.id_user_to = resultSet.getLong("id_user_to");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return theGift;
//	}//===============================================
//
//	//***************************  GIFTS  ************************************
//	//*************************** THE END ************************************
//
//	//***************************  MESSAGES  ************************************
//	//***************************    BEGIN   ************************************
//
//	/*
//	 * Получает сообщение по идентификатору сообщения
//	 */
//	public TestMessage getMessageById(long messageId) throws DAOException
//	{
//		TestMessage theMessage = null;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {messageId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_MESSAGE, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				theMessage = new TestMessage();
//
//				theMessage.id_message = resultSet.getLong("id_message");
//				theMessage.message = resultSet.getString("message");
//				theMessage.date_send = resultSet.getInt("date_send");
//				theMessage.date_read = resultSet.getInt("date_read");
//				theMessage.status = resultSet.getInt("status");
//				theMessage.id_user = resultSet.getLong("id_user");
//				theMessage.id_user_from = resultSet.getLong("id_user_from");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return theMessage;
//	}//===============================================
//
//	/*
//	 * Возвращает количество входящих сообщений (не удаленныых)
//	 */
//	public int getIncomingMessageCount(long userId) throws DAOException
//	{
//		int count = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId, TestMessage.STATUS_ALL_DEL, TestMessage.STATUS_RECEIVER_DEL};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_MESSAGE_COUNT_INCOMING, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				count = resultSet.getInt(1);
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return count;
//	}//===============================================
//
//	/*
//	 * Возвращает количество исходящих сообщений (не удаленныых)
//	 */
//	public int getOutgoingMessageCount(long userId) throws DAOException
//	{
//		int count = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId, TestMessage.STATUS_ALL_DEL, TestMessage.STATUS_RECEIVER_DEL};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_MESSAGE_COUNT_OUTGOING, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				count = resultSet.getInt(1);
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return count;
//	}//===============================================
//
//	/*
//	 * Получает последнее отправленне сообщение по получателю и отправителю
//	 */
//	public TestMessage getLastMessageByClients(long sendIdClient, long receiveIdClient) throws DAOException
//	{
//		TestMessage theMessage = null;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {sendIdClient, receiveIdClient};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_MESSAGE_LAST, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				theMessage = new TestMessage();
//				theMessage = getMessageById(resultSet.getLong("id_message"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return theMessage;
//	}//===============================================
//
//	/*
//	 * Возвращает количество непрочитанных сообщений
//	 */
//	public short getUnreadMessagesCount(long userId) throws DAOException
//	{
//		short count = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_UNREAD_MESSAGE_COUNT, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				count = resultSet.getShort("msg_unread");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return count;
//	}//===============================================
//
//	//***************************  MESSAGES  ************************************
//	//***************************  THE END   ************************************
//
//	//***************************  CLIENT  ************************************
//	//***************************   BEGIN  ************************************
//
//	public TestClient getClientById(long userId) throws DAOException
//	{
//		TestClient client = null;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_USER, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				//client = buildCLient(resultSet);
//				client = new TestClient();
//				//==================================
//				//==================================vt_user
//				client.id_user = resultSet.getLong("id_user");
//				client.phone = resultSet.getLong("phone");
//				client.password = resultSet.getString("password");
//				client.mac = resultSet.getLong("mac");
//				client.nickname = resultSet.getString("nickname");
//				client.sex = resultSet.getByte("sex");
//				client.age = resultSet.getByte("age");
//				client.is_online = resultSet.getByte("is_online");
//				client.date_offline = resultSet.getInt("date_offline");
//				client.delete_status = resultSet.getByte("delete_status");
//
//				client.id_ip = resultSet.getInt("id_ip");
//				client.id_version = resultSet.getInt("id_version");
//				client.id_port = resultSet.getInt("id_port");
//				client.id_model = resultSet.getInt("id_model");
//				client.id_city = resultSet.getInt("id_city");
//				client.id_country = resultSet.getInt("id_country");
//				client.timezone = resultSet.getInt("timezone");
//				//==================================vt_user_meta
//				client.icq = resultSet.getString("icq");//
//				client.date_birth = resultSet.getLong("date_birth");//
////				client.count_photos = resultSet.getInt("count_photos");//
//				client.interests = resultSet.getString("interests");//
//				client.about = resultSet.getString("about");//
//				client.id_photo = resultSet.getLong("id_photo");//
//				client.date_top_up = resultSet.getLong("date_top_up");
//				//==================================
//				client.email = resultSet.getString("email");
//				client.activity = resultSet.getInt("activity");
////				client.is_email_confirm = resultSet.getByte("is_email_confirm");
//
//				client.date_reg = resultSet.getInt("date_reg");
//				client.from_id_user = resultSet.getLong("from_id_user");
//				//
//				client.sms_valid_begin_hour = resultSet.getByte("sms_valid_begin_hour");
//				client.sms_valid_begin_minute = resultSet.getByte("sms_valid_begin_minute");
//				client.sms_valid_end_hour = resultSet.getByte("sms_valid_end_hour");
//				client.sms_valid_minute_end = resultSet.getByte("sms_valid_end_minute");
//				//==================================
//				//==================================
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return client;
//	}//===============================================
//
//	private TestClient buildCLient(ResultSet resultSet)
//	{
//		return null;
//	}//===============================================
//
//	public int getCountryId(String countryName) throws DAOException
//	{
//		int countryId = -1;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {countryName};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_COUNTRY, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				countryId = resultSet.getInt("id_country");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return countryId;
//	}//===============================================
//
//	public int getCityId(String cityName) throws DAOException
//	{
//		int cityId = -1;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {cityName};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_CITY, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				cityId = resultSet.getInt("id_city");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return cityId;
//	}//===============================================
//
//	public List<City> getCitiesByName(String namePart) throws DAOException {
//		List<City> cities = new ArrayList<City>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//		Object[] values = {new String(namePart + "%")};
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO._SQL_CITY, false, values);
//			resultSet = preparedStatement.executeQuery();
//			while (resultSet.next())
//			{
//				City city = new City();
//				city.id = resultSet.getInt("id_city");
//				//---------------------
//				city.name = resultSet.getString("name");
//				city.name += ", " + resultSet.getString("region");
//				//---------------------
//				cities.add(city);
//			}
//		}
//		catch (SQLException e)
//		{
//			e.printStackTrace();
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return cities;
//	}
//
//	public Map<Long, String> getCityByName(String cityName) throws DAOException
//	{
//		//_SQL_CITY
//		Map<Long, String> city = new LinkedHashMap<Long, String>();
//		String cityInfo = null;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {cityName};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO._SQL_CITY, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				cityInfo = new String(resultSet.getString("c.name") + "," + " " + resultSet.getString("r.name"));
//				city.put(resultSet.getLong("c.id_city"), cityInfo);
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return city;
//	}//===============================================
//
//	public int getTimeZone(int cityId) throws DAOException
//	{
//		int timezone = -1;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {cityId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_TIMEZONE, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				timezone = resultSet.getInt("timezone");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return timezone;
//	}//===============================================
//
//	public int getRegion(int cityId) throws DAOException
//	{
//		int regionId = 0;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {cityId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_REGION, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				regionId = resultSet.getInt("id_region");
//				;
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return regionId;
//	}//===============================================
//
//	public int getCapitalOfRegion(int regionId) throws DAOException
//	{
//		int capitalId = 0;
//
//		//SQL_CAPITAL
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {regionId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_CAPITAL, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				capitalId = resultSet.getInt("id_city");
//				;
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return capitalId;
//	}//===============================================
//
//	public Vector<NumSet> getRegionNumSets(int regionId) throws DAOException
//	{
//		Vector<NumSet> numSet = null;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {regionId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_NUMSET, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				numSet = new Vector<NumSet>();
//				numSet.add(new NumSet(resultSet.getInt("def"), resultSet.getInt("from_num"), resultSet.getInt("to_num")));
//
//				while (resultSet.next())
//				{
//					numSet.add(new NumSet(resultSet.getInt("def"), resultSet.getInt("from_num"), resultSet.getInt("to_num")));
//				}
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return numSet;
//	}//===============================================
//
//	public int getCountryPrefix(int cityId) throws DAOException
//	{
//		int prefix = -1;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {cityId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_PREFIX, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				prefix = resultSet.getInt("prefix");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return prefix;
//	}//===============================================
//
//	public Vector<String> getCityFavPlacesByCityId(long cityId) throws DAOException
//	{
//		Vector<String> favPlaces = new Vector<String>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {cityId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_TOP_PLACES, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				favPlaces.add(resultSet.getString("name"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return favPlaces;
//	}//===============================================
//
//	public byte wantToMeet(long userId1, long userId2) throws DAOException
//	{
//		byte status = -1;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {userId1, userId2};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO._SQL_WANT_MEET, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				status = resultSet.getByte("status");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return status;
//	}//===============================================
//
//	/*
//	 * Получает последнюю встречу между двумя пользователями.
//	 */
//	public TestMeeting getLastMeeting(long userId1, long userId2) throws DAOException
//	{
//		//SQL_LAST_MEETING
//		TestMeeting testMeeting = null;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = userId1 <= userId2 ? new Object[] {userId1, userId2} : new Object[] {userId2, userId1};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_LAST_MEETING, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				testMeeting = new TestMeeting();
//				testMeeting.id_user_1 = resultSet.getLong("id_user_1");
//				testMeeting.id_user_2 = resultSet.getLong("id_user_2");
//				testMeeting.date_start_grinwich = resultSet.getInt("date_start");
//				testMeeting.date_finish_grinwich = resultSet.getInt("date_finish");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return testMeeting;
//	}//===============================================
//
//	public Vector<User> getUserTopByCityId(long cityId) throws DAOException
//	{
//		Vector<User> userTop = new Vector<User>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {cityId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_TOP_USER, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next())
//			{
//				User user = new User();
//
//				user.id = resultSet.getLong("u.id_user");
//				user.name = resultSet.getString("u.nickname");
//				user.age = resultSet.getShort("u.age");
//				user.sex = resultSet.getByte("u.sex");
//
//				userTop.add(user);
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return userTop;
//	}//===============================================
//
//
//	public Vector<Long> getAll(Long userId, short thePageNumber, short theElementsCount) throws DAOException {
//		Vector<Long> all = new Vector<Long>();
//		TestSettings filters = getSettings(userId);
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
////		"SELECT id_user FROM vt_user WHERE id_city = ? AND  age >= ? AND age <= ? AND sex = ? LIMIT ?,?";
//		Object[] values = {filters.cityId, filters.wantMeetAgeFrom, filters.wantMeetAgeTo, filters.sex, thePageNumber*theElementsCount, theElementsCount};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_ALL, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			while (resultSet.next()) {
//				all.add(resultSet.getLong("id_user"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return all;
//	}//===============================================
//
//	/**
//	 * Метод уже не используется.
//	 */
//	/*
//	public Long newUser(long cityId, long userId) throws DAOException
//	{
//		Long newId = null;
//		TestSettings settings = getSettings(userId);
//		//SQL_NEW_USER
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {cityId, settings.wantMeetAgeFrom, settings.wantMeetAgeTo, settings.sex, userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_NEW_USER, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				newId = resultSet.getLong("id_user");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return newId;
//	}//===============================================
//	 */
//
//	public void deleteClientByPhone(long phone) throws DAOException
//	{
//		//SQL_DELETE
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//
//		Object[] values = {phone};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_DELETE, false, values);
//			preparedStatement.execute();
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//	}//===============================================
//
//	public TestClient getClientByPhone(long phone) throws DAOException
//	{
//		//SQL_USER_PHONE
//		long userId = -1;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {phone};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_USER_PHONE, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				userId = resultSet.getLong("id_user");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		return getClientById(userId);
//	}//===============================================
//
//	public TestClient getClientByMac(long mac) throws DAOException
//	{
//		long userId = -1;
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {mac};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_USER_MAC, false, values);
//			resultSet = preparedStatement.executeQuery();
//
//			if (resultSet.next())
//			{
//				userId = resultSet.getLong("id_user");
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//
//		return getClientById(userId);
//	}//===============================================
//
//	public void setActivity(long userId, int activity) throws DAOException {
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {activity, userId};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_SET_ACTIVITY, false, values);
//			preparedStatement.execute();
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(resultSet);
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//	}
//
//	public void changePasswordByPhone(long phone, String password) throws DAOException
//	{
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//
//		Object[] values = {password, phone};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_CHANGE_PASSWORD_BY_PHONE, false, values);
//			preparedStatement.executeUpdate();
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//	}
//
//	public void changeMacByPhone(long phone, long mac) throws DAOException
//	{
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//
//		Object[] values = {mac, phone};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_CHANGE_MAC_BY_PHONE, false, values);
//			preparedStatement.executeUpdate();
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//	}
//
//	public long[] getMacInPhoneInterval(long from, long to) throws DAOException
//	{
//		long[] macs = null;
//		Vector<Long> tmp = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {from, to};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_MACS_BY_PHONE, false, values);
//			resultSet = preparedStatement.executeQuery();
//			while (resultSet.next())
//			{
//				tmp.add(resultSet.getLong("mac"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		macs = new long[tmp.size()];
//		for (int i = 0; i < macs.length; i++)
//		{
//			macs[i] = tmp.elementAt(i);
//		}
//		return macs;
//	}
//
//	public long[] getIdsInPhoneInterval(long from, long to) throws DAOException
//	{
//		long[] ids = null;
//		Vector<Long> tmp = new Vector<Long>();
//
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//		ResultSet resultSet = null;
//
//		Object[] values = {from, to};
//
//		try
//		{
//			connection = daoFactory.getConnection();
//			preparedStatement = DAOUtil.prepareStatement(connection, UserDAO.SQL_GET_IDS_BY_PHONE, false, values);
//			resultSet = preparedStatement.executeQuery();
//			while (resultSet.next())
//			{
//				tmp.add(resultSet.getLong("id_user"));
//			}
//		}
//		catch (SQLException e)
//		{
//			throw new DAOException(e);
//		}
//		finally
//		{
//			DAOUtil.close(preparedStatement);
//			DAOUtil.close(connection);
//		}
//		ids = new long[tmp.size()];
//		for (int i = 0; i < ids.length; i++)
//		{
//			ids[i] = tmp.elementAt(i);
//		}
//		return ids;
//	}
}