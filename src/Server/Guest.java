package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

import Client.Member;
import Client.Message;

/**
 * 클라이언트 하나를 담당하는 서버측의 "메세지 리스너" 클래스. 
 * 클라이언트의 ServerMsgListener 클래스와 메시지를 주고 받는다. 
 * 모든 메시지는 4자리의 prefix가 (접두사) 붙고
 * 이 prefix 에 따라 어떤 상황에서 오는 메시지인지 판별한다.
 * 
 * ServerHandler 로 새로운 클라이언트가 접속할때마다 
 * 그 클라이언트를 담당할 Guest 를 새로 하나 생성한다. 
 * 각 Guest 클래스는 각 클라이언트의 메시지를 받아서 ServerHandler 로 
 * 메시지의 처리를 넘긴다. (prefix 에 따라 적절한 ServerHandler 의 메소드를 호출)
 */
public class Guest extends Thread {

	private ServerHandler server;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Member member;
	private String roomName = "";

	public Guest(ServerHandler server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	@Override
	public void run() {
		String msg;
		String prefix;
		try {
			while ((msg = in.readLine()) != null) {
				prefix = msg.substring(0, 4);
				msg = msg.substring(4);
				if (prefix.equals(Message.LOGIN_REQUEST_CHECKID)) {
					if (server.checkId(msg)) {
						sendMsg(Message.LOGIN_CHECKID_SUCCESS);
					} else {
						sendMsg(Message.LOGIN_CHECKID_FAIL);
					}
				} else if (prefix.equals(Message.LOGIN_REQUEST_CHECKNICK)) {
					if (server.checkNick(msg)) {
						sendMsg(Message.LOGIN_CHECKNICK_SUCCESS);
					} else {
						sendMsg(Message.LOGIN_CHECKNICK_FAIL);
					}
				} else if (prefix.equals(Message.LOGIN_REQUEST_JOIN)) {
					if (server.requestJoin(msg)) {
						sendMsg(Message.LOGIN_JOIN_SUCCESS);
					} else {
						sendMsg(Message.LOGIN_JOIN_FAIL);
					}
				} else if (prefix.equals(Message.LOGIN_REQUEST)) {
					String tmp[] = msg.split(",");

					Member member = server.getMember(tmp[0]);
					if (member == null) {
						sendMsg(Message.LOGIN_FAIL_WRONG_ID);
					} else {
						if (!member.getPwd().equals(tmp[1])) {
							sendMsg(Message.LOGIN_FAIL_WRONG_PWD);
						} else {
							if (server.checkIdMap(tmp[0])) {
								sendMsg(Message.LOGIN_FAIL_LOGINED_ID);
							} else {
								sendMsg(Message.LOGIN_SUCCESS + member.getId() + "," + member.getPwd() + "," + member.getName() + "," + member.getNick() + "," + member.getPhone());
								setMember(member);
								server.userLogin(this);
								server.broadcastWaitRoomUpdate();
							}
						}
					}
				} else if (prefix.equals(Message.WAIT_USER_OUT)) {
					userLeave(false);
				} else if (prefix.equals(Message.WAIT_CHAT_MSG)) {
					server.broadcast_waitRoom(Message.WAIT_CHAT_MSG + msg);
					//서버는 wait_chat_msg 요청 prefix가 들어오게되면 서버는 대기방에 메세지를 뿌리게 된다.
				}
				/****************방만들기 요청 *************/
				else if (prefix.equals(Message.ROOM_REQUEST_MAKE)) {
					if (server.addRoom(msg)) {
						roomName = msg;
						sendMsg(Message.ROOM_MAKE_SUCCESS + roomName);
						server.addUserToRoom(this, roomName);
						server.removeUserFromWait(this);
						server.broadcastChatRoomUpdate(roomName);
						server.broadcastWaitRoomUpdate();
						sendMsg(Message.ROOM_SET_NAME + roomName);
					} else {
						sendMsg(Message.ROOM_MAKE_FAIL);
					}
					/*****************************************************/
				} else if (prefix.equals(Message.ROOM_USER_OUT)) {
					userLeave(false);
				} else if (prefix.equals(Message.ROOM_USER_IN)) {
					roomName = msg;
					server.addUserToRoom(this, msg);
					server.removeUserFromWait(this);
					server.broadcastChatRoomUpdate(msg);
					server.broadcastWaitRoomUpdate();
					server.broadcast_chatRoom(Message.ROOM_CHAT_MSG + "-" + member.getNick() + "님이 입장하셨습니다", roomName);
					sendMsg(Message.ROOM_SET_NAME + roomName);
				} else if (prefix.equals(Message.ROOM_CHAT_MSG)) {
					server.broadcast_chatRoom(Message.ROOM_CHAT_MSG + msg, roomName);
				} else if (prefix.equals(Message.ROOM_INVITE_USER)) {
					server.inviteUser(member.getId(), msg, roomName);
				} else if (prefix.equals(Message.ROOM_INVITE_DENY)) {
					String tmp[] = msg.split(",");
					server.inviteDeny(tmp[0], tmp[1], tmp[2]);
				} else if (prefix.equals(Message.ROOM_REQUEST_WAITUSER)) {
					server.returnWaitUsers(this);
				} else if (prefix.equals(Message.LOGOUT)) {
					userLeave(true);
				} else if (prefix.equals(Message.GET_ADDR)) {
					server.getAddr(this, msg, roomName);
				} else if (prefix.equals(Message.ROOM_CHAT_WHISPER)) {
					int index = msg.indexOf(",");
					String nick = msg.substring(0, index);
					msg = msg.substring(index + 1);
					server.sendWhisper(this, nick, msg, roomName);
				} else if (prefix.equals(Message.ROOM_KICKOFF)) {
					server.kickOff(msg, roomName);
				} else if (prefix.equals(Message.MOD_UPDATE_USERINFO)) {
					String[] tmp = msg.split(","); // msg = id,pwd,name,nick,phone
					String beforeNick = member.getNick();
					String afterNick = tmp[3];
					if (server.modUserInfo(tmp)) {
						sendMsg(Message.MOD_UPDATE_SUCCESS + msg);
						if (!beforeNick.equals(afterNick)) {
							member.setNick(afterNick);
							if (roomName.equals("")) {
								server.broadcastWait_userUpdate();
							} else {
								server.broadcastChatRoomUpdate(roomName);
							}
						}
					} else {
						sendMsg(Message.MOD_UPDATE_FAIL);
					}
				}
			}
		} catch (IOException e) {
			// 연결이 끊길 경우 IOException 이 발생함
			userLeave(false);
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e) {
			String errMsg = "디비 에러 : " + e.getMessage();
			System.out.println(errMsg);
			sendMsg(Message.ERR_DATABASE + errMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public void sendMsg(String msg) {
		out.println(msg);
		out.flush();
	}

	private void userLeave(boolean isLogout) {
		if (member == null) // 로그인하기 전엔 member == null 이다
			return;
		if (roomName.equals("")) { // roomName 이 "" 이면 대기실에서 나가는 것임
			server.removeUserFromWait(this);
			server.removeUserFromIdMap(member.getId());
			server.broadcastWaitRoomUpdate();
		} else {
			if (isLogout) {
				server.removeUserFromIdMap(member.getId());
			} else {
				server.addUserToWait(this);
			}
			server.removeUserFromRoom(this, roomName);
			server.broadcastWaitRoomUpdate();
			server.broadcastChatRoomUpdate(roomName);
			server.broadcast_chatRoom(Message.ROOM_CHAT_MSG + "-" + member.getNick() + "님이 나가셨습니다", roomName);
			roomName = "";
			
		}
	}

	public Member getMember() {
		return member;
	}

	public Socket getSocket() {
		return socket;
	}
}
