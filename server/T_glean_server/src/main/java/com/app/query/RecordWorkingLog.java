package com.app.query;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.server.db.DAO;

/**
 * Servlet implementation class RecordWorkingLog
 */
@WebServlet("/rwlog")
public class RecordWorkingLog extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecordWorkingLog() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain;charset=UTF-8");
		System.out.println("/rwlog called");

		DAO dao = new DAO();
		
		String wname = request.getParameter("wname");
		String btnName = request.getParameter("btnName");
		int result = dao.RecordLog(wname, btnName, request);
		
		if(btnName.equals("finishwork"))
			dao.finishWork(wname, request);
		
		PrintWriter out = response.getWriter();
		out.print(result);
	}

}
