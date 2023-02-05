package edu.iastate.cs228.hw3;

/**
 *  
 * @author Holden Brown
 * 
 *
 */

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class PrimeFactorization implements Iterable<PrimeFactor>
{
	private static final long OVERFLOW = -1;
	private long value; 	// the factored integer 
							// it is set to OVERFLOW when the number is greater than 2^63-1, the
						    // largest number representable by the type long. 
	
	/**
	 * Reference to dummy node at the head.
	 */
	private Node head;
	  
	/**
	 * Reference to dummy node at the tail.
	 */
	private Node tail;
	
	private int size;     	// number of distinct prime factors 


	// ------------
	// Constructors 
	// ------------
	
    /**
	 *  Default constructor constructs an empty list to represent the number 1.
	 *  
	 *  Combined with the add() method, it can be used to create a prime factorization.  
	 */
	public PrimeFactorization() 
	{	 
		head = new Node();
		tail = new Node();
		
		head.next = tail;
		tail.previous = head;
		
		size = 0;
	}

	
	/** 
	 * Obtains the prime factorization of n and creates a doubly linked list to store the result.   
	 * Follows the direct search factorization algorithm in Section 1.2 of the project description. 
	 * 
	 * @param n
	 * @throws IllegalArgumentException if n < 1
	 */
	public PrimeFactorization(long n) throws IllegalArgumentException 
	{
		if(n < 1) throw new IllegalArgumentException();
		
		int d = 2;
		int m = 0;
		
		head = new Node();
		tail = new Node();
		
		head.next = tail;
		tail.previous = head;
		
		size = 0;
		
		while(d * d <= n) {
			while(n % d == 0) {
				n = n / d;
				m++;
			}
			if(m > 0) 
			{
				add(d, m);
			}
			m = 0;
			
			if(d > 2) {
				d += 2;
			}
			else {
				d++;
			}
		}
		if(isPrime(n))
		{
		add((int)n, 1);
		}
		updateValue();
	}
	
	
	/**
	 * Copy constructor. It is unnecessary to verify the primality of the numbers in the list.
	 * 
	 * @param pf
	 */
	public PrimeFactorization(PrimeFactorization pf)
	{
		head = new Node();
		tail = new Node();
		
		head.next = tail;
		tail.previous = head;
		
		size = 0;
		
		PrimeFactorizationIterator iter =  pf.iterator();
		while(iter.hasNext())
		{
			iter.next();
			add(iter.pending.pFactor.prime, iter.pending.pFactor.multiplicity);
		}
		value = pf.value();
	}
	
	/**
	 * Constructs a factorization from an array of prime factors.  Useful when the number is 
	 * too large to be represented even as a long integer. 
	 * 
	 * @param pflist
	 */
	public PrimeFactorization (PrimeFactor[] pfList)
	{
		head = new Node();
		tail = new Node();
		
		head.next = tail;
		tail.previous = head;
		
		size = 0;
		
		for(int i = 0; i < pfList.length; i++)
		{
			add(pfList[i].prime, pfList[i].multiplicity);
		}
		
		updateValue();
	}
	
	

	// --------------
	// Primality Test
	// --------------
	
    /**
	 * Test if a number is a prime or not.  Check iteratively from 2 to the largest 
	 * integer not exceeding the square root of n to see if it divides n. 
	 * 
	 *@param n
	 *@return true if n is a prime 
	 * 		  false otherwise 
	 */
    public static boolean isPrime(long n) 
	{
    	int d = 2;
	    while(d * d <= n) {
	    	if(n % d == 0) {
	    		return false; 
	    	}
	    	d++;
	    }
		return true; 
	}   

   
	// ---------------------------
	// Multiplication and Division 
	// ---------------------------
	
	/**
	 * Multiplies the integer v represented by this object with another number n.  Note that v may 
	 * be too large (in which case this.value == OVERFLOW). You can do this in one loop: Factor n and 
	 * traverse the doubly linked list simultaneously. For details refer to Section 3.1 in the 
	 * project description. Store the prime factorization of the product. Update value and size. 
	 * 
	 * @param n
	 * @throws IllegalArgumentException if n < 1
	 */
	public void multiply(long n) throws IllegalArgumentException 
	{
		if(n < 1) throw new IllegalArgumentException();
		
		PrimeFactorization nFact = new PrimeFactorization(n);
		multiply(nFact);
}
	
	
	/**
	 * Multiplies the represented integer v with another number in the factorization form.  Traverse both 
	 * linked lists and store the result in this list object.  See Section 3.1 in the project description 
	 * for details of algorithm. 
	 * 
	 * @param pf 
	 */
	public void multiply(PrimeFactorization pf)
	{

		PrimeFactorization result = new PrimeFactorization();
		PrimeFactorizationIterator thisIter =  this.iterator();
		PrimeFactorizationIterator pfIter =  pf.iterator();
		PrimeFactorizationIterator resIter =  result.iterator();
		
		while(pfIter.hasNext() && thisIter.hasNext()) {
			
			if(pfIter.cursor.pFactor.prime < thisIter.cursor.pFactor.prime) {
				resIter.add(pfIter.cursor.pFactor);
				pfIter.next();
			}
			else if(pfIter.cursor.pFactor.prime == thisIter.cursor.pFactor.prime) {
				PrimeFactor insert = new PrimeFactor(thisIter.cursor.pFactor.prime,pfIter.cursor.pFactor.multiplicity + thisIter.cursor.pFactor.multiplicity);
				resIter.add(insert);
				pfIter.next();
				thisIter.next();
			}
			else {
				resIter.add(thisIter.cursor.pFactor);
				thisIter.next();
			}
		}
		
		while(pfIter.hasNext()) {
			resIter.add(pfIter.cursor.pFactor);
			pfIter.next();
		}
		while(thisIter.hasNext()) {
			resIter.add(thisIter.cursor.pFactor);
			thisIter.next();
		}
		
		head = result.head;
		tail = result.tail;
		size = result.size;
		
		updateValue();
	}
	
	
	/**
	 * Multiplies the integers represented by two PrimeFactorization objects.  
	 * 
	 * @param pf1
	 * @param pf2
	 * @return object of PrimeFactorization to represent the product 
	 */
	public static PrimeFactorization multiply(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		PrimeFactorization result = new PrimeFactorization();
		PrimeFactorizationIterator thisIter =  pf1.iterator();
		PrimeFactorizationIterator pfIter =  pf2.iterator();
		PrimeFactorizationIterator resIter =  result.iterator();
		
		while(pfIter.hasNext() && thisIter.hasNext()) {
			
			if(pfIter.cursor.pFactor.prime < thisIter.cursor.pFactor.prime) {
				resIter.add(pfIter.cursor.pFactor);
				pfIter.next();
			}
			else if(pfIter.cursor.pFactor.prime == thisIter.cursor.pFactor.prime) {
				PrimeFactor insert = new PrimeFactor(thisIter.cursor.pFactor.prime,pfIter.cursor.pFactor.multiplicity + thisIter.cursor.pFactor.multiplicity);
				resIter.add(insert);
				pfIter.next();
				thisIter.next();
			}
			else {
				resIter.add(thisIter.cursor.pFactor);
				thisIter.next();
			}
		}
		
		while(pfIter.hasNext()) {
			resIter.add(pfIter.cursor.pFactor);
			pfIter.next();
		}
		while(thisIter.hasNext()) {
			resIter.add(thisIter.cursor.pFactor);
			thisIter.next();
		}
		

		return result;
	}

	
	/**
	 * Divides the represented integer v by n.  Make updates to the list, value, size if divisible.  
	 * No update otherwise. Refer to Section 3.2 in the project description for details. 
	 *  
	 * @param n
	 * @return  true if divisible 
	 *          false if not divisible 
	 * @throws IllegalArgumentException if n <= 0
	 */
	public boolean dividedBy(long n) throws IllegalArgumentException
	{
		if(n <= 0) throw new IllegalArgumentException();
		if(this.value != -1 && this.value < n) return false;
		
		PrimeFactorization nFact = new PrimeFactorization(n);
		

		return dividedBy(nFact);
	}

	
	/**
	 * Division where the divisor is represented in the factorization form.  Update the linked 
	 * list of this object accordingly by removing those nodes housing prime factors that disappear  
	 * after the division.  No update if this number is not divisible by pf. Algorithm details are 
	 * given in Section 3.2. 
	 * 
	 * @param pf
	 * @return	true if divisible by pf
	 * 			false otherwise
	 */
	public boolean dividedBy(PrimeFactorization pf)
	{
		if(this.value != -1 && pf.value != -1 && this.value < pf.value) return false;
		else if(this.value != -1 && pf.value == -1) return false;
		if(value == pf.value) 
		{
			this.clearList();
			value = 1;
			return true;
		}
		
		PrimeFactorization copy = new PrimeFactorization(this);
		PrimeFactorizationIterator iterCopy =  copy.iterator();
		PrimeFactorizationIterator iterpf =  pf.iterator();
		while(iterCopy.hasNext())
		{
			if(iterCopy.cursor.pFactor.prime >= iterpf.cursor.pFactor.prime)
			{
				if(iterCopy.cursor.pFactor.prime > iterpf.cursor.pFactor.prime)
				{
					return false;
				}
				else if(iterCopy.cursor.pFactor.prime == iterpf.cursor.pFactor.prime && 
						iterCopy.cursor.pFactor.multiplicity < iterpf.cursor.pFactor.multiplicity)
				{
					return false; 
				}
				else
				{
					if(iterCopy.cursor.pFactor.prime == iterpf.cursor.pFactor.prime 
							&& iterCopy.cursor.pFactor.multiplicity >= iterpf.cursor.pFactor.multiplicity)
					{
						iterCopy.cursor.pFactor.multiplicity -= iterpf.cursor.pFactor.multiplicity;
						if(iterCopy.cursor.pFactor.multiplicity == 0)
						{
							iterCopy.remove();
						}
					}
					iterCopy.next();
					iterpf.next();
				}
			}
			else if( !iterCopy.hasNext() && iterpf.hasNext())
			{
				return false;
			}
			else if(iterpf.cursor == pf.tail)
			{
				head.next = copy.head.next;
				tail.previous = copy.tail.previous;
				copy.head.next.previous = head;
				copy.tail.previous.next = tail;
				size = copy.size;
				
				return true;
			}
		}
		return true;
	}

	
	/**
	 * Divide the integer represented by the object pf1 by that represented by the object pf2. 
	 * Return a new object representing the quotient if divisible. Do not make changes to pf1 and 
	 * pf2. No update if the first number is not divisible by the second one. 
	 *  
	 * @param pf1
	 * @param pf2
	 * @return quotient as a new PrimeFactorization object if divisible
	 *         null otherwise 
	 */
	public static PrimeFactorization dividedBy(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		if (pf1.value() % pf2.value() == 0)
		{
			return new PrimeFactorization(pf1.value() / pf2.value());
		}
		else
		{
			return null;
		}
	}

	
	// -------------------------------------------------
	// Greatest Common Divisor and Least Common Multiple 
	// -------------------------------------------------

	/**
	 * Computes the greatest common divisor (gcd) of the represented integer v and an input integer n.
	 * Returns the result as a PrimeFactor object.  Calls the method Euclidean() if 
	 * this.value != OVERFLOW.
	 *     
	 * It is more efficient to factorize the gcd than n, which can be much greater. 
	 *     
	 * @param n
	 * @return prime factorization of gcd
	 * @throws IllegalArgumentException if n < 1
	 */
	public PrimeFactorization gcd(long n) throws IllegalArgumentException
	{
		if(n < 1) throw new IllegalArgumentException();
		updateValue();
		long s = 0;
		
		if(this.value != OVERFLOW) 
		{
			s = Euclidean(this.value,n);
			return new PrimeFactorization(s); 
		}

		
		return null; 
	}
	

	/**
	  * Implements the Euclidean algorithm to compute the gcd of two natural numbers m and n. 
	  * The algorithm is described in Section 4.1 of the project description. 
	  * 
	  * @param m
	  * @param n
	  * @return gcd of m and n. 
	  * @throws IllegalArgumentException if m < 1 or n < 1
	  */
 	public static long Euclidean(long m, long n) throws IllegalArgumentException
	{
 		if(m < 1 || n < 1) throw new IllegalArgumentException();
        while (n != 0) 
        {
            long temp = n;
            n = m % n;
            m = temp;
        }
        return m;
	}

 	
	/**
	 * Computes the gcd of the values represented by this object and pf by traversing the two lists.  No 
	 * direct computation involving value and pf.value. Refer to Section 4.2 in the project description 
	 * on how to proceed.  
	 * 
	 * @param  pf
	 * @return prime factorization of the gcd
	 */
	public PrimeFactorization gcd(PrimeFactorization pf)
	{
		PrimeFactorization result = new PrimeFactorization();
		PrimeFactorizationIterator thisIter =  this.iterator();
		PrimeFactorizationIterator pfIter =  pf.iterator();
		PrimeFactorizationIterator resIter =  result.iterator();
		
		
		while(pfIter.hasNext()) {
			while(this.containsPrimeFactor(pfIter.cursor.pFactor.prime)) {
				if(thisIter.cursor.pFactor.prime == pfIter.cursor.pFactor.prime) {
					PrimeFactor insert = new PrimeFactor(thisIter.cursor.pFactor.prime,Math.min(pfIter.cursor.pFactor.multiplicity, thisIter.cursor.pFactor.multiplicity));
					resIter.add(insert);
					thisIter.next();
					break;
				}
				thisIter.next();
			}
			pfIter.next();
		}
		result.updateValue();
		return result;
	}
	
	
	/**
	 * 
	 * @param pf1
	 * @param pf2
	 * @return prime factorization of the gcd of two numbers represented by pf1 and pf2
	 */
	public static PrimeFactorization gcd(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		PrimeFactorization result = new PrimeFactorization();
		PrimeFactorizationIterator thisIter =  pf1.iterator();
		PrimeFactorizationIterator pfIter =  pf2.iterator();
		PrimeFactorizationIterator resIter =  result.iterator();
		
		
		while(pfIter.hasNext()) {
			while(pf1.containsPrimeFactor(pfIter.cursor.pFactor.prime)) {
				if(thisIter.cursor.pFactor.prime == pfIter.cursor.pFactor.prime) {
					PrimeFactor insert = new PrimeFactor(thisIter.cursor.pFactor.prime,Math.min(pfIter.cursor.pFactor.multiplicity, thisIter.cursor.pFactor.multiplicity));
					resIter.add(insert);
					thisIter.next();
					break;
				}
				thisIter.next();
			}
			pfIter.next();
		}
		result.updateValue();
		return result;
	}

	
	/**
	 * Computes the least common multiple (lcm) of the two integers represented by this object 
	 * and pf.  The list-based algorithm is given in Section 4.3 in the project description. 
	 * 
	 * @param pf  
	 * @return factored least common multiple  
	 */
	public PrimeFactorization lcm(PrimeFactorization pf)
	{

		PrimeFactorization result = new PrimeFactorization();
		PrimeFactorizationIterator thisIter =  this.iterator();
		PrimeFactorizationIterator pfIter =  pf.iterator();
		PrimeFactorizationIterator resIter =  result.iterator();
		
		this.updateValue();
		pf.updateValue();
		if(this.value == -1 || pf.value == -1) {
			result.value = -1;
			return result;
		}
		
		
		while(pfIter.hasNext() && thisIter.hasNext()) {
			
			if(pfIter.cursor.pFactor.prime < thisIter.cursor.pFactor.prime) {
				System.out.println(pfIter.cursor.pFactor);
				resIter.add(pfIter.cursor.pFactor);
				pfIter.next();
			}
			else if(pfIter.cursor.pFactor.prime == thisIter.cursor.pFactor.prime) {
				PrimeFactor insert = new PrimeFactor(thisIter.cursor.pFactor.prime,Math.max(pfIter.cursor.pFactor.multiplicity, thisIter.cursor.pFactor.multiplicity));
				System.out.println(insert.toString());
				resIter.add(insert);
				pfIter.next();
				thisIter.next();
			}
			else {
				System.out.println(thisIter.cursor.pFactor);
				resIter.add(thisIter.cursor.pFactor);
				thisIter.next();
			}
		}
		
		while(pfIter.hasNext()) {
			resIter.add(pfIter.cursor.pFactor);
			pfIter.next();
		}
		while(thisIter.hasNext()) {
			resIter.add(thisIter.cursor.pFactor);
			thisIter.next();
		}
		
		result.updateValue();
		return result;
	}

	
	/**
	 * Computes the least common multiple of the represented integer v and an integer n. Construct a 
	 * PrimeFactors object using n and then call the lcm() method above.  Calls the first lcm() method. 
	 * 
	 * @param n
	 * @return factored least common multiple 
	 * @throws IllegalArgumentException if n < 1
	 */
	public PrimeFactorization lcm(long n) throws IllegalArgumentException 
	{
		if(n < 1) throw new IllegalArgumentException();
		PrimeFactorization nFact = new PrimeFactorization(n);
		return lcm(nFact); 
	}

	/**
	 * Computes the least common multiple of the integers represented by pf1 and pf2. 
	 * 
	 * @param pf1
	 * @param pf2
	 * @return prime factorization of the lcm of two numbers represented by pf1 and pf2
	 */
	public static PrimeFactorization lcm(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		PrimeFactorization result = new PrimeFactorization();
		PrimeFactorizationIterator thisIter =  pf1.iterator();
		PrimeFactorizationIterator pfIter =  pf2.iterator();
		PrimeFactorizationIterator resIter =  result.iterator();
		
		pf2.updateValue();
		pf1.updateValue();
		if(pf1.value == -1 || pf2.value == -1) {
			result.value = -1;
			return result;
		}
		
		
		while(pfIter.hasNext() && thisIter.hasNext()) {
			
			if(pfIter.cursor.pFactor.prime < thisIter.cursor.pFactor.prime) {
				System.out.println(pfIter.cursor.pFactor);
				resIter.add(pfIter.cursor.pFactor);
				pfIter.next();
			}
			else if(pfIter.cursor.pFactor.prime == thisIter.cursor.pFactor.prime) {
				PrimeFactor insert = new PrimeFactor(thisIter.cursor.pFactor.prime,Math.max(pfIter.cursor.pFactor.multiplicity, thisIter.cursor.pFactor.multiplicity));
				System.out.println(insert.toString());
				resIter.add(insert);
				pfIter.next();
				thisIter.next();
			}
			else {
				System.out.println(thisIter.cursor.pFactor);
				resIter.add(thisIter.cursor.pFactor);
				thisIter.next();
			}
		}
		
		while(pfIter.hasNext()) {
			resIter.add(pfIter.cursor.pFactor);
			pfIter.next();
		}
		while(thisIter.hasNext()) {
			resIter.add(thisIter.cursor.pFactor);
			thisIter.next();
		}
		
		result.updateValue();
		return result;
	}

	
	// ------------
	// List Methods
	// ------------
	
	/**
	 * Traverses the list to determine if p is a prime factor. 
	 * 
	 * Precondition: p is a prime. 
	 * 
	 * @param p  
	 * @return true  if p is a prime factor of the number v represented by this linked list
	 *         false otherwise 
	 * @throws IllegalArgumentException if p is not a prime
	 */
	public boolean containsPrimeFactor(int p) throws IllegalArgumentException
	{
		if(!isPrime(p)) throw new IllegalArgumentException();
		PrimeFactorizationIterator iter = iterator();
		while(iter.hasNext())
		{
			iter.next();
			if(iter.pending.pFactor.prime == p) return true;
		}
		return false; 
	}
	
	// The next two methods ought to be private but are made public for testing purpose. Keep
	// them public 
	
	/**
	 * Adds a prime factor p of multiplicity m.  Search for p in the linked list.  If p is found at 
	 * a node N, add m to N.multiplicity.  Otherwise, create a new node to store p and m. 
	 *  
	 * Precondition: p is a prime. 
	 * 
	 * @param p  prime 
	 * @param m  multiplicity
	 * @return   true  if m >= 1
	 *           false if m < 1   
	 */
    private boolean add(int p, int m) 
    {
    	if(m < 1) return false;
    	int t = 0;
    	PrimeFactorizationIterator iter = iterator();
    	if(head.next == null)
    	{
    		iter.add(new PrimeFactor(p,m));
    		size++;
    		return true;
    	}
    	else 
    	{
    		while(iter.hasNext())
    		{
    			if(iter.cursor.pFactor.prime == p)
    			{
    				iter.cursor.pFactor.multiplicity += m;
    				t++;
    			}
    			iter.next();
    		}
    		if(t == 0) {
    		iter.add(new PrimeFactor(p,m));}
    	}
    	
    	return true;
    }

	    
    /**
     * Removes m from the multiplicity of a prime p on the linked list.  It starts by searching 
     * for p.  Returns false if p is not found, and true if p is found. In the latter case, let 
     * N be the node that stores p. If N.multiplicity > m, subtracts m from N.multiplicity.  
     * If N.multiplicity <= m, removes the node N.  
     * 
     * Precondition: p is a prime. 
     * 
     * @param p
     * @param m
     * @return true  when p is found. 
     *         false when p is not found. 
     * @throws IllegalArgumentException if m < 1
     */
    public boolean remove(int p, int m) throws IllegalArgumentException
    {
    	if(m < 1) throw new IllegalArgumentException();
    	PrimeFactorizationIterator iter = iterator();
    	while(iter.hasNext())
    	{
    		iter.next();
    		if(iter.pending.pFactor.prime == p)
    		{
    			if(iter.pending.pFactor.multiplicity > m)
    			{
    				iter.pending.pFactor.multiplicity -= m;
    			}
    			else
    			{
    				iter.remove();
    			}
    			return true;
    		}
    	}
    	return false; 
    }


    /**
     * 
     * @return size of the list
     */
	public int size() 
	{
		return size; 
	}

	
	/**
	 * Writes out the list as a factorization in the form of a product. Represents exponentiation 
	 * by a caret.  For example, if the number is 5814, the returned string would be printed out 
	 * as "2 * 3^2 * 17 * 19". 
	 */
	@Override 
	public String toString()
	{
		PrimeFactorizationIterator iter =  iterator();
		String result = "";
		while(iter.hasNext())
		{
			result += iter.cursor.toString();
			iter.next();
			if(iter.hasNext())
			{
				result += " * ";
			}
		}
		return result; 
	}

	
	// The next three methods are for testing, but you may use them as you like.  

	/**
	 * @return true if this PrimeFactorization is representing a value that is too large to be within 
	 *              long's range. e.g. 999^999. false otherwise.
	 */
	public boolean valueOverflow() {
		return value == OVERFLOW;
	}

	/**
	 * @return value represented by this PrimeFactorization, or -1 if valueOverflow()
	 */
	public long value() {
		return value;
	}

	
	public PrimeFactor[] toArray() {
		PrimeFactor[] arr = new PrimeFactor[size];
		int i = 0;
		for (PrimeFactor pf : this)
			arr[i++] = pf;
		return arr;
	}


	
	@Override
	public PrimeFactorizationIterator iterator()
	{
	    return new PrimeFactorizationIterator();
	}
	
	/**
	 * Doubly-linked node type for this class.
	 */
    private class Node 
    {
		public PrimeFactor pFactor;		// prime factor 
		public Node next;
		public Node previous;
		
		/**
		 * Default constructor for creating a dummy node.
		 */
		public Node()
		{
			pFactor = null;
		}
	    
		/**
		 * Precondition: p is a prime
		 * 
		 * @param p	 prime number 
		 * @param m  multiplicity 
		 * @throws IllegalArgumentException if m < 1 
		 */
		public Node(int p, int m) throws IllegalArgumentException 
		{	
			if(m < 1) throw new IllegalArgumentException();
			
			pFactor = new PrimeFactor(p,m);
		}   

		
		/**
		 * Constructs a node over a provided PrimeFactor object. 
		 * 
		 * @param pf
		 * @throws IllegalArgumentException
		 */
		public Node(PrimeFactor pf)  
		{
			if(pf.multiplicity < 1) throw new IllegalArgumentException();
			
			pFactor = pf;
		}


		/**
		 * Printed out in the form: prime + "^" + multiplicity.  For instance "2^3". 
		 * Also, deal with the case pFactor == null in which a string "dummy" is 
		 * returned instead.  
		 */
		@Override
		public String toString() 
		{
			if(pFactor == null) {
				return "dummy";
			}
			else if(pFactor.multiplicity == 1) {
				return String.valueOf(pFactor.prime);
			}
			else {
				return String.valueOf(pFactor.prime) + "^" + String.valueOf(pFactor.multiplicity); 
			}
		}
    }

    //change to private
    private class PrimeFactorizationIterator implements ListIterator<PrimeFactor>
    {  	
        // Class invariants: 
        // 1) logical cursor position is always between cursor.previous and cursor
        // 2) after a call to next(), cursor.previous refers to the node just returned 
        // 3) after a call to previous() cursor refers to the node just returned 
        // 4) index is always the logical index of node pointed to by cursor
    	
        private Node cursor = head.next;
        private Node pending = null;    // node pending for removal
        private int index = 0;      
  	  
    	// other instance variables ... 
    	  
      
        /**
    	 * Default constructor positions the cursor before the smallest prime factor.
    	 */
    	public PrimeFactorizationIterator()
    	{
    		cursor = head.next;
    		pending = null;
    		index = 0;
    	}

    	@Override
    	public boolean hasNext()
    	{
    		return index < size; 
    	}

    	
    	@Override
    	public boolean hasPrevious()
    	{
    		return index > 0; 
    	}

 
    	@Override 
    	public PrimeFactor next() 
    	{
    		if (!hasNext())
    		{
    			throw new NoSuchElementException();
    		}
    		
    		pending = cursor;
    		cursor = cursor.next;
    		index++;
    		return pending.pFactor;
    	}

 
    	@Override 
    	public PrimeFactor previous() 
    	{
    		if (!hasPrevious())
    		{
    			throw new NoSuchElementException();
    		}
    		
    		index--;
    		pending = cursor;
    		cursor = cursor.previous;
    		return pending.pFactor;
    	}

   
    	/**
    	 *  Removes the prime factor returned by next() or previous()
    	 *  
    	 *  @throws IllegalStateException if pending == null 
    	 */
    	@Override
    	public void remove() throws IllegalStateException
    	{
    		if (pending == null) throw new IllegalStateException();
    		
    		if(pending.previous != null)
    		{
    			pending.previous.next = pending.next;
    		}
    		
    		if(pending.next != null)
    		{
    			pending.next.previous = pending.previous;
    		}
    		size--;
    	}
 
 
    	/**
    	 * Adds a prime factor at the cursor position.  The cursor is at a wrong position 
    	 * in either of the two situations below: 
    	 * 
    	 *    a) pf.prime < cursor.previous.pFactor.prime if cursor.previous != head. 
    	 *    b) pf.prime > cursor.pFactor.prime if cursor != tail. 
    	 * 
    	 * Take into account the possibility that pf.prime == cursor.pFactor.prime. 
    	 * 
    	 * Precondition: pf.prime is a prime. 
    	 * 
    	 * @param pf  
    	 * @throws IllegalArgumentException if the cursor is at a wrong position. 
    	 */
    	@Override
        public void add(PrimeFactor pf) throws IllegalArgumentException 
        {
        	
        	Node temp = new Node(pf);
        	link(tail.previous,temp);
        	size++;
        	index++;
        }


    	@Override
		public int nextIndex() 
		{
			return index;
		}


    	@Override
		public int previousIndex() 
		{
			return index - 1;
		}

		@Deprecated
		@Override
		public void set(PrimeFactor pf) 
		{
			throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support set method");
		}
        
    	// Other methods you may want to add or override that could possibly facilitate 
    	// other operations, for instance, addition, access to the previous element, etc.
    	// 
    	// ...
    	// 
    }

    
    // --------------
    // Helper methods 
    // -------------- 
    // Do you use windows 98?
    
    /**
     * Inserts toAdd into the list after current without updating size.
     * 
     * Precondition: current != null, toAdd != null
     */
    private void link(Node current, Node toAdd)
    {
    	toAdd.previous = current;
    	toAdd.next = current.next;
        current.next.previous = toAdd;
        current.next = toAdd;
    }
    
  

	 
    /**
     * Removes toRemove from the list without updating size.
     */
    private void unlink(Node toRemove)
    {
    	toRemove.previous.next = toRemove.next;
    	toRemove.next.previous = toRemove.previous;
    }



    /**
	  * Remove all the nodes in the linked list except the two dummy nodes. 
	  * 
	  * Made public for testing purpose.  Ought to be private otherwise. 
	  */
	private void clearList()
	{
		while(size > 0)
		{
			unlink(head.next);
			size--;
		}
	}	
	
	/**
	 * Multiply the prime factors (with multiplicities) out to obtain the represented integer.  
	 * Use Math.multiply(). If an exception is throw, assign OVERFLOW to the instance variable value.  
	 * Otherwise, assign the multiplication result to the variable. 
	 * 
	 */
	private void updateValue()
	{
		try {		
			PrimeFactorizationIterator iter = iterator();
			PrimeFactor first = iter.next();
			long result = (long)Math.pow(first.prime,first.multiplicity);
			
			while(iter.hasNext())
			{
				PrimeFactor temp = iter.next();
				result = Math.multiplyExact(result ,(long)Math.pow(temp.prime,temp.multiplicity));
			}
			value = result;
		} 
			
		catch (ArithmeticException e) 
		{
			value = OVERFLOW;
		}
		
	}
}
