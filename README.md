# PrototypeRDBMS
Custom RDBMS implementation



# Design Principles in Prototype DBMS
I have adhered to Object Oriented Programming (OOPS), SOLID design principles and
used other design principles such as Factory, Singleton, and Builder throughout the
application development process.
I am including just a few of the snippets (as examples) from my code demonstrating the
application of one more of these fundamental design principles together.
By creating a parent class CoreDatabaseQuery and extending it to several child classes like
CreateQuery, UpdateQuery, I have utilized the Single Responsibility Principle (SRP) and
the Open/Closed Principle (OCP).

# Single Responsibility Principle (SRP):
The Single Responsibility Principle states that a module should have only one reason to
change. Throughout the development of my project, I have adhered to this principle, keeping
the individual classes loosely coupled.
If there is a need to add/delete an element, it can be done only by making changes in the
respective class.
I have created separate classes for all database operations after the data is validated and
parsed in separate modules. I have shown two of such examples below where I have created
CreateQuery, SelectQuery, UpdateQuery, DeleteQuery classes to handle their respective
operations following the design principle.

# Open / Closed Principle (OCP):
The OCP states that our classes and modules open for extension but closed for modification.
Below the snippet of the code in which I have created a parent class CoreDatabaseQuery
which have some common methods required for all types of query execution. The child
classes have extended this and implemented their respective CRUD query operations in
separate concrete classes.


# Dependency Inversion (DIP):
As the principle of DIP states that High-level modules should not depend on low-level
modules. Both should depend on abstractions. I have ensured this my code throughout the
development process.
UserAuthentication is a high-level module that provides user authentication services. It
implements the IUserAuthentication interface which acts as the blueprint for the class.
UserAuthentication depends on the IEncryptDecryptService interface, which is an
abstraction. The dependency is established through the constructor, where an
IEncryptDecryptService instance is injected into the UserAuthentication class.
By injecting the IEncryptDecryptService as a dependency, the control over the concrete
implementation of encryption and decryption services is inverted.
We can see that the UserAuthentication relies only on the abstraction defined as per the
IEncryptDecryptService. It does not manage the implementation.
This also helps us to achieve the important concept of loose coupling.

# Singleton Design Pattern and Builder Design Pattern:
I have implemented the ConfigUtils class is designed as a Singleton which ensures there's
only one instance in the application. The getInstance() method follows the concept of lazy
initialization and is created only when required.
The singleton class ensures that there is a single point for loading and accessing configuration
properties.


# Sequence of Queries executed for functional testing

Queries executed:
1. CREATE TABLE student ( student_id INT PRIMARY KEY, name VARCHAR(255),
date_of_birth DATE, course VARCHAR(255), course_duration INT);
2. CREATE TABLE employee ( employee_id INT PRIMARY KEY, name
VARCHAR(255), hire_date DATE, job_title VARCHAR(255), salary DECIMAL(10,
2));
3. CREATE TABLE order_table ( order_id INT PRIMARY KEY, customer_name
VARCHAR(255), order_date DATE, total_amount DECIMAL(10, 2), status
VARCHAR(50));



# Transaction Validation Testing:
# Sequence of queries executed:

BEGIN TRANSACTION
INSERT INTO employee VALUES ( 1, Steven Rogers, 2023-10-26, Manager, 75000.00);
SELECT * FROM employee
COMMIT
SELECT * FROM employee
END TRANSACTION
The transaction sequence is started as soon as the user types BEGIN
TRANSACTION command. This transaction follows ACID property and it not written
onto the file immediately. We can see this from the result of first SELECT query which
prints the data which exists in file. When the user enters COMMIT, the transaction is
committed to the file which can be seen from the testing below.


SELECT * FROM employee
COMMIT
SELECT * FROM employee
END TRANSACTION
The update query changes are persisted to file only when COMMIT
is encountered. The SELECT query command shows the result from the persistent file at
every step of the transaction flow execution.



# Queries executed:
BEGIN TRANSACTION
DELETE FROM employee WHERE employee_id = 1
SELECT * FROM employee
COMMIT
SELECT * FROM employee
END TRANSACTION

The delete query is executed inside a transaction. Upon this execution the changes in the table
are not immediately reflected as the result is stored in interim data buffer. The changes are
then committed into the file storage when COMMIT keyword is encountered.



# Queries Executed with Rollback:
BEGIN TRANSACTION
INSERT INTO employee VALUES ( 1, Steven Rogers, 2023-10-26, Manager, 75000.00);
SELECT * FROM employee
ROLLBACK
SELECT * FROM employee
END TRANSACTION
Since the transaction was rolled back, the insert query data was not persisted to the file upon
execution.





