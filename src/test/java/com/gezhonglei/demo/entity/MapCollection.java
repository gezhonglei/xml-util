package com.gezhonglei.demo.entity;

import java.util.Map;

import com.gezhonglei.commons.xml.annotation.XmlMap;

public class MapCollection {

	@XmlMap(isItemVisible=true, itemTag="item", isKeyTag=true, isValueTag=true, valueName="value")
	private Map<String, String> map1;
	
	@XmlMap(valueType=Integer.class, isItemVisible=true, itemTag="item", isKeyTag=true, isValueTag=true, valueName="")
	private Map<String, Integer> map2;
	
	@XmlMap(valueType=Integer.class, isItemVisible=true, itemTag="item", isKeyTag=true, isValueTag=false, valueName="value")
	private Map<String, Integer> map3;
	
	@XmlMap(isItemVisible=true, itemTag="item", isKeyTag=false, isValueTag=false, valueName="value")
	private Map<String, String> map4;
	
	@XmlMap(isItemVisible=true, itemTag="item", isKeyTag=false, isValueTag=true, valueName="")
	private Map<String, String> map5;
	
	@XmlMap(isItemVisible=false, isKeyTag=true, isValueTag=true, valueName="")
	private Map<String, String> map6;
	
	@XmlMap(isItemVisible=false, isKeyTag=true, isValueTag=false, valueName="value")
	private Map<String, String> map7;

	public Map<String, String> getMap1() {
		return map1;
	}

	public Map<String, Integer> getMap2() {
		return map2;
	}

	public Map<String, Integer> getMap3() {
		return map3;
	}

	public Map<String, String> getMap4() {
		return map4;
	}

	public Map<String, String> getMap5() {
		return map5;
	}

	public Map<String, String> getMap6() {
		return map6;
	}

	public Map<String, String> getMap7() {
		return map7;
	}

	public void setMap1(Map<String, String> map1) {
		this.map1 = map1;
	}

	public void setMap2(Map<String, Integer> map2) {
		this.map2 = map2;
	}

	public void setMap3(Map<String, Integer> map3) {
		this.map3 = map3;
	}

	public void setMap4(Map<String, String> map4) {
		this.map4 = map4;
	}

	public void setMap5(Map<String, String> map5) {
		this.map5 = map5;
	}

	public void setMap6(Map<String, String> map6) {
		this.map6 = map6;
	}

	public void setMap7(Map<String, String> map7) {
		this.map7 = map7;
	}
	
}
