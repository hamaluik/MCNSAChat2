package com.mcnsa.mcnsachat2.util;

public class ColourHandler {
	String col = new String("&F");

	public ColourHandler(String colourName) {
		col = translateName(colourName);
	}

	public String colourString(String colour) {
		// TODO: add proper hex escape to create a colour
		return "";
	}

	public String translateName(String name) {
		// TODO: translate colour names into symbols
		return "";
	}

	public String stripColours(String str) {
		// TODO: strip colours from it!
		return str;
	}
}
