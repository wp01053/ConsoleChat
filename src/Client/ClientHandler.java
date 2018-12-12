package Client;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * 
 * GUI 환경과 ServerMsgListener 의 중간다리 역할
 * 
 * 채팅방, 대기실, 로그인창, 가입창 등의 GUI환경에서 발생하는 이벤트를 처리후 ServerMsgListener 의 sendMsg 를 통해
 * 서버로 메시지를 전달한다.
 * 
 * ServerMsgListener 는 메시지를 받은 후 메시지의 종류에 따라 GUI 를 변화시키는데 (채팅내용 업데이트, 유저목록 업데이트,
 * 방 목록 업데이트 등) 이 때 이 클래스의 getChatRoom(), getWaitRoom() 같은 메소드를 이용해서 GUI 에 접근한다.
 * 
 * 원활한 중간다리 역할을 위해서 모든 GUI(다이얼로그 및 Panel)들을 멤버변수로 가진다.
 */
public class ClientHandler 
{
	private static final ClientHandler instance = new ClientHandler();
	private ServerMsgListener sml;
	private JoinDialog joinDialog;
	private MainFrame frame;
	private LoginPanel loginPanel;
	private WaitRoom waitRoom;
	private Member member;
	private ChatRoom chatRoom;
	private InviteDialog inviteDialog;
	private PwdDialog pwdDialog;

	public static void main(String[] args) 
	{
		ClientHandler.getInstance().startClient();
	}

	private ClientHandler() 
	{
		frame = new MainFrame();
		frame.setHandler(this);
	}

	public static ClientHandler getInstance() 
	{
		return instance;
	}

	public void startClient() 
	{
		openLogin();
	}

	private void openLogin() 
	{ // 로그인연결시 소켓통신
		Socket socket = null;
		try 
		{
			socket = new Socket(Message.SERVER_URL, Message.CHAT_PORT);
			sml = new ServerMsgListener(this, socket);
			sml.start();
			loginPanel = new LoginPanel();
			frame.setPanel(loginPanel);
			frame.setVisible(true);
		}
		catch (Exception e) 
		{
			JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다");
		}
	}

	public void openJoin() 
	{
		joinDialog = new JoinDialog(frame, "회원가입", true, Message.MODE_JOIN);
		joinDialog.setVisible(true);
	}

	public void requestJoin(String msg) 
	{
		sml.sendMsg(Message.LOGIN_REQUEST_JOIN + msg);
	}

	public MainFrame getFrame() 
	{
		return frame;
	}

	public LoginPanel getLoginPanel()
	{
		return loginPanel;
	}

	public void requestCheckId(String id)
	{
		System.out.println(this.hashCode());
		sml.sendMsg(Message.LOGIN_REQUEST_CHECKID + id);
	}

	public void requestCheckNick(String nick, int checkMode)
	{
		switch (checkMode) 
		{
		case Message.LOGIN_CHECK:
			sml.sendMsg(Message.LOGIN_REQUEST_CHECKNICK + nick);
			break;
		case Message.MOD_CHECK:
			sml.sendMsg(Message.MOD_REQUEST_CHECKNICK + nick);
			break;
		}
	}

	public JoinDialog getJoinDialog() 
	{
		return joinDialog;
	}

	public void requestLogin(String msg) 
	{
		sml.sendMsg(Message.LOGIN_REQUEST + msg);
	}

	public void openWaitRoom() 
	{ // 클라이언트가 대기방 처음 접속시
		waitRoom = new WaitRoom();
		frame.setPanel(waitRoom);
		frame.setMenuItemEnabled(true);
		if (member != null) 
		{
			waitRoom.setChatBorder("전체 채팅방 id : " + member.getNick());
		}
		waitRoom.focusText();
	}

	public void setMember(Member member) 
	{
		// 멤버 리스트 추가
		this.member = member;
	}

	public WaitRoom getWaitRoom() 
	{
		return waitRoom;
	}

