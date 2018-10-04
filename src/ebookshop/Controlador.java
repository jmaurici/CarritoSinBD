package ebookshop;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import modelo.Book;

public class Controlador extends HttpServlet {
	private Vector<Book> shoplist;

	public void init(ServletConfig conf) throws ServletException {
		super.init(conf);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	private void cargaLibros(HttpServletRequest req) {
		Vector<String> blist = new Vector<String>();
		HttpSession session = req.getSession(true);
		blist.addElement("Beginning JSP, JSF and Tomcat. Zambon/Sekler $39.99");
		blist.addElement("Beginning JBoss Seam. Nusairat $39.99");
		blist.addElement("Founders at Work. Livingston $25.99");
		blist.addElement("Business Software. Sink $24.99");
		blist.addElement("Foundations of Security. Daswani/Kern/Kesavan $39.99");
		session.setAttribute("listaLibros", blist);

	}

	@SuppressWarnings("deprecation")
	private void facturar(HttpServletRequest req) {
		float dollars = 0;
		int books = 0;
		for (int i = 0; i < shoplist.size(); i++) {
			Book aBook = (Book) shoplist.elementAt(i);
			float price = aBook.getPrice();
			int qty = aBook.getQuantity();
			dollars += price * qty;
			books += qty;
		}
		req.setAttribute("dollars", new Float(dollars).toString());
		req.setAttribute("books", new Integer(books).toString());

	}

	@SuppressWarnings("deprecation")
	private void remove(HttpServletRequest req) {
		String pos = req.getParameter("position");
		shoplist.removeElementAt((new Integer(pos)).intValue());

	}

	public void add(HttpServletRequest req) {
		boolean found = false;
		Book aBook = getBook(req);
		if (shoplist == null) { // the shopping cart is empty
			shoplist = new Vector<Book>();
			shoplist.addElement(aBook);
		} else { // update the #copies if the book is already there
			for (int i = 0; i < shoplist.size() && !found; i++) {
				Book b = (Book) shoplist.elementAt(i);
				if (b.getTitle().equals(aBook.getTitle())) {
					b.setQuantity(b.getQuantity() + aBook.getQuantity());
					shoplist.setElementAt(b, i);
					found = true;
				}
			} // for (i..
			if (!found) { // if it is a new book => Add it to the shoplist
				shoplist.addElement(aBook);
			}
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession(true);
		shoplist = (Vector<Book>) session.getAttribute("carrito");
		if (shoplist == null) // primera vez ...
		{
			shoplist = new Vector<Book>();
			session.setAttribute("carrito", shoplist);
		}
		String do_this = req.getParameter("do_this");
		String rutaJSP = "/";
		if (do_this == null) // primera vez ...
			cargaLibros(req);
		else
			switch (do_this) {
			case "checkout":
				rutaJSP = "/Checkout.jsp";
				facturar(req);
				break;
			case "remove":
				remove(req);
				break;
			case "add":
				add(req);
				break;
			}

		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(rutaJSP);
		rd.forward(req, res);

	} // doPost

	private Book getBook(HttpServletRequest req) {
		String myBook = req.getParameter("book");
		int n = myBook.indexOf('$');
		String title = myBook.substring(0, n);
		String price = myBook.substring(n + 1);
		String qty = req.getParameter("qty");
		return new Book(title, Float.parseFloat(price), Integer.parseInt(qty));
	} // getBook
}
