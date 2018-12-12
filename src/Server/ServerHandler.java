package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import Client.Member;
import Client.Message;
import db.DBManager;
import db.OracleManager;

/**
 * 
 * 서버측의 주요 로직처리를 담당.
 * Guest 클래스에서 메시지를 받은 후 prefix의 종류에 따라
 * ServerHandler의 적절한 함수를 호출한다.
 * 3개의 중요한 Collection 을 멤버변수로 가진다. 
 * 여기서 Collection 은 저장된 데이터를 하나씩 꺼낼 수 있는 iterator라는 
 * 인터페이스를 반환한다. 
 * - guests : 대기실의 유저들을 저장 
 * - roomMap : 채팅방의 유저들을 저장 
 * - idMap : 로그인한 모든 유저들의 id 와 ip 주소를 저장
 * - thorws = 예외 .ex) throws SQLException 
 * - 같은 클래스에 있고 다른 메소드에서 던지면, 그 예외처리를 한 곳에서
 * - 한번에 처리해준다.
 */
public class ServerHandler extends Thread {
	List<Guest> guests; // 대기실의 유저를 담아두는 리스트
	private HashMap<String, String> idMap;
	// 접속중인 유저들의 아이디와 아이피를 저장하는 리스트
	
	private HashMap<String, ArrayList<Guest>> roomMap;
	//각 방의 이름과 유저들을 담아두는 리스트
	