	public void logout() 
	{
		System.out.println("로그아웃 완료 !!");
		sml.sendMsg(Message.LOGOUT);
		loginPanel.setPwdField("");
		frame.setPanel(loginPanel);
		frame.setMenuItemEnabled(false);
	}

	public void sendWaitChat(String txt) 
	{
		System.out.println("대기방에서 채팅중  ID : " + txt);
		// 대기방에서의 채팅
		sml.sendMsg(Message.WAIT_CHAT_MSG + txt);
	}

	public Member getMember() 
	{
		// 기존 멤버 불러오기
		return member;
	}

	/******************* 방 ***************************/
	public void makeRoom(String roomName) 
	{
		System.out.println("방만들기");
		sml.sendMsg(Message.ROOM_REQUEST_MAKE + roomName);
	}

	public void openChatRoom(String msg) 
	{
		System.out.println("채팅방 열림");

		chatRoom = new ChatRoom();
		frame.setPanel(chatRoom);
		chatRoom.focusText();
	}

	public void exitChatRoom() 
	{
		System.out.println("방에서 나가기");
		openWaitRoom();
		sml.sendMsg(Message.ROOM_USER_OUT);
	}

	public void enterRoom(String roomName) 
	{
		System.out.println("방입장 하기 버튼 클릭");
		openChatRoom(roomName);
		sml.sendMsg(Message.ROOM_USER_IN + roomName);
	}

	/******************* 방 ***************************/

	public ChatRoom getChatRoom() 
	{
		System.out.println("채팅전송 완료 !!");
		return chatRoom;
	}

	public void sendRoomChat(String text) 
	{
		System.out.println("방에서의 채팅");
		sml.sendMsg(Message.ROOM_CHAT_MSG + text);

	}

	public void invite(String nick) 
	{

		sml.sendMsg(Message.ROOM_INVITE_USER + nick);
	}

	public void inviteDeny(String msg) 
	{
		sml.sendMsg(Message.ROOM_INVITE_DENY + msg);
	}

	public void openInvite() 
	{
		System.out.println("초대 버튼 클릭");

		inviteDialog = new InviteDialog(frame, "초대", true);
		sml.sendMsg(Message.ROOM_REQUEST_WAITUSER);
		inviteDialog.setVisible(true);
	}

	public InviteDialog getInviteDialog() 
	{

		System.out.println("초대 창 띄우기");

		return inviteDialog;
	}

	public void exitWaitRoom() 
	{ // 대기방에서 로그아웃시 처리문
		sml.sendMsg(Message.WAIT_USER_OUT);

		frame.dispose();
		System.exit(0);
	}

	public void sendWhisper(String nick, String msg) 
	{
		// 상대방에게 귓속말시 처리문
		sml.sendMsg(Message.ROOM_CHAT_WHISPER + nick + "," + msg);

	}

	public void kickOff(String nick) 
	{
		// 상대방 강퇴시 처리문
		System.out.println("강퇴 완료 !!");
		sml.sendMsg(Message.ROOM_KICKOFF + nick);
	}

	public void requestModify(String msg) 
	{
		// 암호 입력후 정보수정 팝업창 열림
		System.out.println("정보 수정 완료 !! ");

		// 정보수정시 비밀번호 확인창
		sml.sendMsg(Message.MOD_UPDATE_USERINFO + msg);
	}

	public void openModify() 
	{
		// 암호 입력후 정보수정 팝업창 열림

		System.out.println("정보 수정 창열림 ");

		joinDialog = new JoinDialog(frame, "정보 수정", true, Message.MODE_MODIFY);
		joinDialog.setVisible(true);
	}

	public void openPwdCheck() 
	{
		// 암호를 입력하세요 라는 팝업창 출력 부분
		System.out.println("정보 수정 비번입력 해주세요 ");

		pwdDialog = new PwdDialog(frame);
		pwdDialog.setVisible(true);
	}

}
