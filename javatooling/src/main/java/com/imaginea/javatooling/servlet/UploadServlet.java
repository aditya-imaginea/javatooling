package com.imaginea.javatooling.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.imaginea.javatooling.helpers.ClasspathHacker;
import com.imaginea.javatooling.helpers.JavaDiff;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_DIRECTORY = "upload";
	private static final int THRESHOLD_SIZE = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		if (!checkClassLoaded("com.sun.source.tree.TreeVisitor")) {
			loadJar();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// checks if the request actually contains upload file
		if (!ServletFileUpload.isMultipartContent(request)) {
			// if not, we stop here
			return;
		}

		// configures some settings
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(THRESHOLD_SIZE);
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(MAX_FILE_SIZE);
		upload.setSizeMax(REQUEST_SIZE);

		// constructs the directory path to store upload file
		// String uploadPath = getServletContext().getRealPath("")
		// + File.separator + UPLOAD_DIRECTORY;
		String uploadPath = "/tmp/upload";
		// creates the directory if it does not exist
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		// JavaDiff diff = null;
		try {
			// parses the request's content to extract file data
			List formItems = upload.parseRequest(request);
			Iterator iter = formItems.iterator();
			List<String> filePaths = new ArrayList<String>(2);
			// iterates over form's fields
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				// processes only fields that are not form fields
				if (!item.isFormField()) {
					String fileName = new File(item.getName()).getName();
					String filePath = uploadPath + File.separator + fileName;
					System.out.println(filePath);
					filePaths.add(filePath);
					File storeFile = new File(filePath);
					// saves the file on disk
					item.write(storeFile);
				}
			}
			String url = "http://" + request.getServerName() + ":"
					+ request.getServerPort() + request.getContextPath()
					+ "/rest/diffservice/compare?" + "old=" + filePaths.get(0)
					+ "&new=" + filePaths.get(1);
			System.out.println(url);
			response.sendRedirect(url);
		} catch (Exception ex) {
			ex.printStackTrace();
			request.setAttribute("message",
					"There was an error: " + ex.getMessage());
			getServletContext().getRequestDispatcher("/message.jsp").forward(
					request, response);
		}

	}

	private boolean checkClassLoaded(String className) {
		try {
			Class cls = Class.forName(className);
			System.out.println("\n" + cls.getName() + " loaded from:"
					+ cls.getProtectionDomain().getCodeSource().getLocation());
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	// is a real dirty hack .. but classloader delegation seems to be eating
	// away tools.jar
	private void loadJar() {
		try {
			System.out.println("Dirty Hack: loading the tools.jar physically ");
			ClasspathHacker.addFile("tools.jar");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