	private MemberDAO dao;
	//데이터 베이스로 sql문을 전송하고 결과를 받아오는 역할
	public static void main(String[] args) {
		try {
			new ServerHandler(new OracleManager()).start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public ServerHandler(DBManager dbmgr) {
		guests = new ArrayList<Guest>();
		idMap = new HashMap<String, String>();
		roomMap = new HashMap<String, ArrayList<Guest>>();
		try {
			dao = MemberDAO.getInstance(dbmgr);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		ServerSocket ss = null;
		Socket socket = null;
		try {
			ss = new ServerSocket(Message.CHAT_PORT);
			 InetAddress addr = InetAddress.getLocalHost();
			System.out.println("서버 아이피는 : " +addr.getHostAddress().toString()); 
			System.out.println("서버가 시작되었습니다.");
			while (true) {
				socket = ss.accept();
				Guest guest = new Guest(this, socket);
				guest.start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean checkId(String id) throws SQLException {
		return dao.checkId(id);
	}

	public boolean checkNick(String nick) throws SQLException {
		return dao.checkNick(nick);
	}

	public boolean requestJoin(String msg) throws SQLException {
		return dao.requestJoin(msg.split(","));
	}

	public Member getMember(String id) throws SQLException {
		Member member = dao.getMember(id);
		return member;
	}

	public void addUserToWait(Guest guest) {
		guests.add(guest);
	}

	/***********로그인시 idMap에 유저 정보 저장*********************/
	public void userLogin(Guest guest) {
		guests.add(guest);
		idMap.put(guest.getMember().getId(), guest.getSocket().getInetAddress().getHostAddress());
		System.out.println("현재 입장한 클라이언트는 : " + guest.getMember().getId()); 

	}

	public void broadcastWaitRoomUpdate() {
		broadcastWait_userUpdate();
		broadcastWait_roomUpdate();
	}

	public void broadcastWait_userUpdate() {
		String msg = "";
		Iterator<Guest> it = guests.iterator();
		while (it.hasNext()) {
			Guest g = it.next();
			msg += g.getMember().getNick() + ",";
		}
		broadcast_waitRoom(Message.WAIT_USER_UPDATE + msg);
		
	}

	public void broadcastWait_roomUpdate() {
		String msg2 = "";
		Set<String> set = roomMap.keySet();
		Iterator<String> it2 = set.iterator();
		while (it2.hasNext()) {
			String roomName = it2.next();
			ArrayList<Guest> list = roomMap.get(roomName);
			msg2 += roomName + " : " + list.size() + "명,";
		}
		broadcast_waitRoom(Message.WAIT_ROOM_UPDATE + msg2);
	}

	public void broadcast_waitRoom(String msg) {
		Iterator<Guest> it = guests.iterator();
		while (it.hasNext()) {
			Guest guest = it.next();
			guest.sendMsg(msg);
			
		}
	}

	public void broadcast_chatRoom(String msg, String roomName) {
		ArrayList<Guest> list = roomMap.get(roomName);
		if (list == null)
			return;
		Iterator<Guest> it = list.iterator();
		while (it.hasNext()) {
			Guest g = it.next();
			g.sendMsg(msg);
		}
	}

	public void removeUserFromWait(Guest guest) {
		guests.remove(guest);
	}

	public void removeUserFromIdMap(String id) {
		idMap.remove(id);
	}

	public void removeUserFromRoom(Guest guest, String roomName) {
		ArrayList<Guest> list = roomMap.get(roomName);
		if (list == null)
			return;
		list.remove(guest);
		if (list.size() == 0)
			roomMap.remove(roomName);
	}

	public void broadcastChatRoomUpdate(String roomName) {
		String msg = "";
		ArrayList<Guest> list = roomMap.get(roomName);
		if (list == null)
			return;
		Iterator<Guest> it = list.iterator();
		while (it.hasNext()) {
			Guest g = it.next();
			msg += g.getMember().getNick() + ",";
		}
		broadcast_chatRoom(Message.CHAT_USER_UPDATE + msg, roomName);
	}

	public boolean checkRoomName(String roomName) {
		return !roomMap.containsKey(roomName);
	}

	public boolean addRoom(String roomName) {
		if (roomMap.containsKey(roomName)) {
			return false;
		} else {
			roomMap.put(roomName, new ArrayList<Guest>());
			return true;
		}

	}
/****************유저 방 **********************/
	public void addUserToRoom(Guest g, String roomName) {
		ArrayList<Guest> list = roomMap.get(roomName);
		list.add(g);
	}

	public boolean checkIdMap(String id) {
		return idMap.containsKey(id);
	}
/*******************유저 방초대******************/
	public void inviteUser(String inviterId, String receiverNick, String roomName) {
		for (Guest g : guests) {
			if (g.getMember().getNick().equals(receiverNick)) {
				g.sendMsg(Message.ROOM_INVITE_REQUEST + inviterId + "," + roomName);
				break;
			}
		}
	}

	public void inviteDeny(String inviterId, String roomName, String denierNick) {
		ArrayList<Guest> list = roomMap.get(roomName);
		for (Guest g : list) {
			if (g.getMember().getId().equals(inviterId)) {
				g.sendMsg(Message.ROOM_INVITE_DENIED + denierNick);
				break;
			}
		}
	}

	public void returnWaitUsers(Guest guest) {
		String msg = "";
		for (Guest g : guests) {
			msg += g.getMember().getNick() + ",";
		}
		guest.sendMsg(Message.ROOM_RETURN_WAITUSER + msg);
	}

	public void getAddr(Guest guest, String nick, String roomName) {
		ArrayList<Guest> list = roomMap.get(roomName);
		for (Guest g : list) {
			if (g.getMember().getNick().equals(nick)) {
				g.sendMsg(Message.GET_ADDR + idMap.get(guest.getMember().getId()));
				break;
			}
		}
	}

	public void sendWhisper(Guest sender, String receiverNick, String msg, String roomName) {
		String senderNick = sender.getMember().getNick();
		ArrayList<Guest> users = roomMap.get(roomName);
		Guest receiver = null;

		Iterator<Guest> it = users.iterator();
		while (it.hasNext()) {
			Guest g = it.next();
			if (g.getMember().getNick().equals(receiverNick)) {
				receiver = g;
				break;
			}
		}
		if (receiver == null) {
			sender.sendMsg(Message.ROOM_WHISPER_FAIL + receiverNick);
		} else {
			msg = "[" + senderNick + "]\t- " + msg;
			sender.sendMsg(Message.ROOM_CHAT_MSG + msg);
			receiver.sendMsg(Message.ROOM_CHAT_MSG + msg);
		}
	}

	public void kickOff(String nick, String roomName) {
		ArrayList<Guest> list = roomMap.get(roomName);
		for (Guest g : list) {
			if (g.getMember().getNick().equals(nick)) {
				g.sendMsg(Message.ROOM_KICKOFF);
				break;
			}
		}
	}

	public boolean modUserInfo(String[] tmp) throws SQLException {
		return dao.updateMember(tmp);
	}

}
