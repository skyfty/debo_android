package com.qcwl.debo.http;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;


public class ApiResponse {

	protected String data = "";
	protected String message = "";
	protected PageObj page = null;
	protected int code = -1;

	public ApiResponse(String data,String message) {
		this.data = data;
		this.message = message;
		this.code = Constants.OP_SUCCESS;
	}

	public ApiResponse(org.json.JSONArray resp) {
		this.data = resp.toString();
		this.message = resp.toString();
		this.code = Constants.OP_SUCCESS;
	}

	public ApiResponse(JSONObject resp) {
		if (resp == null)
			return;

		if (resp.has(Constants.DATA))
			this.data = resp.optString(Constants.DATA);
		if (resp.has(Constants.MESSAGE))
			this.message = resp.optString(Constants.MESSAGE);
		if (resp.has(Constants.CODE))
			this.code = resp.optInt(Constants.CODE);
		if (resp.has("page"))
			this.page = JSON.parseObject(resp.optString("page"), PageObj.class);

	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public PageObj getPage() {
		return page;
	}

	public void setPage(PageObj page) {
		this.page = page;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "ApiResponse [data=" + data + ", message=" + message + ", page="
				+ page + ", code=" + code + "]";
	}


}
