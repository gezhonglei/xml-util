package com.gezhonglei.demo.entity;

import java.util.ArrayList;
import java.util.List;

import com.gezhonglei.commons.xml.annotation.XmlTag;

@XmlTag(name = "configuration")
public class RestConfig {
	@XmlTag("server")
	private String serverName;
	@XmlTag("port")
	private int serverPort;
	@XmlTag("username")
	private String userName;
	@XmlTag("password")
	private String passWord;
	@XmlTag
	private boolean useSSL;
	@XmlTag(name = "accesslist", subClass=AccessResource.class)
	private List<AccessResource> accessResources = new ArrayList<AccessResource>();
	
	@XmlTag
	private MapCollection maps;

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public List<AccessResource> getAccessResources() {
		return accessResources;
	}

	public void setAccessResources(List<AccessResource> accessResources) {
		this.accessResources = accessResources;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	public MapCollection getMaps() {
		return maps;
	}

	public void setMaps(MapCollection maps) {
		this.maps = maps;
	}
}
