package com.app.query;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.server.db.DAO;
import com.server.db.Einformation;

/**
 * Servlet implementation class GetDate_Time
 */
@WebServlet("/getdt")
public class GetDate_Time extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetDate_Time() {
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
		response.setContentType("application/json;charset=UTF-8");
		System.out.println("/getdt called");
		
		String wname = request.getParameter("wname");
		DAO dao = new DAO();
		ArrayList<Einformation> al = dao.entireWdati(wname);
		PrintWriter out = response.getWriter();
		out.print("{");
		out.print("\"data\":[");
		for(int i = 0; i < al.size(); i++) {
			out.print("{\"date\":\"" + al.get(i).getDate() + "\",");
			if(i == al.size() - 1)
				out.print("\"time\":\"" + al.get(i).getTime() + "\"}");
			else
				out.print("\"time\":\"" + al.get(i).getTime() + "\"},");
		}
		out.print("]");
		out.print("}");
	}

}
