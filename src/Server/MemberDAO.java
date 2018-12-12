package Server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Client.Member;
import db.DBManager;

/**
 *  Query 담당 클래스
 */
public class MemberDAO {

	private static MemberDAO instance;
	private PreparedStatement checkIDStmt;
	private PreparedStatement checkNickStmt;
	private PreparedStatement getStmt;
	private PreparedStatement joinStmt;
	private PreparedStatement updateStmt;

	private MemberDAO(DBManager dbmgr) throws SQLException {
		String checkId = "select id from roomchat where id = ?";
		String checkNick = "select id from roomchat where nick = ?";
		String getMember = "select * from roomchat where id = ?";
		String join = "insert into roomchat values(?, ?, ?, ?, ?)";
		String update = "update roomchat set id = ?, pwd = ?, name = ?, nick = ?, phone = ? where id = ?";

		checkIDStmt = dbmgr.create(checkId);
		checkNickStmt = dbmgr.create(checkNick);
		getStmt = dbmgr.create(getMember);
		joinStmt = dbmgr.create(join);
		updateStmt = dbmgr.create(update);
	}

	public static synchronized void methodA() {
		
	}
	
	 public synchronized static MemberDAO getInstance(DBManager dbmgr) throws SQLException {
		if (instance == null)
			instance = new MemberDAO(dbmgr);
		return instance;
	}

	public Member getMember(String id) throws SQLException {
		Member member = null;
		getStmt.setString(1, id);
		ResultSet rs = getStmt.executeQuery();
		if (rs.next())
			member = new Member(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
		return member;
	}

	public boolean checkId(String id) throws SQLException {
		checkIDStmt.setString(1, id);
		return !checkIDStmt.executeQuery().next();


	}

	public boolean checkNick(String nick) throws SQLException {
		checkNickStmt.setString(1, nick);
		return !checkNickStmt.executeQuery().next();
	}

	public boolean requestJoin(String[] msg) throws SQLException {
		joinStmt.setString(1, msg[0]);
		joinStmt.setString(2, msg[1]);
		joinStmt.setString(3, msg[2]);
		joinStmt.setString(4, msg[3]);
		joinStmt.setString(5, msg[4]);
		return joinStmt.executeUpdate() != 0;
	}

	public boolean updateMember(String[] info) throws SQLException {
		updateStmt.setString(1, info[0]);
		updateStmt.setString(2, info[1]);
		updateStmt.setString(3, info[2]);
		updateStmt.setString(4, info[3]);
		updateStmt.setString(5, info[4]);
		updateStmt.setString(6, info[0]);
		return updateStmt.executeUpdate() != 0;
	}
}
