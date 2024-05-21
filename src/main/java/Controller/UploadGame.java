package Controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import model.ProductModel;
import model.game;


/**
 * Servlet implementation class AddGame
 */
@WebServlet("/AddGame")
@MultipartConfig()
public class UploadGame extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SAVE_DIR = "img";
	static ProductModel GameModels = new ProductModelDM();
	
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	LocalDateTime now = LocalDateTime.now();
	
	 private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>();
	    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>();
	    
	    static {
	        // Set di estensioni consentite
	        ALLOWED_EXTENSIONS.add("jpg");
	        ALLOWED_EXTENSIONS.add("jpeg");
	        ALLOWED_EXTENSIONS.add("png");
	        ALLOWED_EXTENSIONS.add("gif");
	        
	        // Set di content types consentiti
	        ALLOWED_CONTENT_TYPES.add("image/jpeg");
	        ALLOWED_CONTENT_TYPES.add("image/png");
	        ALLOWED_CONTENT_TYPES.add("image/gif");
	    }
	
    public UploadGame() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		out.write("Error: GET method is used but POST method is required");
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Collection<?> games = (Collection<?>) request.getSession().getAttribute("games");
		String savePath = request.getServletContext().getRealPath("") + File.separator + SAVE_DIR;
		game g1 = new game();
		/*
		File fileSaveDir = new File(savePath);
		
		 * if (!fileSaveDir.exists()) { fileSaveDir.mkdir(); }
		 */
		String fileName= null;
		String message = "upload =\n";
		if (request.getParts() != null && request.getParts().size() > 0) {
			for (Part part : request.getParts()) {
				fileName = extractFileName(part);
			
				if (fileName != null && !fileName.equals("")) {
	                String fileExtension = getFileExtension(fileName);
	                String contentType = part.getContentType();
					// Validazione dell'estensione e del contenuto consentito
	                if (isAllowedExtension(fileExtension) && isAllowedContentType(contentType)) {
                        File fileSaveDir = new File(savePath);
                        if (!fileSaveDir.exists()) {
                            fileSaveDir.mkdir();
                        }
                        
                        part.write(savePath + File.separator + fileName);
                        g1.setImg(fileName);
                        message = message + fileName + "\n";
                    } else {
                        request.setAttribute("error", "Errore: Formato del file non consentito");
                    }
                } else {
                    request.setAttribute("error", "Errore: Bisogna selezionare almeno un file");
                }
			}
		}
		
		g1.setName(request.getParameter("nomeGame"));
		g1.setYears(request.getParameter("years"));
		g1.setAdded(dtf.format(now));
		g1.setQuantity(Integer.valueOf(request.getParameter("quantita")));
		g1.setPEG(Integer.valueOf(request.getParameter("PEG")));
		g1.setIva(Integer.valueOf(request.getParameter("iva")));
		g1.setGenere(request.getParameter("genere"));
		g1.setDesc(request.getParameter("desc"));
		g1.setPrice(Float.valueOf(request.getParameter("price")));
		
		try {
			GameModels.doSave(g1);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//request.setAttribute("message", message);
		request.setAttribute("stato", "success!");
		
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/gameList?page=admin&sort=added DESC");
		dispatcher.forward(request, response);
	}
	
	
	private String extractFileName(Part part) {
		// content-disposition: form-data; name="file"; filename="file.txt"
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length() - 1);
			}
		}
		return "";
	}

	
	// Metodo ausiliario per controllare se un array di byte inizia con un altro array di byte
	private boolean startsWith(byte[] array, byte[] prefix) {
	    if (prefix.length > array.length) {
	        return false;
	    }
	    for (int i = 0; i < prefix.length; i++) {
	        if (array[i] != prefix[i]) {
	            return false;
	        }
	    }
	    return true;
	}
	
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
	
    private boolean isAllowedExtension(String extension) {
        return ALLOWED_EXTENSIONS.contains(extension);
    }
    
    private boolean isAllowedContentType(String contentType) {
        return ALLOWED_CONTENT_TYPES.contains(contentType);
    }

}
