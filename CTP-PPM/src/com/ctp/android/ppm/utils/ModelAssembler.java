package com.ctp.android.ppm.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.model.DayProgressModel;
import com.ctp.android.ppm.model.JsonHoursModel;
import com.ctp.android.ppm.model.JsonProject;
import com.ctp.android.ppm.model.ProjectModel;
import com.ctp.android.ppm.model.WeekProgressModel;
import com.ctp.android.ppm.model.ProjectModel.OperationFlag;

/**
 * Assembler class marshaling the internal weekly model to/from the model used
 * by the PPM Rest web services.
 * 
 * @author kbiels
 * 
 */
public class ModelAssembler {

	/**
	 * Marshaling the internal weekly model to the model used by the PPM Rest
	 * web services.
	 * 
	 * @param weeklyModel
	 *            the internal weekly model
	 * @return the list of objects used by the PPM web services
	 */
	public static List<JsonHoursModel> fromWeekProgressModel(
			WeekProgressModel weeklyModel) {
		ArrayList<JsonHoursModel> ppmModel = new ArrayList<JsonHoursModel>();
		JsonHoursModel hour;
		for (DayProgressModel day : weeklyModel.getDayList()) {
			for (ProjectModel project : day.getProjectList()) {
				hour = new JsonHoursModel();
				hour.setDate(project.getJsonDate());
				hour.setHours(project.getHoursLogged());
				hour.setProjectId(project.getId());
				// FIXME:
				hour.setIdhours(project.getIdhours());
				ppmModel.add(hour);
			}
		}
		return ppmModel;
	}

	/**
	 * Marshaling the object from the internal model to the model used in the
	 * REST webservice.
	 * 
	 * @param projectList
	 *            List of projects to be updated.
	 * @return List of REST objects to be updated.
	 */
	public static List<JsonHoursModel> fromProjectList(
			List<ProjectModel> projectList) {
		ArrayList<JsonHoursModel> hoursList = new ArrayList<JsonHoursModel>();
		JsonHoursModel jsonHoursModel;
		for (ProjectModel project : projectList) {
			jsonHoursModel = new JsonHoursModel();
			jsonHoursModel.setDate(project.getJsonDate());
			jsonHoursModel.setHours(project.getHoursLogged());
			jsonHoursModel.setProjectId(project.getId());
			// FIXME
			if (project.getIdhours() != 0) {
				jsonHoursModel.setIdhours(project.getIdhours());
			} else {
				jsonHoursModel.setIdhours(new Date().getTime() + projectList.indexOf(project));
			}

			hoursList.add(jsonHoursModel);
		}
		return hoursList;
	}

	/**
	 * Marshal the result from the PPM webservice to the internal model.
	 * 
	 * @param hoursList
	 *            List of HoursModel JSON objects returned from the webservice
	 * @param weekNumber
	 *            Number of the week for which we are getting the data
	 * @param year
	 *            Year for which we are getting the data
	 * @param cal
	 *            Instance of a calendar set to Monday of the selected week
	 * @return internal model for the week.
	 */
	public static WeekProgressModel toWeekProgressModel(
			List<JsonHoursModel> hoursList, int weekNumber, int year,
			Calendar cal, Context context) {

		DateFormat df = new SimpleDateFormat(
				context.getString(R.string.normalDateFormat));
		DateFormat dfMonth = new SimpleDateFormat(
				context.getString(R.string.monthlyDateFormat));
		DateFormat dfJson = new SimpleDateFormat(
				context.getString(R.string.PpmDateFormat));
		double weeklyBase = Double.parseDouble(context
				.getString(R.string.weeklyBase));
		double logged = 0;
		double notLogged = 0;
		// map where the key in the day in string format,
		// and the value has the amount of hours logged for this day
		Map<String, Double> hoursPerDayMap = new HashMap<String, Double>();
		for (JsonHoursModel hour : hoursList) {
			Double hoursLogged = hoursPerDayMap.get(hour.getDate());
			if (hoursLogged == null) {
				hoursPerDayMap.put(hour.getDate(), new Double(hour.getHours()));
			} else {
				hoursPerDayMap.put(hour.getDate(), hour.getHours()
						+ hoursLogged);
			}
		}

		WeekProgressModel weekProgressModel = new WeekProgressModel();
		weekProgressModel.setWeekId(weekNumber);
		weekProgressModel.setYear(year);
		weekProgressModel.setLabel(context.getString(R.string.week) + " "
				+ weekNumber + ", " + dfMonth.format(cal.getTime()));
		
		List<DayProgressModel> dayList = new ArrayList<DayProgressModel>();

		double dailyHoursBase = Double.parseDouble(context
				.getString(R.string.dailyHoursBase));
		Double hoursLogged;
		DayProgressModel dayProgressModel;
		for (int dayIndex = 1; dayIndex <= 7; dayIndex++) {
			int progress = 0;
			// get the hours logged for this day
			hoursLogged = hoursPerDayMap
					.get(dfJson.format(cal.getTime()));
			if (hoursLogged == null) {
				progress = 0;
			} else {
				progress = (int) (hoursLogged / dailyHoursBase * 100);
				if (progress > 100) {
					progress = 100;
				}
				logged += hoursLogged;
			}

			dayProgressModel = new DayProgressModel();
			dayProgressModel.setDayId(dayIndex);
			dayProgressModel.setYear(year);
			dayProgressModel.setDayOfTheYear(cal.get(Calendar.DAY_OF_YEAR));
			dayProgressModel.setLabel(df.format(cal.getTime()));
			dayProgressModel.setProjectList(marshalProjectList(hoursList,
					dfJson.format(cal.getTime())));
			cal.add(Calendar.DATE, 1);
			dayProgressModel.setProgress(progress);
			dayList.add(dayProgressModel);
		}
		notLogged = weeklyBase - logged;
		if (notLogged < 0) {
			notLogged = 0;
		}
		weekProgressModel.setLoggedHoursAmmount(logged);
		weekProgressModel.setNotLoggedHoursAmmount(notLogged);
		
		weekProgressModel.setDayList(dayList);

		return weekProgressModel;
	}

