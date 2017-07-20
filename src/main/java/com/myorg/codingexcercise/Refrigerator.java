package com.myorg.codingexcercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * You are about to build a Refrigerator which has SMALL, MEDIUM, and LARGE sized shelves.
 *
 * Method signature are given below. You need to implement the logic to
 *
 *  1. To keep track of items put in to the Refrigerator (add or remove)
 *  2. Make sure enough space available before putting it in
 *  3. Make sure space is used as efficiently as possible
 *  4. Make sure code runs efficiently
 *
 *
 * Created by kamoorr on 7/14/17.
 */
public class Refrigerator {

    /**
     * Refrigerator Total Cubic Feet (CuFt)
     */
    private int cubicFt;

    /**
     * Large size shelf count and size of one shelf
     */
    private int largeShelfCount;
    private int largeShelfCuFt;

    /**
     * Medium size shelf count and size of one shelf
     */
    private int mediumShelfCount;
    private int mediumShelfCuFt;

    /**
     * Medium size shelf count and size of one shelf
     */
    private int smallShelfCount;
    private int smallShelfCuFt;
    
    private Map<String, Shelf> itemsToShelfMap;
    private Map<Integer,List<Shelf>> shelfSizeToShelfMap;
    private int shelfIDGenerator =1;

    /**
     *
     *  Create a new refrigerator by specifying shelfSize and count for SMALL, MEDIUM, LARGE shelves
     * @param largeShelfCount
     * @param largeShelfCuFt
     * @param mediumShelfCount
     * @param mediumShelfCuFt
     * @param smallShelfCount
     * @param smallShelfCuFt
     */
   public Refrigerator(int largeShelfCount, int largeShelfCuFt, int mediumShelfCount, int mediumShelfCuFt, int smallShelfCount, int smallShelfCuFt) {

       /**
        * Calculating total cuft as local variable to improve performance. Assuming no vacant space in the refrigerator
        *
        */
        this.cubicFt = (largeShelfCount * largeShelfCuFt) + (mediumShelfCount * mediumShelfCuFt) + (smallShelfCount* smallShelfCuFt);

        this.largeShelfCount = largeShelfCount;
        this.largeShelfCuFt = largeShelfCuFt;
        createShelfs(largeShelfCount, largeShelfCuFt);

        this.mediumShelfCount = mediumShelfCount;
        this.mediumShelfCuFt = mediumShelfCuFt;
        createShelfs(mediumShelfCount, mediumShelfCuFt);

        this.smallShelfCount = smallShelfCount;
        this.smallShelfCuFt = smallShelfCuFt;
        createShelfs(smallShelfCount, smallShelfCuFt);
        
        //initializing this mapping
        this.itemsToShelfMap = new HashMap<String,Shelf>();

    }

    /**
     * Implement logic to put an item to this refrigerator. Make sure
     *  -- You have enough vacant space in the refrigerator
     *  -- Make this action efficient in a way to increase maximum utilization of the space, re-arrange items when necessary
     *
     * Return
     *      true if put is successful
     *      false if put is not successful, for example, if you don't have enough space any shelf, even after re-arranging
     *
     *
     * @param item
     */
    public boolean put(Item item) {
    	
    	//get potential Shelfs based on Item Size
    	//Check if any of the shelfs have enough space, if not get next level shelfs
    	//repeat step 2
    	//if not try rearranging the shelfs
    	//else return false
    	 boolean shelfFound = false;
    	
    	if(item.getCubicFt() <=0 || (this.cubicFt - this.getUsedSpace())< item.getCubicFt() )
    	return false;
    	
    	for(Integer shelfSize : this.shelfSizeToShelfMap.keySet())
    	{
    		//get eligible size shelfs
    		if(item.getCubicFt() <= shelfSize)
    		{
    			List<Shelf> shelfList = this.shelfSizeToShelfMap.get(shelfSize);
    			
    			for(Shelf shelf : shelfList)
    			{
    				System.out.println("current shelf to be put in is "+shelf.getShelfId() + " Capacity: "+shelf.getSizeInCuFt() + " sapce acuupied :"+ shelf.getCurntUtlizn() );
    				
    				//if current shelf has space then add the item to shelf
    				if (shelf.getSizeInCuFt() -shelf.getCurntUtlizn() >= item.getCubicFt())
    				{
    					shelf.addItem(item);
    					this.itemsToShelfMap.put(item.getItemId(),shelf);
    					shelfFound = true;
        				break;
    				}
    				
    			}
    			
    		}
    		if(shelfFound)
    			break;
    	}
    	//if item was not added then try reshuffling
    	reshuffle(item);
    	
    	return shelfFound;
    }


    /**
     * remove and return the requested item
     * Return null when not available
     * @param itemId
     * @return
     */
    public Item get(String itemId) {
    	
    	Item item = null;
    	
    	if(itemId == null || itemId.isEmpty()|| this.itemsToShelfMap == null || this.itemsToShelfMap.isEmpty())
        return null;
    	
    	if(this.itemsToShelfMap.containsKey(itemId))
    	{
    		Shelf shelf = this.itemsToShelfMap.get(itemId);
    		item = shelf.getItem(itemId);
    	}
    	
    	return item;

    }

    /**
     * Return current utilization of the space
     * @return
     */
    public float getUtilizationPercentage() {
        return 0;
    }

    /**
     * Return current utilization in terms of cuft
     * @return
     */
   public int getUsedSpace() {
    	
    	int usedSpace= 0;
    	if( this.shelfSizeToShelfMap != null && !this.shelfSizeToShelfMap.isEmpty() )
    	{
    		for(Integer shelfSize : this.shelfSizeToShelfMap.keySet())
        	{
        			List<Shelf> shelfList = this.shelfSizeToShelfMap.get(shelfSize);
        			if(shelfList != null && !shelfList.isEmpty() )
        			{
        			  for(Shelf shelf : shelfList)
        			  {
        				  usedSpace +=  shelf.getCurntUtlizn(); 
        			  }
        			}
    	      }
    	}
        return usedSpace;
    }

   private Map<Integer,List<Shelf>> createShelfs(int shelfCount, int shelfCapacity)
   {
	
	List<Shelf> shelfList; 
	
	if(shelfCount > 0 && shelfCapacity > 0)
	{
		
	//iterate over shelf count to create  shelfs	
	  for (int i =0; i<shelfCount;++i)
	  {
		  //create new shelf
		  Shelf shelf = new Shelf(shelfIDGenerator,shelfCapacity);
		  
		  //add shelf to shelfSizeTOShelf Mapping
		  
		  if(shelfSizeToShelfMap == null )
		  {
			  shelfSizeToShelfMap = new TreeMap<Integer, List<Shelf>>(); 
		  }
		  
		  if(shelfSizeToShelfMap.containsKey(shelfCapacity)){
			  shelfList = shelfSizeToShelfMap.get(shelfCapacity); 
		  }
		  else{
			   shelfList = new ArrayList<Shelf>();
			   shelfSizeToShelfMap.put(shelfCapacity,shelfList);			   
			   }
		  
		  shelfList.add(shelf);
		  ++shelfIDGenerator;
	  }
	}
	return shelfSizeToShelfMap;
  }
   
   private void reshuffle(Item item)
   {
	   //based on item size get the Shelfs
	   //in each shelf check what item can be removed and current be added such that it increases max space utilization for that shelf
	   //keep list of all removed (get) items and try putting them (put(item)) into the shelf until each finds a shelf.
	   // if there is any item that can't be placed then return fails else true.
	   //this will be iterative.
   }

}
