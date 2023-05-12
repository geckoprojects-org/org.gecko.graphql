/**
 */
package org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestPackage
 * @generated
 */
public interface GraphQLTestFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GraphQLTestFactory eINSTANCE = org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.impl.GraphQLTestFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Catalog</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Catalog</em>'.
	 * @generated
	 */
	Catalog createCatalog();

	/**
	 * Returns a new object of class '<em>Catalog Entry</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Catalog Entry</em>'.
	 * @generated
	 */
	CatalogEntry createCatalogEntry();

	/**
	 * Returns a new object of class '<em>Product</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Product</em>'.
	 * @generated
	 */
	Product createProduct();

	/**
	 * Returns a new object of class '<em>SKU</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>SKU</em>'.
	 * @generated
	 */
	SKU createSKU();

	/**
	 * Returns a new object of class '<em>Data Fetcher Test</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Fetcher Test</em>'.
	 * @generated
	 */
	DataFetcherTest createDataFetcherTest();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	GraphQLTestPackage getGraphQLTestPackage();

} //GraphQLTestFactory
