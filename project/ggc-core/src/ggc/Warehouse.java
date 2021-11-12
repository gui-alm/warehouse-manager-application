package ggc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import ggc.exceptions.BadEntryException;
import ggc.exceptions.DuplicatePartnerKeyExceptionCore;
import ggc.exceptions.InvalidDateExceptionCore;
import ggc.exceptions.UnavailableProductExceptionCore;
import ggc.exceptions.UnknownPartnerKeyExceptionCore;
import ggc.exceptions.UnknownProductKeyExceptionCore;
import ggc.exceptions.UnknownTransactionKeyExceptionCore;
import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.ComplexProduct;
import ggc.products.Product;
import ggc.products.Recipe;
import ggc.products.RecipeComponent;
import ggc.transactions.Acquisition;
import ggc.transactions.Breakdown;
import ggc.transactions.Sale;
import ggc.transactions.Transaction;
import ggc.utils.BatchComparator;
import ggc.utils.BatchComparatorPrice;
import ggc.utils.Observer;
import ggc.utils.ProductComparator;
import ggc.utils.Visitor;

/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable, Visitor {

	private int _date = 0;
	private double _availableBalance = 0;
	private double _accountingBalance = 0;
	private int _transactionsIndex = 0;

	/** Serial number for serialization. */
	private static final long serialVersionUID = 202109192006L;

	// A TreeMap is used so the IDs are inserted by alphabetical order.
	private TreeMap<String, Partner> _partners = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	private TreeMap<String, ArrayList<Batch>> _batchesByProduct = new TreeMap<>();
	private TreeMap<String, ArrayList<Batch>> _batchesByPartner = new TreeMap<>();

	private TreeMap<String, Product> _products = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  
	private List<Batch> _batches = new ArrayList<>();

	private HashMap<Integer, Transaction> _transactions = new HashMap<>();

	/**
	 * Used to check the type of a Transaction class (Acquisition, Sale or Breakdown).
	 */
	public String visit(Acquisition acquisition) {
		return "ACQUISITION";
	}
	
	/**
	 * Used to check the type of a Transaction class (Acquisition, Sale or Breakdown).
	 */
	public String visit(Sale sale) {
		return "SALE";
	}

	/**
	 * Used to check the type of a Transaction class (Acquisition, Sale or Breakdown).
	 */
	public String visit(Breakdown breakdown){
		return "BREAKDOWN";
	}

	/**
	 * Imports data from a file.
	 * 
   	 * @param txtfile filename to be loaded.
   	 * @throws IOException
   	 * @throws BadEntryException
   	*/
  	void importFile(String txtfile) throws IOException, BadEntryException {
    	try (BufferedReader in = new BufferedReader(new FileReader(txtfile))) {
			String s;
			while ((s = in.readLine()) != null) {
			  String line = new String(s.getBytes(), "UTF-8");
	  
			  String[] fields = line.split("\\|");
			  switch (fields[0]) {
			  case "PARTNER":
				try {
					registerPartner(fields[1], fields[2], fields[3]);
				} catch (DuplicatePartnerKeyExceptionCore e) {
					e.printStackTrace();
				}
				break;
			  case "BATCH_S": 
			  	registerBatch(fields[1], fields[2], 
			  	Double.parseDouble(fields[3]), Double.parseDouble(fields[4]));
				break;
			  case "BATCH_M": 
			  	registerBatch(fields[1], fields[2], 
			  	Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), 
			  	Double.parseDouble(fields[5]), fields[6]);
			  	break;
			  default: 
			  	throw new BadEntryException(fields[0]);
			  }
			}
		  } catch (FileNotFoundException e) {
			e.printStackTrace();
		  } catch (IOException e) {
			e.printStackTrace();
		  } catch (BadEntryException e) {
			e.printStackTrace();
		  }
  	}
	
	/**
	 * @return the warehouse's current date.
	 */
  	public int getDate(){
    	return _date;
  	}

	/**
     * @return the warehouse's current available balance
     */
	public double getAvailableBalance(){
		return _availableBalance;
	}


	/**
	 * Updates the accounting balance with the unpaid sales value.
	 * 
     * @return the warehouse's current accounting balance
     */
	public double getAccountingBalance(){
		for(Transaction t : _transactions.values()){
			if(t.accept(this) == "SALE"){
				if(!t.isPaid()){
					_accountingBalance -= ((Sale) t).getPriceToPay();
					t.calculatePrice(getDate(), _partners.get(t.getPartnerID()).state());
					_accountingBalance += ((Sale) t).getPriceToPay();
				}
			}

		}
		return _accountingBalance;
	}

	/**
     * Adds the given amount to the warehouse's current available balance.
     * 
     * @param amount
     */
	public void addAvailableBalance(double amount){
		_availableBalance += amount;
	}

	/**
     * Adds the given amount to the warehouse's current accounting balance.
     * 
     * @param amount
     */
	public void addAccountingBalance(double amount){
		_accountingBalance += amount;
	}

	/**
     * @return the transactions index
     */
	public int getTransactionsIndex(){
		return _transactionsIndex;
	}

	/**
     * Increases the value of the variable that controls transactions indexes.
     */
	private void increaseTransactionsIndex() {
		_transactionsIndex++;
	}

	/**
	 * Changes the value of the warehouse's date.
	 * 
	 * @param date the warehouse's current date
	 * @throws InvalidDateExceptionCore
	 */
  	public void setDate(int date) throws InvalidDateExceptionCore {
    	if(date < 0){
    		throw new InvalidDateExceptionCore(); 
    	} else {
      		_date = date; 
    	}
 	}

	/**
	 * Advances the date by the given amount.
	 * 
	 * @param amount the amount to advance the date by
	 * @throws InvalidDateExceptionCore if the amount to advance is zero or negative 
	 */
  	public void advanceDate(int amount) throws InvalidDateExceptionCore { 
    	if(amount <= 0){
      		throw new InvalidDateExceptionCore();
    	} else {
      		setDate(_date + amount);
    	}
  	}
	

	/**
	 * Creates a partner and stores it.
	 * 
	 * @param id the partner's id
	 * @param name the partner's name
	 * @param address the partner's address
	 * @throws DuplicatePartnerKeyExceptionCore if a partner with the 
	 *    given id already exists
	 */  
  	public void registerPartner(String id, String name, String address) 
  	throws DuplicatePartnerKeyExceptionCore {

    	if(_partners.keySet().contains(id)){
      		throw new DuplicatePartnerKeyExceptionCore();
    	}
    
    	Partner p = new Partner(id, name, address);
    	_partners.put(id, p);
 	}

	/**
	 * Searches the partner storage for a specified partner.
	 * 
	 * @param id the partner's id 
	 * @return a string with the attributes of the partner
	 * @throws UnknownPartnerKeyExceptionCore when there isn't a partner
	 *    with the given id
	 */
	public String getPartner(String id) 
  	throws UnknownPartnerKeyExceptionCore {
    	if(!_partners.keySet().contains(id)){
      		throw new UnknownPartnerKeyExceptionCore();
		}
    	return _partners.get(id).buildAttributesString();
  	}

	/**
	 * @return a list with every partner's attributes string
	 */
  	public ArrayList<String> getAllPartners(){
    	ArrayList<String> result = new ArrayList<>();

    	_partners.values().stream()
    	.forEach(o->{result.add(o.buildAttributesString());});

    	return result;
	}

	/**
	 * Auxiliary function. Used to add a batch to a list of batches
	 *    of the same product. 
	 * 
	 * @param batch the batch to be added
	 */
	private void addBatchByProduct(Batch batch){
		if(_batchesByProduct.keySet().contains(batch.getProductID())){
			this._batchesByProduct.get(batch.getProductID()).add(batch);
		} else {
			this._batchesByProduct.put(batch.getProductID(), new ArrayList<Batch>());
			addBatchByProduct(batch);
		}
	}

	/**
	 * Auxiliary function. Used to add a batch to a list of batches
	 *    owned by the same partner.
	 * 
	 * @param batch the batch to be added
	 */
	private void addBatchByPartner(Batch batch){
		if(_batchesByPartner.keySet().contains(batch.getPartnerID())){
			this._batchesByPartner.get(batch.getPartnerID()).add(batch);
		} else {
			this._batchesByPartner.put(batch.getPartnerID(), new ArrayList<Batch>());
			addBatchByPartner(batch);
		}
	}

	/**
     * Gets the lowest price of all the batches of a specified product. If no batch
	 * 	  of the given product exists, the biggest price in the history of the product
	 *    is returned.
     * 
     * @param productID
     * @return the lowest price 
     */
	public double getLowestPrice(String productID) {
		List<Double> prices = new ArrayList<>();

		if(_batchesByProduct.get(productID).isEmpty()){
			return _products.get(productID).getPrice();
		}

		for(Batch b : _batchesByProduct.get(productID)){
			prices.add(b.getPrice());
		}

		return Collections.min(prices);
	}

	/**
     * Gets the batch with the lowest price of a specified product.
     * 
     * @param productID
     * @return the batch with the lowest price
     */
	public Batch getLowestPriceBatch(String productID){
		double lowest = getLowestPrice(productID);

		for(Batch b : _batchesByProduct.get(productID)){
			if(b.getPrice() == lowest){
				return b;
			}
		}

		// code is never reached
		return null;
	}

	/**
	 * Auxiliary function. Used to update the product storage when 
	 *    a new simple batch is registered.
	 * 
	 * @param productID the product's id
	 * @param partnerID the partner's id
	 * @param price the price of the product
	 * @param stock the stock of the product
	 */
	private void addProductFromBatch(String productID, String partnerID, 
	double price, double stock) {
		if(!_products.keySet().contains(productID)){
			Product newP = new Product(productID, partnerID, price, stock);
			for(Partner partner : _partners.values()){
				newP.registerObserver(partner);
			}
			this._products.put(productID, newP);
		} else {
			_products.get(productID).addStock(stock);
			double currPrice = _products.get(productID).getPrice();
			if(currPrice < price) 
				_products.get(productID).setPrice(price);

			if(price < getLowestPrice(productID))
				_products.get(productID).notifyObserver("BARGAIN", price);
		}
	}

	/**
	 * Auxiliary function. Used to update the product storage when 
	 *    a new complex batch is registered.
	 * 
	 * @param productID the product's id
	 * @param partnerID the partner's id
	 * @param price the price of the product
	 * @param stock the stock of the product
	 */
	private void addProductFromBatch(String productID, String partnerID, 
	double price, double stock, double aggravation, String recipe) {
		if(!_products.keySet().contains(productID)){
			Recipe newRecipe = new Recipe(recipe);
			Product newP = new ComplexProduct(productID, partnerID, price, 
			stock, aggravation, newRecipe);
			for(Partner partner : _partners.values()){
				newP.registerObserver(partner);
			}
			this._products.put(productID, newP);
		} else {
			_products.get(productID).addStock(stock);
			double currPrice = _products.get(productID).getPrice();
			if(currPrice < price) 
				_products.get(productID).setPrice(price);

			if(price < getLowestPrice(productID))
				_products.get(productID).notifyObserver("BARGAIN", price);
		}
	}

	/**
	 * Creates a new simple batch and stores it. Also updates the storage
	 *    of products.
	 * 
	 * @param productID the product of the batch
	 * @param partnerID the id of the supplier of the batch
	 * @param price the price of the batch
	 * @param stock the amount of products in the batch
	 */
	public void registerBatch(String productID, String partnerID, 
	double price, double stock){
		Batch newBatch = new Batch(productID, partnerID, price, stock);

		addProductFromBatch(productID, partnerID, price, stock);

		_batches.add(newBatch);
		addBatchByPartner(newBatch);
		addBatchByProduct(newBatch);
		Collections.sort(_batches, new BatchComparator());
		Collections.sort(_batches, new BatchComparatorPrice());
	}

	/**
	 * Creates a new complex batch and stores it. Also updates the storage
	 *    of products.
	 * 
	 * @param productID the product of the batch
	 * @param partnerID the id of the supplier of the batch
	 * @param price the price of the batch
	 * @param stock the amount of products in the batch
	 */
	public void registerBatch(String productID, String partnerID, 
	double price, double stock, double aggravation, String recipe){
		Batch newBatch = new Batch(productID, partnerID, price, stock);

		addProductFromBatch(productID, partnerID, price, stock, 
		aggravation, recipe);

		_batches.add(newBatch);
		addBatchByPartner(newBatch);
		addBatchByProduct(newBatch);
		Collections.sort(_batches, new BatchComparator());
		Collections.sort(_batches, new BatchComparatorPrice());
	}


	/**
	 * @return the list of batches
	 */
	public List<Batch> getAvailableBatches(){
		return _batches;
	}


	/**
	 * @return a sorted list of every existing product
	 */
	public List<Product> getAllProducts(){
		ArrayList<Product> _result = new ArrayList<>(_products.values());
		Collections.sort(_result, new ProductComparator());
		return _result;
	}

	/**
     * Searches for the all the batches by a specified partner.
     * 
     * @param partner
     * @return a list of batches by a specified partner
     * @throws UnknownPartnerKeyExceptionCore
     */
	public List<Batch> getBatchesByPartner(String partner)
	throws UnknownPartnerKeyExceptionCore{
		ArrayList<Batch> result;
		if(_partners.keySet().contains(partner)){
			result = _batchesByPartner.get(partner);
			if (result == null)
				return result;
			Collections.sort(result, new BatchComparator());
			Collections.sort(result, new BatchComparatorPrice());
			return result;
		} else {
			throw new UnknownPartnerKeyExceptionCore();
		} 
	}

	/**
     * Searches for all the batches of a specified product.
     * 
     * @param product the product
     * @return a list of batches of a specified product
     * @throws UnknownProductKeyExceptionCore when the product doesn't exist
     */
	public List<Batch> getBatchesByProduct(String product) 
	throws UnknownProductKeyExceptionCore{
		ArrayList<Batch> result;
		if(_products.keySet().contains(product)){
			result = _batchesByProduct.get(product);
			if (result == null)
				return result;
			Collections.sort(result, new BatchComparator());
			Collections.sort(result, new BatchComparatorPrice());
			return result;
		} else {
			throw new UnknownProductKeyExceptionCore();
		} 
	}

	/**
     * Receives a partner and product key and toggles the partner's notifications 
     *    for the product on or off depending on its current state.
     * 
     * @param productID
     * @param partnerID
     * @throws UnknownProductKeyExceptionCore when the product doesn't exist
     * @throws UnknownPartnerKeyExceptionCore when the partner doesn't exist
     */
	public void toggleProductNotifications(String productID, String partnerID) 
	throws UnknownProductKeyExceptionCore, UnknownPartnerKeyExceptionCore{
		if (!_products.keySet().contains(productID)){
			throw new UnknownProductKeyExceptionCore();
		}

		if(!_partners.keySet().contains(partnerID)){
			throw new UnknownPartnerKeyExceptionCore();
		}

		Observer partner = _partners.get(partnerID);
		Product product = _products.get(productID);

		if(!product.hasObserver(partner)){
			product.registerObserver(partner);
		} else {
			product.unregisterObserver(partner);
		}
	}

	/** 
     * Gets a list with all the notifications of a specified partner.
     * @param partnerID the partner
     * @return list with all the notifications of a specified partner
     */
	public List<String> getPartnerNotifications(String partnerID){
		return _partners.get(partnerID).getNotifications();
	}

	/**
     * Clears the notifications list of a specified partner.
     * @param partnerID the partner
     */
	public void clearPartnerNotifications(String partnerID){
		_partners.get(partnerID).clearNotifications();
	}

	/**
	 * @param productName the name of the product
	 * @return true if a product with the given name exists
	 */
	public boolean isKnown(String productName){
		return _products.keySet().contains(productName);
	}


	/**
     * Registers a new acquisition for known products (complex or simple) and for 
     *    unknown simple products.
     * 
     * @param partnerID the partner's id
     * @param productID the product's id
     * @param price the product's price
     * @param amount the product's amount
     * @throws UnknownPartnerKeyExceptionCore when the partner doesn't exist
     */
	public void registerAcquisition(String partnerID, String productID, double price, int amount) 
	throws UnknownPartnerKeyExceptionCore {

		if(!this._partners.keySet().contains(partnerID)){
			throw new UnknownPartnerKeyExceptionCore();
		}

		Transaction newAcquisition = new Acquisition(getTransactionsIndex(), 
		partnerID, productID, amount, price * amount, getDate());
		_transactions.put(getTransactionsIndex(), newAcquisition);
		increaseTransactionsIndex();

		registerBatch(productID, partnerID, price, amount);
		addAvailableBalance(-(price * amount));
		addAccountingBalance(-(price * amount));
		_partners.get(partnerID).addPurchasesValue(price * amount);
	}

	/**
     * Registers a new acquisition of an uknown complex product.
     * 
     * @param partnerID the partner's id
     * @param productID the product's id
     * @param price the product's price
     * @param amount the product's amount
     * @param recipe the product's recipe
     * @param aggravation the product's aggravation
     * @throws UnknownPartnerKeyExceptionCore when the partner doesn't exist
     * @throws UnknownProductKeyExceptionCore when the product doesn't exist
     */
	public void registerAcquisition(String partnerID, String productID, 
	double price, int amount, LinkedHashMap<String, Integer> recipe, double aggravation) 
	throws UnknownPartnerKeyExceptionCore, UnknownProductKeyExceptionCore {
		
		if(!this._partners.keySet().contains(partnerID)){
			throw new UnknownPartnerKeyExceptionCore();
		}

		Recipe newRecipe = new Recipe(recipe);
		for(RecipeComponent rc : newRecipe.components()){
			if(!productExists(rc.getProduct()))
				throw new UnknownProductKeyExceptionCore(rc.getProduct());
		}
		
		Transaction newAcquisition = new Acquisition(getTransactionsIndex(), 
		partnerID, productID, amount, price * amount, getDate());
		_transactions.put(getTransactionsIndex(), newAcquisition);
		increaseTransactionsIndex();

		registerBatch(productID, partnerID, price, amount, aggravation, newRecipe.toString());
		addAvailableBalance(-(price * amount));
		addAccountingBalance(-(price * amount));
		_partners.get(partnerID).addPurchasesValue(price * amount);
	}

	/**
     * Checks if there are enough components to produce the given amount of 
     *    the complex product.
     * 
     * @param productID the product's id
     * @param amount the product's amount
     * @return true if it is possible to produce the product, false if otherwise
     * @throws UnavailableProductExceptionCore when the product isn't enough
     */
	public boolean checkComplexProductAmount(String productID, int amount) 
	throws UnavailableProductExceptionCore{

        Product product = _products.get(productID);
        Recipe recipe = product.getRecipe();

        amount -= _products.get(productID).getStock();

        for(RecipeComponent rc : recipe.components()){
            Product p = _products.get(rc.getProduct());
            int amountNeeded = rc.getQuantity() * amount;
            
            if(!p.isComplex()){
                if(p.getStock() < amountNeeded) {
                    throw new UnavailableProductExceptionCore(p.getID(), 
					amountNeeded, (int) Math.round(p.getStock()));
                }
            } else {
                checkComplexProductAmount(rc.getProduct(), amountNeeded);
            }
        }

        return true;
    }

	/**
	 * Removes the given batch from the system.
	 * 
	 * @param batch
	 */
	public void removeBatch(Batch batch){
		_batches.remove(batch);
		_batchesByPartner.get(batch.getPartnerID()).remove(batch);
		_batchesByProduct.get(batch.getProductID()).remove(batch);
	}

	/**
	 * Sets product's stock to zero if it is negative.
	 * 
	 * @param product
	 */
	public void correctProductStock(Product product){
		if(product.getStock() < 0){
			product.setStock(0);
		}
	}

	/**
     * Determines the price of the product's sale and removes the amount 
     *    from the system. 
     * 
     * @param productID the product's ID
     * @param amount the product's amount
     * @param price the product's price
     * @return the total price of the product's removal
     * @throws UnavailableProductExceptionCore when there is no product stock
     */
	public double determinePrice(String productID, int amount, double price) 
	throws UnavailableProductExceptionCore {
        int amountStillNeeded = amount;

        double totalPrice = price;

        if(_products.get(productID).getStock() <= 0){
            throw new UnavailableProductExceptionCore(productID, amount, 0);
        }

        Batch lowest = getLowestPriceBatch(productID);

        if(lowest.getStock() >= amount){
            amountStillNeeded = 0;
            totalPrice += (amount * lowest.getPrice());
            _products.get(productID).addStock(-(amount));
            correctProductStock(_products.get(productID));
            lowest.addStock(-(amount));
            if(lowest.getStock() == 0){
                removeBatch(lowest);
            }
        } else {
            amountStillNeeded -= lowest.getStock();
            totalPrice += (lowest.getStock() * lowest.getPrice());
            _products.get(productID).addStock(-(lowest.getStock()));
            correctProductStock(_products.get(productID));
            lowest.addStock(-(lowest.getStock()));
            removeBatch(lowest);
            totalPrice = determinePrice(productID, amountStillNeeded, totalPrice);
        }

        return totalPrice;
    }
	
	/**
     * Creates the needed amount of product using its components and determines the 
     *    price of the aggregation.
     * 
     * @param productID the product's ID
     * @param amount the product's amount
     * @param price the product's price
     * @return the total price of the product's production
     * @throws UnavailableProductExceptionCore when the components aren't enough
     */
	public double makeComplexProduct(String productID, int amount, double price) 
	throws UnavailableProductExceptionCore{
        ComplexProduct product = (ComplexProduct) _products.get(productID);
        Recipe recipe = product.getRecipe();
		double maxPrice = 0; 

        amount -= _products.get(productID).getStock();
        double totalPrice = determinePrice(productID, 
		(int) _products.get(productID).getStock(), price);

        for(RecipeComponent rc : recipe.components()){
            
            Product p = _products.get(rc.getProduct());
			maxPrice += getLowestPrice(p.getID()) * rc.getQuantity();
            int amountNeeded = rc.getQuantity() * amount;
            
            if(!p.isComplex()){
				totalPrice += (1 + product.getAggravation()) * 
				determinePrice(rc.getProduct(), amountNeeded, 0);
            } else {
                if(p.getStock()>= amountNeeded){
                    totalPrice += determinePrice(rc.getProduct(),amountNeeded, 0);
                } else {
                    totalPrice = (1 + product.getAggravation()) * 
					makeComplexProduct(rc.getProduct(),amountNeeded, totalPrice);
                }
            }
        }

		maxPrice *= (1 + product.getAggravation());

        if(_products.get(productID).getPrice() < maxPrice) {
            _products.get(productID).setPrice(maxPrice);
        }

        return totalPrice;
    }


	/**
     * Registers a new sale to the system.
     * 
     * @param partnerID the partner's ID
     * @param productID the product's ID
     * @param deadline the sale's deadline
     * @param amount the amount of products sold
     * @throws UnavailableProductExceptionCore when the product isn't enough
     * @throws UnknownProductKeyExceptionCore when the product doesn't exist
     * @throws UnknownPartnerKeyExceptionCore when the partner doesn't exist
     */
	public void registerSale(String partnerID, String productID, int deadline, int amount)
    throws UnavailableProductExceptionCore, UnknownProductKeyExceptionCore, UnknownPartnerKeyExceptionCore {

        Product product = _products.get(productID);
        Partner partner = _partners.get(partnerID);

        if(product == null){
            throw new UnknownProductKeyExceptionCore();
        }

        if(partner == null){
            throw new UnknownPartnerKeyExceptionCore();
        }

        if(!product.isComplex()){

            if(product.getStock() >= amount){
                double basePrice = determinePrice(productID, amount, 0);
                Sale newSale = new Sale(getTransactionsIndex(), partnerID, 
				productID, amount, deadline, basePrice, !product.isComplex());
                _transactions.put(getTransactionsIndex(), newSale);
                increaseTransactionsIndex();
                partner.addPerformedSalesValue(basePrice);
				
				newSale.calculatePrice(getDate(), partner.state());
				addAccountingBalance(newSale.getPriceToPay());
            } else {
                throw new UnavailableProductExceptionCore(productID, amount, 
				(int) Math.round(product.getStock()));
            }

        } else {
            if(checkComplexProductAmount(productID, amount)){
                double basePrice = makeComplexProduct(productID,amount,0);

                Sale newSale = new Sale(getTransactionsIndex(), partnerID, 
				productID, amount, deadline, basePrice, !product.isComplex());
                _transactions.put(getTransactionsIndex(), newSale);
                increaseTransactionsIndex();
                partner.addPerformedSalesValue(basePrice);
				
				newSale.calculatePrice(getDate(), partner.state());
				addAccountingBalance(newSale.getPriceToPay());
            }
            else {
                throw new UnavailableProductExceptionCore(productID, amount, 
				(int) Math.round(product.getStock()));
            }
        }
    }

	/**
     * Receives the payment of the given transaction.
     * 
     * @param index the index of the transaction
     * @throws UnknownTransactionKeyExceptionCore when the transaction key is invalid
     */
	public void receivePayment(int index)
	throws UnknownTransactionKeyExceptionCore {

		if(this.getTransactionsIndex() <= index || index < 0)
			throw new UnknownTransactionKeyExceptionCore();

		Transaction t = _transactions.get(index);

		if(t.accept(this) != "SALE")
			return;
		
		Partner partner = _partners.get(t.getPartnerID());
		Sale sale = (Sale) t;

		if(t.isPaid()){
			return;
		}

		double oldValue = sale.getPriceToPay();

		t.calculatePrice(getDate(), _partners.get(t.getPartnerID()).state());
		t.setPaid(getDate());

		partner.addPaidSalesValue(sale.getPriceToPay());
		partner.checkStatus(sale.getDeadline() - getDate(), sale.getPriceToPay());

		Product product = _products.get(sale.getProductID());
		product.addStock(-(sale.getAmount()));

		addAvailableBalance(sale.getPriceToPay());
		addAccountingBalance(sale.getPriceToPay() - oldValue);
	}
	
	/**
     * Gets the transaction's string representation.
     * 
     * @param index the index of the transaction
     * @return the corresponding transaction
     * @throws UnknownTransactionKeyExceptionCore when the transaction key is invalid
     */
	public String getTransaction(int index) throws UnknownTransactionKeyExceptionCore{

		if(index >= getTransactionsIndex())
			throw new UnknownTransactionKeyExceptionCore();
			
		Transaction t = _transactions.get(index);

		if(t.accept(this) == "SALE"){
			Sale sale = (Sale) t;
			Partner partner = _partners.get(sale.getPartnerID());
			sale.calculatePrice(getDate(), partner.state());
			return sale.toString();
		} 

		return t.toString();
	}

	/**
     * Gets a list of batches with the given price
     * @param price the price
     * @return Gets a list of batches with the given price
     */
	public List<Batch> getBatchesByPrice(double price){
		List<Batch> result = new ArrayList<>();

		for(Batch batch : _batches){
			if(batch.getPrice() < price){
				result.add(batch);
			}
		}

		Collections.sort(result, new BatchComparator());
		Collections.sort(result, new BatchComparatorPrice());
		return result;
	}

	/**
     * Checks if the given partner exists.
     * 
     * @param partnerID the partner's ID
     * @return true if the partner exists, false if otherwise
     */
	public boolean partnerExists(String partnerID){
		return _partners.keySet().contains(partnerID);
	}

	/**
     * Checks if the given product exists.
     * 
     * @param productID the product's ID
     * @return true if the product exists, false if otherwise
     */
	public boolean productExists(String productID){
		return _products.keySet().contains(productID);
	}

	/**
     * Registers a new breakdown on the system.
     * 
     * @param partnerID the partner's ID
     * @param productID the product's ID
     * @param amount the product's amount
     * @throws UnavailableProductExceptionCore when the amount asked for is too much
     * @throws UnknownPartnerKeyExceptionCore when the partner doesn't exist
     * @throws UnknownProductKeyExceptionCore when the product doesn't exist
     */
	public void registerBreakdown(String partnerID, String productID, int amount) 
	throws UnavailableProductExceptionCore, UnknownPartnerKeyExceptionCore, UnknownProductKeyExceptionCore {
		Product product = _products.get(productID);

		if(!partnerExists(partnerID))
			throw new UnknownPartnerKeyExceptionCore();

		if(!productExists(productID))
			throw new UnknownProductKeyExceptionCore();

		if(amount > product.getStock())
			throw new UnavailableProductExceptionCore(productID, amount, 
			(int) Math.round(product.getStock()));
		
		if(!product.isComplex())
			return;

		Breakdown newBreakdown = new Breakdown(getTransactionsIndex(), 
		partnerID, productID, amount);

		double saleValue = determinePrice(productID, amount, 0);
		newBreakdown.setSaleValue(saleValue);

		Recipe recipe = product.getRecipe();

		double componentsTotalPrice = 0;

		for(RecipeComponent rc : recipe.components()){
			componentsTotalPrice += getLowestPrice(rc.getProduct()) * 
			(rc.getQuantity() * amount);
			registerBatch(rc.getProduct(), partnerID, getLowestPrice(productID), 
			rc.getQuantity() * amount);
		}

		newBreakdown.calculateBasePrice(componentsTotalPrice);
		newBreakdown.calculatePricePaid(getDate());

		StringBuilder str = new StringBuilder();

        for(RecipeComponent rc : recipe.components()){
            str.append(String.format("%s:%s:%s#", rc.getProduct(), rc.getQuantity() * amount, 
			(int) Math.round(rc.getQuantity() * amount * getLowestPrice(rc.getProduct()))));
        }
		str.setLength(str.length() - 1);

		newBreakdown.setDescription(str.toString());

		_transactions.put(getTransactionsIndex(), newBreakdown);
		increaseTransactionsIndex();
		_partners.get(partnerID).checkStatus(0, newBreakdown.getPricePaid());
		addAvailableBalance(newBreakdown.getPricePaid());
		addAccountingBalance(newBreakdown.getPricePaid());
	}

	/**
     * Gets a list of the partner's acquistions.
     * 
     * @param partnerID the partner's ID
     * @return a list of the partner's acquistions
     * @throws UnknownPartnerKeyExceptionCore when the partner doesn't exist
     */
	public List<String> getPartnerAcquisitions(String partnerID) 
	throws UnknownPartnerKeyExceptionCore {

		if(!_partners.keySet().contains(partnerID))
			throw new UnknownPartnerKeyExceptionCore();

		List<String> result = new ArrayList<>();

		for(Transaction t : _transactions.values()){
			if(t.accept(this) == "ACQUISITION"){
				if(t.getPartnerID().equals(partnerID)){
					result.add(t.toString());
				}
			}
		}

		return result;
	}
	
	/**
     * Gets a list of the partner's sales.
     * 
     * @param partnerID the partner's ID
     * @returna list of the partner's sales
     * @throws UnknownPartnerKeyExceptionCore when the partner doesn't exist
     */
	public List<String> getPartnerSales(String partnerID) 
	throws UnknownPartnerKeyExceptionCore{
        if(!_partners.keySet().contains(partnerID))
            throw new UnknownPartnerKeyExceptionCore();
        
        List<String> result = new ArrayList<>();

        for(Transaction t : _transactions.values()){
            if(t.accept(this) == "SALE"){
				if(t.getPartnerID().equals(partnerID)){
					t.calculatePrice(getDate(), _partners.get(t.getPartnerID()).state());
					result.add(t.toString());
				}
            }

			if(t.accept(this) == "BREAKDOWN"){
				if(t.getPartnerID().equals(partnerID)){
					result.add(t.toString());
				}
			}
        }

        return result;
    }

	/**
     * Gets a list of the partner's payments.
     * 
     * @param partnerID the partner's ID
     * @return list of the partner's payments
     * @throws UnknownPartnerKeyExceptionCore when the partner doesn't exist
     */
	public List<String> getPaymentsByPartner(String partnerID) 
	throws UnknownPartnerKeyExceptionCore {

		if(!partnerExists(partnerID))
			throw new UnknownPartnerKeyExceptionCore();

		List<String> result = new ArrayList<>();

		for(Transaction t : _transactions.values()){
			if(t.accept(this) == "SALE"){
				if(t.isPaid() && t.getPartnerID().equals(partnerID)){
					result.add(t.toString());
				}
			}

		}
		return result;
	}



}
