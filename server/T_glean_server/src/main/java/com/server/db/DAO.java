package com.server.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class DAO {
	public String MLogin(String id, String pwd) {	//200 : Success / 400 : Failed / 444 : Server Error
		System.out.println("MLogin called");
		String wcode = "";
		DBConnection dc = new DBConnection();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from mlogin where WId='" + id + "' and WPwd='" + pwd + "'";
		
		//ensure ID and PWD match
		try {
			con = dc.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next())
				wcode = rs.getString(1);
		} catch(Exception e) {
			e.printStackTrace();
			return "444";
		} finally {
			dc.close(con, pstmt, rs);
		}
		
		//if matched, get workername
		if(!(wcode.equals(""))) {
			String WName = getWnameFromWcode(wcode);
			return "200#" + WName;
		}
		else
			return "400";
	}
	
	//Record Working Log
	public int RecordLog(String wname, String btnName, HttpServletRequest request) {
		System.out.println("RecordLog called");
		String wcode = getWcodeFromWname(wname);
		//System.out.println("wcode in RecordLog : " + wcode);
		writeFile(wcode, btnName, request);
		return 200;
	}
	
	//Write log file
	public void writeFile(String wcode, String contents, HttpServletRequest request) {
		System.out.println("writeFile called");
		Date date = new Date();
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm:ss");
		String nowDate = sdfdate.format(date);	//���� ��¥
		String nowTime = sdftime.format(date);	//���� �ð�

		String filename = nowDate + ".txt";		//file name ex) 2018-09-15.txt
		String saveFolder = "log";				//folder name : log
		ServletContext context = request.getSession().getServletContext();
		String realPath = context.getRealPath(saveFolder);
		
		try {
			File file = new File(realPath);
			if(!file.exists())
				file.mkdirs();
			
			BufferedWriter fw = new BufferedWriter(new FileWriter(realPath + "\\" + filename, true));
			fw.write(wcode + "#" + nowTime + "#" + contents + "\r\n");
			//contents : AA001#08:30:12#startwork
			fw.flush();
			fw.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//���� ������� ���� �ð� ������
	public long getTodayWtime(String wname, HttpServletRequest request) {
		System.out.println("getTodayWtime called");
		long result = 0, lunchT = 0, restT = 0;
		String wcode = getWcodeFromWname(wname);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		ArrayList<String> log = getLastSf(wcode, getEntireLog(wcode, getNowDate(), request));
		Date swork = null, fwork = null, slunch = null, flunch = null, srest = null, frest = null;
		
		for(int i = 0; i < log.size() ; i++) {
			int firstSep = log.get(i).indexOf("#");		//firstSeparator
			int lastSep = log.get(i).lastIndexOf("#");	//lastSeparator
			int lIndexN = log.get(i).length();			//lastIndexNumber
			String wcodeInLog = log.get(i).substring(0, firstSep);
			String contentsInLog = log.get(i).substring(lastSep + 1, lIndexN);
			String timeInLog = log.get(i).substring(firstSep + 1, lastSep);
			
			if(wcodeInLog.equals(wcode)) {
				try {
					if(contentsInLog.equals("finishwork"))
						fwork = sdf.parse(timeInLog);
					
					if(contentsInLog.equals("startwork"))
						swork = sdf.parse(timeInLog);
					
					if(contentsInLog.equals("finishrest")) 
						frest = sdf.parse(timeInLog);
					
					if(contentsInLog.equals("startrest")) 
						srest = sdf.parse(timeInLog);
					
					if(contentsInLog.equals("finishlunch")) 
						flunch = sdf.parse(timeInLog);
					
					if(contentsInLog.equals("startlunch")) 
						slunch = sdf.parse(timeInLog);
					
					if(frest != null && srest != null) {	//restTime�� ���� start�� finish�� �� �� �αױ�Ͽ� �������ٸ�
						restT += (frest.getTime() - srest.getTime());
						
						frest = null;
						srest = null;
					}
					
					if(flunch != null && slunch != null) {	//lunchTime�� ���� start�� finish�� �� �� �αױ�Ͽ� �������ٸ�
						lunchT += (flunch.getTime() - slunch.getTime());
						
						flunch = null;
						slunch = null;
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//����� ���ٸ� (nulló���� ���� �־���)
		if(fwork == null && swork == null)
			result += 0;
		
		//swork, fwork ����� �ִٸ� --> DB���� ������ ���� ������ ������
		if(fwork != null && swork != null) {
			result += getWtime(wcode);
			return result;
		}
		
		//swork�� ����� ������ fwork�� ����� ���ٸ� --> ���� �ð��� �������� ���� ���� �ð� ���
		else if(fwork == null && swork != null) {
			result += getWtime(wcode);	//�켱 DB���� ���� ���� �ð��� ���� ������
			Date time = new Date();
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
			//GMT�� �ƴ� UTC + 09:00(KST)�� ���� �츮���� �ð��̱� ������ ����
			
			try {
				Date nowTime = sdf.parse(sdf2.format(time));	//���� �ð�(KST)
				result += (nowTime.getTime() - swork.getTime());
				//System.out.println("nowTime.getTime() - swork.getTime() --> " + (nowTime.getTime() - swork.getTime()));
				
				if(frest == null && srest != null) 			//restTime�� �����ϰ� finishrest�� ���ϰ� ���� ������ ���� �����ӽ�
					restT += (nowTime.getTime() - srest.getTime());
				
				if(flunch == null && slunch != null) 		//lunchTime�� �����ϰ� finishlunch�� ���ϰ� ���� ������ ���� �����ӽ�
					lunchT += (nowTime.getTime() - slunch.getTime());
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		//System.out.println("result : " + result + ", restT : " + restT + ", lunchT : " + lunchT);
		//System.out.println("getTodayWtime : " + (result - (restT + lunchT)));
		return result - (restT + lunchT);
	}
	
	//MainActivity ȭ������ ���� ���� �� startwork, startlunch, startrest �� �������� �߾����� ã�ƺ�����
	public int whatWasDoing(String wname, HttpServletRequest request) {
		System.out.println("whatWasDoing called");
		int returnV = 101;
		String wcode = getWcodeFromWname(wname);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		ArrayList<String> log = getLastSf(wcode, getEntireLog(wcode, getNowDate(), request));
		Date swork = null, fwork = null, slunch = null, flunch = null, srest = null, frest = null;
		
		for(int i = 0; i < log.size() ; i++) {
			int firstSep = log.get(i).indexOf("#");		//firstSeparator
			int lastSep = log.get(i).lastIndexOf("#");	//lastSeparator
			int lIndexN = log.get(i).length();			//lastIndexNumber
			String wcodeInLog = log.get(i).substring(0, firstSep);
			String contentsInLog = log.get(i).substring(lastSep + 1, lIndexN);
			String timeInLog = log.get(i).substring(firstSep + 1, lastSep);
			
			if(wcodeInLog.equals(wcode)) {
				try {
					if(contentsInLog.equals("startwork"))
						swork = sdf.parse(timeInLog);
					else if(contentsInLog.equals("finishwork"))
						fwork = sdf.parse(timeInLog);
					else if(contentsInLog.equals("startlunch"))
						slunch = sdf.parse(timeInLog);
					else if(contentsInLog.equals("finishlunch"))
						flunch = sdf.parse(timeInLog);
					else if(contentsInLog.equals("startrest"))
						srest = sdf.parse(timeInLog);
					else if(contentsInLog.equals("finishrest"))
						frest = sdf.parse(timeInLog);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		/*
		if(swork == null)
			System.out.println("swork : null");
		else
			System.out.println("swork : " + sdf.format(swork));
		
		if(fwork == null)
			System.out.println("fwork : null");
		else
			System.out.println("fwork : " + sdf.format(fwork));
		
		if(slunch == null)
			System.out.println("slunch : null");
		else
			System.out.println("slunch : " + sdf.format(slunch));
		
		if(flunch == null)
			System.out.println("flunch : null");
		else
			System.out.println("flunch : " + sdf.format(flunch));
		
		if(srest == null)
			System.out.println("srest : null");
		else
			System.out.println("srest : " + sdf.format(srest));
		
		if(frest == null)
			System.out.println("frest : null");
		else
			System.out.println("frest : " + sdf.format(frest));
		*/
		//fwork�� null�̸� swork�� ������ ���ÿ��� ���� �� == ��ٹ�ư �ȴ�����
		if(fwork == null && swork != null)
			returnV += 1;
		
		//flunch�� null�̸� slunch�� ������ ���ÿ��� ���� �� == ���ɳ� ��ư �ȴ�����
		if(flunch == null && slunch != null)
			returnV += 2;
		
		//frest�� null�̸� srest�� ������ ���ÿ��� ���� �� == �޽ĳ� ��ư �ȴ�����
		if(frest == null && srest != null)
			returnV += 3;
		
		//swork, fwork �� �� null == �ش� ��¥�� �ش� ���� ���
		if(swork == null && fwork == null) 
			returnV += 0;
		
		return returnV;
	}
	
	//when work finished, calculate time and upload to db
	public void finishWork(String wname, HttpServletRequest request) {
		System.out.println("finishWork called");
		String entireTstr = "", wcode = getWcodeFromWname(wname);		//etResult : calculate result
		long entireT = 0, lunchT = 0, restT = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date swork = null, fwork = null, slunch = null, flunch = null, srest = null, frest = null;
		
		ArrayList<String> lastLog = getLastSf(wcode, getEntireLog(wcode, getNowDate(), request));
		
		for(int i = 0; i < lastLog.size(); i++) {
			int firstSep = lastLog.get(i).indexOf("#");		//firstSeparator
			int lastSep = lastLog.get(i).lastIndexOf("#");	//lastSeparator
			int lIndexN = lastLog.get(i).length();			//lastIndexNumber
			String wcodeInLog = lastLog.get(i).substring(0, firstSep);
			String contentsInLog = lastLog.get(i).substring(lastSep + 1, lIndexN);
			String timeInLog = lastLog.get(i).substring(firstSep + 1, lastSep);
			
			if(wcodeInLog.equals(wcode)) {
				try {
					if(contentsInLog.equals("finishwork")) 		//finishwork
						fwork = sdf.parse(timeInLog);
					
					if(contentsInLog.equals("startwork")) {		//startwork
						swork = sdf.parse(timeInLog);

						//�̰��� �Ʒ��� if�� 2���� �� ������ �������� �о�鿴�� ������ finishwork�� �� ó�� ������ startwork�� �������� ������ ����
						if(frest == null && srest != null)		//restTime�� �����ϰ� finishrest�� ���ϰ� �ٷ� finishwork
							restT += (fwork.getTime() - srest.getTime());
						
						if(flunch == null && slunch != null)	//lunchTime�� �����ϰ� finishlunch�� ���ϰ� �ٷ� finishwork
							lunchT += (fwork.getTime() - slunch.getTime());
					}
					
					if(contentsInLog.equals("finishrest")) 
						frest = sdf.parse(timeInLog);
					
					if(contentsInLog.equals("startrest")) 
						srest = sdf.parse(timeInLog);
					
					if(contentsInLog.equals("finishlunch")) 
						flunch = sdf.parse(timeInLog);
					
					if(contentsInLog.equals("startlunch")) 
						slunch = sdf.parse(timeInLog);
					
					if(frest != null && srest != null) {	//restTime�� ���� start�� finish�� �� �� �αױ�Ͽ� �������ٸ�
						restT += (frest.getTime() - srest.getTime());
						
						frest = null;
						srest = null;
					}
					
					if(flunch != null && slunch != null) {	//lunchTime�� ���� start�� finish�� �� �� �αױ�Ͽ� �������ٸ�
						lunchT += (flunch.getTime() - slunch.getTime());
						
						flunch = null;
						slunch = null;
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//��ü ��ð� ��� ���� ���ߴ��� ���� �Ŀ� �޽Ľð��� ���ɽð��� ��
		entireT += (fwork.getTime() - swork.getTime());
		entireT -= (restT + lunchT);
		
		System.out.println("entireT : " + entireT);
		if(entireT < 0) {
			entireT = 0;
			System.out.println("entireT : " + entireT);
		}
		
		Date et = new Date(entireT);
		entireTstr = sdf.format(et);
		
		uploadWorkTime(wcode, entireTstr);
	}
	
	public void uploadWorkTime(String wcode, String entireTstr) {
		System.out.println("uploadWorkTime called");
		DBConnection dc = new DBConnection();
		Connection con = null;
		Statement stmt = null;

		String sql = "insert into worktime(WCode, WDate, WTime)";
		sql += "values('" + wcode + "','" + getNowDate() + "','" + entireTstr + "')";
		System.out.println("wcode : " + wcode + ", wdate : " + getNowDate() + ", time : " + entireTstr);
		
		try {
			con = dc.getConnection();
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			dc.close(con, stmt);
		}
	}
	
	//get entire log
	public ArrayList<String> getEntireLog(String wcode, String date, HttpServletRequest request) {
		System.out.println("getEntireLog called");
		String filename = date + ".txt";
		String saveFolder = "log";
		ServletContext context = request.getSession().getServletContext();
		String realPath = context.getRealPath(saveFolder);
		ArrayList<String> al = new ArrayList<>();
		
		try {
			File file = new File(realPath + "\\" + filename);	//find matching date
			if(!file.exists())
				return al;
			
			BufferedReader breader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = breader.readLine()) != null)
				al.add(line);
			breader.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return al;
	}
	
	//��ü(���糯¥) �α׿���  
	public ArrayList<String> getLastSf(String wcode, ArrayList<String> al) {
		System.out.println("getLastSf called");
		ArrayList<String> rsal = new ArrayList<String>();
		
		for(int i = al.size() - 1; i >= 0 ; i--) {
			//System.out.println(al.get(i));
			
			int firstSep = al.get(i).indexOf("#");		//firstSeparator
			int lastSep = al.get(i).lastIndexOf("#");	//lastSeparator
			int lIndexN = al.get(i).length();			//lastIndexNumber
			String wcodeInLog = al.get(i).substring(0, firstSep);
			String contentsInLog = al.get(i).substring(lastSep + 1, lIndexN);
			
			if(wcodeInLog.equals(wcode))
				rsal.add(al.get(i));
			
			if(wcodeInLog.equals(wcode) && contentsInLog.equals("startwork")) {
				//testP(rsal);
				return rsal;
			}
		}
		
		return rsal;
	}
	
	public void testP(ArrayList<String> al) {
		System.out.println("testP called");
		for(int i = 0; i < al.size(); i++)
			System.out.println(al.get(i));
	}
	
	//get entire date and time
	public ArrayList<Einformation> entireWdati(String wname) {
		System.out.println("entireWdati called");
		String wcode = getWcodeFromWname(wname);
		String sql = "select * from worktime where WCode='" + wcode + "'";
		DBConnection dc = new DBConnection();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		ArrayList<Einformation> al = new ArrayList<>();
		
		try {
			con = dc.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				Einformation e = new Einformation();
				e.setDate(rs.getString(2));
				e.setTime(rs.getString(3));
				//System.out.println(rs.getString(2));
				//System.out.println(rs.getString(3));
				al.add(e);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return al;
	}
	
	public String getWnameFromWcode(String wcode) {
		System.out.println("getWnameFromWcode called");
		DBConnection dc = new DBConnection();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String wname = "";
		String sql = "select * from workerinfo where WCode='" + wcode + "'";
		try {	//get wcode
			con = dc.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next())
				wname = rs.getString(2);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			dc.close(con, pstmt, rs);
		}
		
		return wname;
	}
	
	public String getWcodeFromWname(String wname) {
		System.out.println("getWcodeFromWname called");
		DBConnection dc = new DBConnection();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String wcode = "";
		String sql = "select * from workerinfo where WName='" + wname + "'";
		try {	//get wcode
			con = dc.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next())
				wcode = rs.getString(1);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			dc.close(con, pstmt, rs);
		}
		//System.out.println(wcode);
		
		return wcode;
	}
	
	public String getNowDate() {
		System.out.println("getNowDate called");
		Date date = new Date();
		SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");
		return sdfdate.format(date);	//���� ��¥
	}
	
	public long getWtime(String wcode) {
		long result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		DBConnection dc = new DBConnection();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from worktime where WCode='" + wcode + "' and WDate='" + getNowDate() + "'";
		
		try {
			con = dc.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next())
				result += sdf.parse(rs.getString(3)).getTime();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			dc.close(con, pstmt, rs);
		}
		
		return result;
	}
}