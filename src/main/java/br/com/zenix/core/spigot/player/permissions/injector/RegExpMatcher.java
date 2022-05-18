package br.com.zenix.core.spigot.player.permissions.injector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import br.com.zenix.core.spigot.player.permissions.injector.loaders.LoaderNetUtil;
import br.com.zenix.core.spigot.player.permissions.injector.loaders.LoaderNormal;

public class RegExpMatcher implements PermissionMatcher {

	protected static Pattern rangeExpression;

	static {
		RegExpMatcher.rangeExpression = Pattern.compile("(\\d+)-(\\d+)");
	}

	private Object patternCache;

	public RegExpMatcher() {
		final Class<?> cacheBuilder = this.getClassGuava("com.google.common.cache.CacheBuilder");
		final Class<?> cacheLoader = this.getClassGuava("com.google.common.cache.CacheLoader");
		try {
			final Object obj = cacheBuilder.getMethod("newBuilder", new Class[0]).invoke(null);
			final Method maximumSize = obj.getClass().getMethod("maximumSize", Long.TYPE);
			final Object obj2 = maximumSize.invoke(obj, 500);
			Object loader;
			if (this.hasNetUtil()) {
				loader = new LoaderNetUtil();
			} else {
				loader = new LoaderNormal();
			}
			final Method build = obj2.getClass().getMethod("build", cacheLoader);
			this.patternCache = build.invoke(obj2, loader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isMatches(String expression, String permission) {
		try {
			final Method get = this.patternCache.getClass().getMethod("get", Object.class);
			get.setAccessible(true);
			final Object obj = get.invoke(this.patternCache, expression);
			return ((Pattern) obj).matcher(permission).matches();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e2) {
			e2.printStackTrace();
		} catch (InvocationTargetException e3) {
			e3.printStackTrace();
		} catch (NoSuchMethodException e4) {
			e4.printStackTrace();
		} catch (SecurityException e5) {
			e5.printStackTrace();
		}
		return false;
	}

	private Class<?> getClassGuava(String str) {
		Class<?> clasee = null;
		try {
			if (this.hasNetUtil()) {
				str = "net.minecraft.util." + str;
			}
			clasee = Class.forName(str);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return clasee;
	}

	private boolean hasNetUtil() {
		try {
			Class.forName("net.minecraft.util.com.google.common.cache.LoadingCache");
			return true;
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}
}
