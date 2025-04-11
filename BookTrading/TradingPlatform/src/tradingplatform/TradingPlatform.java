package tradingplatform;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class BuyerAgent{
	// Creation of buyer agent object
	private String name;
	private String targetBook = "";
	private SellerAgent bestSeller = null;
	private Double bestPrice = null;
	
	public BuyerAgent(String name, String book){
		// The initialisation of buyer agent
		this.name = name;
		this.targetBook = book;
		System.out.println("Hello! Buyer \"" + this.name + "\" is ready.");
	}
	
	public String toString() {
		// Return the name of the object when being called
		return this.name;
	}
	
	public String getBook() {
		// Return the title of the buyer's target book
		return this.targetBook;
	}
	
	public Boolean request(List<SellerAgent> sellers) {
		// Send requests to seller agents in the market for price offer
		if (sellers.size() > 0) {
			System.out.println("Buyer \"" + this.name + "\" is trying to buy the book \"" + targetBook + "\".\n");
			for(SellerAgent seller: sellers) {
				seller.offerPrice(this, this.targetBook);
			}
			if (bestPrice != null) {
				System.out.println("\nSeller \"" + bestSeller + "\" offered the best price of £" + bestPrice + ".");
				purchaseOrder(bestSeller);
				return true;
			}else {
				System.out.println("\nThe target book is not available in the market.");
				System.out.println("Action from buyer \"" + this.name + "\" is now on hold.");
				return false;
			}
		}else {
			System.out.println("There is no seller agent in the market.");
			System.out.println("Action from buyer \"" + this.name + "\" is now on hold.");
			return false;
		}
	}
	
	public void receiveQuote(SellerAgent seller, Double price) {
		// Compare the received price offers and find out the best offer
		if (bestSeller == null) {
			bestSeller = seller;
			bestPrice = price;
		}else {
			if (price < bestPrice) {
				bestSeller = seller;
				bestPrice = price;
			}
		}
	}
	
	public void purchaseOrder(SellerAgent bestSeller) {
		// Send purchase order to the seller agent offering the best offer
		TradingPlatform.pageBreak();
		System.out.println("Buyer \"" + this.name + "\" issues a purchase order to seller \"" + bestSeller + "\".");
		Boolean state = bestSeller.receiveOrder(this, targetBook);
		if (state == true) {
			TradingPlatform.deleteBuyer(this);
		}
	}
}

class SellerAgent{
	// Creation of seller agent object
	private String name;
	private Map<String, Double> catalogue = new HashMap<>();
	
	public SellerAgent(String name, String book, Double price){
		// The initialisation of seller agent
		this.name = name;
		this.catalogue.put(book, price);
	}
	
	public void addBook(String book, Double price) {
		// Adding book to the catalogue
		this.catalogue.put(book, price);
	}
	
	public String toString() {
		// Return the name of the object when being called
		return this.name;
	}
	
	public Map<String, Double> getCatalogue() {
		// Return the catalogue of the seller
		return this.catalogue;
	}
	
	public void offerPrice(BuyerAgent buyer, String book) {
		// Response to buyer's request
		if (catalogue.containsKey(book)) {
			Double price = catalogue.get(book);
			buyer.receiveQuote(this, price);
			System.out.println("Seller \"" + this.name + "\" offers the price of £" + price + ".");
		}else {
			System.out.println("Seller \"" + this.name + "\" does not have the book.");
		}
	}
	
	public Boolean receiveOrder(BuyerAgent buyer, String book) {
		// Response to buyer's purchase order
		System.out.println("Seller \"" + this.name + "\" received the purchase order of book \"" + book + "\" from buyer \"" + buyer + "\".");
		System.out.println("The book \"" + book + "\" is delivered to buyer \"" + buyer + "\".");
		this.catalogue.remove(book);
		if (this.catalogue.size() <= 0) {
			TradingPlatform.deleteSeller(this);
		}
		return true;
	}
}

public class TradingPlatform {
	// Creation of trading platform
	private static boolean state = true;
	private static int page = 0;
	private static List<BuyerAgent> buyers = new ArrayList<>();
	private static List<SellerAgent> sellers = new ArrayList<>();
	static Scanner scanner = new Scanner(System.in);
		
	public static void pageBreak(){
		// Display a page break line on the UI
		System.out.println("\n----------------------------------------------------------\n");
    }
	
	public static int menuPage(){
		// The menu page
		System.out.println("Welcome to the Book Trading Simulation!\n");
        System.out.println("Action Menu:");
        System.out.println("1. Create an Agent");
        System.out.println("2. Add Book in Seller's Catalogue");
        System.out.println("3. View the Market Detail");
        System.out.println("4. Exit\n");
        System.out.print("Please select an action: ");
        int nextPage = 0;
        try {
        	nextPage = scanner.nextInt();
        	if (nextPage < 1 | nextPage > 4){
        		throw new Exception();
        	}else if (nextPage == 4){
        		state = false;
        	}
    		scanner.nextLine();
        }catch (Exception e){
        	scanner.nextLine();
        	pageBreak();
        	System.out.println("There is an error of the input.");
        	System.out.println("Please try again.");
        	nextPage = 0;
        }finally {
        	pageBreak();  
        }
        return nextPage;
    }
	
