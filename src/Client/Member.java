package Client;

/**
 * 
 * 로그인한 사용자의 정보를 담아두기 위한 클래스
 * 
 */
public class Member {
	private String id;
	private String pwd;
	private String name;
	private String nick;
	private String phone;

	public Member(String id, String pwd, String name, String nick, String phone) 
	{
		this.id = id;
		this.pwd = pwd;
		this.name = name;
		this.nick = nick;
		this.phone = phone;
	}

	public Member(String[] info) 
	{
		id = info[0];
		pwd = info[1];
		name = info[2];
		nick = info[3];
		phone = info[4];
	}

	public String getId() 
	{
		return id;
		
	}

	public void setId(String id) 
	{
		this.id = id;
	}

	public String getPwd() 
	{
		return pwd;
	}

	public void setPwd(String pwd) 
	{
		this.pwd = pwd;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getNick() 
	{
		return nick;
	}

	public void setNick(String nick) 
	{
		this.nick = nick;
	}

	public String getPhone() 
	{
		return phone;
	}

	public void setPhone(String phone) 
	{
		this.phone = phone;
	}

	public void setAll(String[] tmp) 
	{
		id = tmp[0];
		pwd = tmp[1];
		name = tmp[2];
		nick = tmp[3];
		phone = tmp[4];
	}

}
