package com.qcwl.debo.http;

public class PageObj {
	public int page;

	public int count;

	public int pageCount;

	public int total;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "PageObj [page=" + page + ", count=" + count + ", pageCount="
				+ pageCount + ", total=" + total + "]";
	}


}
