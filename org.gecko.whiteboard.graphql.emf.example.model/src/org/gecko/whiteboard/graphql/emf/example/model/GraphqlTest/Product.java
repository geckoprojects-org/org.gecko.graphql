/**
 */
package org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest;

import java.math.BigInteger;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Product</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getSkus <em>Skus</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getSize <em>Size</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#isActive <em>Active</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getPrice <em>Price</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getCurrency <em>Currency</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getInputTest <em>Input Test</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getOutputTest <em>Output Test</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getSkusMutationOnly <em>Skus Mutation Only</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getInputValueList <em>Input Value List</em>}</li>
 *   <li>{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getCurrencies <em>Currencies</em>}</li>
 * </ul>
 *
 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct()
 * @model
 * @generated
 */
public interface Product extends CatalogEntry {
	/**
	 * Returns the value of the '<em><b>Skus</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.SKU}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Skus</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Skus</em>' containment reference list.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct_Skus()
	 * @model containment="true" ordered="false"
	 *        annotation="http://www.eclipse.org/OCL/Collection nullFree='false'"
	 * @generated
	 */
	EList<SKU> getSkus();

	/**
	 * Returns the value of the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Size</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Size</em>' attribute.
	 * @see #setSize(BigInteger)
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct_Size()
	 * @model
	 * @generated
	 */
	BigInteger getSize();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getSize <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Size</em>' attribute.
	 * @see #getSize()
	 * @generated
	 */
	void setSize(BigInteger value);

	/**
	 * Returns the value of the '<em><b>Active</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Active</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Active</em>' attribute.
	 * @see #setActive(boolean)
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct_Active()
	 * @model default="false" required="true"
	 * @generated
	 */
	boolean isActive();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#isActive <em>Active</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Active</em>' attribute.
	 * @see #isActive()
	 * @generated
	 */
	void setActive(boolean value);

	/**
	 * Returns the value of the '<em><b>Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Price</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Price</em>' attribute.
	 * @see #setPrice(double)
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct_Price()
	 * @model required="true"
	 * @generated
	 */
	double getPrice();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getPrice <em>Price</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Price</em>' attribute.
	 * @see #getPrice()
	 * @generated
	 */
	void setPrice(double value);

	/**
	 * Returns the value of the '<em><b>Currency</b></em>' attribute.
	 * The default value is <code>"DOLLAR"</code>.
	 * The literals are from the enumeration {@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Currency</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Currency</em>' attribute.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency
	 * @see #setCurrency(Currency)
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct_Currency()
	 * @model default="DOLLAR"
	 * @generated
	 */
	Currency getCurrency();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getCurrency <em>Currency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Currency</em>' attribute.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency
	 * @see #getCurrency()
	 * @generated
	 */
	void setCurrency(Currency value);

	/**
	 * Returns the value of the '<em><b>Input Test</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Input Test</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Input Test</em>' attribute.
	 * @see #setInputTest(String)
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct_InputTest()
	 * @model
	 * @generated
	 */
	String getInputTest();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getInputTest <em>Input Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Input Test</em>' attribute.
	 * @see #getInputTest()
	 * @generated
	 */
	void setInputTest(String value);

	/**
	 * Returns the value of the '<em><b>Output Test</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Output Test</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Output Test</em>' attribute.
	 * @see #setOutputTest(String)
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct_OutputTest()
	 * @model
	 * @generated
	 */
	String getOutputTest();

	/**
	 * Sets the value of the '{@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product#getOutputTest <em>Output Test</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Output Test</em>' attribute.
	 * @see #getOutputTest()
	 * @generated
	 */
	void setOutputTest(String value);

	/**
	 * Returns the value of the '<em><b>Skus Mutation Only</b></em>' containment reference list.
	 * The list contents are of type {@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.SKU}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Skus Mutation Only</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Skus Mutation Only</em>' containment reference list.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct_SkusMutationOnly()
	 * @model containment="true" ordered="false"
	 *        annotation="http://www.eclipse.org/OCL/Collection nullFree='false'"
	 * @generated
	 */
	EList<SKU> getSkusMutationOnly();

	/**
	 * Returns the value of the '<em><b>Input Value List</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Input Value List</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Input Value List</em>' attribute list.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct_InputValueList()
	 * @model
	 * @generated
	 */
	EList<String> getInputValueList();

	/**
	 * Returns the value of the '<em><b>Currencies</b></em>' attribute list.
	 * The list contents are of type {@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency}.
	 * The literals are from the enumeration {@link org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Currencies</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Currencies</em>' attribute list.
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency
	 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage#getProduct_Currencies()
	 * @model default="DOLLAR"
	 * @generated
	 */
	EList<Currency> getCurrencies();

} // Product
