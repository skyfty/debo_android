package com.qcwl.debo.ui.circle;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Mark<br>
 * @param <T>2013下午4:11:51
 */
public abstract class BaseAdapterExt<T> extends android.widget.BaseAdapter {

	protected Activity context;

	protected List<T> items;

	private long itemId;
	/**
	 * items中的元素的个数的限制，0代表没有限制
	 */
	protected int MAX_SIZE = 0;

	/********************* 以下是自定义 ************************/
	// protected BaseAdapter(int size) {
	// this.MAX_SIZE = size;
	// }

	public BaseAdapterExt(List<T> items, Activity context) {
		this.items = items;
		this.context = context;
		if (this.items == null) {
			this.items = new ArrayList<T>();
		}
	}

	public BaseAdapterExt(List<T> items, Activity context, ViewGroup listView) {
		this.items = items;
		this.context = context;
		this.listView = listView;
		if (this.items == null) {
			this.items = new ArrayList<T>();
		}
	}

	/********************* 以下是自定义 ************************/

	public void setMaxSize(int maxSize) {
		this.MAX_SIZE = maxSize;
	}

	/**
	 * 设置完数据以后是否直接刷新
	 * 
	 * @param items
	 *            参数为 null, 则清空adater的数据
	 * @param isNotify
	 */
	public void setItems(List<T> items, boolean isNotify) {
		if (items == null) {
			items = new ArrayList<T>();
		}
		this.items = items;
		if (this.MAX_SIZE != 0) {
			// 只取前MAX_SIZE个元素
			if (this.items.size() > MAX_SIZE) {
				this.items = this.items.subList(0, MAX_SIZE);
			}
		}
		if (isNotify) {
			notifyDataSetChanged();
		}
	}

	/**
	 * 新增一个元素<br>
	 * 
	 * @param obj
	 * @param isNotify
	 *            ： 是否需要刷新
	 */
	public void appendItem(T obj, boolean isNotify) {
		this.items.add(obj);
		if (this.MAX_SIZE != 0) {
			// 只取最后MAX_SIZE个元素
			if (this.items.size() > MAX_SIZE) {
				this.items = this.items.subList(this.items.size() - MAX_SIZE,
						this.items.size());
			}
		}
		if (isNotify) {
			notifyDataSetChanged();
		}
	}

	public void appendItems(List<T> items, boolean isNotify) {
		this.items.addAll(items);
		if (isNotify) {
			notifyDataSetChanged();
		}
	}

	public List<T> getItems() {
		return this.items;
	}

	private ViewGroup listView;

	protected ViewGroup getListView() {
		return listView;
	}

	/********************* 以下是父类实现 ************************/
	@Override
	public int getCount() {
		if (items != null) {
			return items.size();
		}
		return 0;
	}

	@Override
	public T getItem(int position) {
		if (items != null) {
			return items.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	/**
	 * 如果抛异常会导致程序崩溃, 子类需要捕获并处理<br>
	 * 
	 * @param itemId
	 * @throws ClassCastException
	 */
	public void setItemId(String itemId) throws ClassCastException {
		this.itemId = Long.valueOf(itemId);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
