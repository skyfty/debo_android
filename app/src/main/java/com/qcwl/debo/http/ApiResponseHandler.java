package com.qcwl.debo.http;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class ApiResponseHandler extends JsonHttpResponseHandler {
	private Context context;

	public ApiResponseHandler(Context context) {
		this.context = context;
	}

    @Override
	public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
		super.onFailure(statusCode, headers, responseString, throwable);
		String err = responseString != null ? responseString : "statusCode:" + statusCode;
		if (throwable != null && throwable.getMessage() != null)
			err += "," + throwable.getMessage();
		onFailure(err);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
		super.onFailure(statusCode, headers, throwable, errorResponse);
		String err = "statusCode:" + statusCode;
		if (throwable != null && throwable.getMessage() != null)
			err += "," + throwable.getMessage();
		if (errorResponse != null)
			onFailure(err + "," + errorResponse.toString());
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
		super.onFailure(statusCode, headers, throwable, errorResponse);
		String err = "statusCode:" + statusCode;
		if (throwable != null && throwable.getMessage() != null)
			err += "," + throwable.getMessage();
		if (errorResponse != null)
			onFailure(err + "," + errorResponse.toString());
	}
	
	

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
		super.onSuccess(statusCode, headers, response);
		Logger.d("array", response.toString());
		ApiResponse apiResponse = new ApiResponse(response);
		onSuccess(apiResponse);

	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
		// super.onSuccess(statusCode, headers, response);
		Logger.d("object", response.toString());
		ApiResponse apiResponse = new ApiResponse(response);
		onSuccess(apiResponse);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String responseString) {
		// super.onSuccess(statusCode, headers, responseString);
		Logger.d("string", responseString.toString());
		ApiResponse apiResponse = new ApiResponse(responseString,responseString);
		onSuccess(apiResponse);
	}

	public void onFailure(String errMessage) {
		Log.e("ApiResponse", errMessage);

		if (context != null) {
			/*if (errMessage.length() < 20)
				Toast.makeText(context, errMessage, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(context, errMessage.substring(10), Toast.LENGTH_LONG).show();*/
		}
	}

	public abstract void onSuccess(ApiResponse apiResponse);

}
