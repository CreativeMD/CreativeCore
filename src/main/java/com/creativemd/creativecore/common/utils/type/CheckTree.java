package com.creativemd.creativecore.common.utils.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CheckTree<T> {
	
	public final BiConsumer<T, Boolean> setter;
	public final Function<T, Boolean> getter;
	public final Function<T, Collection<? extends T>> getChildren;
	
	public final CheckTreeEntry root;
	
	public CheckTree(T root, BiConsumer<T, Boolean> setter, Function<T, Boolean> getter, Function<T, Collection<? extends T>> getChildren) {
		this.setter = setter;
		this.getter = getter;
		this.getChildren = getChildren;
		this.root = new CheckTreeEntry(null, root);
	}
	
	public CheckTree(List<T> rootFields, BiConsumer<T, Boolean> setter, Function<T, Boolean> getter, Function<T, Collection<? extends T>> getChildren) {
		this.setter = setter;
		this.getter = getter;
		this.getChildren = getChildren;
		this.root = new CheckTreeEntry(rootFields);
	}
	
	public void reload() {
		reload(root);
	}
	
	private void reload(CheckTreeEntry entry) {
		if (entry.content != null)
			entry.enabled = getter.apply(entry.content);
		
		if (entry.children != null)
			for (int i = 0; i < entry.children.size(); i++)
				reload(entry.children.get(i));
	}
	
	public void apply() {
		apply(root);
	}
	
	private void apply(CheckTreeEntry entry) {
		if (entry.content != null)
			setter.accept(entry.content, entry.enabled);
		
		if (entry.children != null)
			for (int i = 0; i < entry.children.size(); i++)
				apply(entry.children.get(i));
	}
	
	public class CheckTreeEntry {
		
		public final CheckTreeEntry parent;
		public final T content;
		protected boolean enabled;
		public List<CheckTreeEntry> children;
		
		public CheckTreeEntry(List<T> children) {
			this.parent = null;
			this.children = new ArrayList<>();
			for (T t : children)
				this.children.add(new CheckTreeEntry(this, t));
			this.content = null;
		}
		
		public CheckTreeEntry(CheckTreeEntry parent, T content) {
			this.parent = parent;
			this.content = content;
			Collection<? extends T> entries = getChildren.apply(content);
			if (entries == null)
				children = null;
			else {
				children = new ArrayList<>(entries.size());
				for (T t : entries)
					children.add(new CheckTreeEntry(this, t));
			}
			this.enabled = getter.apply(content);
		}
		
		public boolean isEnabled() {
			if (!enabled)
				return false;
			
			if (children == null || children.isEmpty())
				return true;
			
			for (CheckTreeEntry entry : children)
				if (!entry.isEnabled())
					return false;
			return true;
		}
		
		public boolean isChildEnabled() {
			if (children != null)
				for (CheckTreeEntry entry : children)
					if (entry.isEnabled() || entry.isChildEnabled())
						return true;
			return false;
		}
		
		public void enable() {
			enableInteral();
			if (parent != null)
				parent.enabled = parent.isChildEnabled();
		}
		
		public void disable() {
			disableInteral();
			if (parent != null)
				parent.enabled = false;
		}
		
		protected void enableInteral() {
			if (children != null)
				for (CheckTreeEntry entry : children)
					entry.enableInteral();
			enabled = true;
		}
		
		protected void disableInteral() {
			if (children != null)
				for (CheckTreeEntry entry : children)
					entry.disableInteral();
			enabled = false;
		}
		
		public void toggle() {
			if (isEnabled())
				disable();
			else
				enable();
		}
		
	}
}
