package com.ctp.android.ppm.logic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.ctp.android.ppm.activities.DailyViewActivity;
import com.ctp.android.ppm.model.JsonHoursModel;
import com.ctp.android.ppm.model.ProjectModel;
import com.ctp.android.ppm.model.ProjectModel.OperationFlag;
import com.ctp.android.ppm.utils.ModelAssembler;

/**
 * Saving the hours logged for the selected day.
 * 
 * @author kbiels
 * 
 */
public class SaveLoggedHoursDayThread extends Thread {

	private List<ProjectModel> mProjectList;
	private Activity mActivity;
	private Context contex;
	private ProgressDialog mDialog;

	public SaveLoggedHoursDayThread(List<ProjectModel> projectList,
			Activity activity, ProgressDialog dialog, Context contex) {
		this.mProjectList = projectList;
		this.mActivity = activity;
		this.mDialog = dialog;
		this.contex = contex;
	}

	public void run() {

		if (mDialog != null) {
			mDialog.dismiss();
		}

		Log.i(this.getName(), "Saving the hours to the PPM");

		List<ProjectModel> createList = new ArrayList<ProjectModel>();
		List<ProjectModel> updateList = new ArrayList<ProjectModel>();
		List<ProjectModel> deleteList = new ArrayList<ProjectModel>();
		for (ProjectModel project : mProjectList) {
			if (project.getOperation() == OperationFlag.CREATE) {
				createList.add(project);
			} else if (project.getOperation() == OperationFlag.UPDATE) {
				updateList.add(project);
			} else if (project.getOperation() == OperationFlag.DELETE) {
				deleteList.add(project);
			}
		}

		RestClient restClient;
		if (mActivity != null) {
			restClient = new RestClient(mActivity.getApplicationContext());
		} else {
			restClient = new RestClient(contex);
		}
		String resultDelete = "";
		String resultCreate = "";
		String resultUpdate = "";
		if (deleteList.size() > 0) {
			List<JsonHoursModel> hoursList = ModelAssembler
					.fromProjectList(deleteList);
			for (JsonHoursModel hour : hoursList) {
				try {
					resultCreate = restClient.createHour(hour,
							HttpMethod.DELETE);
					Log.i(this.getName(), "resultDelete = " + resultDelete);
				} catch (RestClientException ex) {
					showErrorMessage();
					Log.e(this.getName(), "FAILED DELETE: " + ex.toString());
					return;
				}
			}
		}
		if (createList.size() > 0) {
			List<JsonHoursModel> hoursList = ModelAssembler
					.fromProjectList(createList);
			for (JsonHoursModel hour : hoursList) {
				try {
					resultCreate = restClient.createHour(hour, HttpMethod.PUT);
					Log.i(this.getName(), "resultCreate = " + resultCreate);
				} catch (RestClientException ex) {
					showErrorMessage();
					Log.e(this.getName(), "FAILED PUT: " + ex.toString());
					return;
				}
			}
		}
		if (updateList.size() > 0) {
			List<JsonHoursModel> hoursList = ModelAssembler
					.fromProjectList(updateList);
			for (JsonHoursModel hour : hoursList) {
				try {
					resultUpdate = restClient.createHour(hour, HttpMethod.POST);
					Log.i(this.getName(), "resultUpdate = " + resultUpdate);
				} catch(RestClientException ex) {
					showErrorMessage();
					Log.e(this.getName(), "FAILED POST: " + ex.toString());
					return;
				}
			}
		}

		if (mActivity != null && mActivity instanceof DailyViewActivity) {
			((DailyViewActivity) mActivity).callbackFinishDailyActivity();
		}
	}
	
	/**
	 * Calling a method from the activity to show a Toast
	 * with the error message.
	 */
	private void showErrorMessage() {
		if (mActivity != null && mActivity instanceof DailyViewActivity) {
			((DailyViewActivity) mActivity).displayErrorMsg("Error in the HTTP connection. Please try again.");
		}
	}

}
