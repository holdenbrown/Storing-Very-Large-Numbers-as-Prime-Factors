# Storing-Very-Large-Numbers-as-Prime-Factors
PrimeFactorization is a Java project that provides efficient and comprehensive tools for prime factorization and arithmetic operations involving large numbers represented in prime factor form. This project consists of two essential classes: PrimeFactor.java and PrimeFactorization.java.

PrimeFactor.java
PrimeFactor.java defines a class to represent a prime factor along with its multiplicity. This class encapsulates prime factorization information, enabling calculations involving prime factors and their multiplicities.

Features
Encapsulation: The PrimeFactor class encapsulates prime factor and multiplicity as a single entity, promoting clean and organized code.
Arithmetic Operations: Supports arithmetic operations on prime factors, enabling calculations with large numbers in prime factor form.
Usage
Instantiate the PrimeFactor class by providing a prime number and its multiplicity.
Utilize the class methods to perform arithmetic operations and access prime factor information.
PrimeFactorization.java
PrimeFactorization.java defines a class to represent the prime factorization of a positive integer. This class uses a doubly-linked list data structure to efficiently store and manage prime factors along with their multiplicities.

Features
Linked List Structure: Utilizes a doubly-linked list to efficiently store prime factors and multiplicities, accommodating large numbers.
Arithmetic Operations: Provides methods to perform arithmetic operations on prime factorizations, enabling calculations with large numbers.
Value Overflow Handling: Includes mechanisms to handle value overflow situations and represent large numbers beyond Java's long range.
Usage
Instantiate the PrimeFactorization class by providing a positive integer.
Utilize the class methods to access prime factorization information, perform arithmetic operations, and handle value overflow scenarios.

Example
PrimeFactorization pf = new PrimeFactorization(120); // Represents the prime factorization of 120
PrimeFactor[] factors = pf.toArray(); // Returns an array of PrimeFactor objects
long value = pf.value(); // Returns the value represented by the prime factorization