	/**
	 * Marshal the project list to the internal daily model.
	 * 
	 * @param hoursList
	 *            List of objects returned from the WS
	 * @param jsonDate
	 *            Date for which we are creating the project list.
	 * @return List of the projects for the selected day.
	 */
	public static List<ProjectModel> marshalProjectList(
			List<JsonHoursModel> hoursList, String jsonDate) {
		List<ProjectModel> projectList = new ArrayList<ProjectModel>();
		ProjectModel projectModel;
		for (JsonHoursModel hour : hoursList) {
			if (hour.getDate().equals(jsonDate)) {
				projectModel = new ProjectModel();
				projectModel.setId(hour.getProjectId());
				projectModel.setHoursLogged(hour.getHours());
				projectModel.setJsonDate(jsonDate);
				// FIXME:
				projectModel.setIdhours(hour.getIdhours());
				projectList.add(projectModel);
			}
		}
		return projectList;
	}

	/**
	 * Marshal result from WS for a daily view to the internal model.
	 * 
	 * @param myProjects
	 *            List of projects returned from WS.
	 * @param hoursList
	 *            List of logged hours returned from WS.
	 * @return List<ProjectModel> List of internal model projects.
	 */
	public static List<ProjectModel> toDailyProjectList(
			List<JsonProject> myProjects, List<JsonHoursModel> hoursList,
			String jsonDate) {
		List<ProjectModel> projectList = new ArrayList<ProjectModel>();
		Map<Integer, Double> loggedHoursMap = new HashMap<Integer, Double>();
		// FIXME
		Map<Integer, Long> idHoursMapTODELETE = new HashMap<Integer, Long>();
		for (JsonHoursModel hour : hoursList) {
			if(jsonDate.equals(hour.getDate())) {
				loggedHoursMap.put(hour.getProjectId(), hour.getHours());
				// FIXME
				idHoursMapTODELETE.put(hour.getProjectId(), hour.getIdhours());
			}
		}
		ProjectModel projectModel;
		Double hours;
		//FIXME:
		Long idhours;
		for (JsonProject project : myProjects) {
			projectModel = new ProjectModel();
			projectModel.setId(project.getProjectId());
			projectModel.setProjectName(project.getProjectName());
			projectModel.setJsonDate(jsonDate);
			hours = loggedHoursMap.get(project.getProjectId());
			if (hours != null) {
				projectModel.setHoursLogged(hours);
			}

			// FIXME
			idhours = idHoursMapTODELETE.get(project.getProjectId());
			if (idhours != null) {
				projectModel.setIdhours(idhours);
			}

			// TODO: what about the comment?
			// projectModel.setComment(comment)
			projectList.add(projectModel);
		}
		return projectList;
	}

	/**
	 * Copy the week model to the selected week. Skip the personal data, copy
	 * only the project info and hours logged.
	 * 
	 * @param weekCopyTo
	 *            Week copying to
	 * @param weekCopyFrom
	 *            Week copying from
	 * @return The copied week with the required data.
	 */
	public static WeekProgressModel copyOtherWeek(WeekProgressModel weekCopyTo,
			WeekProgressModel weekCopyFrom) {
		DateFormat dfJson = new SimpleDateFormat(CommonUtils.PPM_DATE_FORMAT);
		int weekDiff = weekCopyTo.getWeekId() - weekCopyFrom.getWeekId();
		Calendar cal = Calendar.getInstance();
		cal.clear();
		weekCopyTo.setLoggedHoursAmmount(weekCopyFrom.getLoggedHoursAmmount());
		weekCopyTo.setNotLoggedHoursAmmount(weekCopyFrom
				.getNotLoggedHoursAmmount());
		DayProgressModel dayTo;
		DayProgressModel dayFrom;
		Date copiedDate;
		for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
			dayTo = weekCopyTo.getDayList().get(dayIndex);
			dayFrom = weekCopyFrom.getDayList().get(dayIndex);
			dayTo.setProjectList(dayFrom.getProjectList());
			dayTo.setProgress(dayFrom.getProgress());
			for (ProjectModel project : dayTo.getProjectList()) {
				copiedDate = new Date();
				try {
					copiedDate = dfJson.parse(project.getJsonDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				cal.setTime(copiedDate);
				cal.add(Calendar.DATE, 7 * weekDiff);
				project.setJsonDate(dfJson.format(cal.getTime()));
				project.setOperation(OperationFlag.CREATE);
				// FIXME:
				project.setIdhours(new Date().getTime() + dayIndex);
			}
		}
		return weekCopyTo;
	}

	public static DayProgressModel copyOtherDay(DayProgressModel dayCopyTo,
			DayProgressModel dayCopyFrom) {

		return null;
	}
}
