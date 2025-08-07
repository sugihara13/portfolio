package wireboutique.controller.serviceMgr;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import wireboutique.beans.ProductBean;
import wireboutique.beans.ProductContents;
import wireboutique.beans.TaxBean;
import wireboutique.bo.UploadImageValidator;
import wireboutique.dataaccess.database.ProductDAO;
import wireboutique.dataaccess.database.CategoryDAO;
import wireboutique.dataaccess.database.ManufacturerDAO;
import wireboutique.dataaccess.database.TaxDAO;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Servlet implementation class ProductController
 */
@WebServlet("/ServiceMgr/ProductRegistration")
@MultipartConfig(maxFileSize=15*1024*1024)//15Mb
public class ProductRegistrationContloller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final Pattern productNamePattern= Pattern.compile("[^\\.\\!$\\?_\\{\\}\\^¬¦]{2,200}");
	private final BigDecimal maxListPrice = BigDecimal.valueOf(99999999.99);
	
	//productを追加/編集のモードを決める true=追加 false=編集.
	private boolean isAddMode = true;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProductRegistrationContloller() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uploadDir = (String)request.getSession().getAttribute(ProductContents.UploadDirectryAttrName);
		if(uploadDir != null) {
			deleteUploadFiles(uploadDir);
			request.getSession().removeAttribute(ProductContents.UploadDirectryAttrName);
		}
		
		request.setAttribute("isAddProduct", isAddMode());
		
		
		ManufacturerDAO manuDAO = new ManufacturerDAO();
		CategoryDAO cateDAO = new CategoryDAO();
		TaxDAO taxDAO = new TaxDAO();
		
		Map<Integer, String> manufacturers= manuDAO.getManufacturers();
		Map<Integer, String> catedories = cateDAO.getCategories();
		List<TaxBean> taxCategories = taxDAO.getTaxCategories();
		
		
		request.setAttribute("manufacturers", manufacturers);
		request.setAttribute("categories", catedories);
		request.setAttribute("taxCategories", taxCategories);
		
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("/WEB-INF/ServiceMgr/ProductEditing.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		switch (request.getParameter("product-registration-action")) {
			case "confirm" -> {
				RequestDispatcher dispatcher = null;
				ArrayList<String> msgLines = new ArrayList<String>();
				
				ProductBean beforeProduct = null;
				
				Boolean isAddProduct = false;
				boolean inputIsValid = true;
				Integer id=0;
				String name	= null ,manufacturer = null ,category = null;
				Integer manufacturerId = -1,categoryId = -1 ,taxId = -1 , stock = -1;
				TaxBean taxCategory=null;
				LocalDateTime releaseDate = null;
				BigDecimal listPrice = null;
				boolean isPublic = false;
				
				HashMap<String, Part> uploadContents = new HashMap<String, Part>();
				
				
				Function<String,Integer> parseId = (String param) -> { 
					try {
						Integer v = Integer.valueOf(param);
						if(v > -1)
							return v;
						else return -1;
					}
					catch(NumberFormatException e){
						e.printStackTrace();
						return -1;
					}
				};
				
				//validation---------------					
				//name
				if(request.getParameter("name") != null) {
					if(productNamePattern.matcher(request.getParameter("name")).matches()) {
						name = request.getParameter("name");
					}
					else {
						inputIsValid = false;
						msgLines.add("product name is invalid.");
					}
				}
				else msgLines.add("product name field is empty.");
				
				//manufacturer
				if(request.getParameter("manufacturer") != null) {
					manufacturerId = parseId.apply(request.getParameter("manufacturer"));
					if(manufacturerId < 0) {
						inputIsValid = false;
						msgLines.add("manufacturer is invalid.");
					}
					else {
						ManufacturerDAO manuDAO = new ManufacturerDAO();
						manufacturer = manuDAO.getName(manufacturerId);
						if (manufacturer == null) {
							inputIsValid = false;
							msgLines.add("manufacturer is invalid.");
						}
					}
				}
				else msgLines.add("manufacturer field is empty.");
				
				//category
				if(request.getParameter("category") != null) {
					categoryId = parseId.apply(request.getParameter("category"));
					if(categoryId < 0) {
						inputIsValid = false;
						msgLines.add("category is invalid.");
					}
					else {
						CategoryDAO cateDAO = new CategoryDAO();
						category = cateDAO.getName(categoryId);
						if (category == null) {
							inputIsValid = false;
							msgLines.add("category is invalid.");
						}
					}
				}
				else msgLines.add("category field is empty.");
				
				//listprice
				if(request.getParameter("list-price") != null) {
					String inListprice	=	request.getParameter("list-price");
					boolean priceIsValid = true;
					
					try {
						listPrice = new BigDecimal(inListprice);
					} catch (NumberFormatException e) {
						e.printStackTrace();
						inputIsValid =false;
						priceIsValid =false;
						msgLines.add("list-price.");
					}
					
					if(priceIsValid) {
						if(listPrice.compareTo(BigDecimal.ZERO) == -1) {
							inputIsValid = false;
							msgLines.add("list-price require price >= 0.");
						}
						else if(listPrice.scale()>2) {
							inputIsValid = false;
							msgLines.add("list-price is invalid..");
						}
						else if(listPrice.compareTo(maxListPrice) == 1){
							inputIsValid = false;
							msgLines.add("list-price require price < 100,000,000.");
						}
					}
					else msgLines.add("list-price is invalid.");
				}
				else msgLines.add("list-price is invalid.");
				
				//tax
				if(request.getParameter("tax-category") != null) {
					taxId = parseId.apply(request.getParameter("tax-category"));
					if(taxId < 0) {
						inputIsValid = false;
						msgLines.add("tax-category is invalid.");
					}
					else {
						TaxDAO taxDAO = new TaxDAO();
						taxCategory = taxDAO.getTaxCategory(taxId);
						if (taxCategory == null) {
							inputIsValid = false;
							msgLines.add("tax-category is invalid.");
						}
					}
				}
				else msgLines.add("tax-category field is empty.");
				
				//release date
				if(request.getParameter("release-date") != null) {
					try {
						releaseDate = LocalDate.parse(request.getParameter("release-date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(0,0);
					}
					catch (DateTimeParseException e) {
						e.printStackTrace();
						inputIsValid = false;
						msgLines.add("release-date is invalid.");
					}
				}
				else msgLines.add("category field is empty.");
				
				//ispublic
				if(request.getParameter("public-setting") != null) {
					if(request.getParameter("public-setting").equals("true"))
						isPublic = true;
					else if(request.getParameter("public-setting").equals("false"))
						isPublic = false;
				}
				else msgLines.add("please select public-setting.");		
				
				
				if(request.getParameter("isAddProduct") != null) {
					if(request.getParameter("isAddProduct").equals("true")) {
						isAddProduct = true;
						
						//stock
						if(request.getParameter("stock-limit") != null && request.getParameter("stock-limit").equals("unlimited")) {
							stock = null;
						}
						else if(request.getParameter("initial-stock") != null) {
							try {
								stock = Integer.valueOf(request.getParameter("initial-stock"));
								if(stock < 0) {
									inputIsValid = false;
									msgLines.add("initial stock require value >= 0.");
								}
							}
							catch(NumberFormatException e){
								e.printStackTrace();
								inputIsValid = false;
								msgLines.add("stock is invalid.");
							}
						}
						else msgLines.add("stock is invalid.");
						
						//contents
						if(request.getPart("main-image") != null)
							uploadContents.put("pannel", request.getPart("main-image"));
						
						UploadImageValidator imgValidator = new UploadImageValidator();
						
						for(Map.Entry<String,Part> part:uploadContents.entrySet()) {
							String filename = part.getValue().getSubmittedFileName();
							
							if(!imgValidator.isValidFileName(filename)) {
								inputIsValid = false ;
								msgLines.add("File Name/"+filename+" is invalid.");
								break;
							}
							
							if(!imgValidator.isValidImage(part.getValue().getInputStream())) {
								inputIsValid = false ;
								msgLines.add("Upload File/"+filename+" is invalid.");
								break;
							}
												
						}
					}
					else if(request.getParameter("isAddProduct").equals("false")) {
						isAddProduct = false;
						
						//id
						if(request.getParameter("id") != null) {
							id = parseId.apply(request.getParameter("id"));
							if(id < 0) {
								inputIsValid = false;
								msgLines.add("product Id is invalid.");
							}
							else {
								ProductDAO productDAO = new ProductDAO();
								beforeProduct = productDAO.getProduct(request.getParameter("id"), false);
								if (beforeProduct == null) {
									inputIsValid = false;
									msgLines.add("product Id is invalid.");
								}
							}
						}
						else msgLines.add("product is invalid.");
						
					}
					else {
						request.setAttribute("errorMsg", "form param Error.");
						dispatcher = request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrErrorPage.jsp");
					}
				}
				//-----------------------------------------------

				
				if(inputIsValid) {
					String strId = "-1";
					if(isAddProduct == false)
						strId = String.valueOf(id);
					
					else {
						//content
						
						String uploadDir = null;
						try {
							uploadDir = createUploadDirectory(request);
						}
						catch (IOException e) {
							e.printStackTrace();
							request.setAttribute("errorMsg", "Error.");
							dispatcher = request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrErrorPage.jsp");
						}
						
						//uploadされたファイル名をフロントへ表示するためのMap
						HashMap<String,String> uploadFileNames = new HashMap<String, String>();
						UploadImageValidator imgValidator=new UploadImageValidator();
						
						for(Map.Entry<String, Part> file:uploadContents.entrySet()) {
							
							uploadFileNames.put(file.getKey(), file.getValue().getSubmittedFileName());
							
							StringBuilder saveFilename = new StringBuilder(uploadDir);
							saveFilename.append("/").append(file.getKey())
							.append(imgValidator.getExtension(file.getValue().getSubmittedFileName()));
							
							file.getValue().write(saveFilename.toString());
						}
						
						request.setAttribute("uploadFileNames", uploadFileNames);
						
						request.getSession().setAttribute(ProductContents.UploadDirectryAttrName, uploadDir);	
					}
					
					ProductBean product=new ProductBean(strId,name,category,manufacturer,
															listPrice,taxCategory,releaseDate,"",stock ,isPublic);
					
					request.setAttribute("product", product);
					request.setAttribute("manufacturerId", manufacturerId);
					request.setAttribute("categoryId", categoryId);
					
					request.setAttribute("isAddProduct", isAddProduct);
					
					if(!isAddProduct)
						request.setAttribute("beforeProduct", beforeProduct);
					
					if(dispatcher == null)
						dispatcher = request.getRequestDispatcher("/WEB-INF/ServiceMgr/ProductEditingConfirmation.jsp");
					
				}
				else {
					if(beforeProduct != null){
						setEditMode(request, beforeProduct);
					}
					
					request.setAttribute("msgLines", msgLines);
					request.setAttribute("isAddProduct", isAddProduct);
					
					ManufacturerDAO manuDAO = new ManufacturerDAO(); 
					CategoryDAO cateDAO = new CategoryDAO();
					TaxDAO taxDAO = new TaxDAO();
					
					Map<Integer, String> manufacturers= manuDAO.getManufacturers();
					Map<Integer, String> catedories = cateDAO.getCategories();
					List<TaxBean> taxCategories = taxDAO.getTaxCategories();
					
					request.setAttribute("manufacturers", manufacturers);
					request.setAttribute("categories", catedories);
					request.setAttribute("taxCategories", taxCategories);
					
					dispatcher = 
							request.getRequestDispatcher("/WEB-INF/ServiceMgr/ProductEditing.jsp");
				}
				dispatcher.forward(request, response);
			}
			case "apply" -> {
				RequestDispatcher dispatcher = null;
				ArrayList<String> msgLines = new ArrayList<String>();
				
				request.getParameter("main-image");
				
				ProductBean beforeProduct = null;
				
				Boolean isAddProduct = false;
				boolean inputIsValid = true;
				Integer id=0;
				String name	= null ,manufacturer = null ,category = null;
				Integer manufacturerId = -1,categoryId = -1 ,taxId = -1 , stock = -1;
				TaxBean taxCategory=null;
				LocalDateTime releaseDate = null;
				BigDecimal listPrice = null;
				boolean isPublic = false;
				String contentUrl = null;
				
				
				Function<String,Integer> parseId = (String param) -> { 
					try {
						Integer v = Integer.valueOf(param);
						if(v > -1)
							return v;
						else return -1;
					}
					catch(NumberFormatException e){
						e.printStackTrace();
						return -1;
					}
				};
				
				//validation---------------					
				//name
				if(request.getParameter("name") != null) {
					if(productNamePattern.matcher(request.getParameter("name")).matches()) {
						name = request.getParameter("name");
					}
					else {
						inputIsValid = false;
						msgLines.add("product name is invalid.");
					}
				}
				else msgLines.add("product name field is empty.");
				
				//manufacturer
				if(request.getParameter("manufacturer") != null) {
					manufacturerId = parseId.apply(request.getParameter("manufacturer"));
					if(manufacturerId < 0) {
						inputIsValid = false;
						msgLines.add("manufacturer is invalid.");
					}
					else {
						ManufacturerDAO manuDAO = new ManufacturerDAO();
						manufacturer = manuDAO.getName(manufacturerId);
						if (manufacturer == null) {
							inputIsValid = false;
							msgLines.add("manufacturer is invalid.");
						}
					}
				}
				else msgLines.add("manufacturer field is empty.");
				
				//category
				if(request.getParameter("category") != null) {
					categoryId = parseId.apply(request.getParameter("category"));
					if(categoryId < 0) {
						inputIsValid = false;
						msgLines.add("category is invalid.");
					}
					else {
						CategoryDAO cateDAO = new CategoryDAO();
						category = cateDAO.getName(categoryId);
						if (category == null) {
							inputIsValid = false;
							msgLines.add("category is invalid.");
						}
					}
				}
				else msgLines.add("category field is empty.");
				
				//listprice
				if(request.getParameter("list-price") != null) {
					String inListprice	=	request.getParameter("list-price");
					boolean priceIsValid = true;
					
					try {
						listPrice = new BigDecimal(inListprice);
					} catch (NumberFormatException e) {
						e.printStackTrace();
						inputIsValid =false;
						priceIsValid =false;
						msgLines.add("list-price.");
					}
					
					if(priceIsValid) {
						if(listPrice.compareTo(BigDecimal.ZERO) == -1) {
							inputIsValid = false;
							msgLines.add("list-price require price >= 0.");
						}
						else if(listPrice.scale()>2) {
							inputIsValid = false;
							msgLines.add("list-price is invalid.");
						}
						else if(listPrice.compareTo(maxListPrice) == 1){
							inputIsValid = false;
							msgLines.add("list-price require price < 100,000,000.");
						}
					}
					else msgLines.add("list-price is invalid.");
				}
				else msgLines.add("list-price is invalid.");
				
				//tax
				if(request.getParameter("tax-category") != null) {
					taxId = parseId.apply(request.getParameter("tax-category"));
					if(taxId < 0) {
						inputIsValid = false;
						msgLines.add("tax-category is invalid.");
					}
					else {
						TaxDAO taxDAO = new TaxDAO();
						taxCategory = taxDAO.getTaxCategory(taxId);
						if (taxCategory == null) {
							inputIsValid = false;
							msgLines.add("tax-category is invalid.");
						}
					}
				}
				else msgLines.add("tax-category field is empty.");
				
				//release date
				if(request.getParameter("release-date") != null) {
					try {
						releaseDate = LocalDate.parse(request.getParameter("release-date"), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(0,0);
					}
					catch (DateTimeParseException e) {
						e.printStackTrace();
						inputIsValid = false;
						msgLines.add("release-date is invalid.");
					}
				}
				else msgLines.add("category field is empty.");
				
				//ispublic
				if(request.getParameter("public-setting") != null) {
					if(request.getParameter("public-setting").equals("true"))
						isPublic = true;
					else if(request.getParameter("public-setting").equals("false"))
						isPublic = false;
				}
				else msgLines.add("please select public-setting.");		
				
				
				if(request.getParameter("isAddProduct") != null) {
					if(request.getParameter("isAddProduct").equals("true")) {
						isAddProduct = true;
						
						//stock
						if(request.getParameter("stock-limit") != null && request.getParameter("stock-limit").equals("unlimited")) {
							stock = null;
						}
						else if(request.getParameter("initial-stock") != null) {
							try {
								stock = Integer.valueOf(request.getParameter("initial-stock"));
								if(stock < 0) {
									inputIsValid = false;
									msgLines.add("initial stock require value >= 0.");
								}
							}
							catch(NumberFormatException e){
								e.printStackTrace();
								inputIsValid = false;
								msgLines.add("stock is invalid.");
							}
						}
						else msgLines.add("stock is invalid.");
						
						//contents
					}
					
					else if(request.getParameter("isAddProduct").equals("false")) {
						isAddProduct = false;
						
						//id
						if(request.getParameter("id") != null) {
							id = parseId.apply(request.getParameter("id"));
							if(id < 0) {
								inputIsValid = false;
								msgLines.add("product Id is invalid.");
							}
							else {
								ProductDAO productDAO = new ProductDAO();
								beforeProduct = productDAO.getProduct(request.getParameter("id"), false);
								if (beforeProduct == null) {
									inputIsValid = false;
									msgLines.add("product Id is invalid.");
								}
								else {
									stock = beforeProduct.getStock();
									contentUrl = beforeProduct.getContentURL();
								}
							}
						}
						else msgLines.add("product is invalid.");
						
					}
					else {
						request.setAttribute("errorMsg", "form param Error.");
						dispatcher = request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrErrorPage.jsp");
					}
				}
				//-------------------------
				
				if(inputIsValid) {
					String strId = "-1";
					if(isAddProduct == false)
						strId = String.valueOf(id);
					
					
					//registration-------------------------
					HttpSession session = request.getSession();
					//to database
					ProductDAO productDAO = new ProductDAO();
					
					if(isAddProduct == true) {
						//contents path は登録後にIDを取得してから決め,データベースへはupdateをかける.
						contentUrl = "";			
						
						
						ProductBean product=new ProductBean(strId,name,category,manufacturer,
								listPrice,taxCategory,releaseDate,contentUrl,stock ,isPublic);
						
						//productを登録しIdを取得
						String newProductId = productDAO.setProduct(product);
						product.setId(newProductId);
						
						//content files
						String uploadDir = (String)session.getAttribute(ProductContents.UploadDirectryAttrName);
						if(uploadDir != null) {
							String saveDir = createSaveDirectory(newProductId,request);
							
							//move files
							moveUploadFiles(Paths.get(uploadDir), Paths.get(saveDir));
							deleteUploadFiles(uploadDir);
							
							//save directryをcontent Urlとして更新
							product.setContentURL(saveDirectryPath(newProductId));
						}
						
						//content url をupdate
						productDAO.updateProduct(product);
						
						log("ProductRegistration :"+product);
						System.out.println("ProductRegistration :"+product);
						
						request.setAttribute("product", product);
					}
					else if(isAddProduct == false){
						strId = String.valueOf(id);
						
						ProductBean product=new ProductBean(strId,name,category,manufacturer,
								listPrice,taxCategory,releaseDate,contentUrl,stock ,isPublic);
						productDAO.updateProduct(product);
						
						request.setAttribute("product", product);
					}

					session.removeAttribute(ProductContents.UploadDirectryAttrName);
					//-------------------------------------

					
					request.setAttribute("isAddProduct", isAddProduct);
					
					if(!isAddProduct)
						request.setAttribute("beforeProduct", beforeProduct);
					
					dispatcher = 
						request.getRequestDispatcher("/WEB-INF/ServiceMgr/ProductEditingConfirmed.jsp");
					dispatcher.forward(request, response);
				}
				else {
					if(beforeProduct != null){
						setEditMode(request, beforeProduct);
					}
					
					request.setAttribute("msgLines", msgLines);
					request.setAttribute("isAddProduct", isAddProduct);
					
					ManufacturerDAO manuDAO = new ManufacturerDAO(); 
					CategoryDAO cateDAO = new CategoryDAO();
					TaxDAO taxDAO = new TaxDAO();
					
					Map<Integer, String> manufacturers= manuDAO.getManufacturers();
					Map<Integer, String> catedories = cateDAO.getCategories();
					List<TaxBean> taxCategories = taxDAO.getTaxCategories();
					
					request.setAttribute("manufacturers", manufacturers);
					request.setAttribute("categories", catedories);
					request.setAttribute("taxCategories", taxCategories);
					
					dispatcher = 
							request.getRequestDispatcher("/WEB-INF/ServiceMgr/ProductEditing.jsp");
							dispatcher.forward(request, response);
				}
			}
			case null,default->{
				request.setAttribute("errorMsg", "faild action");
				request.getRequestDispatcher("/WEB-INF/ServiceMgr/ServiceMgrErrorPage.jsp").forward(request, response);
			}
		}
	}

	private String createUploadDirectory(HttpServletRequest request) throws IOException {
		StringBuilder strPath = new StringBuilder("WEB-INF/upload");
		strPath.append("/")
			.append("productEditing/")
			.append(request.getSession().getId());
		
		Path path = Paths.get(request.getServletContext().getRealPath(strPath.toString()));
		if(!Files.exists(path))
			Files.createDirectories(path);
		return path.toString();
	}
	
	private String  saveDirectryPath(String productId) {
		StringBuilder strPath=new StringBuilder(ProductContents.SaveDirectry);
		strPath.append("/")
			.append(productId)
			.append("/contents/");
		
		return strPath.toString();
	}
	
	//product contentを保存するディレクトリを作成し相対パスを返す
	private String createSaveDirectory(String productId,HttpServletRequest request) throws IOException {
		String strPath = saveDirectryPath(productId);
		
		Path path = Paths.get(request.getServletContext().getRealPath(strPath.toString()));
		if(!Files.exists(path))
			Files.createDirectories(path);
		return path.toString();
	}
	
	private boolean deleteUploadFiles(String uploadDirectry) {
		Path dir = Paths.get(uploadDirectry);
		if(Files.exists(dir)){
		    System.out.println("ProductEditing deleteUploadFiles target directry: "+dir);


			DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
		        public boolean accept(Path file) throws IOException {
		            //return (Files.size(file) > 8192L);
		        	//enumにアップロードするcontentsNameを定義してそれをもとにフィルタするか
		       	 return true;
		        }
		    };

		    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
		    	for(Path file: stream) {
		    		System.out.println("ProductEditing deleteUploadFiles target file: "+file);
		    		Files.delete(file);
		    	}

		    	Files.delete(dir);
		        return Files.exists(dir);
		    }
		    catch (IOException e) {
		    	e.printStackTrace();
		    	return false;
			}
		}
		return true;
	}
	
	private boolean moveUploadFiles(Path uploadDir ,Path saveDir) {
		//String uploadDir = (String)session.getAttribute(ProductContents.UploadDirectryAttrName);
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
	        public boolean accept(Path file) throws IOException {
	            //return (Files.size(file) > 8192L);
	        	//enumにアップロードするcontentsNameを定義してそれをもとにフィルタするか
	       	 return true;
	        }
	    };	
		
		//save content
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadDir, filter)){
				for(Path file:stream) {
					Path target = saveDir.resolve(file.getFileName());
					if(!Files.exists(target))
						Files.createFile(target);
					
					Files.move(file, target,StandardCopyOption.REPLACE_EXISTING);
					System.out.println("ProductEditing moveUploadFiles source : "+file);
					System.out.println("ProductEditing moveUploadFiles target : "+target);
				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
	}

	protected boolean isAddMode() {
		return isAddMode;
	}

	protected void setAddMode() {
		this.isAddMode = true;
	}
	
	protected void setEditMode(HttpServletRequest request, ProductBean target) {
		this.isAddMode = false;
		request.setAttribute("product", target);
	}
}
