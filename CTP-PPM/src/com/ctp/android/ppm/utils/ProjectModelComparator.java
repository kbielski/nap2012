package com.ctp.android.ppm.utils;

import java.util.Comparator;

import com.ctp.android.ppm.model.ProjectModel;

public class ProjectModelComparator implements Comparator<ProjectModel> {

	@Override
	public int compare(ProjectModel pm1, ProjectModel pm2) {
		return (pm1.getHoursLogged() > pm2.getHoursLogged() ? -1 : (pm1
				.getHoursLogged() == pm2.getHoursLogged() ? 0 : 1));
	}

}