	public static int createPage(){
		// The page for choosing which agent to be created
        System.out.println("Agent Type:");
        System.out.println("1. Buyer Agent");
        System.out.println("2. Seller Agent\n");
        System.out.print("Please select which agent to create\n(Enter '0' to go back previous page): ");
        int nextPage = 0;
        try {
        	nextPage = scanner.nextInt();
        	if (nextPage < 0 | nextPage > 2) {
        		throw new Exception();
        	}else if (nextPage == 1){
           		nextPage = 11;
    		}else if (nextPage == 2){
    			nextPage = 12;
    		}else if (nextPage == 0){
    			nextPage = 0;
    		}
        }catch (Exception e){
        	pageBreak();
        	System.out.println("There is an error of the input.");
        	System.out.println("Please try again.");
        	nextPage = 1;
        }finally {
        	scanner.nextLine();
        	pageBreak();  
        }
        return nextPage;
    }
	
	public static int createbuyer(){
		// The page to create the buyer agent
		int nextPage = 0;
        System.out.print("Please enter the buyer name\n(Enter '0' to go back previous page): ");
        String name = scanner.nextLine();
        if (name.equals("0")) {
        	pageBreak();
        	nextPage = 1;
        	return nextPage;
        }
        BuyerAgent buyerToCreate = null;
        for (BuyerAgent buyer : buyers) {
            if (buyer.toString().toLowerCase().equals(name.toLowerCase())) {
                buyerToCreate = buyer;
                break;
            }
        }
        if (buyerToCreate != null) {
        	pageBreak();
            System.out.println("Buyer already existed.");
        }else {
	        System.out.print("\nPlease enter the title of the book\n(Enter '0' to restart): ");
	        String title = scanner.nextLine();
	        pageBreak();
	        if (title.equals("0")) {
	        	nextPage = 11;
	            return nextPage;
	        }
	        BuyerAgent newBuyer = new BuyerAgent(name, title);
	        buyers.add(newBuyer);
	        newBuyer.request(sellers);
        }
        nextPage = 0;
		pageBreak();
        return nextPage;
    }
	
	public static int createSeller(){
		// The page to create seller agent
		int nextPage = 0;
        System.out.print("Please enter the seller name\n(Enter '0' to go back previous page): ");
        String name = scanner.nextLine();
        if (name.equals("0")) {
        	pageBreak();
        	nextPage = 1;
        	return nextPage;
        }
        SellerAgent sellerToCreate = null;
        for (SellerAgent seller : sellers) {
            if (seller.toString().toLowerCase().equals(name.toLowerCase())) {
                sellerToCreate = seller;
                break;
            }
        }
        if (sellerToCreate != null) {
        	pageBreak();
            System.out.println("Seller already existed.");
        }else {
        	System.out.print("\nPlease enter the title of the book\n(Enter '0' to restart): ");
            String title = scanner.nextLine();
            if (title.equals("0")) {
            	pageBreak();
	        	nextPage = 12;
	            return nextPage;
	        }
            System.out.print("\nPlease enter the price of the book\n(Enter '0' to restart): ");
            try {
            	Double price = scanner.nextDouble();
            	if (price == 0) {
    	        	pageBreak();
    	        	nextPage = 2;
    	            return nextPage;
    		    }
                SellerAgent newSeller = new SellerAgent(name, title, price);
                sellers.add(newSeller);
                List<SellerAgent> newList = new ArrayList<>();
                newList.add(newSeller);
                for (BuyerAgent buyer : buyers) {
                	pageBreak();
                	Boolean state = buyer.request(newList);
                	if (state == true) {
                		break;
                	}
                }
            }catch (Exception e){
            	scanner.nextLine();
            	pageBreak();
            	System.out.println("There is an error of the input.");
            	System.out.println("Please try again.");
            	nextPage = 12;
            	return nextPage;
            }finally {
            	pageBreak();  
            }
            
        }
        nextPage = 0;
        return nextPage;
    }
	
