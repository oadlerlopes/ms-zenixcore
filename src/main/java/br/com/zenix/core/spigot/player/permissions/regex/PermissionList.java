package br.com.zenix.core.spigot.player.permissions.regex;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import br.com.zenix.core.spigot.player.permissions.injector.FieldReplacer;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class PermissionList extends HashMap<String, Permission> {
	private static final long serialVersionUID = 1L;
	private static final Map<Class<?>, FieldReplacer<Permission, Map>> CHILDREN_MAPS;
	private static FieldReplacer<PluginManager, Map> INJECTOR;

	static {
		CHILDREN_MAPS = new HashMap<Class<?>, FieldReplacer<Permission, Map>>();
	}

	private final Multimap<String, Map.Entry<String, Boolean>> childParentMapping;

	public PermissionList() {
		this.childParentMapping = (Multimap<String, Map.Entry<String, Boolean>>) Multimaps
				.synchronizedMultimap((Multimap) HashMultimap.create());
	}

	public PermissionList(final Map<? extends String, ? extends Permission> existing) {
		super(existing);
		this.childParentMapping = (Multimap<String, Map.Entry<String, Boolean>>) Multimaps
				.synchronizedMultimap((Multimap) HashMultimap.create());
	}

	public static PermissionList inject(final PluginManager manager) {
		if (PermissionList.INJECTOR == null) {
			PermissionList.INJECTOR = new FieldReplacer<PluginManager, Map>(manager.getClass(), "permissions",
					Map.class);
		}
		final Map existing = PermissionList.INJECTOR.get(manager);
		final PermissionList list = new PermissionList(existing);
		PermissionList.INJECTOR.set(manager, list);
		return list;
	}

	private FieldReplacer<Permission, Map> getFieldReplacer(final Permission perm) {
		FieldReplacer<Permission, Map> ret = PermissionList.CHILDREN_MAPS.get(perm.getClass());
		if (ret == null) {
			ret = new FieldReplacer<Permission, Map>(perm.getClass(), "children", Map.class);
			PermissionList.CHILDREN_MAPS.put(perm.getClass(), ret);
		}
		return ret;
	}

	private void removeAllChildren(final String perm) {
		final Iterator<Map.Entry<String, Map.Entry<String, Boolean>>> it = this.childParentMapping.entries().iterator();
		while (it.hasNext()) {
			if (it.next().getValue().getKey().equals(perm)) {
				it.remove();
			}
		}
	}

	@Override
	public Permission put(final String k, final Permission v) {
		for (final Map.Entry<String, Boolean> ent : v.getChildren().entrySet()) {
			this.childParentMapping.put(ent.getKey(), new SimpleEntry<String, Boolean>(v.getName(), ent.getValue()));
		}
		final FieldReplacer<Permission, Map> repl = this.getFieldReplacer(v);
		repl.set(v, new NotifyingChildrenMap(v));
		return super.put(k, v);
	}

	@Override
	public Permission remove(final Object k) {
		final Permission ret = super.remove(k);
		if (ret != null) {
			this.removeAllChildren(k.toString());
			this.getFieldReplacer(ret).set(ret, new LinkedHashMap(ret.getChildren()));
		}
		return ret;
	}

	@Override
	public void clear() {
		this.childParentMapping.clear();
		super.clear();
	}

	public Collection<Map.Entry<String, Boolean>> getParents(final String permission) {
		return this.childParentMapping.get(permission.toLowerCase());
	}

	private class NotifyingChildrenMap extends LinkedHashMap<String, Boolean> {
		private static final long serialVersionUID = 1L;
		private final Permission perm;

		public NotifyingChildrenMap(final Permission perm) {
			this.perm = perm;
		}

		@Override
		public Boolean remove(final Object perm) {
			this.removeFromMapping(String.valueOf(perm));
			return super.remove(perm);
		}

		private void removeFromMapping(final String child) {
			final Iterator<Map.Entry<String, Boolean>> it = PermissionList.this.childParentMapping.get(child)
					.iterator();
			while (it.hasNext()) {
				if (it.next().getKey().equals(this.perm.getName())) {
					it.remove();
				}
			}
		}

		@Override
		public Boolean put(final String perm, final Boolean val) {
			PermissionList.this.childParentMapping.put(perm, new SimpleEntry(this.perm.getName(), val));
			return super.put(perm, val);
		}

		@Override
		public void clear() {
			PermissionList.this.removeAllChildren(this.perm.getName());
			super.clear();
		}
	}
}
