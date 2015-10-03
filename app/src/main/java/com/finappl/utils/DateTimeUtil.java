package com.finappl.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateTimeUtil {

	private final String CLASS_NAME = this.getClass().getName();
	
	private static DateTimeUtil instance = null;

	public boolean isDateAfterOrEquals(String checkDateStr, String withDateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		try{
			Date checkDate = sdf.parse(checkDateStr);
			Date withDate = sdf.parse(withDateStr);

			if(withDate.equals(checkDate) || withDate.before(checkDate)){
				return true;
			}
		}
		catch(ParseException e){
			Log.e(CLASS_NAME, "Date Parse Exception");
		}
		return false;
	}

	public List<String> getAllDatesInWeekOnDate(String dateStr){
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		List<String> datesList = new ArrayList<>();

		String dateStrArr[] = dateStr.split("-");
		now.set(Integer.parseInt(dateStrArr[2]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[0]));

		int day = now.get(Calendar.DAY_OF_WEEK);
		switch (day){
			case 1:
				now.add(Calendar.DATE, -6);
				break;
			case 2:
				now.add(Calendar.DATE, 0);
				break;
			case 3:
				now.add(Calendar.DATE, -1);
				break;
			case 4:
				now.add(Calendar.DATE, -2);
				break;
			case 5:
				now.add(Calendar.DATE, -3);
				break;
			case 6:
				now.add(Calendar.DATE, -4);
				break;
			case 7:
				now.add(Calendar.DATE, -5);
				break;
		}

		// Print dates of the current week starting on Monday
		for (int i = 0; i < 7; i++) {
			datesList.add(format.format(now.getTime()));
			now.add(Calendar.DATE, 1);
		}

		return datesList;
	}

	public String getDayOfWeekFromDate(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE", Locale.US);
		try{
			return dateFormat.format(sdf.parse(dateStr));
		}
		catch(ParseException e){
			Log.e(CLASS_NAME, "Parse Exception"+e);
		}
		return null;
	}

	public int getLastDayOfTheMonth(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		try{
			Date today = sdf.parse(dateStr);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(today);

			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.DATE, -1);

			Date lastDayOfMonth = calendar.getTime();

			return Integer.parseInt(sdf.format(lastDayOfMonth).split("-")[0]);
		}
		catch(ParseException e){
			Log.e(CLASS_NAME, "Date Parse Exception");
		}
		return 0;
	}

	public boolean checkBetween(Date date, Date dateStart, Date dateEnd){
	    if (date != null && dateStart != null && dateEnd != null){
	        if (date.after(dateStart) && date.before(dateEnd)){
	            return true;
	        }
	        else{
	            return false;
	        }
	    }
	    return false;
	}
	
	// get how manyth day this date is in that particular month
	public int getDayNumberInMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DB_DATE);
		String dateStr = sdf.format(new Date());
		
		String dateArr[] = dateStr.split("-");

		Calendar calendar = Calendar.getInstance();

		int year = Integer.parseInt(dateArr[0]);
		int month = Integer.parseInt(dateArr[1]) - 1;
		int date = Integer.parseInt(dateArr[2]);

		calendar.set(year, month, date);
		int days = calendar.get(Calendar.DAY_OF_MONTH);

		// System.out.println("Number of Days: " + days);

		return days;
	}
	
	//get max days in that particular month
	public int getMaxDaysInMonth(String dateStr){
		String dateArr[] = dateStr.split("-");
		
		Calendar calendar = Calendar.getInstance();
		
		int year = Integer.parseInt(dateArr[0]);
		int month = Integer.parseInt(dateArr[1]) - 1;
		int date = Integer.parseInt(dateArr[2]);
		
		calendar.set(year, month, date);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		//System.out.println("Number of Days: " + days);
		
		return days;
	}
	
	//get how manyth day this date is in that particular year
	public int getDayNumberInYear(){
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DB_DATE);
		String dateStr = sdf.format(new Date());
		
		String dateArr[] = dateStr.split("-");
		
		Calendar calendar = Calendar.getInstance();
		
		int year = Integer.parseInt(dateArr[0]);
		int month = Integer.parseInt(dateArr[1]) - 1;
		int date = Integer.parseInt(dateArr[2]);
		
		calendar.set(year, month, date);
		int days = calendar.get(Calendar.DAY_OF_YEAR);
		
		//System.out.println("Number of Days: " + days);
		
		return days;
	}
	
	public int getMaxDaysInYear(String dateStr){
		String dateArr[] = dateStr.split("-");
		
		Calendar calendar = Calendar.getInstance();
		
		int year = Integer.parseInt(dateArr[0]);
		int month = Integer.parseInt(dateArr[1]) - 1;
		int date = Integer.parseInt(dateArr[2]);
		
		calendar.set(year, month, date);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		
		//System.out.println("Number of Days: " + days);
		
		return days;
	}
	
	//method to check if the chosen date is from past, present or future
	public String checkDateForPastPresentFuture(String choosenDateStr, String checkWhat){
		SimpleDateFormat sdf =  null;
		String choosenDateStrArr[] = choosenDateStr.split("-");
		
		if(checkWhat.equals("YEAR")){
			sdf = new SimpleDateFormat(Constants.DB_YEAR);
			choosenDateStr = choosenDateStrArr[0];
		}
		else if(checkWhat.equals("YEARMONTH")){
			sdf = new SimpleDateFormat(Constants.DB_YEARMONTH);
			choosenDateStr = choosenDateStrArr[0]+"-"+choosenDateStrArr[1];
		}
		
		Date todaysDate = null;
		Date choosenDate = null;
		
		try{
			choosenDate = sdf.parse(choosenDateStr);
			todaysDate = sdf.parse(sdf.format(new Date()));
		} 
		catch (ParseException e){
			Log.e(this.getClass().getName(), "Exception during date conversion in checkDateForPastPresentFuture Util method in DateTimeUtil Class :"+e.getMessage());
		}
		
		//check if past date
		if(choosenDate.before(todaysDate)){
			Log.i(this.getClass().getName(), "The choosen date is past date");
			return "PAST";
		}
		//check if future date
		else if(choosenDate.after(todaysDate)){
			Log.i(this.getClass().getName(), "The choosen date is future date");
			return "FUTURE";
		}
		////check if today
		else{
			Log.i(this.getClass().getName(), "The choosen date/ pre selected date is today's date");
			return "TODAY";
		}
	}
	
	//method which returns DateTimestamp
	public String getDateTimeStamp(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		
		return sdf.format(date);
	}
	
	//method to convert yyyy-MM-dd to dd MMM yyyy format
	public String convertDateToGoodFormat(String dateStr){
		SimpleDateFormat sdfBad = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfGood = new SimpleDateFormat("dd MMM yyyy");
		
		try{
			String goodDateStr = sdfGood.format(sdfBad.parse(dateStr));
			Log.i(CLASS_NAME, "Bad date ("+dateStr+") converted to good date ("+goodDateStr+")");
			return goodDateStr;
		} 
		catch (ParseException e){
			Log.i(CLASS_NAME, "Error while parsing Bad date ("+dateStr+")"+e.getMessage());
			return "JUNK";
		}
	}
	
	//	method to convert UI date(String)dd MMM yyyy -> DB date(String) dd-MM-yyyy
	public String uiDateStringToDbDateString(String dateStr){
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd MMM yyyy");
		SimpleDateFormat sdfDb = new SimpleDateFormat("dd-MM-yyyy");
		
		try{
			return sdfDb.format(sdfUi.parse(dateStr));
		} 
		catch (ParseException e){
			Log.e(CLASS_NAME, "Error in uiDateStringToDbDateString() while parsing date"+e.getMessage());
		}
		
		return null;
	}
	
	//	method to convert UI date(String)dd MM yyyy -> UI date(String) dd MMM yyyy
	public String uiDateStringToUIDateString(String dateStr){
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd MM yyyy");
		SimpleDateFormat sdfUi2 = new SimpleDateFormat("dd MMM yyyy");
		
		try{
			return sdfUi2.format(sdfUi.parse(dateStr));
		} 
		catch (ParseException e){
			Log.e(CLASS_NAME, "Error in uiDateStringToDbDateString() while parsing date"+e.getMessage());
		}
		
		return null;
	}
	
	//	method to convert date(String) from yyyy-MM-dd to Date object
	public Date  appDateStringToDateObj(String appDateStr){
		SimpleDateFormat sdfUi = new SimpleDateFormat("yyyy-MM-dd");
		
		try 
		{
			return sdfUi.parse(appDateStr);
		} 
		catch (ParseException e) 
		{
			Log.e(CLASS_NAME, "Error in appDateStringToUiDateString() while parsing date"+e.getMessage());
		}
		return null;
	}
	
	//	method to convert date(date) -> UI date(String) dd MMM yyyy
	public String dateDateToUIDateString(Date date)
	{
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd MMM yyyy");
		
		return sdfUi.format(date);
	}
	
	//	method to convert date(date) -> db date(String) dd-MM-yyyy
	public String dateDateToDbDateString(Date date)
	{
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd-MM-yyyy");
		
		return sdfUi.format(date);
	}
	
	//	method to convert date(date) -> db date(String) dd-MM-yyyy HH:mm:ss
	public String dateDateToDbDateString1(Date date)
	{
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		return sdfUi.format(date);
	}
	
	//	method to convert UI date(String)dd MMM yyyy -> date(Date) 
	public Date uiDateToDateDate(String dateStr)
	{
		SimpleDateFormat sdfUi = new SimpleDateFormat("dd MMM yyyy");
		
		try 
		{
			return sdfUi.parse(dateStr);
		} 
		catch (ParseException e) 
		{
			Log.e(CLASS_NAME, "Error in uiDateStringToDbDateString() while parsing date"+e.getMessage());
		}
		return null;
	}
	
	//get instance
	private DateTimeUtil(){}

	public synchronized static DateTimeUtil getInstance() 
	{
		if (instance == null) 
		{
			instance = new DateTimeUtil();
		}
		return instance;
	}
}