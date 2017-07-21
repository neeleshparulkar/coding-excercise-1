package com.myorg.codingexcercise;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collections;
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
public class Refrigerator implements Cloneable {

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
    
    private Item lastReshuffledItem =null;

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
    	 Refrigerator cloneRefrigerator = null;
    	
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
    					if(shelf.addItem(item)){
    						System.out.println("Put: item added to shelf: "+shelf.getShelfId()+ " itemId = "+item.getItemId());	
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
    	if(!shelfFound)
    	{
    		try {
    			cloneRefrigerator = (Refrigerator) this.clone();
				shelfFound = reshuffle(item,cloneRefrigerator);
				if(shelfFound){
		    		this.itemsToShelfMap = cloneRefrigerator.itemsToShelfMap;
		    		this.shelfSizeToShelfMap = cloneRefrigerator.shelfSizeToShelfMap;
		    	}
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	if(!shelfFound){
    		System.out.println("Put: Item was not added: "+item.getItemId());
    	}
    	
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
   
   private boolean reshuffle(Item item,Refrigerator cloneRefrigerator) throws CloneNotSupportedException
   {
	   //based on item size get the Shelfs
	   //in each shelf check what item can be removed and current be added such that it increases max space utilization for that shelf
	   //keep list of all removed (get) items and try putting them (put(item)) into the shelf until each finds a shelf.
	   // if there is any item that can't be placed then return fails else true.
	   //this will be iterative.
	   
	   List<Item> lstUtlzdShelfItems = null;
	   boolean shelfFound =false;
	   cloneRefrigerator.lastReshuffledItem = item;
	   
	   
	  for(Integer shelfSize : cloneRefrigerator.shelfSizeToShelfMap.keySet())
   		{
		  if(item.getCubicFt() <= shelfSize)
		  {
			 List<Shelf> shlefList = cloneRefrigerator.shelfSizeToShelfMap.get(shelfSize);
			 for(Shelf shelf:shlefList )
			 {
				lstUtlzdShelfItems =  findLeastUtilizedItems(shelf,item);
				 if (lstUtlzdShelfItems != null && !lstUtlzdShelfItems.isEmpty()  )
				 {
					 shelfFound = true;
					 break;
				 }
			 }
		  }
		  if(shelfFound)
			  break;
   		}
	  if(lstUtlzdShelfItems != null && !lstUtlzdShelfItems.isEmpty())
	  {
		  for(Item removedItem:lstUtlzdShelfItems)
		  {
			  shelfFound =  cloneRefrigerator.replace(removedItem,cloneRefrigerator);
		  }
	  }
	 return shelfFound;  
   }
   private List<Item> findLeastUtilizedItems(Shelf shelf, Item itemToAdd)
   {
	   List<Item> removedItemList = new ArrayList<Item>();
	   int removedItemsTotalCuFt = 0;
	   //if shelf is already full
	   if(shelf.getCurntUtlizn() == shelf.getSizeInCuFt())
	   {
		   return null;
	   } else 
	   {
		 
		 List<Item> list = new ArrayList<Item>(shelf.getShelfContainer().values()); 
		 Collections.sort(list);
		 for(Item shelfItem:list )
		 {
			
			 if(itemToAdd.getCubicFt() > shelfItem.getCubicFt()  && removedItemsTotalCuFt < itemToAdd.getCubicFt()) {
					Item removedItem =  shelf.getItem(shelfItem.getItemId());
					removedItemList.add(removedItem);
					removedItemsTotalCuFt +=removedItem.getCubicFt();
					if(shelf.addItem(itemToAdd) )
					{
					 	break;
					}	
			 }
			 else
			 {
				 break;
			 }
				 
		 }
		 
		 if(!removedItemList.isEmpty())
		 {
		
			 Collections.sort(removedItemList, Collections.reverseOrder());
			 if(shelf.getCurntUtlizn() < shelf.getSizeInCuFt())
			 {
				for(Item item:removedItemList) 
				{
					if(shelf.addItem(item))
					{
						removedItemList.remove(item);
					}
					else
						break;
				}
			 }
		 }
		 
		   
		   return removedItemList;
	   }
	   
   }
   
   private boolean replace(Item removedItem, Refrigerator cloneRefrigerator)
   {
	   boolean shelfFound = false;
	   
	   if(cloneRefrigerator.lastReshuffledItem.getItemId().equals(removedItem.getItemId()))
		   return shelfFound;
   	
   	if(removedItem.getCubicFt() <=0 || (cloneRefrigerator.cubicFt - cloneRefrigerator.getUsedSpace())< removedItem.getCubicFt() )
   	return false;
   	
   	for(Integer shelfSize : cloneRefrigerator.shelfSizeToShelfMap.keySet())
   	{
   		//get eligible size shelfs
   		if(removedItem.getCubicFt() <= shelfSize)
   		{
   			List<Shelf> shelfList = cloneRefrigerator.shelfSizeToShelfMap.get(shelfSize);
   			
   			for(Shelf shelf : shelfList)
   			{
   				
   				//if current shelf has space then add the item to shelf
   					if(shelf.addItem(removedItem)){
   						System.out.println("Replace: removedItem added to shelf: "+shelf.getShelfId()+ " removedItem = "+removedItem.getItemId());	
   						cloneRefrigerator.itemsToShelfMap.put(removedItem.getItemId(),shelf);
   					shelfFound = true;
       				break;
   					}
   				
   			}
   			
   		}
   		if(shelfFound)
   			break;
   	}
   	//if item was not added then try reshuffling
   	if(!shelfFound)
   	{
   		try {
				shelfFound = reshuffle(removedItem,cloneRefrigerator);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   	}
   	if(!shelfFound){
   		System.out.println("Replace: Item was not added: "+removedItem.getItemId());
   	}
   	return shelfFound;
   }
   
   public Refrigerator clone()
   {
	   try {
            Refrigerator refClone =  (Refrigerator) super.clone();
            refClone.itemsToShelfMap = new HashMap<String, Shelf>();
            for(String str :this.itemsToShelfMap.keySet())
            {
                Shelf cloneShelf = this.itemsToShelfMap.get(str).clone();
            	refClone.itemsToShelfMap.put(str,cloneShelf);
            }
            //(Map<String, Shelf>)((HashMap<String, Shelf>)this.itemsToShelfMap).clone();
            refClone.shelfSizeToShelfMap = new TreeMap<Integer,List<Shelf>>();
            for(Integer shelfSize :this.shelfSizeToShelfMap.keySet())
            {
                List<Shelf> cloneList = new ArrayList<Shelf>();
                List<Shelf> list = this.shelfSizeToShelfMap.get(shelfSize);
                for(Shelf shelf: list)
                {
            	Shelf cloneShelf = shelf.clone();
            	cloneList.add(cloneShelf);
                }
                refClone.shelfSizeToShelfMap.put(shelfSize,cloneList);
            }
            
		  return refClone;
       } catch (CloneNotSupportedException e) {
           e.printStackTrace();
           throw new RuntimeException();
       }
   }

}
