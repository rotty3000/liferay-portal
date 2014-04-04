package com.liferay.portal.service.remote;

import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * @author Tomas Polesovsky
 */
@XmlType
public class JAXWSServiceObject {
	private String stringParam;
	private Date dateParam;
	private int intParam;

	public String getStringParam() {
		return stringParam;
	}

	public void setStringParam(String stringParam) {
		this.stringParam = stringParam;
	}

	public Date getDateParam() {
		return dateParam;
	}

	public void setDateParam(Date dateParam) {
		this.dateParam = dateParam;
	}

	public int getIntParam() {
		return intParam;
	}

	public void setIntParam(int intParam) {
		this.intParam = intParam;
	}

	@Override
	public String toString() {
		return "JAXWSServiceObject{" +
			"stringParam='" + stringParam + '\'' +
			", dateParam=" + dateParam +
			", intParam=" + intParam +
			'}';
	}
}