	public static int updateSeller(){
		// The page to update seller's catalogue
        SellerAgent sellerToUpdate = null;
        int nextPage = 0;
		System.out.print("Please enter the seller name\n(Enter '0' to go back previous page): ");
        String name = scanner.nextLine();
        if (name.equals("0")) {
        	pageBreak();
        	nextPage = 0;
        	return nextPage;
        }
    	for (SellerAgent seller : sellers) {
            if (seller.toString().equals(name)) {
                sellerToUpdate = seller;
                break;
            }
        }
        if (sellerToUpdate == null) {
        	pageBreak();
            System.out.println("Seller not found.");
            pageBreak();
            nextPage = 2;
            return nextPage;
        }
		System.out.print("\nPlease enter the title of the book\n(Enter '0' to restart): ");
        String title = scanner.nextLine();
        if (title.equals("0")) {
        	pageBreak();
        	nextPage = 2;
            return nextPage;
        }
		System.out.print("\nPlease enter the price of the book\n(Enter '0' to restart): ");
		try{
			Double price = scanner.nextDouble();
			if (price == 0) {
	        	pageBreak();
	        	nextPage = 2;
	            return nextPage;
		    }
			sellerToUpdate.addBook(title, price);
	        List<SellerAgent> newList = new ArrayList<>();
	        newList.add(sellerToUpdate);
	        for (BuyerAgent buyer : buyers) {
	        	pageBreak();
	        	Boolean state = buyer.request(newList);
	        	if (state == true) {
	        		break;
	        	}
	        }
		}catch (Exception e){
        	scanner.nextLine();
        	pageBreak();
        	System.out.println("There is an error of the input.");
        	System.out.println("Please try again.");
        	nextPage = 2;
        	return nextPage;
        }finally {
        	pageBreak();  
        }
		
        nextPage = 0;
        return nextPage;
    }
	
	public static int infoPage(){
		// The page to display the market information
        System.out.println("  Buyer\t\tBook");
        System.out.println("---------------------------------");
        for (int i = 0; i < buyers.size(); i++) {
        	if (i > 0 & i < buyers.size()) {
        		System.out.println("+...............................+");
        	}
        	BuyerAgent buyer = buyers.get(i);
        	String book = buyer.getBook();
        	System.out.println("+ " + buyer + "\t" + book + "\t\t+");
        }
        if (buyers.size() == 0) {
        	System.out.println("+\t\t\t\t+");
        }
        System.out.println("---------------------------------");
        System.out.println("\n  Seller\tBook\t\tPrice(£)");
        System.out.println("-----------------------------------------");
        for (int i = 0; i < sellers.size(); i++) {
        	if (i > 0 & i < sellers.size()) {
        		System.out.println("+.......................................+");
        	}
        	SellerAgent seller = sellers.get(i);
        	Map<String, Double> catalogue = seller.getCatalogue();
        	if (catalogue.size() > 0) {
        		boolean firstRow = true;
            	for (Entry<String, Double> entry : catalogue.entrySet()) {
            		if (firstRow) {
            			System.out.println("+ " + seller + "\t" + entry.getKey() + "\t\t" + entry.getValue() + "\t+");
            			firstRow = false;
            		}else {
            			System.out.println("+ \t\t" + entry.getKey()+ "\t\t" + entry.getValue()+ "\t+");
            		}
    			}
        	}else {
        		System.out.println(seller);
        	}
        }
        if (sellers.size() == 0) {
        	System.out.println("+\t\t\t\t\t+");
        }
        System.out.println("-----------------------------------------");
        System.out.print("\nEnter \"0\" to go back: ");
        int nextPage = 0;
        try{
        	nextPage = scanner.nextInt();
        	if (nextPage != 0) {
        		nextPage = 0;
        	}
        }catch (Exception e){
        	scanner.nextLine();
        	pageBreak();
        	System.out.println("There is an error of the input.");
        	System.out.println("Entering Menu Page...");
        	nextPage = 0;
        }finally {
        	pageBreak();  
        }
        return nextPage;
    }
	
	public static void deleteBuyer(BuyerAgent buyer) {
		// Delete buyer agent when the purchase is completed
		buyers.remove(buyer);
		pageBreak();
		System.out.println("Buyer \"" + buyer + "\" has completed the purchase.");
		System.out.println("Buyer \"" + buyer + "\" is terminated.");
	}
	
	public static void deleteSeller(SellerAgent seller) {
		// Delete the seller agent when all books in the catalogue are sold out
		sellers.remove(seller);
		pageBreak();
		System.out.println("Seller \"" + seller + "\" has sold out all the books in the catalogue.");
		System.out.println("Seller \"" + seller + "\" is terminated.");
	}
		
    public static void main(String[] args) {
    	// The loop to display which page on the UI
        pageBreak();
        while (state == true) {
        	switch (page) {
        	case 0:
        		page = menuPage();
        		break;
        	case 1:
        		page = createPage();
        		break;
        	case 2:
        		page = updateSeller();
        		break;
        	case 3:
        		page = infoPage();
        		break;
        	case 11:
        		page = createbuyer();
        		break;
        	case 12:
        		page = createSeller();
        		break;
        	}
        }
        System.out.println("Goodbye!");
        scanner.close();
    }
}