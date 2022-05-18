package br.com.zenix.core.plugin.utilitaries;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class Scroller {

	private int position, width;
	private List<String> list;
	private String message;

	public Scroller(String message, int width, int spaceBetween) {
		this.message = message;
		this.list = new ArrayList<>();
		this.width = width;

		if (message.length() < width) {
			StringBuilder sb = new StringBuilder(message);
			while (sb.length() < width) {
				sb.append(" ");
			}
			message = sb.toString();
		}
		width -= 2;
		if (width < 1) {
			width = 1;
		}
		if (spaceBetween < 0) {
			spaceBetween = 0;
		}
		for (int i = 0; i < message.length() - width; i++) {
			this.list.add(message.substring(i, i + width));
		}
		StringBuilder space = new StringBuilder();
		for (int i = 0; i < spaceBetween; i++) {
			this.list.add(
					message.substring(message.length() - width + (i > width ? width : i), message.length()) + space);
			if (space.length() < width) {
				space.append(" ");
			}
		}
		for (int i = 0; i < width - spaceBetween; i++) {
			this.list.add(message.substring(message.length() - width + spaceBetween + i, message.length()) + space
					+ message.substring(0, i));
		}
		for (int i = 0; i < spaceBetween; i++) {
			if (i > space.length()) {
				break;
			}
			this.list.add(space.substring(0, space.length() - i)
					+ message.substring(0, width - (spaceBetween > width ? width : spaceBetween) + i));
		}
	}

	public String next() {
		StringBuilder sb = getNext();
		if (sb.charAt(sb.length() - 1) == '§') {
			sb.setCharAt(sb.length() - 1, ' ');
		}
		if (sb.charAt(0) == '§') {
			ChatColor c = ChatColor.GOLD;
			if (c != null) {
				sb = getNext();
				if (sb.charAt(0) != ' ') {
					sb.setCharAt(0, ' ');
				}
			}
		}
		return sb.toString();
	}

	private StringBuilder getNext() {
		if (position >= message.length() + width) {
			position = 0;
		}
		return new StringBuilder(((String) this.list.get(this.position++ % this.list.size())).substring(0));
	}

	public int getPosition() {
		return position;
	}
}