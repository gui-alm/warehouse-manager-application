package ggc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ggc.exceptions.BadEntryException;
import ggc.exceptions.DuplicatePartnerKeyExceptionCore;
import ggc.exceptions.ImportFileException;
import ggc.exceptions.InvalidDateExceptionCore;
import ggc.exceptions.MissingFileAssociationException;
import ggc.exceptions.UnavailableFileException;
import ggc.exceptions.UnavailableProductExceptionCore;
import ggc.exceptions.UnknownPartnerKeyExceptionCore;
import ggc.exceptions.UnknownProductKeyExceptionCore;
import ggc.exceptions.UnknownTransactionKeyExceptionCore;
import ggc.products.Batch;
import ggc.products.Product;

/** Façade for access. */

public class WarehouseManager {

  	/** Name of file storing current store. */
  	private String _filename = "";

  	/** The warehouse itself. */
  	private Warehouse _warehouse = new Warehouse();

  	/**
   	* @@throws IOException
   	* @@throws FileNotFoundException
   	* @@throws MissingFileAssociationException
   	*/
  	public void save() throws IOException, FileNotFoundException, 
  	MissingFileAssociationException {
    	try{
			ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)));
			oo.writeObject(this._warehouse);
			oo.close();
		} catch (IOException e){
			e.printStackTrace();
		} 
  	}

  	/**
   	* @@param filename
   	* @@throws MissingFileAssociationException
   	* @@throws IOException
   	* @@throws FileNotFoundException
   	*/
  	public void saveAs(String filename) 
  	throws MissingFileAssociationException, FileNotFoundException, IOException {
    	_filename = filename;
    	save();
  	}

  	/**
  	 * @@param filename
  	 * @@throws UnavailableFileException
   	*/
  	public void load(String filename) throws UnavailableFileException {
    	
		try{
			ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
			this.setWarehouse((Warehouse) oi.readObject());
			oi.close();
			this._filename = filename;
		} catch (IOException e){
			throw new UnavailableFileException(filename);
		} catch (ClassNotFoundException e){
			throw new UnavailableFileException(filename);
		}

  	}

  /**
   * @param textfile
   * @throws ImportFileException
   */
  	public void importFile(String textfile) throws ImportFileException {
    	try {
	    	_warehouse.importFile(textfile);
    	} catch (IOException | BadEntryException e) {
	    	throw new ImportFileException(textfile);
 		}
  	}

  	public Warehouse getCurrentWarehouse(){
    	return _warehouse;
  	}

	public int getAvailableBalance(){
		return (int) Math.round(getCurrentWarehouse().getAvailableBalance());
	}

	public int getAccountingBalance(){
		return (int) Math.round(getCurrentWarehouse().getAccountingBalance());
	}

	public void setWarehouse(Warehouse warehouse){
    	this._warehouse = warehouse;
  	}

	public boolean hasNoFile(){
		return _filename.equals("");
	}

  	public int displayDate(){
    	return getCurrentWarehouse().getDate();
  	}

  	public void advanceDate(int amount) throws InvalidDateExceptionCore{
    	getCurrentWarehouse().advanceDate(amount);
  	}

  	public void registerPartner(String id, String name, String address) 
  	throws DuplicatePartnerKeyExceptionCore {
    	getCurrentWarehouse().registerPartner(id, name, address);
  	}

  	public ArrayList<String> allPartners(){
    	return getCurrentWarehouse().getAllPartners();
  	}

  	public String findPartner(String id) 
  	throws UnknownPartnerKeyExceptionCore {
    	return getCurrentWarehouse().getPartner(id);
  	}

	public List<Batch> getAvailableBatches(){
		return getCurrentWarehouse().getAvailableBatches();
	}

	public List<Product> getAllProducts(){
		return getCurrentWarehouse().getAllProducts();
	}

	public List<Batch> getBatchesByPartner(String partnerName) 
	throws UnknownPartnerKeyExceptionCore{
		return getCurrentWarehouse().getBatchesByPartner(partnerName);
	}

	public List<Batch> getBatchesByProduct(String productName) 
	throws UnknownProductKeyExceptionCore{
		return getCurrentWarehouse().getBatchesByProduct(productName);
	}

	public void toggleProductNotifications(String product, String partner) 
	throws UnknownProductKeyExceptionCore, UnknownPartnerKeyExceptionCore{
		getCurrentWarehouse().toggleProductNotifications(product, partner);
	}

	public List<String> getPartnerNotifications(String partner){
		return getCurrentWarehouse().getPartnerNotifications(partner);
	}

	public void clearPartnerNotifications(String partner) {
		getCurrentWarehouse().clearPartnerNotifications(partner);
	}

	public boolean productIsKnown(String productName){
		return getCurrentWarehouse().isKnown(productName);
	}

	public void registerSimpleAcquisition(String partnerID, String productID, 
	double price, int amount) throws UnknownPartnerKeyExceptionCore {
		getCurrentWarehouse().registerAcquisition(partnerID, productID, price, amount);
	}

	public void registerComplexAcquisition(String partnerID, String productID, 
	double price, int amount, LinkedHashMap<String, Integer> recipe, double aggravation) 
	throws UnknownPartnerKeyExceptionCore, UnknownProductKeyExceptionCore{
		getCurrentWarehouse().registerAcquisition(partnerID, productID, price, amount, recipe, aggravation);
	}

	public void registerSale(String partnerID, String productID, int deadline, int amount)
	throws UnavailableProductExceptionCore, UnknownProductKeyExceptionCore, UnknownPartnerKeyExceptionCore{
		getCurrentWarehouse().registerSale(partnerID, productID, deadline, amount);
	}

	public String getTransaction(int index) 
	throws UnknownTransactionKeyExceptionCore{
		return getCurrentWarehouse().getTransaction(index);
	}

	public void receivePayment(int index) 
	throws UnknownTransactionKeyExceptionCore{
		getCurrentWarehouse().receivePayment(index);
	}

	public List<Batch> getBatchesByPrice(double price){
		return getCurrentWarehouse().getBatchesByPrice(price);
	}
	
	public List<String> getPartnerAcquisitions(String partnerID) 
	throws UnknownPartnerKeyExceptionCore{
		return getCurrentWarehouse().getPartnerAcquisitions(partnerID);
	}

	public void registerBreakdown(String partnerID, String productID, int amount) 
	throws UnavailableProductExceptionCore, UnknownPartnerKeyExceptionCore, UnknownProductKeyExceptionCore{
		getCurrentWarehouse().registerBreakdown(partnerID, productID, amount);
	}

	public List<String> getPartnerSales(String partnerID) 
	throws UnknownPartnerKeyExceptionCore{
        return getCurrentWarehouse().getPartnerSales(partnerID);
    }

	public List<String> getPaymentsByPartner(String partnerID) 
	throws UnknownPartnerKeyExceptionCore{
		return getCurrentWarehouse().getPaymentsByPartner(partnerID);
	}

}
